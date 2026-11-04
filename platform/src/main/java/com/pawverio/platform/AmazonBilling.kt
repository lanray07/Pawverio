package com.pawverio.platform

import android.content.Context
import com.amazon.device.iap.PurchasingListener
import com.amazon.device.iap.PurchasingService
import com.amazon.device.iap.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BillingState(
    val premium: Boolean = false,
    val storePrice: String? = null,
    val busy: Boolean = false,
    val status: BillingStatus = BillingStatus.DISCONNECTED,
)

enum class BillingStatus {
    DISCONNECTED,
    CONNECTING,
    AVAILABLE,
    UNAVAILABLE,
    PURCHASED,
    RESTORED,
    CANCELLED,
    PENDING,
    ERROR,
}

object EntitlementPolicy {
    fun grantsPremium(
        expectedSku: String,
        actualSku: String,
        entitlement: Boolean,
        canceled: Boolean,
    ): Boolean = expectedSku == actualSku && entitlement && !canceled
}

data class EntitlementReceiptFact(
    val receiptId: String,
    val sku: String,
    val entitlement: Boolean,
    val canceled: Boolean,
)

/** Reduces unordered and repeated receipt pages without letting an old cancellation revoke a repurchase. */
class EntitlementReceiptLedger(private val expectedSku: String) {
    private val canceledByReceipt = mutableMapOf<String, Boolean>()
    val premium: Boolean get() = canceledByReceipt.values.any { canceled -> !canceled }
    fun isActive(receiptId: String): Boolean = canceledByReceipt[receiptId] == false
    val activeReceiptIds: List<String> get() = canceledByReceipt.filterValues { !it }.keys.toList()

    fun mergePage(facts: Iterable<EntitlementReceiptFact>) {
        facts.filter { it.sku == expectedSku && it.entitlement && it.receiptId.isNotBlank() }
            .forEach { fact ->
                canceledByReceipt[fact.receiptId] =
                    (canceledByReceipt[fact.receiptId] ?: false) || fact.canceled
            }
    }

    fun reset() = canceledByReceipt.clear()
}

/**
 * Instantiate once per Application. No production path generates receipts or grants test
 * entitlements.
 */
internal interface AmazonPurchaseGateway {
    fun register(context: Context, listener: PurchasingListener)
    fun userData()
    fun productData(skus: Set<String>)
    fun purchase(sku: String)
    fun updates(reset: Boolean): RequestId
    fun fulfill(receiptId: String)
}

private object StorePurchaseGateway : AmazonPurchaseGateway {
    override fun register(context: Context, listener: PurchasingListener) = PurchasingService.registerListener(context, listener)
    override fun userData() { PurchasingService.getUserData() }
    override fun productData(skus: Set<String>) { PurchasingService.getProductData(skus) }
    override fun purchase(sku: String) { PurchasingService.purchase(sku) }
    override fun updates(reset: Boolean): RequestId = PurchasingService.getPurchaseUpdates(reset)
    override fun fulfill(receiptId: String) = PurchasingService.notifyFulfillment(receiptId, FulfillmentResult.FULFILLED)
}

