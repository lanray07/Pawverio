package com.pawverio.platform

import android.content.Context
import android.content.ContextWrapper
import androidx.test.platform.app.InstrumentationRegistry
import com.amazon.device.iap.PurchasingListener
import com.amazon.device.iap.internal.model.*
import com.amazon.device.iap.model.*
import org.junit.Assert.*
import org.junit.Test
import java.util.Date
import java.util.UUID

/** Injected SDK callbacks test our handling only; these are NOT real Amazon purchase tests. */
class AmazonBillingCallbackTest {
    private val sku = "pawverio.premium.lifetime"
    private val user = UserDataBuilder().setUserId("test-owner").setMarketplace("US").build()

    private class Gateway : AmazonPurchaseGateway {
        val resets = mutableListOf<Boolean>()
        val fulfilled = mutableListOf<String>()
        var lastRequestId = RequestId()
        var fulfillmentFails = false
        override fun register(context: Context, listener: PurchasingListener) {}
        override fun userData() {}
        override fun productData(skus: Set<String>) {}
        override fun purchase(sku: String) {}
        override fun updates(reset: Boolean): RequestId {
            resets += reset
            return RequestId().also { lastRequestId = it }
        }
        override fun fulfill(receiptId: String) {
            if (fulfillmentFails) throw IllegalStateException("Injected store-service failure")
            fulfilled += receiptId
        }
    }

    private fun billing(gateway: Gateway): AmazonBilling {
        val base = InstrumentationRegistry.getInstrumentation().targetContext
        val suffix = UUID.randomUUID().toString()
        val isolated = object : ContextWrapper(base) {
            override fun getApplicationContext(): Context = this
            override fun getSharedPreferences(name: String, mode: Int) =
                base.getSharedPreferences("callback-test-$suffix-$name", mode)
        }
        return AmazonBilling(isolated, sku, gateway, publicKeyAvailable = { true }).also {
            it.connect()
            it.onUserDataResponse(UserDataResponseBuilder().setRequestId(RequestId())
                .setRequestStatus(UserDataResponse.RequestStatus.SUCCESSFUL).setUserData(user).build())
        }
    }

    private fun receipt(id: String, canceled: Boolean = false, actualSku: String = sku) =
        ReceiptBuilder().setReceiptId(id).setSku(actualSku).setProductType(ProductType.ENTITLED)
            .setPurchaseDate(Date(1000)).apply { if (canceled) setCancelDate(Date(2000)) }.build()

    private fun updates(gateway: Gateway, receipts: List<Receipt>, more: Boolean = false) =
        PurchaseUpdatesResponseBuilder().setRequestId(gateway.lastRequestId).setUserData(user)
            .setRequestStatus(PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL)
            .setReceipts(receipts).setHasMore(more).build()

    @Test fun restoredPendingPurchaseIsAcknowledgedOnlyAfterAllPages() {
        val gateway = Gateway()
        val billing = billing(gateway)
        billing.onPurchaseUpdatesResponse(updates(gateway, listOf(receipt("restored")), more = true))
        assertFalse(billing.state.value.premium)
        assertTrue(gateway.fulfilled.isEmpty())
        assertEquals(listOf(true, false), gateway.resets)
        billing.onPurchaseUpdatesResponse(updates(gateway, listOf(receipt("revoked", canceled = true), receipt("other", actualSku = "other"))))
        assertTrue(billing.state.value.premium)
        assertEquals(listOf("restored"), gateway.fulfilled)
        assertEquals(BillingStatus.RESTORED, billing.state.value.status)
    }

    @Test fun fulfillmentFailurePreservesDeliveredEntitlementAndRetriesOnRestore() {
        val gateway = Gateway()
        val billing = billing(gateway)
        gateway.fulfillmentFails = true
        billing.onPurchaseUpdatesResponse(updates(gateway, listOf(receipt("retry"))))
        assertTrue(billing.state.value.premium)
        assertEquals(BillingStatus.ERROR, billing.state.value.status)
        gateway.fulfillmentFails = false
        billing.restore()
        billing.onPurchaseUpdatesResponse(updates(gateway, listOf(receipt("retry"))))
        assertEquals(listOf("retry"), gateway.fulfilled)
        assertEquals(BillingStatus.RESTORED, billing.state.value.status)
    }

