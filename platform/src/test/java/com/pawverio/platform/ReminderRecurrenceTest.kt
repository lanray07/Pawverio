package com.pawverio.platform

import org.junit.Assert.*
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class ReminderRecurrenceTest {
    @Test fun snoozeDoesNotShiftNextDailyMedicationTime() {
        val zone = ZoneId.of("Europe/London")
        val anchor = ZonedDateTime.of(2026,11,4,8,0,0,0,zone)
        val snoozed = anchor.plusMinutes(45)
        val next = ReminderRecurrence.nextAfter(anchor.toInstant().toEpochMilli(),1,snoozed.toInstant().toEpochMilli(),zone)
        assertEquals(anchor.plusDays(1).toInstant().toEpochMilli(),next)
    }
    @Test fun dailyAnchorKeepsWallClockAcrossDaylightSavingChange() {
        val zone = ZoneId.of("Europe/London")
        val anchor = ZonedDateTime.of(2026,10,24,8,0,0,0,zone)
        val next = ReminderRecurrence.nextAfter(anchor.toInstant().toEpochMilli(),1,anchor.plusHours(1).toInstant().toEpochMilli(),zone)
        assertEquals(anchor.plusDays(1).toInstant().toEpochMilli(),next)
        assertEquals(25L*60*60*1000,next-anchor.toInstant().toEpochMilli())
    }
    @Test fun overdueRecurrenceSkipsPastOccurrencesWithoutChangingAnchorTime() {
        val zone = ZoneId.of("UTC")
        val anchor = ZonedDateTime.of(2026,1,1,8,0,0,0,zone)
        val next = ReminderRecurrence.nextAfter(anchor.toInstant().toEpochMilli(),7,anchor.plusDays(25).toInstant().toEpochMilli(),zone)
        assertEquals(anchor.plusDays(28).toInstant().toEpochMilli(),next)
    }
}
