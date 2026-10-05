package com.whitbread.premierinn.createaccount;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxCompoundButton;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.forms.FormUiModel;
import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.action.Action;
import com.whitbread.premierinn.common.forms.action.ClickAction;
import com.whitbread.premierinn.common.forms.action.SubmitFormAction;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.databinding.ActivityCreateAccountBinding;
import com.whitbread.premierinn.databinding.LayoutAccountContactDetailsBinding;
import com.whitbread.premierinn.domain.countries.entity.CountryDomain;
import com.whitbread.premierinn.postcodefinder.ParcelableAddress;
import com.whitbread.premierinn.postcodefinder.PostcodeFinderActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import io.reactivex.Observable;
import kotlin.Unit;

public class CreateAccountViewContainer extends ViewContainer implements CreateAccountPresenter.View {

    private final Observable<ParcelableAddress> postcodeAddressObservable;
    private ActivityCreateAccountBinding binding;
    private LayoutAccountContactDetailsBinding mergedAccountDetailsBinding;

    public CreateAccountViewContainer(@NonNull BaseActivity baseActivity,
                                      @NonNull Observable<ParcelableAddress> postcodeAddressRelay,
                                      ActivityCreateAccountBinding binding) {
        super(baseActivity);
        this.binding = binding;
        getActivity().setToolbar(getActivity().getString(R.string.create_account_label), true);
        this.mergedAccountDetailsBinding = LayoutAccountContactDetailsBinding.bind(binding.getRoot());
        this.postcodeAddressObservable = postcodeAddressRelay;

        setSwitchesText();
    }

    @Override
    public void populateCountriesList(@NonNull List<CountryDomain> countries) {
        binding.addressFormView.setCountries(countries);
    }

