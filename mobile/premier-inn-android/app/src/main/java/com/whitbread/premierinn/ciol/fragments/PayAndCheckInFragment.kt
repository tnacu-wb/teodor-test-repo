package com.whitbread.premierinn.ciol.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.ciol.CheckInOnlineActivity
import com.whitbread.premierinn.ciol.analytics.CiolCompletionAnalyticsModel
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.entity.RegCardPdfModel
import com.whitbread.premierinn.ciol.entity.upsells.UpdateReservationPackagesUiModel
import com.whitbread.premierinn.ciol.mapper.convertToUiModel
import com.whitbread.premierinn.ciol.mapper.toPreStayDomainModel
import com.whitbread.premierinn.ciol.paypal.PayPalClientProvider
import com.whitbread.premierinn.ciol.utils.getDates
import com.whitbread.premierinn.ciol.utils.getGeneralGuestsDetails
import com.whitbread.premierinn.ciol.viewmodel.PayAndCheckInViewModel
import com.whitbread.premierinn.ciol.viewmodel.PayAndCheckInViewModel.BillingAddressError
import com.whitbread.premierinn.ciol.viewmodel.PayAndCheckInViewModel.BillingAddressError.FirstLineInvalidError
import com.whitbread.premierinn.ciol.viewmodel.PayAndCheckInViewModel.BillingAddressError.PostcodeInvalidError
import com.whitbread.premierinn.ciol.viewmodel.PayAndCheckInViewModel.Error
import com.whitbread.premierinn.ciol.viewmodel.PayAndCheckInViewModel.Error.GenericError
import com.whitbread.premierinn.ciol.viewmodel.PayAndCheckInViewModel.Error.InitiatePaymentError
import com.whitbread.premierinn.ciol.viewmodel.PayAndCheckInViewModel.Error.InitiatePaymentGenericError
import com.whitbread.premierinn.ciol.viewmodel.PayAndCheckInViewModel.Error.PaymentMethodsError
import com.whitbread.premierinn.ciol.viewmodel.PayAndCheckInViewModel.NavigationDestination
import com.whitbread.premierinn.ciol.views.footer.PriceBreakdownView
import com.whitbread.premierinn.ciol.views.payandcheckin.GenericPaymentMethodView
import com.whitbread.premierinn.ciol.views.payandcheckin.SavedCardPaymentMethodView
import com.whitbread.premierinn.common.AddressField
import com.whitbread.premierinn.common.AddressFormDataOutput
import com.whitbread.premierinn.common.EXTRA_TRANSACTION_ID
import com.whitbread.premierinn.common.PaymentMethodType
import com.whitbread.premierinn.common.fragment.BaseFragment
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.FragmentPayAndCheckInBinding
import com.whitbread.premierinn.domain.booking.entity.PreStayModel
import com.whitbread.premierinn.domain.ciol.usecase.GERMANY_ISO_CODE
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.utils.positionOfSelectedCountry
import com.whitbread.premierinn.landing.LandingActivityIntent
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethod
import com.whitbread.premierinn.threeCp.ThreeCpActivity
import com.whitbread.premierinn.threeCp.ThreeCpCustomTabActivity
import com.whitbread.premierinn.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val PAY_AND_CHECK_IN_TOOLBAR_PROGRESS = 75
private const val PAY_AND_CHECK_IN_REQUEST_CODE = 801
private const val PAY_AND_CHECK_IN_GPAY_REQUEST_CODE = 802

const val PAY_AND_CHECK_IN_FEATURE_TAG = "PayAndCheckIn"
const val UPSELLS_ADDED_REMOTELY_KEY = "UPSELLS_ADDED_REMOTELY"

@AndroidEntryPoint
class PayAndCheckInFragment : BaseFragment() {

    private lateinit var binding: FragmentPayAndCheckInBinding
    private lateinit var priceBreakdownView: PriceBreakdownView
    private val payPalClientProvider = PayPalClientProvider()

    private val payAndCheckInViewModel: PayAndCheckInViewModel by viewModels()

