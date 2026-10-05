package com.whitbread.premierinn.common.view.calendar

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.kizitonwose.calendarview.model.CalendarDay
import com.whitbread.premierinn.R
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth

class DateRangeSelectionView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CalendarView<DateRangeSelection>(context, attrs, defStyleAttr) {

    private var arrivalDate: LocalDate? = null
    private var departureDate: LocalDate? = null

    override fun onDayClicked(calendarDay: CalendarDay, restrictedNights: Long) {
        val clickedDate = calendarDay.date

        fun whenBothArrivalAndDepartureSet(arrival: LocalDate, departure: LocalDate) {
            arrivalDate = clickedDate
            departureDate = null
            redrawPeriod(arrival, departure)
            redrawDay(clickedDate)
            notifySelectionChanged()
        }

        fun whenNightsAreRestricted(arrival: LocalDate, departure: LocalDate, nights: Long) {
            arrivalDate = clickedDate
            departureDate = clickedDate.plusDays(nights)
            redrawPeriod(arrival, departure)
            redrawPeriod(clickedDate, clickedDate.plusDays(nights))
            notifySelectionChanged()
        }

        fun whenOnlyArrivalSet(arrival: LocalDate) {
            if (clickedDate.isAfter(arrival)) {
                departureDate = clickedDate
                redrawPeriod(arrival, clickedDate)
                notifySelectionChanged()
            } else if (clickedDate.isBefore(arrival)) {
                arrivalDate = clickedDate
                redrawDay(arrival)
                redrawDay(clickedDate)
                notifySelectionChanged()
            }
        }

        fun whenNothingSet() {
            arrivalDate = clickedDate
            redrawDay(clickedDate)
            notifySelectionChanged()
        }

        arrivalDate?.let { arrival ->
            departureDate?.let { departure ->
                when (restrictedNights) {
                    0L -> { whenBothArrivalAndDepartureSet(arrival, departure) }
                    else -> { whenNightsAreRestricted(arrival, departure, restrictedNights) }
                }
            } ?: run {
                whenOnlyArrivalSet(arrival)
            }
        } ?: run {
            whenNothingSet()
        }
    }

    private fun notifySelectionChanged() {
        selection.accept(DateRangeSelection(arrivalDate, departureDate))
    }

    fun removeSelection() {
        fun whenBothArrivalAndDepartureSet(arrival: LocalDate, departure: LocalDate) {
            arrivalDate = null
            departureDate = null
            redrawPeriod(arrival, departure)
            notifySelectionChanged()
        }

        fun whenOnlyArrivalSet(arrival: LocalDate) {
            arrivalDate = null
            redrawDay(arrival)
            notifySelectionChanged()
        }

        arrivalDate?.let { arrival ->
            departureDate?.let { departure ->
                whenBothArrivalAndDepartureSet(arrival, departure)
            } ?: run {
                whenOnlyArrivalSet(arrival)
            }
        }
    }

    override fun textColor(date: LocalDate): Int {
        val colorRes = when {
            date == arrivalDate -> R.color.white
            date == departureDate -> R.color.white
            date.isInSelectedInterval() -> R.color.white
            date.isSelectable() -> R.color.grey_dark
            else -> R.color.grey_light
        }
        return ContextCompat.getColor(context, colorRes)
    }

    override fun font(date: LocalDate): Typeface? {
        val fontRes = when {
            date == arrivalDate -> R.font.proxima_nova_semibold
            date == departureDate -> R.font.proxima_nova_semibold
            date.isInSelectedInterval() -> R.font.proxima_nova_semibold
            else -> R.font.proxima_nova_regular
        }
        return ResourcesCompat.getFont(context, fontRes)
    }

    override fun background(date: LocalDate): Drawable? {
        val drawableRes = when {
            date == arrivalDate -> R.drawable.ic_calendar_arrival_bg
            date == departureDate -> R.drawable.ic_calendar_departure_bg
            date.isInSelectedInterval() -> R.drawable.ic_calendar_selected_date_bg
            else -> null
        }
        return if (drawableRes != null) ContextCompat.getDrawable(context, drawableRes) else null
    }

    override fun isTodaySelected(): Boolean {
        val today = LocalDate.now()
        return today == arrivalDate || today.isInSelectedInterval()
    }

    override fun preselect(preselection: DateRangeSelection) {
        arrivalDate = preselection.start
        departureDate = preselection.end

        arrivalDate?.let { arrival ->
            departureDate?.let { departure ->
                redrawPeriod(arrival, departure)
            } ?: run {
                redrawDay(arrival)
            }
        } ?: run {
            departureDate = null
        }
        arrivalDate?.let {
            scrollToMonth(YearMonth.from(it))
        }
        notifySelectionChanged()
    }

    private fun redrawPeriod(start: LocalDate, end: LocalDate) {
        redrawDay(start)

        var day = start.plusDays(1)
        while (day.isBefore(end)) {
            redrawDay(day)
            day = day.plusDays(1)
        }

        redrawDay(end)
    }

    private fun LocalDate.isInSelectedInterval(): Boolean {
        return arrivalDate?.let { arrival ->
            departureDate?.let { departure ->
                !isBefore(arrival) && !isAfter(departure)
            } ?: false
        } ?: false
    }
}