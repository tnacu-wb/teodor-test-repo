package com.whitbread.premierinn.personaldetails;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxAdapterView;
import com.jakewharton.rxbinding3.widget.RxCompoundButton;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.databinding.ActivityPersonalDetailsBinding;
import com.whitbread.premierinn.databinding.LayoutAccountContactDetailsBinding;
import com.whitbread.premierinn.domain.common.DEExtensionsKt;
import com.whitbread.premierinn.domain.customer.entity.Customer;
import com.whitbread.premierinn.postcodefinder.ParcelableAddress;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.adapter.CountryItem;
import com.whitbread.premierinn.common.adapter.CountrySpinnerItem;
import com.whitbread.premierinn.common.adapter.DynamicCountryListAdapter;
import com.whitbread.premierinn.common.adapter.NothingSelectedItem;
import com.whitbread.premierinn.common.adapter.Divider;
import com.whitbread.premierinn.common.forms.FormUiModel;
import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.action.Action;
import com.whitbread.premierinn.common.forms.action.SubmitFormAction;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.common.view.FormEditTextView;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.countries.entity.CountryDomain;
import com.whitbread.premierinn.personaldetails.action.NationalityChangedAction;
import com.whitbread.premierinn.postcodefinder.PostcodeFinderActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import io.reactivex.Observable;
import kotlin.Unit;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.NONE;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.REQUIRED;
import static com.whitbread.premierinn.common.forms.InputState.State.FAILED;

public class PersonalDetailsViewContainer extends ViewContainer implements PersonalDetailsPresenter.View {

    private final ActivityPersonalDetailsBinding binding;
    private final LayoutAccountContactDetailsBinding mergedAccountDetailsBinding;

    private static final int INITIAL_SKIP_FOR_SPINNER = 2;

    private final Observable<ParcelableAddress> postcodeAddressObservable;

    private DynamicCountryListAdapter countryListAdapter;
    private SwitchCompat marketingToggle;
    private boolean currentMarketingOptIn = false;

    public PersonalDetailsViewContainer(@NonNull BaseActivity activity,
                                        @NonNull Observable<ParcelableAddress> postcodeAddressRelay,
                                        @NonNull DeviceLocaleProvider deviceLocaleProvider,
                                        ActivityPersonalDetailsBinding binding) {
        super(activity);
        this.binding = binding;
        activity.setToolbar(activity.getString(R.string.personal_details_title), true);
        this.mergedAccountDetailsBinding = LayoutAccountContactDetailsBinding.bind(binding.getRoot());
        setupTitlesOptions();
        this.postcodeAddressObservable = postcodeAddressRelay;
        countryListAdapter = new DynamicCountryListAdapter(activity);
        countryListAdapter.setCountries(Collections.emptyList(),
                new NothingSelectedItem(R.layout.country_dropdown_no_item_selected),
                new Divider(R.layout.country_dropdown_divider));
        binding.personalDetailsNationalityDropdown.setAdapter(countryListAdapter);

        // Initialize marketing toggle
        try {
            marketingToggle = binding.personalDetailsMarketingToggle;
            // UK only - hide for DE
            if (deviceLocaleProvider.isLanguageGerman()) {
                binding.personalDetailsMarketingContainer.setVisibility(View.GONE);
            } else {
                binding.personalDetailsMarketingContainer.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            // If binding fails, log and hide the container
            Log.e("PersonalDetails", "Failed to initialize marketing toggle", e);
            if (binding.personalDetailsMarketingContainer != null) {
                binding.personalDetailsMarketingContainer.setVisibility(View.GONE);
            }
        }
    }

    private void setupTitlesOptions() {
        List<String> titlesList = Arrays.asList(getActivity().getResources().getStringArray(R.array.titles));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_popup_selected_item, titlesList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.list_popup_dropdown_items);
        mergedAccountDetailsBinding.accountTitlesSpinner.setAdapter(spinnerArrayAdapter);
    }

    @Override
    public void populateCountriesList(@NonNull List<CountryDomain> countries) {
        binding.addressFormView.setCountries(countries);
    }

    @Override
    public void populateNationalitiesList(@NonNull List<CountryDomain> countries) {
        List<CountryItem> input = new ArrayList<>();
        for (CountryDomain country : countries) {
            input.add(new CountryItem(country));
        }
        countryListAdapter.setCountries(input,
                new NothingSelectedItem(R.layout.country_dropdown_no_item_selected),
                new Divider(R.layout.country_dropdown_divider));
    }

