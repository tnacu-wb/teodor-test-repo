package com.whitbread.premierinn.amend.amendguestsrooms

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.whitbread.premierinn.R
import com.whitbread.premierinn.amend.amendcalendar.AmendCalendarActivity.Companion.AMEND_UPSELLS_RESTRICTIONS
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.Validator
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.view.setValidation
import com.whitbread.premierinn.databinding.ActivityAmendGuestsRoomsBinding
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.RatePlanOpera
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.roomcriteria.RoomCriteriaFragment
import com.whitbread.premierinn.roomcriteria.viewmodel.BaseRoomCriteriaViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AmendGuestsRoomsActivity : BaseActivity<ActivityAmendGuestsRoomsBinding>(), CriteriaViewModelProvider {

    private val roomIndex: Int by lazy { intent.getIntExtra(ROOM_INDEX_KEY, 0) }
    private val roomId: String by lazy { intent.getStringExtra(ROOM_ID_KEY) ?: EMPTY_STRING_DOMAIN}
    private val tempBasketRef by lazy { intent.getStringExtra(TEMP_BASKET_REF) ?: EMPTY_STRING_DOMAIN}
    private val token by lazy { intent.getStringExtra(TOKEN) ?: EMPTY_STRING_DOMAIN}
    private val upsellsRestricted: Boolean by lazy { intent.getBooleanExtra(AMEND_UPSELLS_RESTRICTIONS, false) }
    private val isPromoBooking: Boolean by lazy { intent.getBooleanExtra(IS_PROMO_BOOKING, false) }
    private var ratePlanOpera: RatePlanOpera? = null
    private var updatedReservation: Reservation? = null
    private var leadGuestInput: NameModel? = null
    private val disposable: AutoCompositeDisposable by lazy { AutoCompositeDisposable(lifecycle) }
    private val amendGuestsViewModel: AmendGuestsRoomViewModel by viewModels()
    private val criteriaViewModel: AmendGuestsCriteriaViewModel by viewModels()
    private var roomsRestricted: Boolean? = false
    private var showMealsScreen: Boolean = false
    private lateinit var spinnerArrayAdapter: ArrayAdapter<String>

    private val crashlyticsLogger = LogService()

    override fun inflateBinding(inflater: LayoutInflater): ActivityAmendGuestsRoomsBinding {
        return ActivityAmendGuestsRoomsBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbar(resources.getString(R.string.amend_guests_rooms_title), true)

        setupTitlesList(binding.titlesSpinner)

        binding.amendGuestsButton.setButtonAsUpdate()

        amendGuestsViewModel.states()
                .distinctUntilChanged()
                .subscribe {
                    it.isRoomsRestricted?.let { restrictions ->
                        roomsRestricted = restrictions
                    }
                    it.isGuestNamesRestricted?.let { restrictions ->
                        binding.amendGuestsLeadGuestsLayout.visibility = when (restrictions) {
                            true -> View.GONE
                            else -> View.VISIBLE
                        }
                    }

                    it.getErrorMessageEditRoom?.let { errorMessageEdit ->
                        updateErrorBanner(errorMessageEdit)
                    }

                    when {
                        it.updateCompleted -> {
                            finish()
                        }
                        it.updateWithAvailabilityCompleted -> {
                            ratePlanOpera = it.getRatePlan()
                            updatedReservation = it.getUpdatedReservation()
                            updateButton(it)
                            updateErrorBanner(it.errorMessage)
                            manageMeals(it.getUpdatedReservation())
                        }
                        else -> {
                            showFullScreenLoading(it.fullScreenLoading)
                            updateButton(it)
                            updateLeadGuest(it.getLeadGuest())
                            updateErrorBanner(it.errorMessage)
                        }
                    }
                    it.getErrorMessageEditRoom?.let { errorMessage ->
                        updateErrorBanner(errorMessage)
                    }
                }
                .addTo(disposable)

        amendGuestsViewModel.events()
                .subscribe {
                    when (it) {
                        is AmendGuestsRoomStateEvent.InitialCriteriaLoaded -> {
                            criteriaViewModel.displayCriteria(it.criteria)
                            if (!isPromoBooking) showCriteriaSelection()
                            onCriteriaChanged()
                        }
                        is AmendGuestsRoomStateEvent.GenericErrorEvent -> {
                            crashlyticsLogger.logException(it.error)
                        }
                        is AmendGuestsRoomStateEvent.ShowMealsScreen -> {
                            showMealsScreen = it.show
                        }

                        is AmendGuestsRoomStateEvent.EditNewRoomSuccessEvent -> {
                            amendGuestsViewModel.updateReservationInDbAfterEditRoomSuccess(updatedReservation)
                            binding.amendGuestsButton.setLoadingState(false)
                                setResult(RESULT_OK)
                                finish()
                        }

                        is AmendGuestsRoomStateEvent.EditNewRoomFailEvent -> {
                            binding.amendGuestsButton.setLoadingState(false)
                            updateErrorBanner(it.message)
                        }

                        is AmendGuestsRoomStateEvent.SaveGuestAmendDetailsFailed -> {
                            updateErrorBanner(it.message)
                        }

                        else -> {}
                    }
                }
                .addTo(disposable)

        binding.amendGuestsButton.setOnClickListener { submitInputToViewModel() }

        binding.firstNameInput.setValidation(Validator.NAME_REGEX, getString(R.string.guest_details_first_name_invalid_format_error))
        binding.lastNameInput.setValidation(Validator.NAME_REGEX, getString(R.string.guest_details_last_name_invalid_format_error))
    }

    override fun getCriteriaViewModel(): BaseRoomCriteriaViewModel {
        return criteriaViewModel
    }

    private fun showFullScreenLoading(showLoading: Boolean) {
        binding.amendGuestLoader.isVisible = showLoading
        binding.amendGuestsScrollView.isVisible = !showLoading
    }

    private fun showCriteriaSelection() {
        val index = roomIndex

        roomsRestricted.let { restricted ->
            if (restricted != true) {
                supportFragmentManager.beginTransaction().apply {
                    add(
                        R.id.amend_guests_rooms_fragment_placeholder, RoomCriteriaFragment
                            .newInstance(roomId = index, roomDisplayNumber = index)
                    )
                    commitNow()
                }
            }
        }
    }

    private fun onCriteriaChanged() {
        criteriaViewModel.roomState()
            .distinctUntilChanged()
            .subscribe { amendGuestsViewModel.onCriteriaChanged(it) }
            .addTo(disposable)
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

    private fun submitInputToViewModel() {
        if (hasValidationErrors()) {
            updateErrorBanner(getString(R.string.amend_check_details))
        } else {
            val index = roomIndex
            val title = binding.titlesSpinner.selectedItem as String
            val currentGuest = amendGuestsViewModel.currentState().getLeadGuest()
            leadGuestInput =
                NameModel(
                    title,
                    binding.firstNameInput.inputText,
                    binding.lastNameInput.inputText,
                    currentGuest?.name?.emailAddress
                )

            val newCriteriaState = criteriaViewModel.currentState().getRoom(index)

            amendGuestsViewModel.onSubmitButtonPressed(newCriteriaState, leadGuestInput!!, roomId, tempBasketRef, token)
        }
    }

    private fun hasValidationErrors(): Boolean =
            !binding.firstNameInput.error.isNullOrEmpty() || !binding.lastNameInput.error.isNullOrEmpty()

    private fun updateButton(state: AmendGuestsRoomState) {
        binding.amendGuestsButton.setText(state.buttonText)
        binding.amendGuestsButton.setLoadingState(state.buttonLoading)
    }

    private fun manageMeals(updatedReservation: Reservation?) {
        val editedLeadGuest = updatedReservation?.roomsLeadGuest?.find { it.roomId == roomId }
        val currentGuest = amendGuestsViewModel.currentState().getLeadGuest()

        if (upsellsRestricted.not() && showMealsScreen) {
            showMealsScreen = false

            val newCriteriaState = criteriaViewModel.currentState().getRoom(roomIndex)
            val title = editedLeadGuest?.title ?: binding.titlesSpinner.selectedItem as String
            leadGuestInput =
                NameModel(
                    title, editedLeadGuest?.firstName ?: binding.firstNameInput.inputText,
                    editedLeadGuest?.lastName ?: binding.lastNameInput.inputText,
                    editedLeadGuest?.emailAddress ?: currentGuest?.name?.emailAddress
                )

            binding.amendGuestsButton.setLoadingState(true)
            amendGuestsViewModel.editRoom(roomId, tempBasketRef, token, newCriteriaState, leadGuestInput!!, true)
        } else {
            val newCriteriaState = criteriaViewModel.currentState().getRoom(roomIndex)
            val title = editedLeadGuest?.title ?: binding.titlesSpinner.selectedItem as String
            leadGuestInput =
                NameModel(
                    title, editedLeadGuest?.firstName ?: binding.firstNameInput.inputText,
                    editedLeadGuest?.lastName ?: binding.lastNameInput.inputText,
                    editedLeadGuest?.emailAddress ?: currentGuest?.name?.emailAddress
                )

            binding.amendGuestsButton.setLoadingState(true)

            amendGuestsViewModel.editRoom(roomId, tempBasketRef, token, newCriteriaState, leadGuestInput!!, false)
        }
    }

    private fun updateLeadGuest(leadGuestState: GuestUiModel?) {
        leadGuestState?.let {
            spinnerArrayAdapter.addAll(it.titleOptions)
            binding.titlesSpinner.setSelection(it.titleOptions.indexOf(it.name.title))
            if (leadGuestInput == null) {
                binding.firstNameInput.setText(it.name.firstName)
                binding.lastNameInput.setText(it.name.lastName)
            }
        }
    }

    private fun setupTitlesList(spinner: AppCompatSpinner) {
        spinnerArrayAdapter = ArrayAdapter(this, R.layout.list_popup_selected_item)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.list_popup_dropdown_items)
        spinner.adapter = spinnerArrayAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MANAGE_UPSELLS_REQUEST_CODE -> if (resultCode == RESULT_OK) {
                finish()
            }
        }
    }

    companion object {
        private const val ROOM_INDEX_KEY = "ROOM_INDEX"
        private const val ROOM_ID_KEY = "ROOM_ID"
        private const val ADDED_ROOM_INDEX_KEY = "ADDED_ROOM_INDEX_KEY"
        private const val EXTRA_AMEND_INPUT = "AMEND_RESERVATION_INPUT"
        private const val HOTEL_BRAND = "HOTEL_BRAND"
        private const val SELECTED_RATE_PLAN = "SELECTED_RATE_PLAN"
        private const val UUID_BASKET_REF = "UUID_BASKET_REF"
        private const val MANAGE_UPSELLS_REQUEST_CODE = 7777
        private const val TEMP_BASKET_REF = "TEMP_BASKET_REF"
        private const val TOKEN = "TOKEN"
        private const val IS_PROMO_BOOKING = "IS_PROMO_BOOKING"

        fun createIntent(context: Context,
                         roomIndex: Int, roomId: String,
                         input: ManageBookingInput,
                         hotelBrand: String,
                         selectedRatePlan: String,
                         addedRoomIndex: Int? = null,
                         isUpsellsRestricted: Boolean,
                         tempBasketRef: String,
                         token: String,
                         uuidBasketReference: String,
                         isPromoBooking: Boolean): Intent {
            return Intent(context, AmendGuestsRoomsActivity::class.java).apply {
                putExtra(ROOM_INDEX_KEY, roomIndex)
                putExtra(ROOM_ID_KEY, roomId)
                putExtra(EXTRA_AMEND_INPUT, input)
                putExtra(TEMP_BASKET_REF, tempBasketRef)
                putExtra(TOKEN, token)
                putExtra(SELECTED_RATE_PLAN, selectedRatePlan)
                putExtra(HOTEL_BRAND, hotelBrand)
                putExtra(IS_PROMO_BOOKING, isPromoBooking)
                addedRoomIndex?.let { addedRoomIndex ->
                    putExtra(ADDED_ROOM_INDEX_KEY, addedRoomIndex)
                }
                putExtra(UUID_BASKET_REF, uuidBasketReference)
                putExtra(AMEND_UPSELLS_RESTRICTIONS, isUpsellsRestricted)
            }
        }
    }
}

interface CriteriaViewModelProvider {
    fun getCriteriaViewModel(): BaseRoomCriteriaViewModel
}