class AmazonBilling internal constructor(
    context: Context,
    private val sku: String,
    private val gateway: AmazonPurchaseGateway,
    private val publicKeyAvailable: () -> Boolean = {
        try {
            context.applicationContext.assets.open("AppstoreAuthenticationKey.pem").use { it.read() != -1 }
        } catch (_: Exception) { false }
    },
) :
    PurchasingListener {
    constructor(context: Context, sku: String = "pawverio.premium.lifetime") : this(context.applicationContext, sku, StorePurchaseGateway)
    private val app = context.applicationContext
    private val preferences = app.getSharedPreferences("amazon-entitlement", Context.MODE_PRIVATE)
    private val mutable =
        MutableStateFlow(BillingState(premium = preferences.getBoolean("premium", false)))
    val state: StateFlow<BillingState> = mutable.asStateFlow()
    private var userId: String? = null
    private var restoring = false
    private val receiptLedger = EntitlementReceiptLedger(sku)
    private var hasCompleteLedger = false
    private var connected = false
    private var updatesRequestId: RequestId? = null

    fun connect() {
        mutable.value = mutable.value.copy(busy = true, status = BillingStatus.CONNECTING)
        // Do not start vendor authentication until the app-specific public key is provisioned.
        // A missing developer configuration must not interrupt ordinary offline pet care.
        if (!publicKeyAvailable()) {
            unavailable()
            return
        }
        try {
            gateway.register(app, this)
            connected = true
            gateway.userData()
            gateway.productData(setOf(sku))
        } catch (_: Exception) {
            unavailable()
        }
    }

    fun purchase() {
        if (!connected || userId == null || mutable.value.storePrice == null) {
            unavailable()
            return
        }
        if (mutable.value.busy) return
        mutable.value = mutable.value.copy(busy = true)
        try {
            gateway.purchase(sku)
        } catch (_: Exception) {
            unavailable()
        }
    }

    fun restore() {
        if (restoring) return
        if (!connected || userId == null) {
            unavailable()
            return
        }
        restoring = true
        receiptLedger.reset()
        hasCompleteLedger = false
        mutable.value = mutable.value.copy(busy = true)
        try {
            updatesRequestId = gateway.updates(true)
        } catch (_: Exception) {
            failRestore()
        }
    }

    private fun unavailable() {
        mutable.value = mutable.value.copy(busy = false, status = BillingStatus.UNAVAILABLE)
    }

    private fun failRestore() {
        updatesRequestId = null
        restoring = false
        receiptLedger.reset()
        hasCompleteLedger = false
        // Never overwrite the offline entitlement cache with incomplete or failed receipt pages.
        unavailable()
    }

    private fun persist(premium: Boolean) {
        preferences.edit().putBoolean("premium", premium).putString("user", userId).apply()
        mutable.value = mutable.value.copy(premium = premium)
    }

    // Fulfillment is retried through purchase updates. A store-service failure must not crash
    // the callback or discard an entitlement already delivered and cached on this device.
    private fun fulfill(receiptId: String): Boolean = try {
        gateway.fulfill(receiptId)
        true
    } catch (_: Exception) {
        false
    }

    private fun matches(receipt: Receipt): Boolean =
        EntitlementPolicy.grantsPremium(
            sku,
            receipt.sku,
            receipt.productType == ProductType.ENTITLED,
            receipt.isCanceled,
        )

    private fun fact(receipt: Receipt) = EntitlementReceiptFact(
        receipt.receiptId, receipt.sku, receipt.productType == ProductType.ENTITLED, receipt.isCanceled
    )

    override fun onUserDataResponse(response: UserDataResponse) {
        if (response.requestStatus != UserDataResponse.RequestStatus.SUCCESSFUL) {
            userId = null
            restoring = false
            updatesRequestId = null
            unavailable()
            return
        }
        val current = response.userData.userId
        if (preferences.getString("user", null) != current) {
            restoring = false
            updatesRequestId = null
            receiptLedger.reset()
            hasCompleteLedger = false
            userId = current
            persist(false)
        } else userId = current
        // Refresh revocations and account changes. A failed offline refresh preserves the existing
        // cache.
        restore()
    }

    override fun onProductDataResponse(response: ProductDataResponse) {
        val product =
            if (response.requestStatus == ProductDataResponse.RequestStatus.SUCCESSFUL)
                response.productData[sku]
            else null
        mutable.value =
            mutable.value.copy(
                storePrice = product?.price,
                busy = restoring,
                status = if (product == null) BillingStatus.UNAVAILABLE else BillingStatus.AVAILABLE,
            )
    }

    override fun onPurchaseResponse(response: PurchaseResponse) {
        if (userId == null || response.userData?.userId != userId) {
            unavailable()
            return
        }
        val status =
            when (response.requestStatus) {
                PurchaseResponse.RequestStatus.SUCCESSFUL -> {
                    val receipt = response.receipt
                    if (receipt != null && matches(receipt)) {
                        receiptLedger.mergePage(listOf(fact(receipt)))
                        if (!receiptLedger.isActive(receipt.receiptId)) BillingStatus.ERROR
                        else {
                            persist(receiptLedger.premium)
                            if (fulfill(receipt.receiptId)) BillingStatus.PURCHASED else BillingStatus.ERROR
                        }
                    } else BillingStatus.ERROR
                }
                PurchaseResponse.RequestStatus.ALREADY_PURCHASED -> {
                    restore()
                    return
                }
                PurchaseResponse.RequestStatus.PENDING -> BillingStatus.PENDING
                PurchaseResponse.RequestStatus.INVALID_SKU,
                PurchaseResponse.RequestStatus.NOT_SUPPORTED -> BillingStatus.UNAVAILABLE
                else -> BillingStatus.ERROR
            }
        // Amazon reports cancellation as FAILED; never assume a failed callback means a successful
        // unlock.
        mutable.value = mutable.value.copy(busy = false, status = status)
    }

    override fun onPurchaseUpdatesResponse(response: PurchaseUpdatesResponse) {
        // Ignore late pages/failures from a superseded restore or a previous Amazon account.
        if (response.requestId != updatesRequestId) return
        if (
            response.requestStatus != PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL ||
                response.userData.userId != userId
        ) {
            failRestore()
            return
        }
        if (!restoring && !hasCompleteLedger) {
            // An incremental page cannot reconstruct an unknown cached purchase history.
            restore()
            return
        }
        receiptLedger.mergePage(response.receipts.map(::fact))
        if (response.hasMore()) {
            try {
                updatesRequestId = gateway.updates(false)
            } catch (_: Exception) {
                failRestore()
            }
            return
        }
        hasCompleteLedger = true
        updatesRequestId = null
        persist(receiptLedger.premium)
        restoring = false
        // Pending purchases may arrive only in purchase updates after restart. Acknowledge
        // active receipts after the complete ledger has been persisted, never canceled ones.
        val fulfilled = receiptLedger.activeReceiptIds.map(::fulfill).all { it }
        mutable.value = mutable.value.copy(busy = false, status = if (fulfilled) BillingStatus.RESTORED else BillingStatus.ERROR)
    }
}
