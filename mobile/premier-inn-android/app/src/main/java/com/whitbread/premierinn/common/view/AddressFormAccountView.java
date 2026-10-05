package com.whitbread.premierinn.common.view;

import static com.whitbread.premierinn.common.forms.Input.ValidationType.COMPANY_NAME;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.GERMAN_POSTCODE;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.NONE;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.REQUIRED;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.UK_POSTCODE;
import static com.whitbread.premierinn.common.forms.InputState.State.FAILED;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxAdapterView;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.adapter.CountryListAdapter;
import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.action.Action;
import com.whitbread.premierinn.common.forms.action.ClickAction;
import com.whitbread.premierinn.createaccount.action.CountryChangedAction;
import com.whitbread.premierinn.createaccount.action.HomeWorkToggleAction;
import com.whitbread.premierinn.databinding.LayoutAddressFormCreateAccountBinding;
import com.whitbread.premierinn.domain.common.Address;
import com.whitbread.premierinn.domain.countries.entity.CountryDomain;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import io.reactivex.Observable;

public class AddressFormAccountView extends RelativeLayout {

    private static final int INITIAL_SKIP_FOR_SPINNER = 1;

    LayoutAddressFormCreateAccountBinding binding;

    public AddressFormAccountView(Context context) {
        super(context);
        init(context);
    }

    public AddressFormAccountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddressFormAccountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public AddressFormAccountView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(@NonNull Context context) {
        binding = LayoutAddressFormCreateAccountBinding
                .inflate(LayoutInflater.from(context), this);
    }

    public void populateAddress(@NonNull Address address) {
        CountryListAdapter countryAdapter = (CountryListAdapter) binding.addressFormCountriesSpinner.getAdapter();
        List<CountryDomain> countries = countryAdapter.getCountries();

        String countryCode = address.getCountryCode();

        if (countryCode != null && !countryCode.isEmpty()) {
            for (int i = 0; i < countries.size(); i++) {
                if (countryCode.equals(countries.get(i).getCountryCode())) {
                    binding.addressFormCountriesSpinner.setSelection(i);
                    break;
                }
            }

            if (CountryDomain.Companion.isCountryUk(countryCode)) {
                binding.addressFormLookupPostcodeInput.setText(address.getPostCode());
            } else {
                binding.addressFormPostcodeInput.setText(address.getPostCode());
            }
        }

        binding.addressFormLine1Input.setText(address.getLine1());
        binding.addressFormLine2Input.setText(address.getLine2());
        binding.addressFormTownCityInput.setText(address.getLine4());
        binding.addressFormCompanyInput.setText(address.getCompanyName());
    }

    public void setCountries(@NonNull List<CountryDomain> countries) {
        CountryListAdapter countryAdapter = new CountryListAdapter(getContext(), countries);
        binding.addressFormCountriesSpinner.setAdapter(countryAdapter);
        // Default selection
        for (int i = 0; i < countries.size(); i++) {
            if (CountryDomain.Companion.isCountryUk(countries.get(i).getCountryCode())) {
                binding.addressFormCountriesSpinner.setSelection(i);
                break;
            }
        }
    }

    public Observable<Action> getOngoingEvents() {
        return Observable.merge(binding.addressFormLine1Input.resetEvent(), binding.addressFormLine2Input.resetEvent(),
                binding.addressFormTownCityInput.resetEvent()
                .mergeWith(binding.addressFormLookupPostcodeInput.resetEvent())
                .mergeWith(binding.addressFormPostcodeInput.resetEvent())
                .mergeWith(binding.addressFormCompanyInput.resetEvent())
                .mergeWith(selectedCountryChangedEvent())
                .mergeWith(homeWorkToggleChangedEvent())
                .mergeWith(enterAddressManualClick()));
    }

    public List<Input> getSubmitEvents() {
        return Collections.unmodifiableList(Arrays.asList(
                Input.builder()
                        .id(binding.addressFormLine1Input.getId())
                        .value(binding.addressFormLine1Input.getInputText())
                        .validationTypes(EnumSet.of(REQUIRED)).build(),
                Input.builder()
                        .id(binding.addressFormLine2Input.getId())
                        .value(binding.addressFormLine2Input.getInputText())
                        .validationTypes(EnumSet.of(NONE)).build(),
                Input.builder()
                        .id(binding.addressFormTownCityInput.getId())
                        .value(binding.addressFormTownCityInput.getInputText())
                        .validationTypes(EnumSet.of(NONE)).build(),
                Input.builder()
                        .id(binding.addressFormLookupPostcodeInput.getId())
                        .value(binding.addressFormLookupPostcodeInput.getInputText())
                        .validationTypes(binding.addressFormLookupPostcodeInput.getVisibility() == VISIBLE
                                && CountryDomain.Companion.isCountryUk(((CountryDomain)
                                binding.addressFormCountriesSpinner.getSelectedItem()).getCountryIsoCode())
                                ? EnumSet.of(UK_POSTCODE) : EnumSet.of(NONE))
                        .build(),
                Input.builder()
                        .id(binding.addressFormPostcodeInput.getId())
                        .value(binding.addressFormPostcodeInput.getInputText())
                        .validationTypes(CountryDomain.Companion.isCountryGermanyUsingIsoCode(((CountryDomain)
                                binding.addressFormCountriesSpinner.getSelectedItem()).getCountryIsoCode())
                                ? EnumSet.of(GERMAN_POSTCODE) : EnumSet.of(NONE))
                        .build(),
                Input.builder()
                        .id(binding.addressFormCompanyInput.getId())
                        .value(binding.addressFormCompanyInput.getInputText())
                        .validationTypes(binding.addressFormCompanyInput.getVisibility()
                                == VISIBLE ? EnumSet.of(COMPANY_NAME) : EnumSet.of(NONE))
                        .build(),
                Input.builder()
                        .id(binding.addressFormCountriesSpinner.getId())
                        .value(((CountryDomain) binding.addressFormCountriesSpinner.getSelectedItem()).getCountryCode())
                        .validationTypes(EnumSet.of(REQUIRED)).build(),
                Input.builder()
                        .id(binding.addressFormHomeWorkToggle.getId())
                        .value(binding.addressFormHomeWorkToggle.getCurrentState().blockingFirst())
                        .validationTypes(EnumSet.of(REQUIRED)).build()));
    }

