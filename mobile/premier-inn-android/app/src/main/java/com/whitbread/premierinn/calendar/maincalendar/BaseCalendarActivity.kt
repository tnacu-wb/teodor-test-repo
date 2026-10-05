package com.whitbread.premierinn.calendar.maincalendar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.span.CustomTypefaceSpan
import com.whitbread.premierinn.common.utils.IntentUtils
import com.whitbread.premierinn.common.utils.Truss
import com.whitbread.premierinn.common.view.calendar.DateRangeSelection
import com.whitbread.premierinn.common.view.calendar.DateRangeSelectionView
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ARRIVAL_DATE
import com.whitbread.premierinn.data.common.DEFAULT_MAX_NIGHTS_LEISURE
import com.whitbread.premierinn.databinding.ActivityNewCalendarBinding
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import org.threeten.bp.format.DateTimeFormatter
import kotlin.properties.Delegates

abstract class BaseCalendarActivity : BaseActivity<ActivityNewCalendarBinding>() {

    private val disposables = AutoCompositeDisposable(lifecycle)
    private val dateFormatter = DateTimeFormatter.ofPattern(DateFormat.DAY_DATE_MONTH)
    protected var calendarSelection: DateRangeSelection = DateRangeSelection(null, null)

    protected lateinit var buttonDone: View
    private lateinit var buttonDoneText: TextView
    private lateinit var calendarView: DateRangeSelectionView

    private lateinit var arrivalDateView: TextView
    private lateinit var departureDateView: TextView
    private var menuItem: MenuItem? = null

    private var buttonDoneHeight by Delegates.notNull<Int>()

    protected var arrivalDate : LocalDate? = null
    protected var departureDate : LocalDate? = null
    private var maxNights: Int = DEFAULT_MAX_NIGHTS_LEISURE
    private var maxArrivalDate: Int = DEFAULT_MAX_ARRIVAL_DATE

    override fun inflateBinding(inflater: LayoutInflater): ActivityNewCalendarBinding {
       return ActivityNewCalendarBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buttonDone = binding.btnDone
        buttonDoneText = binding.doneText
        calendarView = binding.calendar
        arrivalDateView = binding.calendarHeader.startDate
        departureDateView = binding.calendarHeader.endDate

        setToolbar("", true)
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_close)

        buttonDoneHeight = buttonDone.apply {
            measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        }.measuredHeight

        calendarView.selection()
                .subscribe {
                    calendarSelection = it
                    updateUi()
                }.addTo(disposables)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_calendar_reset, menu)
        menuItem = menu.findItem(R.id.toolbar_calendar_reset)
        updateResetButton()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.toolbar_calendar_reset) {
            calendarView.removeSelection()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateUi() {
        updateArrivalDateHeaderView()
        updateDepartureDateHeaderView()

        val nights = if (calendarSelection.start != null && calendarSelection.end != null)
            Period.between(calendarSelection.start, calendarSelection.end).days
        else 0
        updateDoneButton(nights)

        if (nights > maxNights) showTooManyNightsDialog()

        updateResetButton()
    }

    private fun updateResetButton() {
        val titleColorRes = if (calendarSelection.start != null) R.color.white else R.color.teal_light
        menuItem?.apply {
            title = Truss()
                    .pushSpan(ForegroundColorSpan(ContextCompat.getColor(this@BaseCalendarActivity, titleColorRes)))
                    .append(title.toString())
                    .popSpan()
                    .build()
        }
    }

    private fun updateArrivalDateHeaderView() {
        calendarSelection.start?.let {
            arrivalDateView.text = it.format(dateFormatter)
        } ?: run {
            arrivalDateView.text = resources.getString(R.string.calendar_select_date)
        }
    }

    private fun updateDepartureDateHeaderView() {
        calendarSelection.end?.let {
            departureDateView.text = it.format(dateFormatter)
        } ?: run {
            departureDateView.text = resources.getString(R.string.calendar_select_date_dash)
        }
    }

    private fun updateDoneButton(nights: Int) {
        buttonDoneText.text = createDoneButtonText(nights)
        buttonDone.isEnabled = nights in 1..maxNights

        buttonDone.animate().cancel()

        if (buttonDone.isEnabled && !buttonDone.isVisible) slideDoneButtonUp()
        else if (!buttonDone.isEnabled && buttonDone.isVisible) slideDoneButtonDown()
    }

    private fun slideDoneButtonUp() {
        buttonDone.visibility = View.VISIBLE
        buttonDone.translationY = buttonDoneHeight.toFloat()

        buttonDone.animate()
                .translationYBy(-buttonDoneHeight.toFloat())
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        buttonDone.visibility = View.VISIBLE
                    }
                })
                .start()
    }

    private fun slideDoneButtonDown() {
        buttonDone.visibility = View.VISIBLE

        buttonDone.animate()
                .translationYBy(buttonDoneHeight.toFloat())
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        buttonDone.visibility = View.GONE
                        buttonDone.translationY = 0f
                    }
                })
                .start()
    }

    private fun createDoneButtonText(nights: Int): CharSequence {
        return Truss()
                .append(resources.getString(R.string.calendar_done))
                .append('\n')
                .pushSpan(CustomTypefaceSpan("", ResourcesCompat.getFont(this, R.font.proxima_nova_regular)))
                .pushSpan(AbsoluteSizeSpan(14, true))
                .append(resources.getQuantityString(R.plurals.nights_in_parenthesis, nights, nights))
                .popSpan()
                .build()
    }

    private fun showTooManyNightsDialog() {
        val dialog = AlertDialog.Builder(this, R.style.PurpleDialog)
                .setTitle(resources.getString(R.string.calendar_too_many_nights_dialog_title, maxNights.toString()))
                .setMessage(resources.getString(R.string.calendar_too_many_nights_dialog_description))
                .setNegativeButton(resources.getString(R.string.calendar_dialog_cancel)) { _, _ -> calendarView.removeSelection() }
                .setPositiveButton(resources.getString(R.string.calendar_dialog_call_us)) { _, _ -> launchTelephone() }
                .create()
        dialog.show()
    }

    private fun launchTelephone() {
        val phoneNumber = resources.getString(R.string.call_view_default_customer_service_number)
        val callIntent = IntentUtils.createTelephoneIntent(phoneNumber)
        if (IntentUtils.checkIntentResolvedActivity(this, callIntent)) {
            startActivity(callIntent)
        } else {
            Toast.makeText(applicationContext, R.string.phone_call_action_not_supported, Toast.LENGTH_LONG).show()
        }
    }

    protected abstract fun setArrivalAndDepartureDate(arrivalDate: LocalDate?, departureDate: LocalDate?)

    protected abstract fun setDoneButton()

    protected abstract fun setContent()

    protected fun setMaxNightsAndMaxArrivalDate(maxNights: Int, maxArrivalDate: Int) {
        this.maxNights = maxNights
        this.maxArrivalDate = maxArrivalDate
    }

    protected fun setUpCalendar() {
        calendarView.setUp(
                minSelectableDate = LocalDate.now(),
                maxSelectableDate = LocalDate.now().plusDays(maxArrivalDate.toLong() + 1),
                initialSelection = DateRangeSelection(arrivalDate, departureDate),
                restrictedDates = 0L)
    }

    protected fun setDates(arrivalDate: LocalDate?, departureDate: LocalDate?) {
        this.arrivalDate = arrivalDate
        this.departureDate = departureDate
    }
}