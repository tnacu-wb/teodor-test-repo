package com.whitbread.premierinn.editpaymentmethods;

import static android.view.View.GONE;
import static com.whitbread.premierinn.common.forms.InputState.State.INVISIBLE;
import static com.whitbread.premierinn.data.common.Constants.PAYMENT_CARD_TYPE;
import static com.whitbread.premierinn.data.common.Constants.PAYMENT_PIBA_TYPE;
import static com.whitbread.premierinn.editpaymentmethods.EditPaymentMethodsActivity.SAVED_CARD_REQUEST_CODE;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.jakewharton.rxbinding3.view.RxView;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.forms.FormUiModel;
import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.action.Action;
import com.whitbread.premierinn.common.forms.action.SubmitFormAction;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.databinding.ActivityEditPaymentMethodsBinding;
import com.whitbread.premierinn.domain.common.Address;
import com.whitbread.premierinn.domain.countries.GetCountries;
import com.whitbread.premierinn.domain.customer.entity.Customer;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveCardDetails;
import com.whitbread.premierinn.paymentmethods.MyAccountAddNewCardPaymentComponentsView;
import com.whitbread.premierinn.paymentmethods.PaymentMethodsActivity;
import com.whitbread.premierinn.threeCp.ThreeCpActivity;
import com.whitbread.premierinn.threeCp.ThreeCpInputSaveCard;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import kotlin.Unit;

public class EditPaymentMethodsViewContainer extends ViewContainer implements EditPaymentMethodsPresenter.View  {

    private final ActivityEditPaymentMethodsBinding binding;

    private boolean customerHasExistingCard;

    private Address billingAddress;

    public EditPaymentMethodsViewContainer(@NonNull BaseActivity activity, boolean customerHasExistingCard,
                                           ActivityEditPaymentMethodsBinding binding) {
        super(activity);
        this.customerHasExistingCard = customerHasExistingCard;
        this.binding = binding;
    }

    @Override
    public void showOnlyAddNewCardView() {
        binding.editPaymentMethodsGdprTopInfo.setVisibility(GONE);
        binding.editPaymentMethodsNoneStoredBanner.setVisibility(View.GONE);
        binding.addNewCardPaymentComponent.setVisibility(View.VISIBLE);
        binding.editPaymentMethodsAddNewCardSynchInfo.setVisibility(View.VISIBLE);
        binding.editPaymentMethodsCta.setText(getActivity().getString(R.string.add_new_card_add_card_details_label));

    }

    @Override
    public void showOldEditCardView() {
        binding.editPaymentMethodsGdprTopInfo.setVisibility(View.VISIBLE);
        binding.editPaymentMethodsNoneStoredBanner.setVisibility(View.VISIBLE);
        binding.editPaymentMethodsCta.setText(getActivity().getString(R.string.save_changes_label));
        binding.editPaymentMethodsAddNewCardSynchInfo.setVisibility(GONE);
        binding.addNewCardPaymentComponent.setVisibility(GONE);

    }


    @Override
    public void navigateToIPage(@NonNull String iPageHtml) {
        Intent intent = ThreeCpActivity.createThreeCpIntentSaveCard(
                getActivity(), new ThreeCpInputSaveCard(iPageHtml));
        getActivity().startActivityForResult(intent, SAVED_CARD_REQUEST_CODE);
    }


    @Override
    public void showCustomerAddressForm(@NotNull Customer customer, @NotNull GetCountries getCountries) {
        binding.editPaymentMethodsCardholderAddress.setVisibility(View.VISIBLE);
        binding.editPaymentMethodsCardholderAddress.paymentDetailsSameAddressTitle.setText(R.string.billing_address);
        billingAddress = customer.getAddress();

        boolean hasAddress = !customer.getAddress().getLine1().isEmpty();
        binding.editPaymentMethodsCardholderAddress.setPaymentAddressSameAsBookerAddress(hasAddress);
        binding.editPaymentMethodsCardholderAddress.getSwitch()
                .setChecked(binding.editPaymentMethodsCardholderAddress.isPaymentAddressSameAsBookerAddress());
        binding.editPaymentMethodsCardholderAddress.setSameAddressSectionVisibility(hasAddress);


        if (binding.editPaymentMethodsCardholderAddress.getSwitch().isChecked()) {
            binding.editPaymentMethodsCardholderAddress.setHtmlAddressLbl(
                    getActivity().getApplicationContext().getString(R.string.payment_methods_billing_address_switch,
                            customer.getAddress().getPostCode()));
            binding.editPaymentMethodsCardholderAddress.hideAddressForm();
        } else {
            binding.editPaymentMethodsCardholderAddress.showAddressForm();
            binding.editPaymentMethodsCardholderAddress.setListOfCountries(getCountries.fetchCountriesFromAsset());
            binding.editPaymentMethodsCardholderAddress.setAddressFields(billingAddress);
            binding.editPaymentMethodsCardholderAddress.setEnterManualAddress(false);
            binding.editPaymentMethodsCardholderAddress.showManualAddressSection();
            binding.editPaymentMethodsCardholderAddress.setFormComponents();
        }
        binding.editPaymentMethodsCardholderAddress.getSwitch()
                .setOnCheckedChangeListener((buttonView, isChecked) -> showOrHideManualAddressSection(isChecked, customer, getCountries));
    }