    @Override
    public void populateContactDetails(@NonNull Customer contactDetail, String language) {
        mergedAccountDetailsBinding.accountFirstNameInput.setText(contactDetail.getFullName().getFirstName());
        mergedAccountDetailsBinding.accountLastNameInput.setText(contactDetail.getFullName().getLastName());
        mergedAccountDetailsBinding.accountEmailInput.setText(contactDetail.getContact().getEmail());
        mergedAccountDetailsBinding.accountContactNumberInput.setText(contactDetail.getContact().getMobile());
        binding.personalDetailsCarRegistrationInput.setText(contactDetail.getCarRegistration());
        if (contactDetail.getNationality() != null && !contactDetail.getNationality().isEmpty()) {
            for (int i = 0; i < countryListAdapter.getCount(); i++) {
                CountrySpinnerItem item = countryListAdapter.getItem(i);
                if (item instanceof CountryItem) {
                    CountryItem countryItem = (CountryItem) item;
                    if (countryItem.getCountry().getCountryCode().equals(contactDetail.getNationality())) {
                        binding.personalDetailsNationalityDropdown.setSelection(i);
                        if (countryItem.getCountry().getRequiresPassportInfo()) {
                            binding.personalDetailsPassportNumber.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                }
            }
        }
        if (contactDetail.getPassport() != null) {
            binding.personalDetailsPassportNumber.setText(contactDetail.getPassport().passportNumber());
        }

        String title = DEExtensionsKt.translateTitleToGermanIfApplicable(contactDetail.getFullName().getTitle(), language);
        if (!title.isEmpty()) {
            mergedAccountDetailsBinding.accountTitlesSpinner.setSelection(((ArrayAdapter<String>)
                    mergedAccountDetailsBinding.accountTitlesSpinner.getAdapter()).getPosition(title));
        }
        binding.addressFormView.populateAddress(contactDetail.getAddress());
    }

    @Override
    public void update(FormUiModel uiModel) {
        switch (uiModel.state()) {
            case IN_PROGRESS:
                binding.personalDetailsSaveChangesButton.setLoadingState(true);
                break;
            case ERROR:
                binding.personalDetailsSaveChangesButton.setLoadingState(false);
                showGenericError();
                break;
            case SUCCESS:
                binding.personalDetailsSaveChangesButton.setLoadingState(false);
                getActivity().setResult(RESULT_OK);
                getActivity().finish();
                break;
            case FORM_UPDATE:
                binding.personalDetailsSaveChangesButton.setLoadingState(false);

                binding.addressFormView.updateInputStates(uiModel.inputStates());

                for (InputState inputState : uiModel.inputStates()) {
                    switch (inputState.id()) {
                        case R.id.account_first_name_input:
                            updateFormField(mergedAccountDetailsBinding.accountFirstNameInput, inputState);
                            break;
                        case R.id.account_last_name_input:
                            updateFormField(mergedAccountDetailsBinding.accountLastNameInput, inputState);
                            break;
                        case R.id.account_contact_number_input:
                            updateFormField(mergedAccountDetailsBinding.accountContactNumberInput, inputState);
                            break;
                        case R.id.account_email_input:
                            updateFormField(mergedAccountDetailsBinding.accountEmailInput, inputState);
                            break;
                        case R.id.personal_details_car_registration_input:
                            updateFormField(binding.personalDetailsCarRegistrationInput, inputState);
                            break;
                        case R.id.personal_details_passport_number:
                            updateFormField(binding.personalDetailsPassportNumber, inputState);
                            break;
                        case R.id.personal_details_gdpr_top_info:
                            binding.personalDetailsGdprTopInfo.setHtmlText(inputState.value().toString());
                            break;
                        default:
                            break;
                    }
                }
            default:
                break;
        }
    }

    private void updateFormField(FormEditTextView formEditTextView, InputState inputState) {
        boolean errorEnabled = inputState.state() == FAILED;
        if (inputState.value() != null) {
            formEditTextView.setText(inputState.value().toString());
        }
        formEditTextView.setErrorEnabled(errorEnabled);
        formEditTextView.setError(inputState.errorMessage());
        updateViewVisibility(formEditTextView, inputState);
    }

    private void updateViewVisibility(@NonNull View view, @NonNull InputState inputState) {
        view.setVisibility(inputState.state() == InputState.State.INVISIBLE ? GONE : VISIBLE);
    }

    @Override
    public void showGenericError() {
        Toast.makeText(getActivity(), getActivity().getString(R.string.personal_details_generic_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public Observable<Unit> onFindAddressClick() {
        return RxView.clicks(getActivity().findViewById(R.id.address_form_find_address_button));
    }

    @Override
    public void startPostcodeFinderActivity() {
        PostcodeFinderActivity.start(getActivity(), getActivity(), binding.addressFormView.getPostcode()
        );
    }

    @Override
    public Observable<ParcelableAddress> onPostcodeFindingSuccess() {
        return postcodeAddressObservable;
    }

    @Override
    public Observable<Action> getActions() {
        return Observable.merge(getSubmitEvents(), getOngoingEvents());
    }

    @Override
    public boolean isBusinessAddress() {
        return binding.addressFormView.isCompanyName();
    }

    @Override
    public void setHomeWorkToggle(boolean isWork) {
        binding.addressFormView.setHomeWorkToggle(isWork);
    }

    public void setMarketingOptIn(boolean optIn) {
        currentMarketingOptIn = optIn;
        // Toggle ON = opted in, Toggle OFF = not opted in (do not receive)
        if (marketingToggle != null) {
            marketingToggle.setChecked(optIn);
        }
    }

    public boolean getMarketingOptIn() {
        return currentMarketingOptIn;
    }

    public Observable<Boolean> onMarketingOptInChange() {
        if (marketingToggle == null) {
            return Observable.empty();
        }
        return RxCompoundButton.checkedChanges(marketingToggle)
                .skip(1) // Skip initial value
                .map(isChecked -> {
                    currentMarketingOptIn = isChecked;
                    return isChecked;
                });
    }

    private Observable<Action> getOngoingEvents() {
        return Observable.merge(binding.addressFormView.getOngoingEvents(), selectedNationalityChangedEvent());
    }

    private Observable<NationalityChangedAction> selectedNationalityChangedEvent() {
        return RxAdapterView.itemSelections(binding.personalDetailsNationalityDropdown).skip(INITIAL_SKIP_FOR_SPINNER)
                .map(index -> {
                    CountrySpinnerItem item = countryListAdapter.getItem(index);
                    if (item instanceof CountryItem) {
                        return NationalityChangedAction.create(((CountryItem) item).getCountry());
                    } else {
                        return NationalityChangedAction.create(null);
                    }
                });
    }

    private Observable<SubmitFormAction> getSubmitEvents() {
        return RxView.clicks(binding.personalDetailsSaveChangesButton).map(__ -> {
            List<Input> inputList = new ArrayList<>(Arrays.asList(
                    Input.builder()
                            .id(mergedAccountDetailsBinding.accountTitlesSpinner.getId())
                            .value(mergedAccountDetailsBinding.accountTitlesSpinner.getSelectedItem())
                            .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED)).build(),
                    Input.builder()
                            .id(mergedAccountDetailsBinding.accountFirstNameInput.getId())
                            .value(mergedAccountDetailsBinding.accountFirstNameInput.getInputText())
                            .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED, Input.ValidationType.FIRST_NAME)).build(),
                    Input.builder()
                            .id(mergedAccountDetailsBinding.accountLastNameInput.getId())
                            .value(mergedAccountDetailsBinding.accountLastNameInput.getInputText())
                            .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED, Input.ValidationType.LAST_NAME)).build(),
                    Input.builder()
                            .id(mergedAccountDetailsBinding.accountContactNumberInput.getId())
                            .value(mergedAccountDetailsBinding.accountContactNumberInput.getInputText())
                            .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED, Input.ValidationType.PHONE)).build(),
                    Input.builder()
                            .id(mergedAccountDetailsBinding.accountEmailInput.getId())
                            .value(mergedAccountDetailsBinding.accountEmailInput.getInputText())
                            .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED, Input.ValidationType.EMAIL)).build(),
                    Input.builder()
                            .id(binding.personalDetailsCarRegistrationInput.getId())
                            .value(binding.personalDetailsCarRegistrationInput.getInputText())
                            .validationTypes(EnumSet.of(NONE)).build(),
                    Input.builder()
                            .id(binding.personalDetailsNationalityDropdown.getId())
                            .value((binding.personalDetailsNationalityDropdown.getSelectedItem()))
                            .validationTypes(EnumSet.of(NONE)).build(),
                    Input.builder()
                            .id(binding.personalDetailsPassportNumber.getId())
                            .value(binding.personalDetailsPassportNumber.getInputText())
                            .validationTypes(binding.personalDetailsPassportNumber.getVisibility() == VISIBLE
                                    ? EnumSet.of(REQUIRED) : EnumSet.of(NONE))
                            .build()));
            inputList.addAll(binding.addressFormView.getSubmitEvents());

            return SubmitFormAction.create(Collections.unmodifiableList(inputList));
        });
    }
}