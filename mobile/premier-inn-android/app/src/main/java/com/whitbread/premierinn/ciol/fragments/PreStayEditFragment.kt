package com.whitbread.premierinn.ciol.fragments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.CheckInOnlineActivity
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.fragments.PreStayFragment.Companion.MODEL_KEY
import com.whitbread.premierinn.ciol.mapper.toPreStayDomainModel
import com.whitbread.premierinn.ciol.utils.changeLayoutMargin
import com.whitbread.premierinn.ciol.utils.getFirstName
import com.whitbread.premierinn.ciol.utils.getLastName
import com.whitbread.premierinn.ciol.utils.getStringWithAsterisk
import com.whitbread.premierinn.ciol.utils.getTitle
import com.whitbread.premierinn.ciol.viewmodel.PreStayEditViewModel
import com.whitbread.premierinn.ciol.viewmodel.PreStaySharedViewModel
import com.whitbread.premierinn.ciol.viewmodel.state.utils.FieldType
import com.whitbread.premierinn.ciol.viewmodel.state.utils.InputState
import com.whitbread.premierinn.common.Validator
import com.whitbread.premierinn.common.fragment.BaseFragment
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.FragmentPreStayEditBinding
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.Serializable

const val PRE_STAY_EDIT_FEATURE_TAG = "PreStayEdit"

@AndroidEntryPoint
class PreStayEditFragment : BaseFragment() {

    private val preStayEditViewModel: PreStayEditViewModel by viewModels()

    private val preStaySharedViewModel: PreStaySharedViewModel by activityViewModels()

    private lateinit var binding: FragmentPreStayEditBinding