    private void showOrHideManualAddressSection(boolean isChecked, @NotNull Customer customer, @NotNull GetCountries getCountries) {
        if (isChecked) {
            binding.editPaymentMethodsCardholderAddress.hideAddressForm();
            billingAddress = customer.getAddress();
        } else {
            binding.editPaymentMethodsCardholderAddress.showAddressForm();
            binding.editPaymentMethodsCardholderAddress.setListOfCountries(getCountries.fetchCountriesFromAsset());
            binding.editPaymentMethodsCardholderAddress.setAddressFields(billingAddress);
            binding.editPaymentMethodsCardholderAddress.setEnterManualAddress(false);
            binding.editPaymentMethodsCardholderAddress.showManualAddressSection();
            binding.editPaymentMethodsCardholderAddress.setFormComponents();
            if (!binding.editPaymentMethodsCardholderAddress.isAddressValid()) {
                binding.editPaymentMethodsCardholderAddress.displayAddressInlineErrors();
            }
        }
    }

    @Override
    public Observable<Action> getActions() {
        Observable<Action> submitFormAction = RxView.clicks(binding.editPaymentMethodsCta)
                .map(__ -> {
                    List<Input> inputs = new ArrayList<>();
                    return SubmitFormAction.create(inputs);
                })
                .cast(Action.class);
        return submitFormAction;
    }

    @Override
    public void update(FormUiModel uiModel) {
        switch (uiModel.state()) {
            case IDLE:
                processInputUpdates(uiModel.inputStates());
                binding.editPaymentMethodsCta.setLoadingState(false);
                break;
            case IN_PROGRESS:
                binding.editPaymentMethodsCta.setLoadingState(true);
                break;
            case SUCCESS:
                if (customerHasExistingCard) {
                    getActivity().setResult(Activity.RESULT_OK);
                } else {
                    getActivity().startActivity(PaymentMethodsActivity.createIntent(getActivity()));
                }
                getActivity().finish();
                break;
            case ERROR:
                binding.editPaymentMethodsCta.setLoadingState(false);
                Toast.makeText(getActivity(), "Something went wrong. Check the details you entered are correct",
                        Toast.LENGTH_LONG).show();
                break;
            default:
        }
    }

    private void processInputUpdates(@Nullable List<InputState> inputStates) {
        if (inputStates == null) {
            return;
        }
        for (InputState inputState : inputStates) {
            switch (inputState.id()) {
                case R.id.edit_payment_methods_none_stored_banner:
                    updateView(binding.editPaymentMethodsNoneStoredBanner, inputState);
                    break;
                case R.id.edit_payment_methods_cta:
                    updateView(binding.editPaymentMethodsCta, inputState);
                    if (inputState.value() != null) {
                        binding.editPaymentMethodsCta.setText(inputState.value().toString());
                    }
                    break;
                case R.id.toolbar:
                    binding.toolbar.setTitle((String) inputState.value());
                    break;
                case R.id.edit_payment_methods_gdpr_top_info:
                    if (inputState.value() != null) {
                        binding.editPaymentMethodsGdprTopInfo.setHtmlText((String) inputState.value());
                    }
                    break;
                default:
            }
        }
    }

    private void updateView(View noCardsStoredBanner, InputState inputState) {
        noCardsStoredBanner.setVisibility(inputState.state() == INVISIBLE ? GONE : View.VISIBLE);
    }

    @Override
    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
        binding.editPaymentMethodsCardholderAddress.setAddressFields(billingAddress);
    }

    @NonNull
    @Override
    public Address getBillingAddress() {
        if (!binding.editPaymentMethodsCardholderAddress.isPaymentAddressSameAsBookerAddress()) {
            billingAddress = binding.editPaymentMethodsCardholderAddress.getManuallyEnteredAddress();
        }
        return billingAddress;
    }

    @NonNull
    public SaveCardDetails getSaveCardDetails() {
        return new SaveCardDetails(
                getMyAccountAddNewCardPaymentComponentsView().isPIBASelected() ? PAYMENT_PIBA_TYPE : PAYMENT_CARD_TYPE,
                getMyAccountAddNewCardPaymentComponentsView().isCNPToggleOn(),
                getMyAccountAddNewCardPaymentComponentsView().getMemorableWord());
    }

    @NonNull
    @Override
    public Observable<Unit> setAddCardButtonClick() {
        return RxView.clicks(binding.editPaymentMethodsCta);
    }

    @Override
    public void setLoadingStateForCta(boolean loading) {
        binding.editPaymentMethodsCta.setLoadingState(loading);
    }

    @NonNull
    @Override
    public MyAccountAddNewCardPaymentComponentsView getMyAccountAddNewCardPaymentComponentsView() {
        return binding.addNewCardPaymentComponent;
    }

    @Override
    public void showSaveCardError() {
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.add_new_card_add_card_error_title)
                .setMessage(R.string.add_new_card_add_card_error_description)
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private boolean isBillingAddressValid() {
        if (!binding.editPaymentMethodsCardholderAddress.isPaymentAddressSameAsBookerAddress()) {
            billingAddress = binding.editPaymentMethodsCardholderAddress.getManuallyEnteredAddress();
        }

        if (!binding.editPaymentMethodsCardholderAddress.isAddressValid()) {
            binding.editPaymentMethodsCardholderAddress.displayAddressInlineErrors();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean isPaymentComponentReadyForSubmission() {
        if (!isBillingAddressValid()) {
            return false;
        }

        if (!getMyAccountAddNewCardPaymentComponentsView().isPIBASelected()) {
            return true;
        }

        if (!getMyAccountAddNewCardPaymentComponentsView().isCNPToggleOn()) {
            return true;
        }

        return getMyAccountAddNewCardPaymentComponentsView().isMemorableWordValid();
    }
}

