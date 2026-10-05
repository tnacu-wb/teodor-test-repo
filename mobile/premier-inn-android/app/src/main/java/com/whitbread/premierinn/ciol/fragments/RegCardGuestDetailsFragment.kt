package com.whitbread.premierinn.ciol.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.whitbread.premierinn.R
import com.whitbread.premierinn.calendar.convertDateFormat
import com.whitbread.premierinn.ciol.CheckInOnlineActivity
import com.whitbread.premierinn.ciol.analytics.CiolCompletionAnalyticsModel
import com.whitbread.premierinn.ciol.entity.AdditionalGuestUiModel
import com.whitbread.premierinn.ciol.entity.AuthorizeCardWebViewResponse
import com.whitbread.premierinn.ciol.entity.LeadGuestUiModel
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.entity.RegCardGuest
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.viewmodel.RegCardGuestDetailsViewModel
import com.whitbread.premierinn.ciol.viewmodel.RegCardGuestDetailsViewModel.Error
import com.whitbread.premierinn.ciol.viewmodel.RegCardGuestDetailsViewModel.Error.GenericError
import com.whitbread.premierinn.ciol.viewmodel.RegCardGuestDetailsViewModel.NavigationDestination
import com.whitbread.premierinn.ciol.views.regcard.AdditionalGuestEntryView
import com.whitbread.premierinn.common.format.DateFormat.DASHED_YEAR_MONTH_DAY
import com.whitbread.premierinn.common.format.DateFormat.SLASHED_DAY_MONTH_YEAR
import com.whitbread.premierinn.common.fragment.BaseFragment
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.FragmentRegCardGuestDetailsBinding
import com.whitbread.premierinn.threeCp.ThreeCpActivity
import com.whitbread.premierinn.threeCp.ThreeCpActivity.Companion.AUTHORIZE_CARD_RESPONSE_EXTRA
import com.whitbread.premierinn.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val REG_CARD_SCREEN_PROGRESS = 35
const val BASKET_REFERENCE_KEY = "BASKET_REFERENCE_KEY"
const val PRE_STAY_MODEL_KEY = "PRE_STAY_MODEL_KEY"
const val UPSELLS_AVAILABLE_KEY = "UPSELLS_AVAILABLE_KEY"
const val REG_CARD_FEATURE_TAG = "RegCard"
const val SELECTED_OCCASION_LABEL_KEY = "RegCard"
private const val AUTHORIZE_CARD_REQUEST_CODE = 1
private const val AUTHORIZE_CARD_SUCCESS = "SUCCESS"

@AndroidEntryPoint
class RegCardGuestDetailsFragment : BaseFragment() {

