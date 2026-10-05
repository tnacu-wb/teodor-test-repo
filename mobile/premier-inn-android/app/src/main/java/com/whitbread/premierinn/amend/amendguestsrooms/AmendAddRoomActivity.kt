package com.whitbread.premierinn.amend.amendguestsrooms

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.whitbread.premierinn.R
import com.whitbread.premierinn.amend.amendcalendar.AmendCalendarActivity.Companion.AMEND_UPSELLS_RESTRICTIONS
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.Validator
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.common.format.PriceFormat
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.common.utils.roomCriteriaSummary
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.databinding.ActivityAmendGuestsRoomsBinding
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.NO_AMOUNT
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RatePlanOpera
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.roomcriteria.RoomCriteriaFragment
import com.whitbread.premierinn.roomcriteria.viewmodel.BaseRoomCriteriaViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.LocalDate

private const val AMEND_UPSELLS_ITEM_AVAILABLE = "AMEND_UPSELLS_ITEM_AVAILABLE"
const val EXTRA_AMEND_INPUT = "AMEND_RESERVATION_INPUT"

@AndroidEntryPoint
class AmendAddRoomActivity : BaseActivity<ActivityAmendGuestsRoomsBinding>(), CriteriaViewModelProvider {

    private val roomIndex: Int by lazy { intent.getIntExtra(ROOM_INDEX_KEY, 0) }
    private val hotelName: String by lazy { intent.getStringExtra(HOTEL_NAME) ?: EMPTY_STRING_DOMAIN}
    private val addRoomIndex: Int by lazy { intent.getIntExtra(ADDED_ROOM_INDEX_KEY, -1) }
    private val tempBasketRef by lazy { intent.getStringExtra(TEMP_BASKET_REF) ?: EMPTY_STRING_DOMAIN}
    private val token by lazy { intent.getStringExtra(TOKEN) ?: EMPTY_STRING_DOMAIN}
    private val uuidBasketRef by lazy { intent.getStringExtra(UUID_BASKET_REFERENCE) ?: EMPTY_STRING_DOMAIN}
    private val upsellsRestricted: Boolean by lazy { intent.getBooleanExtra(AMEND_UPSELLS_RESTRICTIONS, false) }
    private val arrivalDeparturePair: Pair<LocalDate, LocalDate> by lazy { intent.getSerializableExtra(
        ARRIVAL_DEPARTURE_LOCAL_DATE) as Pair<LocalDate, LocalDate> }

    private val disposable: AutoCompositeDisposable by lazy { AutoCompositeDisposable(lifecycle) }
    private val criteriaViewModel: AmendGuestsCriteriaViewModel by viewModels()
    private val amendGuestsViewModel: AmendGuestsRoomViewModel by viewModels()
    private val input by lazy { requireNotNull(intent.getParcelableExtra<ManageBookingInput>(EXTRA_AMEND_INPUT)) }

    private lateinit var spinnerArrayAdapter: ArrayAdapter<String>

    private val crashlyticsLogger = LogService()

    private lateinit var addedRoom: RoomCriteria

    private var ratePlanOpera: RatePlanOpera? = null

