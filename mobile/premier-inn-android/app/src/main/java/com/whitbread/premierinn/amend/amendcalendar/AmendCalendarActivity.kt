package com.whitbread.premierinn.amend.amendcalendar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.bold
import androidx.core.view.isVisible
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.span.CustomTypefaceSpan
import com.whitbread.premierinn.common.utils.IntentUtils
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.common.utils.Truss
import com.whitbread.premierinn.common.view.calendar.DateRangeSelection
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ARRIVAL_DATE
import com.whitbread.premierinn.data.common.DEFAULT_MAX_NIGHTS_LEISURE
import com.whitbread.premierinn.databinding.ActivityAmendCalendarBinding
import com.whitbread.premierinn.domain.common.EMPLOYEE_RATE_MAX_NIGHT
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.PriceDomain
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import org.threeten.bp.format.DateTimeFormatter
import kotlin.properties.Delegates

const val UUID_BASKET_REFERENCE = "uuid_basket_reference"
const val HOTEL_BRAND = "hotel_brand"
const val IS_PROMO_BOOKING = "is_promo_booking"

@AndroidEntryPoint
class AmendCalendarActivity : BaseActivity<ActivityAmendCalendarBinding>() {

    private val disposables = AutoCompositeDisposable(lifecycle)

    private val input by lazy { requireNotNull(intent.getParcelableExtra<ManageBookingInput>(EXTRA_AMEND_INPUT)) }
    private val maxArrivalDate by lazy { requireNotNull(intent.getIntExtra(MAX_ARRIVAL_DATE, DEFAULT_MAX_ARRIVAL_DATE)) }
    private val arrivalDate : LocalDate? by lazy { intent.getSerializableExtra(CALENDAR_SELECTED_ARRIVAL) as? LocalDate }
    private val departureDate : LocalDate? by lazy { intent.getSerializableExtra(CALENDAR_SELECTED_DEPARTURE) as? LocalDate }
    private val token by lazy { requireNotNull(intent.getStringExtra(TOKEN)) }
    private val tempBasketRef by lazy { requireNotNull(intent.getStringExtra(TEMP_BASKET_REFERENCE)) }
    private val currency by lazy { requireNotNull(intent.getStringExtra(CURRENCY)) }
    private val dateRestricted by lazy { intent.getBooleanExtra(AMEND_DATE_RESTRICTIONS, false) }
    private val isPromoBooking by lazy { intent.getBooleanExtra(IS_PROMO_BOOKING, false) }
    private val viewModel: AmendCalendarViewModel by viewModels()

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DateFormat.DAY_DATE_MONTH)
    private var calendarSelection: DateRangeSelection = DateRangeSelection(null, null)

    private var menuItem: MenuItem? = null

    private var buttonContinueHeight by Delegates.notNull<Int>()

    private var nights = 0
    private var uuidBasketReference = EMPTY_STRING_DOMAIN
    private var hotelBrand = EMPTY_STRING_DOMAIN
    private var maxNights by Delegates.notNull<Int>()

    override fun inflateBinding(inflater: LayoutInflater): ActivityAmendCalendarBinding {
        return ActivityAmendCalendarBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbar(StringUtils.EMPTY_STRING, true)
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_close)
        uuidBasketReference = intent.getStringExtra(UUID_BASKET_REFERENCE)!!
        hotelBrand = intent.getStringExtra(HOTEL_BRAND)!!
        maxNights = intent.getIntExtra(MAX_SELECTABLE_NIGHTS, DEFAULT_MAX_NIGHTS_LEISURE)
            .takeIf { !input.isEmployeeBooking } ?: EMPLOYEE_RATE_MAX_NIGHT
        binding.continueButton.setButtonTextGravity(Gravity.CENTER)
        binding.changeDatesButton.setButtonTextGravity(Gravity.CENTER)

        viewModel.events()
            .subscribe {
                when(it) {
                    is AmendCalendarViewModel.StayDatesSuccessEvent -> {
                        binding.changeDatesButton.setLoadingState(true)
                        viewModel.amendSummaryCall(tempBasketRef, uuidBasketReference, token)
                    }
                    is AmendCalendarViewModel.AmendSummarySuccessEvent -> {
                        binding.changeDatesButton.setLoadingState(false)
                        val priceDifference = it.amendSummaryDomain.totalCost - it.amendSummaryDomain.previousTotal
                        updateContinueButton(PriceDomain(priceDifference,currency))
                    }
                    is AmendCalendarViewModel.StayDatesErrorEvent -> {
                        binding.changeDatesButton.setLoadingState(false)
                        binding.calendar.updateErrorBanner(it.error.message.toString())
                        updateResetButton()
                        updateChangeDatesButton()
                    }
                    is AmendCalendarViewModel.GenericErrorEvent -> {
                        binding.changeDatesButton.setLoadingState(false)
                        binding.calendar.updateErrorBanner(it.error.message.toString())
                        slideButtonDown(binding.changeDatesButton)
                    }
                    is AmendCalendarViewModel.AmendedDatesAndUpsellsSavedEvent -> {
                        binding.changeDatesButton.setLoadingState(false)
                        setResult(RESULT_OK)
                        finish()
                    }
                }
            }.addTo(disposables)

        viewModel.states()
                .distinctUntilChanged()
                .subscribe { state ->
                    binding.changeDatesButton.setLoadingState(state.isLoading)

                    state.getErrorMessageChangeStays?.let {
                        binding.calendar.updateErrorBanner(it)
                    }

                }.addTo(disposables)

        binding.changeDatesButton.setOnClickListener {
            changeDatesCheck()
        }

        binding.continueButton.setOnClickListener {

            val intent = Intent().apply {
                putExtra(CALENDAR_SELECTED_ARRIVAL, calendarSelection.start)
                putExtra(CALENDAR_SELECTED_DEPARTURE, calendarSelection.end)
            }
            setResult(RESULT_OK, intent)
            finish()
        }

        buttonContinueHeight = binding.continueWrapper.apply {
            measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        }.measuredHeight

        setUpCalendar()

        binding.calendar.selection()
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
            binding.calendar.removeSelection()
            val restrictedNights =
                viewModel.getRestrictedNights(arrivalDate, departureDate, dateRestricted)
            if (restrictedNights != 0L) {
                binding.calendar.setUpRestrictedDates(
                    restrictedDates = restrictedNights, minSelectableDate = LocalDate.now(),
                    initialSelection = DateRangeSelection(arrivalDate, departureDate)
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateUi() {
        updateArrivalDateHeaderView()
        updateDepartureDateHeaderView()
        binding.calendar.updateErrorBanner()
        nights = if (calendarSelection.start != null && calendarSelection.end != null)
            Period.between(calendarSelection.start, calendarSelection.end).days
        else 0

        binding.changeDatesWrapper.animate().cancel()
        binding.continueWrapper.animate().cancel()

        updateChangeDatesButton()

        if (nights > maxNights) showTooManyNightsDialog()

        updateResetButton()
    }

    private fun setUpCalendar() {
        val restrictedNights = viewModel.getRestrictedNights(arrivalDate, departureDate, dateRestricted)

        binding.calendar.setUp(
                minSelectableDate = LocalDate.now(),
                maxSelectableDate = LocalDate.now().plusDays(maxArrivalDate.toLong() + 1),
                initialSelection = DateRangeSelection(arrivalDate, departureDate),
                restrictedDates = restrictedNights)
    }

    private fun updateResetButton() {
        val titleColorRes = if (calendarSelection.start != null) R.color.white else R.color.teal_light
        menuItem?.apply {
            title = Truss()
                    .pushSpan(ForegroundColorSpan(ContextCompat.getColor(this@AmendCalendarActivity, titleColorRes)))
                    .append(title.toString())
                    .popSpan()
                    .build()
        }
    }

    private fun updateArrivalDateHeaderView() {
        calendarSelection.start?.let {
            binding.calendarHeader.startDate.text = it.format(dateFormatter)
            binding.calendarHeader.arrivingDateLabel.typeface = ResourcesCompat.getFont(this, R.font.proxima_nova_regular)
        } ?: run {
            binding.calendarHeader.startDate.text = resources.getString(R.string.calendar_select_date)
            binding.calendarHeader.arrivingDateLabel.typeface = ResourcesCompat.getFont(this, R.font.proxima_nova_semibold)
        }
    }

    private fun updateDepartureDateHeaderView() {
        calendarSelection.end?.let {
            binding.calendarHeader.endDate.text = it.format(dateFormatter)
            binding.calendarHeader.departureDateLabel.typeface = ResourcesCompat.getFont(this, R.font.proxima_nova_regular)
        } ?: run {
            binding.calendarHeader.endDate.text = resources.getString(R.string.calendar_select_date_dash)
            binding.calendarHeader.departureDateLabel.typeface = ResourcesCompat.getFont(this, R.font.proxima_nova_semibold)
        }
    }

    private fun updateChangeDatesButton() {
        val buttonText = resources.getString(R.string.amend_change_dates)
        binding.changeDatesButton.setText(createButtonText(buttonText).toString())
        binding.changeDatesWrapper.isEnabled = nights in 1..maxNights && !originalArrivalAndDepartureDate()

        when {
            binding.continueWrapper.isVisible && dateRestricted -> {
                slideButtonDown(binding.continueWrapper)
                slideButtonUp(binding.changeDatesWrapper)
            }

            binding.continueWrapper.isVisible -> slideButtonDown(binding.continueWrapper)

            binding.changeDatesWrapper.isEnabled && !binding.changeDatesWrapper.isVisible -> slideButtonUp(binding.changeDatesWrapper)
            !binding.changeDatesWrapper.isEnabled && binding.changeDatesWrapper.isVisible -> slideButtonDown(binding.changeDatesWrapper)
        }
    }

    private fun updateContinueButton() {
        val buttonText = resources.getString(R.string.button_text_continue)
        binding.continueButton.setText(createButtonText(buttonText).toString())
        binding.continueWrapper.isEnabled = nights in 1..maxNights && !originalArrivalAndDepartureDate()

        binding.continueWrapper.animate().cancel()

        if (binding.continueWrapper.isEnabled && !binding.continueWrapper.isVisible) slideButtonUp(binding.continueWrapper)
        else if (!binding.continueWrapper.isEnabled && binding.continueWrapper.isVisible) slideButtonDown(binding.continueWrapper)
    }

    private fun updateContinueButton(priceDifference: PriceDomain) {
        if (binding.continueWrapper.isEnabled && !binding.continueWrapper.isVisible) {
            slideButtonDown(binding.changeDatesWrapper)
            updateContinueButton()
            slideButtonUp(binding.continueWrapper)
            binding.amendPriceDifferenceText.text = createPriceDifferenceText(priceDifference)
            binding.amendPriceDifferenceText.visibility = View.VISIBLE
        }
    }

    private fun changeDatesCheck() {
        if (calendarSelection.start != null && calendarSelection.end != null && !originalArrivalAndDepartureDate()) {
            val updatedDates = Pair(calendarSelection.start!!, calendarSelection.end!!)
            binding.changeDatesButton.setLoadingState(true)
            viewModel.makePromoInfoCallAndChangeDates(
                token, tempBasketRef, updatedDates, input.isBusinessBooking,
                isPromoBooking, uuidBasketReference, hotelBrand,
            )
        }
    }

    private fun originalArrivalAndDepartureDate(): Boolean {
        return calendarSelection.start == arrivalDate && calendarSelection.end == departureDate
    }

    private fun slideButtonDown(button: View) {
        button.visibility = View.VISIBLE

        button.animate()
                .translationYBy(buttonContinueHeight.toFloat())
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        button.visibility = View.GONE
                        button.translationY = 0f
                    }
                })
                .start()
    }

    private fun slideButtonUp(button: View) {
        button.visibility = View.VISIBLE
        button.translationY = buttonContinueHeight.toFloat()

        button.animate()
                .translationYBy(-buttonContinueHeight.toFloat())
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {})
                .start()
    }

    private fun createButtonText(text: String): CharSequence {
        return Truss()
                .append(text)
                .append('\n')
                .pushSpan(CustomTypefaceSpan("", ResourcesCompat.getFont(this, R.font.proxima_nova_regular)))
                .pushSpan(AbsoluteSizeSpan(14, true))
                .append(resources.getQuantityString(R.plurals.nights_in_parenthesis, nights, nights))
                .popSpan()
                .build()
    }

    private fun createPriceDifferenceText(price: PriceDomain): CharSequence {
        val priceDifference = viewModel.createTotalCost(price)

        return SpannableStringBuilder()
                .append(resources.getString(R.string.amend_calendar_price_difference))
                .append(StringUtils.COLON)
                .append(StringUtils.SPACE)
                .bold { append(priceDifference) }
    }

    private fun showTooManyNightsDialog() {
        val dialog = AlertDialog.Builder(this, R.style.PurpleDialog)
                .setTitle(resources.getString(R.string.calendar_too_many_nights_dialog_title, maxNights.toString()))
                .setMessage(resources.getString(R.string.calendar_too_many_nights_dialog_description))
                .setNegativeButton(getString(R.string.calendar_dialog_cancel)) { _, _ -> binding.calendar.removeSelection() }
                .setPositiveButton(getString(R.string.calendar_dialog_call_us)) { _, _ -> launchTelephone() }
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

    private fun finishActivity(updated: Boolean) {
        if (updated) {
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == MANAGE_UPSELLS_REQUEST_CODE) {
            finishActivity(resultCode == Activity.RESULT_OK)
        }
    }

    companion object {
        const val CALENDAR_SELECTED_ARRIVAL = "CALENDAR_SELECTED_ARRIVAL"
        const val CALENDAR_SELECTED_DEPARTURE = "CALENDAR_SELECTED_DEPARTURE"
        private const val ORIGINAL_ROOM_TOTAL_COST = "ORIGINAL_ROOM_TOTAL_COST"
        private const val CURRENCY = "CURRENCY"
        private const val EXTRA_AMEND_INPUT = "AMEND_RESERVATION_INPUT"
        private const val AMEND_DATE_RESTRICTIONS = "AMEND_DATE_RESTRICTIONS"
        const val AMEND_UPSELLS_RESTRICTIONS = "AMEND_UPSELLS_RESTRICTIONS"
        private const val MANAGE_UPSELLS_REQUEST_CODE = 101
        private const val MAX_SELECTABLE_NIGHTS = "MAX_SELECTABLE_NIGHTS"
        private const val MAX_ARRIVAL_DATE = "MAX_ARRIVAL_DATE"
        private const val TOKEN = "TOKEN"
        private const val TEMP_BASKET_REFERENCE = "TEMP_BASKET_REFERENCE"


        @JvmStatic
        fun createIntent(
            context: Context,
            input: ManageBookingInput,
            token: String,
            tempBasketRef: String,
            currentBookingTotal: Float,
            currency: String,
            arrivalDate: LocalDate,
            departureDate: LocalDate,
            isDateRestricted: Boolean,
            isUpsellsRestricted: Boolean,
            maxNights: Int,
            maxArrivalDate: Int,
            uuidBasketReference: String,
            hotelBrand: String,
            isPromoBooking: Boolean,
        ): Intent {
            return Intent(context, AmendCalendarActivity::class.java).apply {
                putExtra(EXTRA_AMEND_INPUT, input)
                putExtra(TOKEN, token)
                putExtra(TEMP_BASKET_REFERENCE, tempBasketRef)
                putExtra(ORIGINAL_ROOM_TOTAL_COST, currentBookingTotal)
                putExtra(CURRENCY, currency)
                putExtra(CALENDAR_SELECTED_ARRIVAL, arrivalDate)
                putExtra(CALENDAR_SELECTED_DEPARTURE, departureDate)
                putExtra(AMEND_DATE_RESTRICTIONS, isDateRestricted)
                putExtra(AMEND_UPSELLS_RESTRICTIONS, isUpsellsRestricted)
                putExtra(MAX_SELECTABLE_NIGHTS, maxNights)
                putExtra(MAX_ARRIVAL_DATE, maxArrivalDate)
                putExtra(UUID_BASKET_REFERENCE, uuidBasketReference)
                putExtra(HOTEL_BRAND, hotelBrand)
                putExtra(IS_PROMO_BOOKING, isPromoBooking)
            }
        }
    }
}