    companion object {
        private const val DATA_KEY = "dataKey"
        private const val ROOM_KEY = "roomKey"
        const val NATIONALITY_KEY = "NATIONALITY_KEY"
        private const val PASSPORT_NUMBER_KEY = "PASSPORT_NUMBER_KEY"

        fun newInstance(
            map: HashMap<Serializable, String>,
            roomId: String? = null,
            nationality: String? = null,
            passportNumber: String? = null,
            preStayUiModel: Parcelable?
        ) =
            PreStayEditFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(DATA_KEY, map)
                    nationality?.let { putString(NATIONALITY_KEY, it) }
                    passportNumber?.let { putString(PASSPORT_NUMBER_KEY, it) }
                    roomId?.let { putString(ROOM_KEY, it) }
                    putParcelable(MODEL_KEY, preStayUiModel)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPreStayEditBinding.inflate(inflater, container, false)
        preStaySharedViewModel.initParams((arguments?.parcelable<PreStayUiModel>(MODEL_KEY) as PreStayUiModel).toPreStayDomainModel())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val containerActivity = (activity as CheckInOnlineActivity)
        containerActivity.setupToolbarTitle(resources.getString(R.string.pre_stay_edit_details))
        containerActivity.changeToolbarIconVisibility(isVisible = true)
        preStayEditViewModel.updateHotelBrand(preStaySharedViewModel.preStayModel.preStayHeaderInfo.hotelBrand)

        val dataMap = arguments?.getSerializable(DATA_KEY) as HashMap<Serializable, String>
        val clickedRoomId = arguments?.getString(ROOM_KEY)
        val preStayUiModel = arguments?.parcelable<PreStayUiModel>(MODEL_KEY)

        preStayEditViewModel.onScreenOpened(preStayUiModel)

        val data = dataMap.values.first()
        val fieldType = dataMap.keys.first()

        if (fieldType == FieldType.LEAD_BOOKER || fieldType == FieldType.LEAD_GUEST || fieldType == FieldType.SECOND_GUEST) {
            preStayEditViewModel.leadBookerOrGuestFlowStateUpdate(
                roomId = clickedRoomId ?: StringUtils.EMPTY_STRING,
                inputMap = dataMap
            )
        }

        populateScreen(fieldType, data)
        displayNationalityLayout()
        preStayEditViewModel.getUserSelectedCountry(
            arguments?.getString(NATIONALITY_KEY),
            arguments?.getString(PASSPORT_NUMBER_KEY)
        )

        containerActivity.setOnToolbarIconClickListener {
            preStayEditViewModel.onValidate()
        }

        binding.titleView.setOnClickListener {
            TitleSelectionBottomSheetFragment().show(requireActivity().supportFragmentManager, null)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    preStaySharedViewModel.title.collect { title ->
                        if (title.isNotEmpty() && binding.titleLayout.error != null) {
                            binding.titleLayout.error = null
                            changeLayoutMargin(binding.titleLayout, false)
                        }
                        binding.titleLayout.editText?.setText(title)
                        preStayEditViewModel.updateFullNameElementTitle(title)
                    }
                }
                launch {
                    preStayEditViewModel.state.collect { state ->
                        state.nationality.selectedCountry?.let(::updateNationalityView)
                        state.identificationType.idType?.let(::updateIdentificationType)
                        state.identificationNumber.idNumber?.let(::updateIdentificationNumber)
                        state.dataValidity?.let { dataValidity ->
                            when (dataValidity) {
                                InputState.VALID -> {
                                    updateSharedStateForValidFlows(clickedRoomId, fieldType)
                                    parentFragmentManager.popBackStack()
                                }

                                InputState.INVALID -> {
                                    displayErrorsForInvalidStates(state)
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.firstNameView.addTextChangedListener { text ->
            if (text?.isNotBlank() == true && binding.firstNameLayout.error != null) {
                binding.firstNameLayout.error = null
                changeLayoutMargin(binding.firstNameLayout, false)
            }
            preStayEditViewModel.onTextChangedUpdateState(
                fieldType,
                FieldType.FIRST_NAME,
                text.toString()
            )
        }

        binding.lastNameView.addTextChangedListener { text ->
            if (text?.isNotBlank() == true && binding.lastNameLayout.error != null) {
                binding.lastNameLayout.error = null
                changeLayoutMargin(binding.lastNameLayout, false)
            }
            preStayEditViewModel.onTextChangedUpdateState(
                fieldType,
                FieldType.LAST_NAME,
                text.toString()
            )
        }
    }

    private fun updateSharedStateForValidFlows(
        clickedRoomId: String?,
        fieldType: Serializable
    ) {
        if (preStayEditViewModel.isGuestFlow()) {
            preStaySharedViewModel.state.value.rooms.firstOrNull { it.roomId == clickedRoomId }
                ?.let {
                    preStaySharedViewModel.updateState(
                        updatedRoom = preStayEditViewModel.updateRoomAfterEdit(fieldType, it),
                        fieldType = fieldType
                    )
                }
                ?: run {
                    Log.w(PRE_STAY_EDIT_FEATURE_TAG, "No room with id $clickedRoomId found")
                }
        }
    }

    private fun displayErrorsForInvalidStates(state: PreStayEditViewModel.ElementInfoState) {
        if (preStayEditViewModel.isLeadBookerFlow() || preStayEditViewModel.isGuestFlow()) {

            state.fullNameElement?.title?.let { title ->
                if (title.isEmpty()) {
                    binding.titleLayout.error = getSpecificError(
                        FieldType.TITLE,
                        title
                    )
                    changeLayoutMargin(binding.titleLayout, true)
                }
            }

            state.fullNameElement?.firstName?.let { element ->
                element.dataValidity?.let { input ->
                    if (input == InputState.INVALID) {
                        binding.firstNameLayout.error =
                            element.map.values.first()?.let { name ->
                                getSpecificError(
                                    FieldType.FIRST_NAME,
                                    name
                                )
                            }
                        changeLayoutMargin(binding.firstNameLayout, true)
                    }
                }
            }

            state.fullNameElement?.lastName?.let { element ->
                element.dataValidity?.let { input ->
                    if (input == InputState.INVALID) {
                        binding.lastNameLayout.error =
                            element.map.values.first()?.let { name ->
                                getSpecificError(
                                    FieldType.LAST_NAME,
                                    name
                                )
                            }
                        changeLayoutMargin(binding.lastNameLayout, true)
                    }
                }
            }

            state.nationality.isValid.takeIf { it }?.run {
                state.identificationType.isValid.takeIf { !it }.run {
                    binding.identificationTypeLayout.error = getSpecificError(
                        FieldType.ID_TYPE, state.identificationType.idType ?: EMPTY_STRING
                    )
                }
                state.identificationNumber.isValid.takeIf { !it }.run {
                    binding.identificationNumberLayout.error = getSpecificError(
                        FieldType.ID_NUMBER, state.identificationNumber.idNumber ?: EMPTY_STRING
                    )
                }
            } ?: run {
                binding.nationalityLayout.error = getSpecificError(
                    FieldType.NATIONALITY, state.nationality.selectedCountry?.countryName ?: EMPTY_STRING
                )
            }
        } else {
            binding.firstNameLayout.error = getSpecificError(
                state.element.keys.first(),
                state.element.values.first()
            )
            changeLayoutMargin(binding.firstNameLayout, true)
        }
    }

    private fun getSpecificError(type: Serializable, input: String): String {
        if (input.isEmpty()) return resources.getString(R.string.form_field_is_required)
        return when (type) {
            FieldType.PHONE -> {
                resources.getString(R.string.form_field_phone_number_not_valid)
            }

            FieldType.EMAIL -> {
                resources.getString(R.string.form_field_email_not_valid)
            }

            FieldType.FIRST_NAME -> {
                StringBuilder().append(resources.getString(R.string.form_field_first_name_not_valid)).append(StringUtils.SPACE).append( Validator.MAX_FIRST_NAME_LENGTH).toString()
            }

            FieldType.LAST_NAME -> {
                StringBuilder().append(resources.getString(R.string.form_field_last_name_not_valid)).append(StringUtils.SPACE).append( Validator.MAX_LAST_NAME_LENGTH).toString()
            }

            else -> {
                StringUtils.EMPTY_STRING
            }
        }
    }

    private fun populateScreen(type: Serializable, value: String) {
        with(binding) {
            when (type) {
                FieldType.LEAD_GUEST,
                FieldType.SECOND_GUEST,
                FieldType.LEAD_BOOKER -> {
                    titleLayout.isVisible = true
                    lastNameLayout.isVisible = true
                    titleLayout.hint = getStringWithAsterisk(resources.getText(R.string.pre_stay_title))
                    firstNameLayout.hint = getStringWithAsterisk(resources.getText(R.string.guest_details_form_first_name))
                    lastNameLayout.hint = getStringWithAsterisk(resources.getText(R.string.guest_details_form_last_name))
                    value.apply {
                        preStaySharedViewModel.updateTitle(this.getTitle())
                        firstNameLayout.editText?.setText(this.getFirstName())
                        lastNameLayout.editText?.setText(this.getLastName())
                    }
                }

                else -> Unit
            }
        }
    }

    private fun updateNationalityView(countryDomain: CountryDomain) {
        binding.nationalityLayout.editText?.setText(countryDomain.nationality)
        if (preStayEditViewModel.isPassportRequired(countryDomain.countryName)) {
            setupIdentificationTypeLayout()
            setupIdentificationNumberLayout()
        } else {
            binding.identificationTypeLayout.isVisible = false
            binding.identificationNumberLayout.isVisible = false
        }
    }

    private fun updateIdentificationType(idType: String) {
        binding.identificationTypeSpinner.setText(idType, false)
        preStayEditViewModel.onIdentificationTypeSelected(idType)
    }

    private fun updateIdentificationNumber(identificationNumber: String) {
        binding.idNumberEditText.setText(identificationNumber)
        preStayEditViewModel.onIdentificationNumberAdded(identificationNumber)
    }

    private fun displayNationalityLayout() {
        binding.nationalityLayout.isVisible = true
        binding.nationalityLayout.hint = getStringWithAsterisk(getString(R.string.personal_details_nationality_label))
        binding.nationalityEditText.setOnClickListener {
            binding.nationalityEditText.requestFocus()
            SelectNationalityBottomSheet { selectedCountry ->
                preStayEditViewModel.onCountrySelected(selectedCountry)
                binding.idNumberEditText.setText(EMPTY_STRING)
                binding.identificationTypeSpinner.setText(EMPTY_STRING)
            }.apply {
                arguments = Bundle().apply {
                    putString(
                        GUEST_NATIONALITY_KEY,
                        arguments?.getString(NATIONALITY_KEY)
                    )
                    putBoolean(SHOW_COUNTRIES_KEY, false)
                }
            }.show(requireActivity().supportFragmentManager)
        }
    }

    private fun setupIdentificationTypeLayout() {
        binding.identificationTypeLayout.apply {
            isVisible = true
            hint = getStringWithAsterisk(getString(R.string.pre_stay_identification_type))
        }
        binding.identificationTypeSpinner.apply {
            val idTypeAdapter = ArrayAdapter(
                requireContext(),
                R.layout.identification_type_dropdown_item,
                arrayListOf(getString(R.string.pre_stay_id_type_passport))
            )
            setAdapter(idTypeAdapter)
            // Preselect the first item
            idTypeAdapter.getItem(0)?.let { preselectedItem ->
                setText(preselectedItem, false)
                preStayEditViewModel.onIdentificationTypeSelected(preselectedItem)
            }
            setOnItemClickListener { _, _, position, _ ->
                val idType = idTypeAdapter.getItem(position)
                preStayEditViewModel.onIdentificationTypeSelected(idType ?: EMPTY_STRING)
            }
        }
    }

    private fun setupIdentificationNumberLayout() {
        binding.identificationNumberLayout.apply {
            isVisible = true
            hint = getStringWithAsterisk(getString(R.string.pre_stay_identification_number))
        }
        binding.idNumberEditText.apply {
            addTextChangedListener(afterTextChanged = { text ->
                var textString = text.toString()
                // Capitalize all lowercase chars
                if (textString != textString.uppercase()) {
                    textString = textString.uppercase()
                    setText(textString)
                }
                setSelection(textString.length)
                preStayEditViewModel.onIdentificationNumberAdded(textString)
            })
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    preStayEditViewModel.onValidate()
                    return@setOnEditorActionListener true
                }
                false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as CheckInOnlineActivity).changeToolbarIconVisibility(isVisible = false)
    }
}
