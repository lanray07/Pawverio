package com.pawverio.platform

import org.junit.Assert.*
import org.junit.Test

class QuietHoursTest {
    @Test fun overnightQuietHoursIncludeLateNightAndEarlyMorning() {
        assertTrue(QuietHours.contains(23,22,7)); assertTrue(QuietHours.contains(0,22,7))
        assertTrue(QuietHours.contains(6,22,7)); assertFalse(QuietHours.contains(7,22,7)); assertFalse(QuietHours.contains(12,22,7))
    }
    @Test fun daytimeAndDisabledRangesHaveCorrectBoundaries() {
        assertTrue(QuietHours.contains(10,9,17)); assertFalse(QuietHours.contains(17,9,17)); assertFalse(QuietHours.contains(9,9,9))
    }
}
