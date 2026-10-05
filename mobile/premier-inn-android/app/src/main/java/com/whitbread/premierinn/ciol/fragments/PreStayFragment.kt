package com.whitbread.premierinn.ciol.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.ciol.CheckInOnlineActivity
import com.whitbread.premierinn.ciol.adapter.GuestRoomListAdapter
import com.whitbread.premierinn.ciol.analytics.CiolCompletionAnalyticsModel
import com.whitbread.premierinn.ciol.entity.GuestRoomClicked
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.mapper.convertToUiModel
import com.whitbread.premierinn.ciol.mapper.convertToUiModelRoomGuestsList
import com.whitbread.premierinn.ciol.mapper.toPreStayDomainModel
import com.whitbread.premierinn.ciol.uimodel.HotelPreferenceUiModel
import com.whitbread.premierinn.ciol.uimodel.InfoBottomSheetData
import com.whitbread.premierinn.ciol.uimodel.PriceBreakdownModel
import com.whitbread.premierinn.ciol.utils.getDates
import com.whitbread.premierinn.ciol.utils.getGeneralGuestsDetails
import com.whitbread.premierinn.ciol.utils.isSecondGuestPresent
import com.whitbread.premierinn.ciol.utils.showCheckInInformationBottomSheet
import com.whitbread.premierinn.ciol.utils.showErrorAlertDialog
import com.whitbread.premierinn.ciol.viewmodel.PreStaySharedViewModel
import com.whitbread.premierinn.ciol.viewmodel.PreStayViewModel
import com.whitbread.premierinn.ciol.viewmodel.PreStayViewModel.NavigationDestination
import com.whitbread.premierinn.ciol.viewmodel.state.utils.FieldType.LEAD_GUEST
import com.whitbread.premierinn.ciol.viewmodel.state.utils.FieldType.SECOND_GUEST
import com.whitbread.premierinn.ciol.views.footer.PriceBreakdownView
import com.whitbread.premierinn.common.fragment.BaseFragment
import com.whitbread.premierinn.common.utils.dpToPx
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.FragmentPreStayBinding
import com.whitbread.premierinn.domain.common.entity.isPibaCNPBooking
import com.whitbread.premierinn.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.Serializable

const val COMPLETION_KEY = "COMPLETION_KEY"
const val ADDRESS_MAX_LINES = 2
const val BOX_STROKE_WIDTH_ERROR = 2f
const val BOX_STROKE_WIDTH_DEFAULT = 1f

@AndroidEntryPoint
class PreStayFragment : BaseFragment() {
    private lateinit var binding: FragmentPreStayBinding
    private lateinit var preStayModel: PreStayUiModel
    private lateinit var priceBreakdownView: PriceBreakdownView

    private var lastClickTime = 0L
    private var paymentOption = EMPTY_STRING

    private val sharedViewModel: PreStaySharedViewModel by activityViewModels()

    private val preStayViewModel: PreStayViewModel by viewModels()

    companion object {
        fun newInstance(parcelable: Parcelable?): PreStayFragment {
            val args = Bundle()
            args.putParcelable(MODEL_KEY, parcelable)
            val fragment = PreStayFragment()
            fragment.arguments = args
            return fragment
        }

        const val MODEL_KEY = "preStayModel"
        const val PRE_STAY_SCREEN_PROGRESS = 20
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPreStayBinding.inflate(inflater, container, false)
        sharedViewModel.initParams((arguments?.parcelable<PreStayUiModel>(MODEL_KEY) as PreStayUiModel).toPreStayDomainModel())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as CheckInOnlineActivity).setupToolbarTitle(resources.getString(R.string.pre_stay_confirm_details))

        preStayModel = arguments?.parcelable<PreStayUiModel>(MODEL_KEY) as PreStayUiModel
        paymentOption = preStayModel.paymentOption

        with(binding) {
            headerContainer.hotelTitle.text = preStayModel.preStayHeaderInfo.hotelName
            headerContainer.hotelStayDate.text = preStayModel.getDates()
            headerContainer.hotelStayInfo.text = preStayModel.getGeneralGuestsDetails(requireContext())
            headerContainer.hotelImage.load(Urls.CONTENT_BASE_URL.plus(preStayModel.preStayHeaderInfo.hotelImage))

            addressContainer.label.text = resources.getText(R.string.checkin_guest_details_address)
            addressContainer.value.maxLines = ADDRESS_MAX_LINES
        }

