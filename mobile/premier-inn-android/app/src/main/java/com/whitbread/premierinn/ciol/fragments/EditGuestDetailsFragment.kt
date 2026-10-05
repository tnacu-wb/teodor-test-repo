package com.whitbread.premierinn.ciol.fragments

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.CheckInOnlineActivity
import com.whitbread.premierinn.ciol.entity.AdditionalGuestUiModel
import com.whitbread.premierinn.ciol.entity.DeRegCardAddressUiModel
import com.whitbread.premierinn.ciol.entity.LeadGuestUiModel
import com.whitbread.premierinn.ciol.entity.RegCardGuest
import com.whitbread.premierinn.ciol.utils.createAddressInputFilter
import com.whitbread.premierinn.ciol.utils.createNameInputFilter
import com.whitbread.premierinn.ciol.utils.createPassportNumberInputFilter
import com.whitbread.premierinn.ciol.utils.createPostcodeInputFilter
import com.whitbread.premierinn.ciol.utils.getDateOfBirthFormatted
import com.whitbread.premierinn.ciol.utils.getFirstName
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.utils.getLastName
import com.whitbread.premierinn.ciol.utils.getNationality
import com.whitbread.premierinn.ciol.utils.getPassportNumber
import com.whitbread.premierinn.ciol.utils.getProfileId
import com.whitbread.premierinn.ciol.utils.getReservationId
import com.whitbread.premierinn.ciol.utils.getStringWithAsterisk
import com.whitbread.premierinn.ciol.utils.getTitle
import com.whitbread.premierinn.ciol.utils.hideError
import com.whitbread.premierinn.ciol.utils.populateDateOfBirthFromGuest
import com.whitbread.premierinn.ciol.utils.setNonEmptyText
import com.whitbread.premierinn.ciol.utils.setupAsDateOfBirthField
import com.whitbread.premierinn.ciol.utils.showError
import com.whitbread.premierinn.ciol.utils.updateFieldHintWithVisibility
import com.whitbread.premierinn.ciol.viewmodel.EditGuestDetailsViewModel
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.CityNameInvalid
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.CityNameMissing
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.CityNameTooLong
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.CountryMissing
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.FirstNameMissing
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.FirstNameTooLong
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.FirstNameTooShort
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.FormValid
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.HomeAddressMissing
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.HomeAddressTooLong
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.LastNameMissing
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.LastNameTooLong
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.LastNameTooShort
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.NationalityMissing
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.PassportNumberMissing
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.PassportNumberTooLong
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.PassportNumberTooShort
import com.whitbread.premierinn.ciol.viewmodel.state.FormValidation.PostcodeMissing
import com.whitbread.premierinn.common.fragment.BaseFragment
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.FragmentEditGuestDetailsBinding
import com.whitbread.premierinn.databinding.RegCardTextInputLayoutItemBinding
import com.whitbread.premierinn.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

const val EDIT_GUEST_DETAILS = "EDIT_GUEST_DETAILS_KEY"
const val IS_LEAD_GUEST = "IS_LEAD_GUEST_KEY"
const val GUEST_MODEL = "GUEST_MODEL_KEY"
const val REG_CARD_DOB_FORMAT = "dd/MM/yyyy"
const val SLASH_SYMBOL = "/"

@AndroidEntryPoint
class EditGuestDetailsFragment : BaseFragment() {

    private lateinit var binding: FragmentEditGuestDetailsBinding

    private val isLeadGuest: Boolean by lazy { arguments?.getBoolean(IS_LEAD_GUEST) ?: false }

    private val guest: RegCardGuest? by lazy {
        arguments?.let {
            it.parcelable<RegCardGuest>(GUEST_MODEL)
        }
    }

