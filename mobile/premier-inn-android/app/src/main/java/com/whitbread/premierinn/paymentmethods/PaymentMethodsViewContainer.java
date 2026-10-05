package com.whitbread.premierinn.paymentmethods;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.customer.MappersKt;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.databinding.ActivityPaymentMethodsBinding;
import com.whitbread.premierinn.domain.customer.entity.Customer;
import com.whitbread.premierinn.editpaymentmethods.EditPaymentMethodsActivity;
import com.whitbread.premierinn.login.LoginActivity;
import com.whitbread.premierinn.login.Screen;
import com.whitbread.premierinn.login.ScreenType;

import io.reactivex.Observable;
import kotlin.Unit;

public class PaymentMethodsViewContainer extends ViewContainer implements PaymentMethodsPresenter.View {

   private final ActivityPaymentMethodsBinding binding;

    private final Relay<Object> confirmDeleteClickRelay = PublishRelay.create();
    private final AlertDialog alertDialog;

    public PaymentMethodsViewContainer(@NonNull BaseActivity activity, ActivityPaymentMethodsBinding binding) {
        super(activity);
        this.binding = binding;
        alertDialog = createAlertDialog();
    }

    @Override
    public void setCardHolderText(String cardHolder) {
        binding.paymentMethodCardHolderName.setText(cardHolder);
    }

    @Override
    public void setCardNumberLastDigits(String lastFourDigits) {
        binding.paymentMethodCardNumber.setText(getActivity().getString(R.string.card_ending_in, lastFourDigits));
    }

    @Override
    public void setCardExpiryText(String cardExpiry) {
        binding.paymentMethodCardExpiry.setText(getActivity().getString(R.string.card_expires, cardExpiry));
    }

    @Override
    public void showLoading(boolean show) {
        binding.paymentMethodsPageLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.paymentMethodsContentContainer.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void showError() {
        Toast.makeText(getActivity(), R.string.payment_methods_error_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDeleteCardPrompt() {
        alertDialog.show();
    }

    @Override
    public Observable<Object> onDeleteCardConfirmed() {
        return confirmDeleteClickRelay;
    }

    @Override
    public Observable<Unit> onDeleteClick() {
        return RxView.clicks(binding.paymentMethodsDelete);
    }

    @Override
    public Observable<Unit> onReplaceCardClick() {
        return RxView.clicks(binding.paymentMethodsReplaceCard);
    }

    @Override
    public void returnToPreviousActivity() {
        getActivity().finish();
    }

    @Override
    public void showDeletionError() {
        Toast.makeText(getActivity(), R.string.payment_methods_deletion_error_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showForceLoginMessage() {
        Toast.makeText(getActivity(), R.string.force_login_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startLogInActivity() {
        getActivity().startActivity(LoginActivity.createIntent(getActivity(),
                new Screen(ScreenType.ACCOUNT_LOGIN.name(), AnalyticsConstants.ScreenState.AUTHENTICATION_ERROR_LOG_IN)));
    }

    @Override
    public void setGdprText(String gdprMessage) {
        binding.paymentMethodsGdprTopInfo.setHtmlText(gdprMessage);
    }

    @Override
    public void editCardDetails(@NonNull Customer customer) {
        getActivity().startActivityForResult(EditPaymentMethodsActivity.createIntent(getActivity(), MappersKt.toParcelable(customer)),
                EditPaymentMethodsActivity.ACTIVITY_RESULT_REQUEST_CODE);
    }

    private AlertDialog createAlertDialog() {
        return new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.payment_methods_delete_card_dialog_title)
                .setMessage(R.string.payment_methods_delete_card_dialog_message)
                .setNegativeButton(R.string.cancel_dialog, (dialog, i) -> dialog.dismiss())
                .setPositiveButton(R.string.payment_methods_delete_card_dialog_confirm,
                        (dialog, i) -> confirmDeleteClickRelay.accept(new Object()))
                .create();
    }
}