    @Override
    public void populateTitlesList(List<String> titlesList) {
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_popup_selected_item, titlesList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.list_popup_dropdown_items);
        mergedAccountDetailsBinding.accountTitlesSpinner.setAdapter(spinnerArrayAdapter);
    }

    @Override
    public void startPostcodeFinderActivity() {
        PostcodeFinderActivity.start(getActivity(), getActivity(), binding.addressFormView.getPostcode()
        );
    }

    @Override
    public Observable<Object> onSetImmediately() {
        return Observable.just(new Object());
    }

    @Override
    public Observable<ParcelableAddress> onPostcodeFindingSuccess() {
        return postcodeAddressObservable;
    }

    @Override
    public Observable<Unit> onFindAddressClick() {
        return RxView.clicks(getActivity().findViewById(R.id.address_form_find_address_button));
    }

    @Override
    public Observable<Action> getActions() {
        return Observable.merge(getSubmitEvents(), getOngoingEvents());
    }

    @Override
    public void showDataUsageContent(String htmlContent) {

        binding.createAccountGdprTopInfo.setHtmlText(htmlContent);
    }

    @Override
    public void update(FormUiModel uiModel) {
        switch (uiModel.state()) {
            case IN_PROGRESS:
                binding.createAccountErrorMessageWrapper.setVisibility(GONE);
                binding.createAccountCreateAccountButton.setLoadingState(true);
                break;
            case ERROR:
                binding.createAccountCreateAccountButton.setLoadingState(false);
                binding.createAccountErrorMessageWrapper.setVisibility(View.VISIBLE);
                binding.createAccountErrorMessageText.setText(uiModel.errorMessage());
                binding.createAccountScrollView.smoothScrollTo(0, 0);
                break;
            case SUCCESS:
                binding.createAccountErrorMessageWrapper.setVisibility(GONE);
                binding.createAccountCreateAccountButton.setLoadingState(false);
                getActivity().setResult(RESULT_OK);
                getActivity().finish();
                break;
            case FORM_UPDATE:
                binding.createAccountErrorMessageWrapper.setVisibility(GONE);
                binding.createAccountCreateAccountButton.setLoadingState(false);

                binding.addressFormView.updateInputStates(uiModel.inputStates());

                for (InputState inputState : uiModel.inputStates()) {
                    switch (inputState.id()) {
                        case R.id.account_first_name_input:
                            mergedAccountDetailsBinding.accountFirstNameInput.setError(inputState.errorMessage());
                            mergedAccountDetailsBinding.accountFirstNameInput
                                    .setErrorEnabled(inputState.state() == InputState.State.FAILED);
                            break;
                        case R.id.account_last_name_input:
                            mergedAccountDetailsBinding.accountLastNameInput.setError(inputState.errorMessage());
                            mergedAccountDetailsBinding.accountLastNameInput
                                    .setErrorEnabled(inputState.state() == InputState.State.FAILED);
                            break;
                        case R.id.account_contact_number_input:
                            mergedAccountDetailsBinding.accountContactNumberInput.setError(inputState.errorMessage());
                            mergedAccountDetailsBinding.accountContactNumberInput
                                    .setErrorEnabled(inputState.state() == InputState.State.FAILED);
                            break;
                        case R.id.account_email_input:
                            mergedAccountDetailsBinding.accountEmailInput.setError(inputState.errorMessage());
                            mergedAccountDetailsBinding.accountEmailInput.setErrorEnabled(inputState.state() == InputState.State.FAILED);
                            break;
                        case R.id.create_account_password_input:
                            binding.createAccountPasswordInput.setError(inputState.errorMessage());
                            binding.createAccountPasswordInput.setErrorEnabled(inputState.state() == InputState.State.FAILED);
                            break;
                        case R.id.create_account_terms_conditions_switch:
                            binding.createAccountTermsConditionsErrorLabel
                                    .setVisibility(inputState.state() == InputState.State.FAILED ? VISIBLE : GONE);
                            binding.createAccountErrorDrawable
                                    .setVisibility(inputState.state() == InputState.State.FAILED ? VISIBLE : GONE);
                            break;
                        default:
                            break;
                    }
                }
            default:
                break;
        }
    }

    @Override
    public void showGenericError() {
        Toast.makeText(getActivity(), R.string.generic_error_description, Toast.LENGTH_LONG).show();
        binding.createAccountCreateAccountButton.setLoadingState(false);
    }

    private Observable<SubmitFormAction> getSubmitEvents() {
        return RxView.clicks(
                binding.createAccountCreateAccountButton).map(__ -> {
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
                            .id(binding.createAccountPasswordInput.getId())
                            .value(binding.createAccountPasswordInput.getInputText())
                            .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED, Input.ValidationType.PASSWORD)).build(),
                    Input.builder()
                            .id(binding.createAccountTermsConditionsSwitch.getId())
                            .value(binding.createAccountTermsConditionsSwitch.isChecked())
                            .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED, Input.ValidationType.CHECKED))
                            .build()));
            inputList.addAll(binding.addressFormView.getSubmitEvents());

            return SubmitFormAction.create(Collections.unmodifiableList(inputList));
        });
    }

    private Observable<Action> getOngoingEvents() {
        return  Observable.merge(
                        mergedAccountDetailsBinding.accountFirstNameInput.resetEvent(),
                        mergedAccountDetailsBinding.accountLastNameInput.resetEvent(),
                        mergedAccountDetailsBinding.accountContactNumberInput.resetEvent())
                .mergeWith(mergedAccountDetailsBinding.accountEmailInput.resetEvent())
                .mergeWith(binding.createAccountPasswordInput.resetEvent())
                .mergeWith(binding.addressFormView.getOngoingEvents())
                .mergeWith(termsConditionsOngoingEvent());
    }

    private Observable<Action> termsConditionsOngoingEvent() {
        return RxCompoundButton.checkedChanges(binding.createAccountTermsConditionsSwitch)
                .map(__ -> ClickAction.create(binding.createAccountTermsConditionsSwitch.getId()));
    }

    private void setSwitchesText() {
        binding.createAccountTermsConditionsMessage.setText(Html.fromHtml(getActivity()
                .getString(R.string.review_booking_terms_conditions_label_html,
                        getActivity().getString(R.string.terms_conditions_web_url))));
        binding.createAccountTermsConditionsMessage.setMovementMethod(LinkMovementMethod.getInstance());
    }
}