    override fun inflateBinding(inflater: LayoutInflater): ActivityAmendGuestsRoomsBinding {
        return ActivityAmendGuestsRoomsBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbar(resources.getString(R.string.amend_guests_add_room_title), true)
        binding.amendGuestsButton.setButtonAs(getString(R.string.amend_button_text_availability))

        amendGuestsViewModel.states()
                .distinctUntilChanged()
                .subscribe {
                    when {
                        it.updateCompleted -> {
                            updateErrorBanner(it.errorMessage)
                            finish()
                        }
                        it.addRoomAvailabilityUpdated -> {
                            ratePlanOpera = it.getRatePlan()
                            binding.amendGuestsButton.visibility = View.GONE
                            updateButton(it)
                            updateErrorBanner(it.addRoomErrorMessage)
                            addedRoom = it.getAddedRoom() ?: RoomCriteria.createWithDefaults()
                            addRoomGuests(it.getAddedRoomCost(), it.getRatePlan()!!, it.deviceLocaleProvider)
                            amendGuestsViewModel.onUpdateLeadGuest(true)
                        }
                        else -> {
                            showFullScreenLoading(it.fullScreenLoading)
                            updateErrorBanner(it.addRoomErrorMessage)
                            updateButton(it)
                        }
                    }
                    it.getErrorMessageAddNewRoom?.let { errorMessage ->
                        updateErrorBanner(errorMessage)
                    }
                }
                .addTo(disposable)

        amendGuestsViewModel.events()
                .subscribe {
                    when (it) {
                        is AmendGuestsRoomStateEvent.InitialAddRoomCriteriaLoaded -> {
                            criteriaViewModel.displayCriteriaToNewRoomAdded()
                            showCriteriaSelection()
                        }
                        is AmendGuestsRoomStateEvent.GenericErrorEvent -> {
                            crashlyticsLogger.logException(it.error)
                        }

                        is AmendGuestsRoomStateEvent.AddNewRoomSuccessEvent -> {
                            amendGuestsViewModel.makeBookingConfirmationCallAfterAddRoom(tempBasketRef, uuidBasketRef,
                                token, hotelName, it.guest, arrivalDeparturePair)
                        }

                        is AmendGuestsRoomStateEvent.AddNewRoomFailEvent -> {
                            binding.addGuestButton.setLoadingState(false)
                            updateErrorBanner(it.message)
                        }

                        is AmendGuestsRoomStateEvent.SavingBookingConfirmAfterAddRoomSuccessEvent -> {
                            binding.addGuestButton.setLoadingState(false)
                            finish()
                        }

                        is AmendGuestsRoomStateEvent.SaveUpdatedBookingConfirmationForAddRoomFailed -> {
                            showBookingConfirmationFailureAmendError()
                        }
                        else -> {}
                    }
                }
                .addTo(disposable)

        binding.amendGuestsButton.setOnClickListener { submitNewCriteriaToViewModel() }
    }

    override fun getCriteriaViewModel(): BaseRoomCriteriaViewModel {
        return criteriaViewModel
    }

    private fun showFullScreenLoading(showLoading: Boolean) {
        binding.amendGuestLoader.isVisible = showLoading
        binding.amendGuestsScrollView.isVisible = !showLoading
    }

