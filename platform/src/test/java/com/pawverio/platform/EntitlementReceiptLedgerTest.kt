package com.pawverio.platform

import org.junit.Assert.*
import org.junit.Test

class EntitlementReceiptLedgerTest {
    private fun fact(id: String, canceled: Boolean = false, sku: String = "premium", entitlement: Boolean = true) =
        EntitlementReceiptFact(id,sku,entitlement,canceled)

    @Test fun everyOrderingOfCanceledOldAndRepurchasedNewReceiptGrantsPremium() {
        val facts = listOf(fact("old"),fact("old",canceled=true),fact("new"))
        facts.forEachIndexed { firstIndex,first ->
            facts.forEachIndexed { secondIndex,second ->
                if (firstIndex != secondIndex) {
                    val third = facts.indices.single { it != firstIndex && it != secondIndex }
                    val ledger = EntitlementReceiptLedger("premium")
                    ledger.mergePage(listOf(first,second,facts[third]))
                    assertTrue(ledger.premium)
                }
            }
        }
    }

    @Test fun cancellationDominatesAnActiveFactForSameReceiptInEitherOrder() {
        listOf(listOf(fact("same"),fact("same",true)),listOf(fact("same",true),fact("same"))).forEach { facts ->
            val ledger = EntitlementReceiptLedger("premium")
            ledger.mergePage(facts)
            assertFalse(ledger.premium)
            assertFalse(ledger.isActive("same"))
        }
    }

    @Test fun wrongSkuNonEntitlementAndBlankIdentityCannotGrantOrRevoke() {
        val ledger = EntitlementReceiptLedger("premium")
        ledger.mergePage(listOf(fact("wrong",sku="other"),fact("consumable",entitlement=false),fact("")))
        assertFalse(ledger.premium)
        ledger.mergePage(listOf(fact("real")))
        ledger.mergePage(listOf(fact("real",true,sku="other"),fact("real",true,entitlement=false)))
        assertTrue(ledger.premium)
    }

    @Test fun fullHistoryWithOnlyCanceledReceiptsHasNoEntitlement() {
        val ledger = EntitlementReceiptLedger("premium")
        ledger.mergePage(listOf(fact("old",true),fact("older",true),fact("old")))
        assertFalse(ledger.premium)
    }

    @Test fun mixedPagesAndRepeatedReceiptsHaveSameResultAsOnePage() {
        val ledger = EntitlementReceiptLedger("premium")
        ledger.mergePage(listOf(fact("old"),fact("new")))
        ledger.mergePage(listOf(fact("old",true),fact("new"),fact("older",true)))
        assertTrue(ledger.premium)
        ledger.mergePage(listOf(fact("new",true)))
        assertFalse(ledger.premium)
        ledger.mergePage(listOf(fact("new")))
        assertFalse(ledger.premium)
    }

    @Test fun resetDropsPreviousAccountAndPartialRestoreFacts() {
        val ledger = EntitlementReceiptLedger("premium")
        ledger.mergePage(listOf(fact("previous-user")))
        assertTrue(ledger.premium)
        ledger.reset()
        assertFalse(ledger.premium)
        ledger.mergePage(emptyList())
        assertFalse(ledger.premium)
        ledger.mergePage(listOf(fact("fresh-user")))
        assertTrue(ledger.premium)
    }
}
