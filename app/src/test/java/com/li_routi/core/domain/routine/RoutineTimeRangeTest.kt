package com.li_routi.core.domain.routine

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RoutineTimeRangeTest {

    @Test
    fun blankStartIsAllowed() {
        assertTrue(isValidRoutineTimeRange(null, "09:00"))
        assertTrue(isValidRoutineTimeRange("  ", "09:00"))
        assertTrue(isValidRoutineTimeRange(null, null))
    }

    @Test
    fun blankEndIsAllowedWhenStartIsSet() {
        assertTrue(isValidRoutineTimeRange("08:00", null))
        assertTrue(isValidRoutineTimeRange("08:00", "  "))
    }

    @Test
    fun startMustBeStrictlyBeforeEnd() {
        assertTrue(isValidRoutineTimeRange("08:00", "09:00"))
        assertTrue(isValidRoutineTimeRange(" 08:00 ", " 09:00 "))
        assertFalse(isValidRoutineTimeRange("08:00", "08:00"))
        assertFalse(isValidRoutineTimeRange("09:00", "08:00"))
    }

    @Test
    fun invalidHourOrMinuteIsRejected() {
        assertFalse(isValidRoutineTimeRange("99:00", "99:30"))
        assertFalse(isValidRoutineTimeRange("24:00", "08:00"))
        assertFalse(isValidRoutineTimeRange("08:00", "12:60"))
        assertFalse(isValidRoutineTimeRange("aa", "bb"))
        assertFalse(isValidRoutineTimeRange("8:00", "09:00"))
        assertFalse(isValidRoutineTimeRange("08:00:00", "09:00"))
    }
}
