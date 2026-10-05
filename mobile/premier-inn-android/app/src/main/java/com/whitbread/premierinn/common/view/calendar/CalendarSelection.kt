package com.whitbread.premierinn.common.view.calendar

import org.threeten.bp.LocalDate

sealed class CalendarSelection
data class DateSelection(val date: LocalDate?) : CalendarSelection()
data class DateRangeSelection(val start: LocalDate?, val end: LocalDate?) : CalendarSelection() {
    init {
        require(start != null || end == null)
        require(if (start != null && end != null) start.isBefore(end) else true)
    }
}