    @Test fun failedRestorePreservesOfflineCache() {
        val gateway = Gateway()
        val billing = billing(gateway)
        billing.onPurchaseUpdatesResponse(updates(gateway, listOf(receipt("cached"))))
        billing.restore()
        billing.onPurchaseUpdatesResponse(PurchaseUpdatesResponseBuilder().setRequestId(gateway.lastRequestId)
            .setRequestStatus(PurchaseUpdatesResponse.RequestStatus.FAILED).build())
        assertTrue(billing.state.value.premium)
        assertFalse(billing.state.value.busy)
        assertEquals(BillingStatus.UNAVAILABLE, billing.state.value.status)
    }

    @Test fun directPurchaseFulfillmentFailureDoesNotCrashOrLoseEntitlement() {
        val gateway = Gateway()
        val billing = billing(gateway)
        billing.onPurchaseUpdatesResponse(updates(gateway, emptyList()))
        gateway.fulfillmentFails = true
        billing.onPurchaseResponse(PurchaseResponseBuilder().setRequestId(RequestId()).setUserData(user)
            .setRequestStatus(PurchaseResponse.RequestStatus.SUCCESSFUL).setReceipt(receipt("delivered")).build())
        assertTrue(billing.state.value.premium)
        assertEquals(BillingStatus.ERROR, billing.state.value.status)
    }

    @Test fun failedIdentityRefreshRejectsLatePurchaseCallbacks() {
        val gateway = Gateway()
        val billing = billing(gateway)
        billing.onPurchaseUpdatesResponse(updates(gateway, emptyList()))
        billing.onUserDataResponse(UserDataResponseBuilder().setRequestId(RequestId())
            .setRequestStatus(UserDataResponse.RequestStatus.FAILED).build())
        billing.onPurchaseResponse(PurchaseResponseBuilder().setRequestId(RequestId()).setUserData(user)
            .setRequestStatus(PurchaseResponse.RequestStatus.SUCCESSFUL).setReceipt(receipt("late")).build())
        assertFalse(billing.state.value.premium)
        assertTrue(gateway.fulfilled.isEmpty())
    }

    @Test fun duplicateRestoreDoesNotResetAnInFlightPagination() {
        val gateway = Gateway()
        val billing = billing(gateway)
        billing.restore()
        assertEquals(listOf(true), gateway.resets)
        billing.onPurchaseUpdatesResponse(updates(gateway, listOf(receipt("first")), more = true))
        billing.restore()
        assertEquals(listOf(true, false), gateway.resets)
        billing.onPurchaseUpdatesResponse(updates(gateway, emptyList()))
        assertTrue(billing.state.value.premium)
    }

    @Test fun previousAccountRestoreCannotOverwriteNewAccountOrCancelItsRequest() {
        val gateway = Gateway()
        val billing = billing(gateway)
        val oldRequest = gateway.lastRequestId
        val newUser = UserDataBuilder().setUserId("second-owner").setMarketplace("US").build()
        billing.onUserDataResponse(UserDataResponseBuilder().setRequestId(RequestId())
            .setRequestStatus(UserDataResponse.RequestStatus.SUCCESSFUL).setUserData(newUser).build())
        val newRequest = gateway.lastRequestId
        assertNotEquals(oldRequest, newRequest)
        billing.onPurchaseUpdatesResponse(PurchaseUpdatesResponseBuilder().setRequestId(oldRequest)
            .setRequestStatus(PurchaseUpdatesResponse.RequestStatus.FAILED).build())
        assertTrue(billing.state.value.busy)
        billing.onPurchaseUpdatesResponse(PurchaseUpdatesResponseBuilder().setRequestId(newRequest)
            .setRequestStatus(PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL).setUserData(newUser)
            .setReceipts(emptyList()).setHasMore(false).build())
        assertFalse(billing.state.value.premium)
        assertEquals(BillingStatus.RESTORED, billing.state.value.status)
        assertTrue(gateway.fulfilled.isEmpty())
    }

    @Test fun stalePageAfterCompleteRestoreCannotGrantPremium() {
        val gateway = Gateway()
        val billing = billing(gateway)
        val stale = updates(gateway, listOf(receipt("stale")))
        billing.onPurchaseUpdatesResponse(updates(gateway, emptyList()))
        billing.onPurchaseUpdatesResponse(stale)
        assertFalse(billing.state.value.premium)
        assertTrue(gateway.fulfilled.isEmpty())
    }
}
