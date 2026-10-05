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

class DateSelectionView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CalendarView<DateSelection>(context, attrs, defStyleAttr) {

    private var selectedDate: LocalDate? = null

    override fun onDayClicked(calendarDay: CalendarDay, restrictedNights: Long) {
        if (selectedDate != calendarDay.date) {
            val oldSelectedDay = selectedDate
            selectedDate = calendarDay.date
            oldSelectedDay?.let {
                redrawDay(it)
            }
            redrawDay(selectedDate!!)
        }
    }

    override fun textColor(date: LocalDate): Int {
        val colorRes = when {
            date == selectedDate -> R.color.white
            date.isSelectable() -> R.color.grey_dark
            else -> R.color.grey_light
        }
        return ContextCompat.getColor(context, colorRes)
    }

    override fun font(date: LocalDate): Typeface? {
        val fontRes = if (date == selectedDate) R.font.proxima_nova_semibold else R.font.proxima_nova_regular
        return ResourcesCompat.getFont(context, fontRes)
    }

    override fun background(date: LocalDate): Drawable? {
        return if (date == selectedDate)
            ContextCompat.getDrawable(context, R.drawable.ic_calendar_single_selected_date_bg)
        else null
    }

    override fun isTodaySelected(): Boolean {
        return selectedDate == LocalDate.now()
    }

    override fun preselect(preselection: DateSelection) {
        selectedDate = preselection.date
        selectedDate?.let {
            redrawDay(it)
        }
        selection.accept(preselection)
    }
}