    private lateinit var binding: FragmentRegCardGuestDetailsBinding
    private val regCardGuestDetailsViewModel: RegCardGuestDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegCardGuestDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as CheckInOnlineActivity).setupToolbarTitle(resources.getString(R.string.reg_card_guest_details_toolbar_title))

        binding.footer.progressBar.progress = REG_CARD_SCREEN_PROGRESS

        regCardGuestDetailsViewModel.onScreenOpened(
            arguments?.getString(BASKET_REFERENCE_KEY),
            arguments?.parcelable<PreStayUiModel>(PRE_STAY_MODEL_KEY) as PreStayUiModel,
            arguments?.getBoolean(UPSELLS_AVAILABLE_KEY) ?: false,
            arguments?.getString(SELECTED_OCCASION_LABEL_KEY) ?: EMPTY_STRING
        )

        parentFragmentManager.setFragmentResultListener(EDIT_GUEST_DETAILS, this) { _, bundle ->
            bundle.parcelable<RegCardGuest>(GUEST_MODEL)?.let {
                regCardGuestDetailsViewModel.onFinishGuestEditing(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                regCardGuestDetailsViewModel.state.collectLatest { onStateChanged(it) }
            }
        }
    }

    private fun onStateChanged(state: RegCardGuestDetailsViewModel.GuestDetailsState) = with(state) {
        error?.let(::handleError)

        binding.guestDetailsProgress.isVisible = isLoading

        if (!stayingGuests.isNullOrEmpty()) {
            binding.additionalGuestsLayout.removeAllViews()

            stayingGuests.forEach { regCardGuest ->
                when (regCardGuest) {
                    is LeadGuestUiModel -> setupLeadGuestDetails(
                        regCardGuest, invalidIds.contains(regCardGuest.getId()), guestIdsWithEmptyFields.contains(regCardGuest.getId()))
                    is AdditionalGuestUiModel -> setupAdditionalGuestDetails(
                        regCardGuest, invalidIds.contains(regCardGuest.getId()), guestIdsWithEmptyFields.contains(regCardGuest.getId()))
                }
            }
        }

        setupContinueButton()
        if (shouldNavigate == true) {
            regCardGuestDetailsViewModel.updateNavigation()
        }
        navigation?.let(::navigateTo)
    }

    private fun setupLeadGuestDetails(
        leadGuest: LeadGuestUiModel,
        showGuestError: Boolean,
        hasEmptyFields: Boolean) {
        val userSelectedNationality = regCardGuestDetailsViewModel.getNationalityFromIsoCode(leadGuest.additionalGuestUiModel.nationality)
        val countryFromNationality = regCardGuestDetailsViewModel.getUserSelectedCountry(userSelectedNationality)
        with(binding.regCardLeadGuestContainer) {
            guestError.isVisible = showGuestError
            if (showGuestError) {
                guestError.setText(resources.getString(R.string.reg_card_missing_guest_info_warning_message))
            }

            regCardGuestLabel.text = resources.getString(R.string.reg_card_lead_guest_label)

            regCardGuestNameLayout.apply {
                regCardElementLabel.text = resources.getString(R.string.reg_card_guest_name_label)
                regCardElementValue.maxLines = Integer.MAX_VALUE
                regCardElementValue.text = listOf(leadGuest.additionalGuestUiModel.firstName, leadGuest.additionalGuestUiModel.lastName)
                    .joinToString(" ")
            }
            regCardGuestHomeAddressLayout.apply {
                regCardElementContainer.isVisible = true
                regCardElementLabel.text = resources.getString(R.string.reg_card_home_address_label)
                regCardElementValue.text = regCardGuestDetailsViewModel.getLeadGuestFullAddress(leadGuest)
                regCardElementValue.maxLines = 2
            }
            regCardGuestDateOfBirthLayout.apply {
                regCardElementLabel.text = resources.getString(R.string.reg_card_date_of_birth_label)
                regCardElementValue.text = convertDateFormat(
                    dateString = leadGuest.additionalGuestUiModel.dateOfBirth,
                    currentFormat = DASHED_YEAR_MONTH_DAY,
                    newFormat = SLASHED_DAY_MONTH_YEAR
                )
            }
            regCardGuestNationalityLayout.apply {
                regCardElementLabel.text = resources.getString(R.string.reg_card_nationality_hint)
                regCardElementValue.text = userSelectedNationality
            }
            regCardGuestPassportNumberLayout.apply {
                regCardElementContainer.isVisible = regCardGuestDetailsViewModel
                    .isPassportRequiredForCountry(countryFromNationality?.countryName.orEmpty()) == true
                if (regCardElementContainer.isVisible) {
                    regCardElementLabel.text = resources.getString(R.string.guest_details_form_passport_number)
                    regCardElementValue.maxLines = Integer.MAX_VALUE
                    regCardElementValue.text = leadGuest.additionalGuestUiModel.passportNumber
                }
            }

            addOrEditDetailsButton.setText(if (hasEmptyFields) R.string.pre_stay_add else R.string.reg_card_edit_button_label)
            addOrEditDetailsButton.setTextColor(
                ContextCompat.getColor(requireContext(), if (showGuestError) R.color.new_error_red else R.color.teal_dark)
            )
            addOrEditDetailsButton.setOnClickListener {
                regCardGuestDetailsViewModel.trackAddLeadGuest()
                (activity as CheckInOnlineActivity).supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container,
                        EditGuestDetailsFragment.newInstance(true, leadGuest)
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun setupAdditionalGuestDetails(additionalGuest: AdditionalGuestUiModel, showGuestError: Boolean, hasEmptyFields: Boolean) {
        val userSelectedNationality = regCardGuestDetailsViewModel.getNationalityFromIsoCode(additionalGuest.nationality)
        binding.additionalGuestsLayout.addView(
            AdditionalGuestEntryView(requireContext()).apply {
                bind(
                    activity = requireActivity(),
                    additionalGuest = additionalGuest,
                    showGuestError = showGuestError,
                    hasEmptyFields = hasEmptyFields,
                    nationality = userSelectedNationality,
                    isPassportRequiredForCountry =
                    regCardGuestDetailsViewModel.isPassportRequiredForCountry(
                        regCardGuestDetailsViewModel.getUserSelectedCountry(userSelectedNationality)?.countryName.orEmpty())
                ) { regCardGuestDetailsViewModel.trackAddAdditionalGuest() }
            }
        )
    }

    private fun setupContinueButton() {
        binding.footer.continueButton.apply {
            setText(resources.getString(R.string.button_text_continue))
            setupContinueButtonClickListener()
        }
    }

    @SuppressLint("CheckResult")
    fun setupContinueButtonClickListener() = this.apply {
        binding.footer.continueButton.onClickOnInternetAvailable().subscribe { _ ->
            regCardGuestDetailsViewModel.onContinueButtonClicked()
        }
    }

    private fun handleError(error: Error) {
        val (title, message) = when (error) {
            GenericError -> getString(R.string.reg_card_save_guest_details_error_title) to getString(R.string.ciol_details_not_saved_error)
            Error.RegCardGuestSaveDetailsError -> getString(R.string.reg_card_save_guest_details_error_title) to getString(R.string.reg_card_save_guest_details_error_message)
        }
        showErrorAlertDialog(title, message)
    }

    private fun showErrorAlertDialog(dialogTitle: String, dialogMessage: String) {
        AlertDialog.Builder(requireContext(), R.style.PurpleDialog)
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                regCardGuestDetailsViewModel.onErrorHandled()
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun navigateTo(destination: NavigationDestination) {
        when(destination) {
            is NavigationDestination.UpsellsScreen -> {
                (activity as CheckInOnlineActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.container, UpsellsFragment.newInstance(regCardPdfModel = destination.regCardPdfModel))
                    .addToBackStack(null)
                    .commit()
            }

            is NavigationDestination.PayScreen -> {
                val selectedOccasionLabel = arguments?.getString(SELECTED_OCCASION_LABEL_KEY) ?: EMPTY_STRING
                (activity as CheckInOnlineActivity).supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container, PayAndCheckInFragment.newInstance(
                            destination.preStayModel,
                            CiolCompletionAnalyticsModel(revenue = destination.outstandingBalanceAmount),
                            selectedOccasionLabel,
                            destination.regCardPdfModel
                        )
                    )
                    .addToBackStack(null)
                    .commit()

            }
            is NavigationDestination.CompletionScreen -> {
                (activity as CheckInOnlineActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.container, CheckInCompletionFragment.newInstance(
                        destination.completionModel
                    ))
                    .addToBackStack(null)
                    .commit()
            }

            is NavigationDestination.AuthorizeCardScreen -> {
                startActivityForResult(
                    ThreeCpActivity.createThreeCpIntentAuthorizeCard(requireContext(), destination.data),
                    AUTHORIZE_CARD_REQUEST_CODE
                )
            }
        }

        regCardGuestDetailsViewModel.onUserNavigated()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            AUTHORIZE_CARD_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    val response = data?.extras?.parcelable<AuthorizeCardWebViewResponse>(AUTHORIZE_CARD_RESPONSE_EXTRA)
                    if (AUTHORIZE_CARD_SUCCESS.equals(response?.authorizationStatus, true)) {
                        regCardGuestDetailsViewModel.authorizeCardCompletedSuccessfully(response?.transactionId ?: EMPTY_STRING)
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.start_ciol_generic_error), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.start_ciol_generic_error), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        fun newInstance(
            basketReference: String,
            preStayModel: Parcelable,
            upsellItemsAreAvailable: Boolean,
            selectedOccasionLabel: String
        ) =
            Bundle().apply {
                putString(BASKET_REFERENCE_KEY, basketReference)
                putParcelable(PRE_STAY_MODEL_KEY, preStayModel)
                putBoolean(UPSELLS_AVAILABLE_KEY, upsellItemsAreAvailable)
                putString(SELECTED_OCCASION_LABEL_KEY, selectedOccasionLabel)
            }.let { bundle ->
                RegCardGuestDetailsFragment().apply {
                    arguments = bundle
                }
            }
    }
}
