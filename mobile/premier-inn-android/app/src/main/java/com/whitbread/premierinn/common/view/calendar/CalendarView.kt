package com.whitbread.premierinn.common.view.calendar

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.Relay
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.format.DateFormat
import io.reactivex.Observable
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter

abstract class CalendarView<T : CalendarSelection> @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var minDate: LocalDate
    private lateinit var maxDate: LocalDate

    protected val selection: Relay<T> = BehaviorRelay.create()

    private val monthFormatter = DateTimeFormatter.ofPattern(DateFormat.MONTH_YEAR_FORMAT)

    private lateinit var calendarView: CalendarView
    private lateinit var errorBanner: LinearLayout
    private lateinit var errorBannerText : TextView

    fun setUp(minSelectableDate: LocalDate, maxSelectableDate: LocalDate, initialSelection: T?, restrictedDates: Long) {
        LayoutInflater.from(context).inflate(R.layout.view_calendar, this, true)
        orientation = VERTICAL
        calendarView = findViewById(R.id.calendarView)
        errorBanner = findViewById(R.id.calendar_error_banner)
        errorBannerText = findViewById(R.id.calendar_error_message)

        minDate = minSelectableDate
        maxDate = maxSelectableDate

        setUpView(restrictedDates)

        val startMonth = YearMonth.from(minSelectableDate)
        val endMonth = YearMonth.from(maxSelectableDate)
        calendarView.setup(startMonth = startMonth, endMonth = endMonth, firstDayOfWeek = DayOfWeek.MONDAY)

        initialSelection?.let {
            preselect(it)
        } ?: run {
            scrollToMonth(startMonth)
        }
    }

    protected abstract fun onDayClicked(calendarDay: CalendarDay, restrictedNights: Long)
    protected abstract fun textColor(date: LocalDate): Int
    protected abstract fun font(date: LocalDate): Typeface?
    protected abstract fun background(date: LocalDate): Drawable?
    protected abstract fun isTodaySelected(): Boolean
    protected abstract fun preselect(preselection: T)

    fun selection(): Observable<T> = selection.distinctUntilChanged()

    private fun setUpView(restrictedNights: Long) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            val dayView = view.findViewById<TextView>(R.id.calendarDayText)
            val labelToday = view.findViewById<View>(R.id.label_today)
            lateinit var day: CalendarDay

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH && day.date.isSelectable()) {
                        onDayClicked(day, restrictedNights)
                    }
                }
            }
        }

        class MonthHeaderViewContainer(view: View) : ViewContainer(view) {
            val monthHeaderView = view.findViewById<TextView>(R.id.calendarMonthHeader)
        }

        class MonthFooterViewContainer(view: View) : ViewContainer(view) {
            val calendarEnd = view.findViewById<TextView>(R.id.calendar_end)
        }

        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val dayView = container.dayView
                dayView.text = day.date.dayOfMonth.toString()
                if (day.owner != DayOwner.THIS_MONTH) {
                    dayView.text = null
                    dayView.background = null
                    container.labelToday.isVisible = false
                } else {
                    dayView.setTextColor(textColor(day.date))
                    dayView.typeface = font(day.date)
                    dayView.background = background(day.date)
                    container.labelToday.isVisible = day.date == LocalDate.now() && !isTodaySelected()
                }
            }
        }

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthHeaderViewContainer> {

            override fun create(view: View) = MonthHeaderViewContainer(view)

            override fun bind(container: MonthHeaderViewContainer, month: CalendarMonth) {
                container.monthHeaderView.text = month.yearMonth.format(monthFormatter)
            }
        }

        calendarView.monthFooterBinder = object : MonthHeaderFooterBinder<MonthFooterViewContainer> {

            override fun create(view: View) = MonthFooterViewContainer(view)

            override fun bind(container: MonthFooterViewContainer, month: CalendarMonth) {
                container.calendarEnd.isVisible = month.yearMonth == YearMonth.from(maxDate)
            }
        }
    }

    protected fun redrawDay(date: LocalDate) {
        calendarView.notifyDateChanged(date, DayOwner.THIS_MONTH)
    }

    protected fun LocalDate.isSelectable(): Boolean {
        return !isBefore(minDate) && !isAfter(maxDate)
    }

    protected fun scrollToMonth(month: YearMonth) {
        calendarView.scrollToMonth(month)
    }

    fun updateErrorBanner(message: String? = null) {
        if (message == null) {
            errorBanner.visibility = View.GONE
        } else if (errorBanner.isGone || errorBannerText.text != message) {
            errorBannerText.text = message
            errorBanner.visibility = View.VISIBLE
        }
    }

    fun setUpRestrictedDates(
        restrictedDates: Long,
        minSelectableDate: LocalDate,
        initialSelection: T?
    ) {
        val startMonth = YearMonth.from(minSelectableDate)

        initialSelection?.let {
            preselect(it)
        } ?: run {
            scrollToMonth(startMonth)
        }
        setUpView(restrictedDates)
    }
}