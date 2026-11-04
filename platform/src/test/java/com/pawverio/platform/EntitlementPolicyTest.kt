package com.pawverio.platform

import org.junit.Assert.*
import org.junit.Test

class EntitlementPolicyTest {
    @Test fun onlyMatchingNonCanceledEntitlementGrantsPremium() {
        assertTrue(EntitlementPolicy.grantsPremium("premium","premium",true,false))
        assertFalse(EntitlementPolicy.grantsPremium("premium","other",true,false))
        assertFalse(EntitlementPolicy.grantsPremium("premium","premium",false,false))
        assertFalse(EntitlementPolicy.grantsPremium("premium","premium",true,true))
    }
}