    private var paymentMethodsViews = mutableListOf<View>()
    private val updateReservationPackagesUiModel: UpdateReservationPackagesUiModel? by lazy {
        arguments?.parcelable(UPDATE_RESERVATION_PACKAGES_MODEL_KEY)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPayAndCheckInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        onBillingAddressSwitchCompat()
        setupPriceBreakdownView()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                payAndCheckInViewModel.state.collectLatest { onStateChanged(it) }
            }
        }

        val preStayUiModel = arguments?.parcelable<PreStayUiModel>(PreStayFragment.MODEL_KEY)
        val analyticsModel = arguments?.parcelable<CiolCompletionAnalyticsModel>(ANALYTICS_MODEL)
        val regCardPdfModel = arguments?.parcelable<RegCardPdfModel>(REG_CARD_PDF_MODEL)
        val specialOccasion = arguments?.getString(SPECIAL_OCCASION, EMPTY_STRING) ?: EMPTY_STRING
        payAndCheckInViewModel.onScreenOpened(
            preStayUiModel?.toPreStayDomainModel(),
            analyticsModel,
            specialOccasion,
            regCardPdfModel
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PAY_AND_CHECK_IN_REQUEST_CODE,
            PAY_AND_CHECK_IN_GPAY_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    payAndCheckInViewModel.onPaymentComponentClosed(
                        data?.extras?.getString(EXTRA_TRANSACTION_ID, EMPTY_STRING) ?: EMPTY_STRING
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        payPalClientProvider.parseBrowserSwitchResult(requireActivity(), requireActivity().intent)
    }

    fun onBackPressed() {
        parentFragmentManager.apply {
            setFragmentResult(PAY_AND_CHECK_IN_FRAGMENT_REQUEST_KEY, bundleOf(
                UPSELLS_ADDED_REMOTELY_KEY to payAndCheckInViewModel.state.value.wereUpsellsUpdatedRemotely
            ))
            popBackStack()
        }
    }

    fun handleOnNewIntent(intent: Intent?) {
        payPalClientProvider.parseBrowserSwitchResult(requireActivity(), intent)
    }

    fun onBillingAddressGenerated(billingAddress: Address) {
        payAndCheckInViewModel.onBillingAddressGenerated(billingAddress)
    }

    private fun setupToolbar() {
        (requireActivity() as CheckInOnlineActivity).setupToolbarTitle(resources.getString(R.string.pay_and_check_in_toolbar_title))
    }

    private fun onBillingAddressSwitchCompat() {
        binding.billingAddressContainer.billingAddressSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            payAndCheckInViewModel.onBillingAddressSwitchStateChanged(isChecked)
        }
    }

    private fun setupPriceBreakdownView() {
        priceBreakdownView = PriceBreakdownView(requireContext()).apply {
            binding.footerContainer.addView(this)

            setupContinueButton(getString(R.string.pay_and_check_in_continue_button_text), false)
            setupProgressBar(PAY_AND_CHECK_IN_TOOLBAR_PROGRESS)
        }
    }

    @SuppressLint("CheckResult")
    private fun setContinueButtonClickListener() {
        priceBreakdownView.setupContinueButtonClickListener {
            val address: Address? = if (!binding.billingAddressContainer.billingAddressSwitchCompat.isChecked) {
                binding.billingAddressFormView.binding.run {
                    Address(
                        line1 = etAddressFormAddressLine1.text.toString(),
                        line2 = etAddressFormAddressLine2.text.toString(),
                        line3 = etAddressFormAddressLine3.text.toString(),
                        postCode = etAddressFormPostcode.text.toString(),
                        countryCode = (sAddressFormCountries.selectedItem as CountryDomain).countryIsoCode
                    )
                }
            } else {
                null
            }

            payAndCheckInViewModel.onContinueButtonPressed(
                billingAddress = address,
                shouldDisplayDetailedAddress = payAndCheckInViewModel.state.value.shouldDisplayDetailedAddress,
                updateReservationPackagesUiModel = updateReservationPackagesUiModel
            )
        }
    }

    private fun onStateChanged(state: PayAndCheckInViewModel.PayAndCheckInState) = with(state) {
        preStayModel?.let {
            setupBookingDetailsCard(it)
            priceBreakdownModel?.let {
                priceBreakdownView.displayPriceBreakdown(
                    it,
                    ::trackExpandButton,
                    preStayModel.paymentOption,
                    payAndCheckInViewModel.state.value.isPibaCpEnabled
                )
            }
        }
        paymentMethods.let(::displayPaymentMethods)

        isLoading.let { isLoading ->
            binding.paymentMethodsLoading.isVisible = isLoading
            binding.paymentTypeHeaderTextView.isVisible = !isLoading
        }

        handleInitiatePaymentLoading(isInitiatePaymentLoading)
        handlePaymentLoading(paymentLoading)

        val leadGuest = preStayModel?.preStayDetails?.reservationGuests
            ?.find { !it.isAccompanyingGuest }
        setupRegCardInfoBannerVisibility(regCardPdfModel != null && leadGuest?.nationality != GERMANY_ISO_CODE)

        binding.billingAddressContainer.billingAddressCode.text = billingAddressCode

        setupBillingAddressView(this)

        payPalData?.run {
            loadPayPalData(this.clientToken, this.billingAddress)
        }

        navigation?.let(::navigateTo)

        error?.let(::handleError)
        handleBillingAddressErrors(billingAddressErrors)
    }

    private fun trackExpandButton(price: Double) {
        payAndCheckInViewModel.trackExpandButton(price)
    }

    private fun setupRegCardInfoBannerVisibility(visible: Boolean) {
        binding.regCardInfoBanner.visibility = if (visible) VISIBLE else GONE
    }

    private fun handleInitiatePaymentLoading(isInitiatePaymentLoading: Boolean) {
        priceBreakdownView.getContinueButton().setLoadingState(isInitiatePaymentLoading)
        paymentMethodsViews.forEach { paymentView ->
            when(paymentView) {
                is SavedCardPaymentMethodView -> paymentView.changeClickStatus(isCardInteractionEnabled = !isInitiatePaymentLoading)
                is GenericPaymentMethodView -> paymentView.changeClickStatus(isCardInteractionEnabled = !isInitiatePaymentLoading)
            }
        }
    }

    private fun handlePaymentLoading(paymentLoading: PayAndCheckInViewModel.PaymentLoading?) {
        binding.paymentTranslucentLoading.apply {
            setupPaymentSpinner()
            val isPaymentLoading = paymentLoading?.run {
                loadingSpinnerInfoText.text = this.loadingMessage
                true
            } ?: false
            translucentConstraintLayout.visibility = if (isPaymentLoading) VISIBLE else GONE
            loadingSpinnerInfoText.visibility = if (isPaymentLoading) VISIBLE else GONE
        }
    }

    private fun setupPaymentSpinner() {
        binding.paymentTranslucentLoading.apply {
            translucentConstraintLayout.isClickable = true
            translucentConstraintLayout.setBackgroundColor(requireContext().getColor(R.color.base_black_70))
            spinnerBackground.background = null
            loadingSpinner.setPadding(0, 0, 0, 0)
            loadingSpinner.indeterminateDrawable.mutate().setColorFilter(
                requireContext().getColor(R.color.white), PorterDuff.Mode.MULTIPLY
            )
        }
    }

    @SuppressLint("CheckResult")
    private fun setupBillingAddressView(state: PayAndCheckInViewModel.PayAndCheckInState) {
        binding.billingAddressFormView.isVisible = state.shouldDisplayDetailedAddress
        if (state.shouldDisplayDetailedAddress) {
            binding.billingAddressFormView.binding.tvAddressFormManualAddress.visibility = GONE
            val ukCountyPosition = positionOfSelectedCountry(state.countries, CountryDomain.UK_CODE)
            binding.billingAddressFormView.apply {
                setCompanyVisibility(false)
                setCountries(state.countries)
                setCountrySelection(state.selectedCountryIndex ?: ukCountyPosition)
                showFindAddressButton(state.showFindAddress)

                formWithTextAndFocus.subscribe { address ->
                    when (address.form()) {
                        AddressFormDataOutput.Form.POSTCODE ->
                            showValidationError(false, AddressFormDataOutput.Form.POSTCODE)
                        AddressFormDataOutput.Form.ADDRESS_LINE_1 ->
                            showValidationError(false, AddressFormDataOutput.Form.ADDRESS_LINE_1)
                        AddressFormDataOutput.Form.COUNTRY -> {
                            payAndCheckInViewModel.onCountrySelected(ukCountyPosition, address.countryIndex())
                        }
                        else -> { /* no-op */ }
                    }
                }
                state.billingAddress?.let(::onBillingAddressChanged)
                showValidationError(false, AddressFormDataOutput.Form.POSTCODE)
                showValidationError(false, AddressFormDataOutput.Form.ADDRESS_LINE_1)
            }
            binding.payAndCheckInScrollableContainer.apply {
                post { smoothScrollTo(0, bottom) }
            }
        } else {
            // In case of error when retrieving countries, set the switch compat state back to checked
            binding.billingAddressContainer.billingAddressSwitchCompat.apply {
                if (!isChecked && state.error != null) {
                    setOnCheckedChangeListener(null)
                    isChecked = true
                    onBillingAddressSwitchCompat()
                }
            }

            // Reset all form fields when billing address switch is rechecked
            if (binding.billingAddressContainer.billingAddressSwitchCompat.isChecked) {
                binding.billingAddressFormView.clearFormFields()
                payAndCheckInViewModel.resetBillingAddressFields()
            }
        }
    }

    private fun navigateTo(destination: NavigationDestination) {
        when (destination) {
            is NavigationDestination.PayScreen -> {
                startActivityForResult(
                    ThreeCpActivity.createThreeCpIntentPayAndCheckIn(
                        requireContext(),
                        destination.data,
                        destination.isForRegCard,
                        destination.shouldShowInfoBanner
                    ),
                    PAY_AND_CHECK_IN_REQUEST_CODE
                )
            }
            is NavigationDestination.GPayScreen -> {
                startActivityForResult(
                    ThreeCpCustomTabActivity.createThreeCpPayAndCheckInIntentForGPay(
                        requireContext(), destination.data
                    ),
                    PAY_AND_CHECK_IN_GPAY_REQUEST_CODE
                )
            }
            is NavigationDestination.CompletionScreen -> {
                (requireActivity() as CheckInOnlineActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.container, CheckInCompletionFragment.newInstance(destination.completionModel))
                    .addToBackStack(null)
                    .commit()
            }
        }
        payAndCheckInViewModel.onUserNavigated()
    }

    private fun setupBookingDetailsCard(preStayModel: PreStayModel) {
        with(binding.headerContainer) {
            hotelTitle.text = preStayModel.preStayHeaderInfo.hotelName
            hotelStayDate.text = preStayModel.convertToUiModel().getDates()
            hotelStayInfo.text = preStayModel.convertToUiModel().getGeneralGuestsDetails(requireContext())
            hotelImage.load(Urls.CONTENT_BASE_URL.plus(preStayModel.preStayHeaderInfo.hotelImage))
        }
    }

    private fun displayPaymentMethods(paymentMethods: List<ParcelablePaymentMethod>) {
        binding.paymentMethodsContainer.removeAllViews()
        paymentMethodsViews.clear()
        paymentMethods.forEachIndexed { index, paymentMethod ->
            val paymentView = when(paymentMethod.type) {
                PaymentMethodType.SAVED_CARD.name -> {
                    inflateSavedCardPaymentMethodView(index, paymentMethods.size, paymentMethod)
                }
                else -> inflateGenericPaymentMethodView(index, paymentMethods.size, paymentMethod)
            }

            binding.paymentMethodsContainer.addView(paymentView)
            paymentMethodsViews.add(paymentView)
        }
        // Re-enable Continue button after payment methods are displayed
        if (paymentMethods.isNotEmpty()) {
            priceBreakdownView.apply {
                getContinueButton().isEnabled = true
                getContinueButton().isClickable = false
                setContinueButtonClickListener()
            }
        }
    }

    private fun inflateGenericPaymentMethodView(
        itemIndex: Int, totalPaymentMethods: Int, paymentMethod: ParcelablePaymentMethod
    ) = GenericPaymentMethodView(
        context = requireContext(),
        itemIndex = itemIndex,
        totalPaymentMethods = totalPaymentMethods
    ).setupView(
        paymentMethod
    ).onPaymentMethodSelected { selectedPaymentMethod ->
        payAndCheckInViewModel.onPaymentMethodSelected(selectedPaymentMethod)
    }

    private fun inflateSavedCardPaymentMethodView(
        itemIndex: Int, totalPaymentMethods: Int, paymentMethod: ParcelablePaymentMethod
    ) = SavedCardPaymentMethodView(
        context = requireContext(),
        itemIndex = itemIndex,
        totalPaymentMethods = totalPaymentMethods
    ).setupView(
        paymentMethod
    ).onPaymentMethodSelected { selectedPaymentMethod ->
        payAndCheckInViewModel.onPaymentMethodSelected(selectedPaymentMethod)
    }

    private fun loadPayPalData(clientToken: String, billingAddress: Address?) {
        payPalClientProvider
            .initialize(requireActivity(), clientToken)
            .tokenizePayPalAccount(
                requireActivity(),
                "Your agreement description"
            ) { payPalNonce, deviceData, error ->
                payAndCheckInViewModel.onPayPalDataReady(payPalNonce, deviceData, billingAddress, error)
            }.collectDeviceData(requireActivity())

        payAndCheckInViewModel.onPayPalDataCollectionInitialised()
    }

    private fun handleError(error: Error) {
        var positiveButtonAction: (() -> Unit)? = null
        var dialogCancelable = true
        val (title, message) = when(error) {
            GenericError,
            Error.PaymentGenericError,
            Error.UpdatePackagesError -> getString(R.string.review_booking_booking_failed_title) to getString(R.string.search_results_error)

            InitiatePaymentError -> getString(R.string.review_booking_error_title) to getString(R.string.review_booking_storage_failed_description)
            InitiatePaymentGenericError -> getString(R.string.review_booking_payment_failed_title) to getString(R.string.review_booking_payment_failed_description)
            PaymentMethodsError -> getString(R.string.review_booking_payment_failed_title) to getString(R.string.review_booking_payment_failed_description)
            is Error.PaymentPendingError -> {
                positiveButtonAction = {
                    requireActivity().startActivity(LandingActivityIntent.create(requireContext()))
                    requireActivity().finish()
                }
                getString(R.string.review_booking_error_title) to getString(R.string.pay_and_check_in_polling_pending_message, error.email)
            }
            Error.PaymentFailedError -> {
                positiveButtonAction = {
                    requireActivity().startActivity(LandingActivityIntent.create(requireContext()))
                    requireActivity().finish()
                }
                getString(R.string.review_booking_error_title) to getString(R.string.pay_and_check_in_refund_error)
            }
            Error.RegCardError -> {
                dialogCancelable = false
                positiveButtonAction = {
                    requireActivity().finish()
                }
                getString(R.string.review_booking_error_title) to getString(R.string.reg_card_api_general_error)
            }
        }

        displayErrorAlertDialog(title, message, positiveButtonAction, dialogCancelable)
    }

    private fun displayErrorAlertDialog(title: String, message: String, positiveButtonAction: (() -> Unit)?, cancelable: Boolean = true) {
        AlertDialog.Builder(requireContext(), R.style.PurpleDialog)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog: DialogInterface, _ ->
                payAndCheckInViewModel.onErrorHandled()
                positiveButtonAction?.invoke()
                dialog.dismiss()
            }
            .setCancelable(cancelable)
            .create()
            .show()
    }

    private fun onBillingAddressChanged(billingAddress: Address) {
        binding.billingAddressFormView.apply {
            setAddressFields(
                AddressField.builder()
                    .postcode(billingAddress.postCode)
                    .addressLine1(billingAddress.line1)
                    .addressLine2(billingAddress.line2)
                    .city(EMPTY_STRING)
                    .companyName(billingAddress.companyName).build()
            )
            showValidationError(false, AddressFormDataOutput.Form.POSTCODE)
            showValidationError(false, AddressFormDataOutput.Form.ADDRESS_LINE_1)
        }
    }

    private fun handleBillingAddressErrors(validationErrors: List<BillingAddressError>) {
        validationErrors.forEach { error ->
            val invalidForm = when (error) {
                FirstLineInvalidError -> AddressFormDataOutput.Form.ADDRESS_LINE_1
                PostcodeInvalidError -> AddressFormDataOutput.Form.POSTCODE
            }

            binding.billingAddressFormView.showValidationError(true, invalidForm)
        }
    }

    companion object {
        fun newInstance(
            preStayUiModel: Parcelable?,
            completionAnalyticsModel: Parcelable,
            specialOccasion: String,
            regCardPdfModel: RegCardPdfModel? = null,
            updateReservationPackagesUiModel: UpdateReservationPackagesUiModel? = null,
        ) =
            Bundle().apply {
                putParcelable(PreStayFragment.MODEL_KEY, preStayUiModel)
                putParcelable(ANALYTICS_MODEL, completionAnalyticsModel)
                putString(SPECIAL_OCCASION, specialOccasion)
                updateReservationPackagesUiModel?.let {
                    putParcelable(
                        UPDATE_RESERVATION_PACKAGES_MODEL_KEY,
                        it
                    )
                }
                regCardPdfModel?.let {
                    putParcelable(REG_CARD_PDF_MODEL, it)
                }
            }.let { bundle ->
                PayAndCheckInFragment().apply {
                    arguments = bundle
                }
            }
    }
}