        if (!sharedViewModel.isStateInitialized) {
            sharedViewModel.onScreenOpened()
        }
        preStayViewModel.onScreenOpened(preStayModel.toPreStayDomainModel())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    sharedViewModel.shouldNavigate.collect { shouldNavigate ->
                        if (shouldNavigate) {
                            binding.preStayInfoLoading.isVisible = false
                            (requireActivity() as? CheckInOnlineActivity)?.apply {
                                this.preStayModel = sharedViewModel.preStayModel.convertToUiModel()
                            }
                            if (preStayViewModel.state.value.selectedOccasion != null) {
                                preStayViewModel.updateReservationPreferences(
                                    sharedViewModel.preStayModel.convertToUiModel(),
                                    preStayViewModel.state.value.selectedOccasion
                                )
                            }
                            preStayViewModel.onConfirmPreCheckInOrContinueNavigation(sharedViewModel.preStayModel.convertToUiModel())
                            sharedViewModel.shouldNavigate(false)
                        }
                    }
                }

                launch {
                    preStayViewModel.state.collectLatest { onStateChanged(it) }
                }

                launch {
                    sharedViewModel.state.collect { state ->
                        with(binding) {
                            handleError(state.error)
                            leadBookerContainer.value.text = sharedViewModel.getBookerName()

                            emailContainer.label.text = resources.getString(R.string.pre_stay_email)
                            emailContainer.value.text = state.email.values.first()
                            if (preStayModel.isThirdPartyBooking) {
                                emailContainer.arrow.visibility = View.VISIBLE
                                emailContainer.clickableLayout.setOnClickListener {
                                    onItemClicked(PreStayEditItemFragment.ITEM_TYPE_EMAIL_ADDRESS)
                                }
                            }

                            phoneContainer.label.text = resources.getText(R.string.pre_stay_phone)
                            phoneContainer.value.text = state.phone.values.first()
                            if (preStayModel.isThirdPartyBooking) {
                                phoneContainer.arrow.visibility = View.VISIBLE
                                phoneContainer.clickableLayout.setOnClickListener {
                                    onItemClicked(PreStayEditItemFragment.ITEM_TYPE_PHONE_NUMBER)
                                }
                            }

                            addressContainer.value.text = state.address.values.first()
                            if (preStayModel.isThirdPartyBooking) {
                                addressContainer.arrow.visibility = View.VISIBLE
                                addressContainer.clickableLayout.setOnClickListener {
                                    onItemClicked(PreStayEditItemFragment.ITEM_TYPE_ADDRESS)
                                }
                            }

                            if (preStayViewModel.state.value.shouldShowGuestInfo) {
                                roomList.layoutManager = LinearLayoutManager(requireContext())
                                roomList.adapter = GuestRoomListAdapter(
                                    state.rooms.convertToUiModelRoomGuestsList(),
                                    sharedViewModel.getLanguage(),
                                    onRoomSelected = ::onRoomClicked
                                )
                            }
                        }
                    }
                }
            }
        }

        setupPriceBreakdownView()
    }

    private fun displayGuestOccasionView(state: PreStayViewModel.PreStayState) = with(state) {
        binding.specialOccasionContainer.apply {
            specialOccasionContainer.isVisible = !preStayViewModel.state.value.specialOccasions.isNullOrEmpty()
            selectOccasionSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
                preStayViewModel.onOccasionsSwitchStateChanged(isChecked)
            }
            selectOccasionLayout.isVisible = shouldDisplaySelectOccasionsLayout
            if (selectOccasionLayout.isVisible) {
                selectOccasionEditText.setOnClickListener {
                    preStayViewModel.onSelectOccasionClicked()
                }
            }
            if (selectedOccasion == null) {
                selectOccasionEditText.setText(EMPTY_STRING)
            }

            if (openBottomSheet) {
                specialOccasions?.let(::showSpecialOccasionsBottomSheet)
            }
            selectOccasionErrorBox.isVisible = showMissingOccasionError
            selectOccasionLayout.boxStrokeWidth = requireContext()
                .dpToPx(if (showMissingOccasionError) BOX_STROKE_WIDTH_ERROR else BOX_STROKE_WIDTH_DEFAULT)
            ContextCompat.getColorStateList(
                requireContext(),
                if (showMissingOccasionError) R.color.text_input_layout_stroke_colour_error else R.color.text_input_layout_stroke_colour_default
            )?.let {
                selectOccasionLayout.setBoxStrokeColorStateList(it)
            }
        }
    }

    private fun setupPriceBreakdownView() {
        priceBreakdownView = PriceBreakdownView(requireContext()).apply {
            binding.footerContainer.addView(this)
            setupProgressBar(PRE_STAY_SCREEN_PROGRESS)
        }
    }

    private fun displayPriceBreakdown(priceBreakdownModel: PriceBreakdownModel) {
        if (priceBreakdownModel.outstandingBalance.amount > 0F) {
            priceBreakdownView.displayPriceBreakdown(priceBreakdownModel, null, paymentOption, preStayViewModel.state.value.isPibaCpEnabled)
        }
    }

    private fun setOnContinueButtonClickListener() {
        val isPibaCNPFlow = paymentOption.isPibaCNPBooking() && (preStayViewModel.hasNoPackages() || preStayModel.isBusinessBooking)
        priceBreakdownView.setupContinueButtonClickListener {
            preStayViewModel.trackContinueButton(preStayViewModel.state.value.selectedOccasion?.label ?: EMPTY_STRING)
            debounceClickAction {
                var roomsValid = true
                var specialOccasionValid = true
                var leadGuestInfoValid = true

                if (preStayModel.isThirdPartyBooking) {
                    leadGuestInfoValid = checkLeadGuestInformationValidity()
                }

                if (preStayViewModel.state.value.shouldShowGuestInfo) {
                    for (room in sharedViewModel.state.value.rooms) {
                        if (!room.isNationalityValidForLeadGuest() || !room.isNationalityValidForAccompanyingGuest()) {
                            (binding.roomList.adapter as GuestRoomListAdapter).onContinueClicked()
                            roomsValid = false
                            break
                        }

                        if (room.numberOfAdults == 1) {
                            (binding.roomList.adapter as GuestRoomListAdapter).onContinueClicked()
                            continue
                        }

                        if (!room.isSecondGuestPresent()) {
                            (binding.roomList.adapter as GuestRoomListAdapter).onContinueClicked()
                            roomsValid = false
                            break
                        } else {
                            (binding.roomList.adapter as GuestRoomListAdapter).onContinueClicked()
                        }
                    }
                }

                if (binding.specialOccasionContainer.specialOccasionContainer.isVisible
                    && binding.specialOccasionContainer.selectOccasionSwitchCompat.isChecked) {
                    if (preStayViewModel.state.value.selectedOccasion == null) {
                        specialOccasionValid = false
                        preStayViewModel.showMissingOccasionError()
                    }
                }

                if (leadGuestInfoValid && roomsValid && specialOccasionValid) {
                    if (isPibaCNPFlow) {
                        val infoBottomSheetData = InfoBottomSheetData.showPibaMessageData(resources)
                        activity?.showCheckInInformationBottomSheet(infoBottomSheetData) { updateReservation() }
                    } else {
                        updateReservation()
                    }
                }
            }
        }
    }

    private fun updateReservation() {
        binding.preStayInfoLoading.isVisible = true
        sharedViewModel.updateReservationInfo(preStayViewModel.state.value.shouldShowGuestInfo)
    }

    private fun checkLeadGuestInformationValidity(): Boolean {
        val emailValid = binding.emailContainer.value.text.isNotBlank()
        val phoneValid = binding.phoneContainer.value.text.isNotBlank()
        val addressValid = binding.addressContainer.value.text.isNotBlank()

        with(binding) {
            leadGuestEmailWarningBox.visibility = if (emailContainer.value.text.isBlank()) View.VISIBLE else View.GONE
            leadGuestPhoneNumberWarningBox.visibility = if (phoneContainer.value.text.isBlank()) View.VISIBLE else View.GONE
            leadGuestAddressWarningBox.visibility = if (addressContainer.value.text.isBlank()) View.VISIBLE else View.GONE
        }
        return emailValid && phoneValid && addressValid
    }

    private fun navigateTo(destination: NavigationDestination) {
        when(destination) {
            is NavigationDestination.CompletionScreen -> {
                (activity as CheckInOnlineActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.container, CheckInCompletionFragment.newInstance(
                        destination.completionModel
                    ))
                    .addToBackStack(null)
                    .commit()
            }
            is NavigationDestination.UpsellsFragment -> {
                (activity as CheckInOnlineActivity).supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container, UpsellsFragment.newInstance(
                            preStayViewModel.state.value.selectedOccasion?.label ?: EMPTY_STRING
                        )
                    )
                    .addToBackStack(null)
                    .commit()
            }

            is NavigationDestination.PayAndCheckInFragment -> {
                (activity as CheckInOnlineActivity).supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container, PayAndCheckInFragment.newInstance(
                            preStayModel,
                            CiolCompletionAnalyticsModel(revenue = preStayModel.outstandingBalance?.amount.toString()),
                            preStayViewModel.state.value.selectedOccasion?.label ?: EMPTY_STRING
                        )
                    )
                    .addToBackStack(null)
                    .commit()
            }
            is NavigationDestination.RegCardGuestDetailsFragment -> {
                (activity as CheckInOnlineActivity).supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container, RegCardGuestDetailsFragment.newInstance(
                            preStayModel.basketReference,
                            preStayModel,
                            destination.areUpsellItemsAvailable,
                            preStayViewModel.state.value.selectedOccasion?.label ?: EMPTY_STRING
                        )
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }
        preStayViewModel.onUserNavigated()
    }

    private fun handleError(error: PreStaySharedViewModel.Error?) {
        if (error is PreStaySharedViewModel.Error.GenericError) {
            if (error.isPibaCnp) {
                showErrorAlertDialog(
                    getString(R.string.piba_error_dialog_title),
                    getString(R.string.piba_error_dialog_message)
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.ciol_details_not_saved_error,
                    Toast.LENGTH_LONG
                ).show()
            }
            sharedViewModel.onErrorHandled()
            preStayViewModel.onErrorHandled()
        }
    }

    private fun onRoomClicked(room: GuestRoomClicked) = debounceClickAction {
        val extraMap = hashMapOf<Serializable, String>()
        val type = if (room.guestType == LEAD_GUEST) LEAD_GUEST else SECOND_GUEST

        extraMap[type] = this.getString(
            R.string.guest_details_booker_detail_format,
            room.guestTitle,
            room.guestFirstName,
            room.guestLastName
        )

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                PreStayEditFragment.newInstance(
                    map = extraMap,
                    roomId = room.roomId,
                    nationality = room.nationality,
                    passportNumber = room.passportNumber,
                    preStayModel
                )
            ).addToBackStack(null)
            .commit()
    }

    private fun onItemClicked(itemType: String) = debounceClickAction {

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                PreStayEditItemFragment.newInstance(itemType, sharedViewModel.preStayModel.convertToUiModel().preStayDetails.bookerDetails)
            ).addToBackStack(null)
            .commit()
    }

    private fun onStateChanged(state: PreStayViewModel.PreStayState) = with(state) {
        binding.preStayInfoLoading.isVisible = isLoading
        if (!specialOccasions.isNullOrEmpty()) {
            displayGuestOccasionView(state)
        }

        navigation?.let(::navigateTo)

        if (state.upsellAvailabilityChanged) {
            setOnContinueButtonClickListener()
        }

        priceBreakdownModel?.let(::displayPriceBreakdown)
        error?.let(::handleError)
    }

    private fun showSpecialOccasionsBottomSheet(specialOccasions: List<HotelPreferenceUiModel>) {
        SelectGuestsOccasionBottomSheet().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(OCCASIONS_KEY, ArrayList(specialOccasions))
            }
            show(this@PreStayFragment.requireActivity().supportFragmentManager) { selectedOccasion ->
                binding.specialOccasionContainer.selectOccasionEditText.setText(selectedOccasion.label)
                preStayViewModel.onSpecialOccasionSelected(selectedOccasion)
            }
            preStayViewModel.onOccasionsBottomSheetDisplayed()
        }
    }

    private fun debounceClickAction(delay: Long = 100, action: () -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= delay) {
            lastClickTime = currentTime
            action()
        }
    }
}