    private val editGuestDetailsViewModel: EditGuestDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditGuestDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editGuestDetailsViewModel.onScreenOpened(isLeadGuest)
        setupToolbar()
        setupBaseViews()
        if (isLeadGuest) setupAdditionalViews()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    editGuestDetailsViewModel.selectedCountry.collectLatest { selectedCountry ->
                        binding.countryTextInput.editText.setText(selectedCountry)
                    }
                }

                launch {
                    editGuestDetailsViewModel.selectedNationality.collectLatest { selectedNationality ->
                        binding.nationalityTextInput.editText.setText(selectedNationality)
                    }
                }

                launch {
                    editGuestDetailsViewModel.isPassportRequired.collectLatest { isPassportRequired ->
                        binding.passportNumberTextInput.textInput.isVisible = isPassportRequired
                        if (!isPassportRequired) binding.passportNumberTextInput.hideError()
                    }
                }

                launch {
                    editGuestDetailsViewModel.formValidation.collectLatest { formValidation ->
                        handleFormValidation(formValidation)
                    }
                }

                launch {
                    editGuestDetailsViewModel.receivedGuest.collectLatest { it?.let(::populateGuestData) }
                }
            }
        }

        guest?.let { editGuestDetailsViewModel.onGuestReceived(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as? CheckInOnlineActivity)?.changeToolbarIconVisibility(isVisible = false)
    }

    private fun setupToolbar() {
        (requireActivity() as CheckInOnlineActivity?)?.run {
            setupToolbarTitle(
                getString(
                    if (isLeadGuest) R.string.reg_card_lead_guest_details_title else R.string.reg_card_additional_guest_details_title
                )
            )
            changeToolbarIconVisibility(isVisible = true)
            setOnToolbarIconClickListener {
                editGuestDetailsViewModel.onSaveButtonPressed(
                    buildGuestUiModel(isLeadGuest),
                    isLeadGuest
                )
            }
        }
    }

    private fun setupBaseViews() {
        // First Name
        binding.firstNameTextInput.apply {
            textInput.hint = getStringWithAsterisk(getString(R.string.guest_details_form_first_name))
            editText.filters = arrayOf(createNameInputFilter())
            invalidateErrorOnFocusChanged()
        }

        // Last Name
        binding.lastNameTextInput.apply {
            textInput.hint = getStringWithAsterisk(getString(R.string.guest_details_form_last_name))
            editText.filters = arrayOf(createNameInputFilter())
            invalidateErrorOnFocusChanged()
        }

        // Date of birth
        binding.dateOfBirthLayout.apply {
            dayInputLayout.setupAsDateOfBirthField(getString(R.string.reg_card_dob_day_input_layout_hint))
            monthInputLayout.setupAsDateOfBirthField(getString(R.string.reg_card_dob_month_input_layout_hint))
            yearInputLayout.setupAsDateOfBirthField(getString(R.string.reg_card_dob_year_input_layout_hint))

            this.dayInputLayout.editText.setOnClickListener { showDateOfBirthDatePicker() }
            this.monthInputLayout.editText.setOnClickListener { showDateOfBirthDatePicker() }
            this.yearInputLayout.editText.setOnClickListener { showDateOfBirthDatePicker() }
        }

        // Country
        binding.countryTextInput.apply {
            textInput.setEndIconDrawable(R.drawable.ic_chevron_down_dropdown)
            editText.isClickable = true
            editText.isFocusable = false
            editText.inputType = InputType.TYPE_NULL
            editText.setOnClickListener {
                SelectNationalityBottomSheet { selectedCountry ->
                    binding.countryTextInput.hideError()
                    editGuestDetailsViewModel.onCountrySelected(selectedCountry.countryName)
                }.apply {
                    arguments = Bundle().apply {
                        putBoolean(SHOW_COUNTRIES_KEY, true)
                    }
                }.show(this@EditGuestDetailsFragment.requireActivity().supportFragmentManager, tag)
            }
        }

        // Nationality
        binding.nationalityTextInput.apply {
            textInput.setEndIconDrawable(R.drawable.ic_chevron_down_dropdown)
            editText.isClickable = true
            editText.isFocusable = false
            editText.inputType = InputType.TYPE_NULL
            textInput.hint = getStringWithAsterisk(getString(R.string.reg_card_nationality_hint))
            editText.setOnClickListener {
                SelectNationalityBottomSheet { selectedCountry ->
                    binding.nationalityTextInput.hideError()
                    binding.passportNumberTextInput.editText.setText(EMPTY_STRING)
                    editGuestDetailsViewModel.onNationalitySelected(selectedCountry.nationality ?: EMPTY_STRING)
                }.apply {
                    arguments = Bundle().apply {
                        putBoolean(SHOW_COUNTRIES_KEY, false)
                    }
                }.show(this@EditGuestDetailsFragment.requireActivity().supportFragmentManager, tag)
            }
        }

        // Passport Number
        binding.passportNumberTextInput.apply {
            textInput.hint = getStringWithAsterisk(getString(R.string.reg_card_passport_number_hint))
            editText.filters = arrayOf(createPassportNumberInputFilter())
            invalidateErrorOnFocusChanged()
        }
    }

    private fun setupAdditionalViews() {
        binding.apply {
            addressLine1TextInput.updateFieldHintWithVisibility(getStringWithAsterisk(getString(R.string.address_form_address_line1)))
            addressLine2TextInput.updateFieldHintWithVisibility(getString(R.string.reg_card_address_line_2_hint))
            addressLine3TextInput.updateFieldHintWithVisibility(getString(R.string.reg_card_address_line_3_hint))
            postcodeTextInput.updateFieldHintWithVisibility(getStringWithAsterisk(getString(R.string.address_form_postcode)))
            cityTextInput.updateFieldHintWithVisibility(getStringWithAsterisk(getString(R.string.reg_card_city_hint)))
            countryTextInput.updateFieldHintWithVisibility(getStringWithAsterisk(getString(R.string.form_field_label_country)))

            addressLine1TextInput.invalidateErrorOnFocusChanged()
            postcodeTextInput.invalidateErrorOnFocusChanged()
            cityTextInput.invalidateErrorOnFocusChanged()
            countryTextInput.invalidateErrorOnFocusChanged()

            arrayOf(createAddressInputFilter()).run {
                addressLine1TextInput.editText.filters = this
                addressLine2TextInput.editText.filters = this
                addressLine3TextInput.editText.filters = this
                cityTextInput.editText.filters = this
            }

            postcodeTextInput.editText.filters = arrayOf(createPostcodeInputFilter())
        }
    }

    private fun showDateOfBirthDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            val date = Calendar.getInstance().apply { set(selectedYear, selectedMonth, selectedDay) }
            val formattedDate = SimpleDateFormat(REG_CARD_DOB_FORMAT).format(date.time)
            val (day, month, year) = formattedDate.split(SLASH_SYMBOL)

            binding.dateOfBirthLayout.apply {
                dayInputLayout.editText.setText(day)
                monthInputLayout.editText.setText(month)
                yearInputLayout.editText.setText(year)
            }
        }

        DatePickerDialog(
            requireContext(),
            datePicker,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun buildGuestUiModel(isLeadGuest: Boolean): RegCardGuest {
        val additionalGuestUiModel = AdditionalGuestUiModel(
            id = guest?.getId() ?: EMPTY_STRING,
            profileId = guest?.getProfileId() ?: EMPTY_STRING,
            firstName = binding.firstNameTextInput.editText.text.toString(),
            lastName = binding.lastNameTextInput.editText.text.toString(),
            dateOfBirth = binding.dateOfBirthLayout.getDateOfBirthFormatted(),
            nationality = binding.nationalityTextInput.editText.text.toString(),
            passportNumber = binding.passportNumberTextInput.editText.text.toString(),
            sameAsBooker = false,
            reservationId = guest?.getReservationId() ?: EMPTY_STRING,
            isAccompanyingGuest = true,
        )

        return if (isLeadGuest) {
            LeadGuestUiModel(
                title = guest?.getTitle() ?: EMPTY_STRING,
                address = DeRegCardAddressUiModel(
                    addressLine1 = binding.addressLine1TextInput.editText.text.toString(),
                    addressLine2 = binding.addressLine2TextInput.editText.text.toString(),
                    addressLine3 = binding.addressLine3TextInput.editText.text.toString(),
                    cityName = binding.cityTextInput.editText.text.toString(),
                    country = editGuestDetailsViewModel.getIsoCodeFromCountryName(binding.countryTextInput.editText.text.toString()) ?: EMPTY_STRING,
                    postalCode = binding.postcodeTextInput.editText.text.toString(),
                ),
                additionalGuestUiModel = additionalGuestUiModel.copy(isAccompanyingGuest = false)
            )
        } else {
            additionalGuestUiModel
        }
    }

    private fun populateGuestData(receivedGuest: RegCardGuest) {
        binding.apply {
            firstNameTextInput.setNonEmptyText(receivedGuest.getFirstName())

            if (receivedGuest.getLastName().isNotEmpty()) {
                lastNameTextInput.editText.setText(receivedGuest.getLastName())
                if (receivedGuest is LeadGuestUiModel) {
                    lastNameTextInput.textInput.isEnabled = false
                    ContextCompat.getColor(requireContext(), R.color.grey_30_transparency)
                        .let { disabledColor ->
                            lastNameTextInput.textInput.defaultHintTextColor =
                                ColorStateList.valueOf(disabledColor)
                            lastNameTextInput.editText.setTextColor(disabledColor)
                        }
                }
            }

            dateOfBirthLayout.populateDateOfBirthFromGuest(receivedGuest)

            if (receivedGuest.getNationality().isNotEmpty()) {
                editGuestDetailsViewModel.onNationalitySelected(
                    editGuestDetailsViewModel.getUserSelectedNationality(receivedGuest.getNationality()))
            }

            passportNumberTextInput.setNonEmptyText(receivedGuest.getPassportNumber())

            if (receivedGuest is LeadGuestUiModel) {
                addressLine1TextInput.setNonEmptyText(receivedGuest.address.addressLine1)
                addressLine2TextInput.setNonEmptyText(receivedGuest.address.addressLine2)
                addressLine3TextInput.setNonEmptyText(receivedGuest.address.addressLine3)
                postcodeTextInput.setNonEmptyText(receivedGuest.address.postalCode)
                cityTextInput.setNonEmptyText(receivedGuest.address.cityName)
                countryTextInput.setNonEmptyText(editGuestDetailsViewModel.getCountryNameFromIsoCode(receivedGuest.address.country))
            }
        }
    }

    private fun RegCardTextInputLayoutItemBinding.invalidateErrorOnFocusChanged() {
        this.editText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                editGuestDetailsViewModel.validateForm(buildGuestUiModel(isLeadGuest), isLeadGuest)
            }
        }
    }

    private fun handleFormValidation(validation: List<FormValidation>) {
        // Modify error view visibility
        binding.apply {
            firstNameTextInput.hideError()
            lastNameTextInput.hideError()
            addressLine1TextInput.hideError()
            addressLine2TextInput.hideError()
            addressLine3TextInput.hideError()
            postcodeTextInput.hideError()
            cityTextInput.hideError()
            countryTextInput.hideError()
            nationalityTextInput.hideError()
            passportNumberTextInput.hideError()
            dateOfBirthLayout.hideError(requireContext())
        }

        validation.forEach {
            when (it) {
                is FormValid -> {
                    editGuestDetailsViewModel.trackSaveButton(it.regCardGuest)
                    parentFragmentManager.apply {
                        setFragmentResult(
                            EDIT_GUEST_DETAILS,
                            bundleOf(
                                IS_LEAD_GUEST to it.isLeadGuest,
                                GUEST_MODEL to it.regCardGuest
                            )
                        )
                        popBackStack()
                    }
                }
                FirstNameMissing -> binding.firstNameTextInput.showError(getString(R.string.reg_card_first_name_missing_error))
                FirstNameTooLong -> binding.firstNameTextInput.showError(getString(R.string.reg_card_first_name_too_long_error))
                FirstNameTooShort -> binding.firstNameTextInput.showError(getString(R.string.reg_card_first_name_too_short_error))
                LastNameMissing -> binding.lastNameTextInput.showError(getString(R.string.reg_card_last_name_missing_error))
                LastNameTooLong -> binding.lastNameTextInput.showError(getString(R.string.reg_card_last_name_too_long_error))
                LastNameTooShort -> binding.lastNameTextInput.showError(getString(R.string.reg_card_last_name_too_short_error))
                NationalityMissing -> binding.nationalityTextInput.showError(getString(R.string.reg_card_nationality_missing_error))
                PassportNumberMissing -> binding.passportNumberTextInput.showError(getString(R.string.reg_card_passport_number_missing_error))
                PassportNumberTooLong -> binding.passportNumberTextInput.showError(getString(R.string.reg_card_passport_number_too_long_error))
                PassportNumberTooShort -> binding.passportNumberTextInput.showError(getString(R.string.reg_card_passport_number_too_short_error))
                CityNameMissing -> binding.cityTextInput.showError(getString(R.string.reg_card_city_name_missing_error))
                CityNameTooLong -> binding.cityTextInput.showError(getString(R.string.reg_card_city_name_too_long_error))
                CityNameInvalid -> binding.cityTextInput.showError(getString(R.string.reg_card_city_name_invalid_characters_error))
                CountryMissing -> binding.countryTextInput.showError(getString(R.string.reg_card_country_missing_error))
                HomeAddressMissing -> binding.addressLine1TextInput.showError(getString(R.string.reg_card_home_address_missing_error))
                HomeAddressTooLong -> binding.addressLine1TextInput.showError(getString(R.string.reg_card_home_address_too_long_error))
                PostcodeMissing -> binding.postcodeTextInput.showError(getString(R.string.reg_card_postcode_missing_error))
                FormValidation.DateOfBirthMissing -> binding.dateOfBirthLayout.showError(
                    requireContext(),
                    getString(R.string.reg_card_dob_missing_error)
                )
                FormValidation.GuestNotAdult -> binding.dateOfBirthLayout.showError(
                    requireContext(),
                    getString(R.string.reg_card_dob_guest_not_adult_error)
                )
                FormValidation.BirthDateInvalid -> binding.dateOfBirthLayout.showError(
                    requireContext(),
                    getString(R.string.reg_card_dob_invalid_error)
                )
            }
        }
    }

    companion object {
        fun newInstance(
            isLeadGuest: Boolean,
            guest: RegCardGuest? = null
        ) = EditGuestDetailsFragment().apply {
            arguments = Bundle().apply {
                putBoolean(IS_LEAD_GUEST, isLeadGuest)
                putParcelable(GUEST_MODEL, guest)
            }
        }
    }
}