    public void updateInputStates(@Nullable List<InputState> inputStates) {
        if (binding.addressFormManualAddressWrapper.getVisibility() == GONE) {
            for (InputState inputState : inputStates) {
                boolean errorEnabled = inputState.state() == FAILED;
                if (errorEnabled) {
                    binding.addressFormManualAddressWrapper.setVisibility(VISIBLE);
                    binding.addressFormManualAddressLabel.setVisibility(GONE);
                    break;
                }
            }
        }

        for (InputState inputState : inputStates) {
            switch (inputState.id()) {
                case R.id.address_form_line1_input:
                    updateFormField(binding.addressFormLine1Input, inputState);
                    break;
                case R.id.address_form_line2_input:
                    updateFormField(binding.addressFormLine2Input, inputState);
                    break;
                case R.id.address_form_town_city_input:
                    updateFormField(binding.addressFormTownCityInput, inputState);
                    break;
                case R.id.address_form_lookup_postcode_input:
                    updateFormField(binding.addressFormLookupPostcodeInput, inputState);
                    break;
                case R.id.address_form_postcode_input:
                    updateFormField(binding.addressFormPostcodeInput, inputState);
                    break;
                case R.id.address_form_manual_address_wrapper:
                    updateField(binding.addressFormManualAddressWrapper, inputState);
                    break;
                case R.id.address_form_manual_address_label:
                    updateField(binding.addressFormManualAddressLabel, inputState);
                    break;
                case R.id.address_form_find_address_button:
                    updateField(binding.addressFormFindAddressButton, inputState);
                    break;
                case R.id.address_form_company_input:
                    updateCompanyFormField(binding.addressFormCompanyInput, inputState);
                    break;
                case R.id.address_form_home_work_toggle:
                    ToggleButtonView.State state = (ToggleButtonView.State) inputState.value();
                    if (state != null) {
                        binding.addressFormHomeWorkToggle.setState(state);
                    }
                    break;
                default:
            }
        }
    }

    public String getPostcode() {
        return binding.addressFormLookupPostcodeInput.getInputText();
    }
    public Boolean isCompanyName() {
        return binding.addressFormCompanyInput.getVisibility() == VISIBLE;
    }

    public void setHomeWorkToggle(Boolean isWork) {
        if (isWork) {
            binding.addressFormHomeWorkToggle.setState(ToggleButtonView.State.RIGHT);
        } else {
            binding.addressFormHomeWorkToggle.setState(ToggleButtonView.State.LEFT);
        }
    }

    private Observable<CountryChangedAction> selectedCountryChangedEvent() {
        return RxAdapterView.itemSelections(binding.addressFormCountriesSpinner).skip(INITIAL_SKIP_FOR_SPINNER)
                .map(index -> ((CountryDomain) binding.addressFormCountriesSpinner.getItemAtPosition(index)).getCountryCode())
                .map(CountryChangedAction::create);
    }

    private Observable<HomeWorkToggleAction> homeWorkToggleChangedEvent() {
        return binding.addressFormHomeWorkToggle.getClick()
                .map(HomeWorkToggleAction::create);
    }

    private Observable<ClickAction> enterAddressManualClick() {
        return RxView.clicks(binding.addressFormManualAddressLabel)
                .map(__ -> ClickAction.create(binding.addressFormManualAddressLabel.getId()));
    }

    private void updateFormField(FormEditTextView formField, InputState inputState) {
        updateField(formField, inputState);
        boolean errorEnabled = inputState.state() == FAILED;

        formField.setErrorEnabled(errorEnabled);
        formField.setError(inputState.errorMessage());

        if (inputState.value() != null) {
            formField.setText(inputState.value().toString());
        }
    }

    private void updateCompanyFormField(FormEditTextView companyFormField, InputState inputState) {
        if (inputState.state() == InputState.State.INVISIBLE) {
            companyFormField.setVisibility(GONE);
        } else if (inputState.state() == InputState.State.VISIBLE) {
            companyFormField.setVisibility(VISIBLE);
        }

        boolean errorEnabled = inputState.state() == FAILED;
        companyFormField.setErrorEnabled(errorEnabled);
        companyFormField.setError(inputState.errorMessage());

        if (inputState.value() != null) {
            companyFormField.setText(inputState.value().toString());
        }
    }

    private void updateField(View field, InputState inputState) {
        if (inputState.state() == InputState.State.INVISIBLE) {
            field.setVisibility(GONE);
        } else if (inputState.state() == InputState.State.VISIBLE) {
            field.setVisibility(VISIBLE);
        }
    }

    private Input.ValidationType getPostcodeValidationType(String countryIsoCode) {
        if (CountryDomain.Companion.isCountryGermanyUsingIsoCode(countryIsoCode)) {
            return GERMAN_POSTCODE;
        } else if (CountryDomain.Companion.isCountryUk(countryIsoCode)) {
            return UK_POSTCODE;
        } else {
            return NONE;
        }
    }
}
