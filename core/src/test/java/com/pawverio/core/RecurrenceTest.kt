package com.pawverio.core

import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class RecurrenceTest {
    private val zone = ZoneId.of("Europe/London")
    private fun instant(value: String) = ZonedDateTime.parse(value).toInstant().toEpochMilli()
    @Test fun dailyRecurrencePreservesClockAcrossSpringDst() {
        val start = instant("2026-03-28T08:00:00Z[Europe/London]")
        val next = Recurrence.nextAfter(start, 1, start, zone)
        assertEquals(instant("2026-03-29T08:00:00+01:00[Europe/London]"), next)
    }
    @Test fun overdueTasksAdvanceWithoutFloodingPastReminders() {
        val start = instant("2026-11-01T08:00:00Z[Europe/London]")
        val now = instant("2026-11-04T12:00:00Z[Europe/London]")
        assertEquals(instant("2026-11-05T08:00:00Z[Europe/London]"), Recurrence.nextAfter(start, 1, now, zone))
    }
    @Test fun weeklySchedulePreservesOriginalWeekday() {
        val start = instant("2026-11-01T08:00:00Z[Europe/London]")
        assertEquals(instant("2026-11-08T08:00:00Z[Europe/London]"), Recurrence.nextAfter(start, 7, start + 1000, zone))
    }
    @Test(expected = IllegalArgumentException::class) fun rejectsZeroDays() { Recurrence.nextAfter(0, 0, 1) }
}
