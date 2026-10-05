package com.whitbread.premierinn.calendar.maincalendar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ARRIVAL_DATE
import com.whitbread.premierinn.data.common.DEFAULT_MAX_NIGHTS_LEISURE
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate

@AndroidEntryPoint
class HomeCalendarActivity : BaseCalendarActivity() {

    private val maxNight: Int by lazy { intent.getIntExtra(MAX_NIGHTS, DEFAULT_MAX_NIGHTS_LEISURE) }
    private val maxArrivalDate: Int by lazy { intent.getIntExtra(MAX_ARRIVAL_DATE, DEFAULT_MAX_ARRIVAL_DATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val arrivalDate = intent.getSerializableExtra(CALENDAR_SELECTED_ARRIVAL) as? LocalDate
        val departureDate = intent.getSerializableExtra(CALENDAR_SELECTED_DEPARTURE) as? LocalDate

        setMaxNightsAndMaxArrivalDate(maxNight, maxArrivalDate)
        setArrivalAndDepartureDate(arrivalDate, departureDate)

        setContent()
        setDoneButton()
    }

    override fun setArrivalAndDepartureDate(arrivalDate: LocalDate?, departureDate: LocalDate?) {
        setDates(arrivalDate, departureDate)
    }

    override fun setContent() { setUpCalendar() }

    override fun setDoneButton() {
        buttonDone.setOnClickListener {
            val intent = Intent().apply {
                putExtra(CALENDAR_SELECTED_ARRIVAL, calendarSelection.start)
                putExtra(CALENDAR_SELECTED_DEPARTURE, calendarSelection.end)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    companion object {
        const val CALENDAR_SELECTED_ARRIVAL = "calendar_selected_arrival"
        const val CALENDAR_SELECTED_DEPARTURE = "calendar_selected_departure"
        const val MAX_NIGHTS = "max_nights"
        const val MAX_ARRIVAL_DATE = "max_arrival_date"

        @JvmStatic
        fun createIntent(context: Context, arrivalDate: LocalDate, departureDate: LocalDate, maxNight: Int, maxArrivalDate: Int): Intent {
            return Intent(context, HomeCalendarActivity::class.java).apply {
                putExtra(CALENDAR_SELECTED_ARRIVAL, arrivalDate)
                putExtra(CALENDAR_SELECTED_DEPARTURE, departureDate)
                putExtra(MAX_NIGHTS, maxNight)
                putExtra(MAX_ARRIVAL_DATE, maxArrivalDate)

            }
        }

        @JvmStatic
        fun createIntent(context: Context,  maxNight: Int, maxArrivalDate: Int): Intent {
            return Intent(context, HomeCalendarActivity::class.java).apply {
                putExtra(MAX_NIGHTS, maxNight)
                putExtra(MAX_ARRIVAL_DATE, maxArrivalDate)
            }
        }
    }
}