    private fun showBookingConfirmationFailureAmendError() {
        AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(getString(R.string.amend_error_title))
            .setMessage(getString(R.string.amend_error_message))
            .setPositiveButton(getText(android.R.string.ok))
            { _, _ ->
                finish()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun showCriteriaSelection() {
        val index = if (addRoomIndex != -1) addRoomIndex else roomIndex
        supportFragmentManager.beginTransaction().apply {
            add(R.id.amend_guests_rooms_fragment_placeholder, RoomCriteriaFragment
                    .newInstance(roomId = index, roomDisplayNumber = index))
            try {
                commitNow()
            } catch (exception: IllegalStateException) {
                commitAllowingStateLoss()
            }
        }

        if (addRoomIndex != -1) binding.amendGuestsLeadGuestsLayout.visibility = View.GONE
        criteriaViewModel.roomState()
                .distinctUntilChanged()
                .subscribe { amendGuestsViewModel.onCriteriaChanged(it) }
                .addTo(disposable)
    }

    private fun addRoomGuests(roomCost: PriceDomain?,
                              ratePlanOpera: RatePlanOpera?,
                              deviceLocaleProvider: DeviceLocaleProvider) {
        setToolbar(resources.getString(R.string.amend_guests_add_room_title), true)
        binding.amendEditErrorBanner.visibility = View.GONE
        binding.amendGuestsRoomsFragmentPlaceholder.visibility = View.GONE
        binding.amendAddedRoomCriteriaSummary.root.visibility = View.VISIBLE
        binding.amendAddedRoomCriteriaSummary.amendAddRoomNumberHeading.text = getString(R.string.room_number, addedRoom.roomNumber)
        binding.amendAddedRoomCriteriaSummary.amendAddRoomCriteria.text = addedRoom.roomCriteriaSummary(this)
        binding.amendAddedRoomCriteriaSummary.amendAddRoomPrice.text = PriceFormat.format(roomCost?.amount ?: NO_AMOUNT,
                roomCost?.currency, deviceLocaleProvider
        )

        setupTitlesList(binding.amendAddedRoomLeadGuest.addRoomLeadGuestTitles)
        binding.amendGuestsButton.visibility = View.GONE
        binding.addGuestButton.visibility = View.VISIBLE
        binding.addGuestButton.setButtonAs(getString(R.string.button_text_continue))
        binding.amendAddedRoomLeadGuest.root.visibility = View.VISIBLE

        setAddRoomGuestOnClickListeners(ratePlanOpera)
    }

    private fun setAddRoomGuestOnClickListeners(ratePlanOpera: RatePlanOpera?) {
        binding.addGuestButton.setOnClickListener {
            validateGuestAndAddRoom(ratePlanOpera)
            updateErrorBanner(null)
        }
    }

    private fun validateGuestAndAddRoom(ratePlanOpera: RatePlanOpera?) {
        when {
            !Validator.NAME_REGEX.matcher(binding.amendAddedRoomLeadGuest.addRoomLeadGuestFirstNameInput.text).matches() -> {
                binding.amendAddedRoomLeadGuest.addRoomLeadGuestFirstNameInput.error =
                    getString(R.string.guest_details_first_name_invalid_format_error)
            }

            !Validator.NAME_REGEX.matcher(binding.amendAddedRoomLeadGuest.addRoomLeadGuestLastNameInput.text).matches() -> {
                binding.amendAddedRoomLeadGuest.addRoomLeadGuestLastNameInput.error =
                    getString(R.string.guest_details_last_name_invalid_format_error)
            }

            else -> {
                val guestNameModel = Guest(
                    addRoomIndex,
                    StringUtils.EMPTY_STRING,
                    binding.amendAddedRoomLeadGuest.addRoomLeadGuestTitles.selectedItem.toString(),
                    binding.amendAddedRoomLeadGuest.addRoomLeadGuestFirstNameInput.text.toString(),
                    binding.amendAddedRoomLeadGuest.addRoomLeadGuestLastNameInput.text.toString(),
                    null, null, null, null
                )

                if (upsellsRestricted) {
                    amendGuestsViewModel.onSubmitButtonPressedAddRoom(ratePlanOpera)
                } else {
                    addUpsells(guestNameModel, ratePlanOpera)
                }
            }
        }
    }

    private fun addUpsells(guestNameModel: Guest, ratePlanOpera: RatePlanOpera?) {
        ratePlanOpera?.let {
            val criteria = criteriaViewModel.currentState().getRoom(addRoomIndex)
            val adults = criteria.numberOfAdults
            val children = criteria.numberOfChildren
            val roomTypeCode = criteria.roomType.code
            binding.addGuestButton.setLoadingState(true)
            amendGuestsViewModel.addNewRoom(tempBasketRef!!, token!!, it.rateType, roomTypeCode,
                adults, children,guestNameModel)
        }
    }

    override fun onBackPressed() {
        binding.amendGuestsButton.setLoadingState(false)
        if (addRoomIndex != -1 && binding.amendAddedRoomCriteriaSummary.root.isVisible) {
            amendGuestsViewModel.onUpdateLeadGuest(false)
            binding.amendAddedRoomCriteriaSummary.root.visibility = View.GONE
            binding.amendAddedRoomLeadGuest.root.visibility = View.GONE
            binding.amendGuestsButton.visibility = View.VISIBLE
            binding.addGuestButton.visibility = View.GONE
            binding.amendGuestsRoomsFragmentPlaceholder.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
        updateErrorBanner(null)
    }

    private fun updateErrorBanner(message: String?) {
        if (message == null) {
            binding.amendEditErrorBanner.isVisible = false
        } else if (!binding.amendEditErrorBanner.isVisible || binding.amendErrorMessage.text != message) {
            binding.amendErrorMessage.text = message
            binding.amendGuestsScrollView.smoothScrollTo(0, 0)
            binding.amendEditErrorBanner.isVisible = true
        }
    }

    private fun submitNewCriteriaToViewModel() {
        val index = if (addRoomIndex != -1) addRoomIndex else roomIndex
        val newCriteriaState = criteriaViewModel.currentState().getRoom(index)
        amendGuestsViewModel.onCheckAvailabilityButtonPressed(newCriteriaState)
    }

    private fun updateButton(state: AmendGuestsRoomState) {
        if (binding.amendGuestsButton.isVisible) {
            binding.amendGuestsButton.setText(state.buttonText)
            binding.amendGuestsButton.setLoadingState(state.addRoomButtonLoading)
        } else if (binding.addGuestButton.isVisible) {
            binding.addGuestButton.setText(state.buttonText)
            binding.addGuestButton.setLoadingState(state.buttonLoading)
        }
    }

    private fun setupTitlesList(spinner: AppCompatSpinner) {
        spinnerArrayAdapter = ArrayAdapter(this, R.layout.list_popup_selected_item, resources
                .getStringArray(R.array.titles))
        spinnerArrayAdapter.setDropDownViewResource(R.layout.list_popup_dropdown_items)
        spinner.adapter = spinnerArrayAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.amend_guests_rooms_fragment_placeholder)
        fragment?.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ADD_UPSELLS_REQUEST_CODE -> if (resultCode == RESULT_OK) {
                finish()
            }
        }
    }

    companion object {
        const val ROOM_INDEX_KEY = "ROOM_INDEX"
        const val ADDED_ROOM_INDEX_KEY = "ADDED_ROOM_INDEX_KEY"
        private const val ADD_UPSELLS_REQUEST_CODE = 6666
        const val HOTEL_BRAND = "HOTEL_BRAND"
        const val SELECTED_RATE_PLAN = "SELECTED_RATE_PLAN"
        private const val TEMP_BASKET_REF = "TEMP_BASKET_REF"
        private const val TOKEN = "TOKEN"
        private const val UUID_BASKET_REFERENCE = "UUID_BASKET_REFERENCE"
        private const val HOTEL_NAME = "HOTEL_NAME"
        private const val ARRIVAL_DEPARTURE_LOCAL_DATE = "ARRIVAL_DEPARTURE_LOCAL_DATE"

        fun createIntent(context: Context, hotelBrand: String,
                         selectedRatePlan: String, tempBasketRef: String, token: String,
                         uuidBasketRef: String,
                         roomIndex: Int, addedRoomIndex: Int? = null, isUpsellsRestricted: Boolean,
                         input: ManageBookingInput, hotelName: String,
                         arrivalDepartureDate: Pair<LocalDate, LocalDate>): Intent {
            return Intent(context, AmendAddRoomActivity::class.java).apply {
                putExtra(ROOM_INDEX_KEY, roomIndex)
                putExtra(SELECTED_RATE_PLAN, selectedRatePlan)
                putExtra(TEMP_BASKET_REF, tempBasketRef)
                putExtra(TOKEN, token)
                putExtra(UUID_BASKET_REFERENCE, uuidBasketRef)
                putExtra(HOTEL_BRAND, hotelBrand)
                putExtra(HOTEL_NAME, hotelName)
                addedRoomIndex?.let { addedRoomIndex -> putExtra(ADDED_ROOM_INDEX_KEY, addedRoomIndex) }
                putExtra(EXTRA_AMEND_INPUT, input)
                putExtra(AMEND_UPSELLS_RESTRICTIONS, isUpsellsRestricted)
                putExtra(AMEND_UPSELLS_ITEM_AVAILABLE, isUpsellsRestricted)
                putExtra(AMEND_UPSELLS_ITEM_AVAILABLE, isUpsellsRestricted)
                putExtra(ARRIVAL_DEPARTURE_LOCAL_DATE, arrivalDepartureDate)
            }
        }
    }
}