package com.whitbread.premierinn.login;


import static com.whitbread.premierinn.common.Constants.RESULT_BUSINESS_LOGIN_SUCCESS;
import static com.whitbread.premierinn.data.common.Constants.EMPTY_STRING;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.tabs.TabLayout;
import com.jakewharton.rxbinding3.material.RxTabLayout;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.common.utils.IntentUtils;
import com.whitbread.premierinn.databinding.ActivityLoginBinding;
import com.whitbread.premierinn.resetpassword.ResetPasswordActivity;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;
import kotlin.Unit;

public class LoginViewContainer extends ViewContainer implements LoginPresenter.View {

    private final ActivityLoginBinding binding;

    private final Observable<String> resetPasswordSuccess;
    private static final int BUSINESS_BOOKER_LOGIN_TAB_POSITION = 1;

    private int businessTabSelectedCount = 0;

    public LoginViewContainer(@NonNull BaseActivity activity, @NonNull Observable<String> resetPasswordSuccess,
                              ActivityLoginBinding loginBinding) {


        super(activity);
        this.resetPasswordSuccess = resetPasswordSuccess;
        this.binding = loginBinding;

        getActivity().setKeyboardVisibilityListener(getActivity().findViewById(R.id.account_login_root_container));

        // Ensure the leisure tab is selected before attaching the listener to avoid triggering
        // showDiscardBookingAlert during initialization when Business was last selected tab
        binding.loginTabs.post(() -> {
            TabLayout.Tab leisureTab = binding.loginTabs.getTabAt(0);
            if (leisureTab != null) {
                binding.loginTabs.selectTab(leisureTab);
                tabSelectedListener();
            }
        });
    }

    private void tabSelectedListener() {
        binding.loginTabs.clearOnTabSelectedListeners();
        binding.loginTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == BUSINESS_BOOKER_LOGIN_TAB_POSITION) {
                    binding.accountLoginEmailContainer.setHint(getActivity().getString(R.string.login_business_email_address));
                    binding.accountLoginMoreAboutBusinessBooker.setVisibility(View.VISIBLE);
                    binding.cabLoginContinueGuestButton.setVisibility(View.GONE);
                    businessTabSelectedCount++;
                    if (businessTabSelectedCount == 1) {
                        showDiscardBookingAlert();
                    }
                } else {
                    binding.accountLoginEmailContainer.setHint(getActivity().getString(R.string.email_address));
                    binding.accountLoginMoreAboutBusinessBooker.setVisibility(View.GONE);
                    binding.cabLoginContinueGuestButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @NotNull
    @Override
    public Observable<LoginDataInput> onLoginDataInputChanged() {
        return Observable.combineLatest(
                RxView.focusChanges(binding.accountLoginEmailInput),
                RxTextView.textChanges(binding.accountLoginEmailInput),
                RxTextView.textChanges(binding.accountLoginPasswordInput),
                (emailErrorEnabled, email, password) ->
                        LoginDataInput.create(emailErrorEnabled, email.toString(), password.toString(),
                                binding.loginTabs.getSelectedTabPosition() == BUSINESS_BOOKER_LOGIN_TAB_POSITION))
                .startWith(LoginDataInput.create(false, EMPTY_STRING, EMPTY_STRING, false));
    }

    @NotNull
    @Override
    public Observable<String> onResetPasswordConfirmation() {
        return resetPasswordSuccess;
    }

    @Override
    public void showLoginFailedError(boolean show, @StringRes int resStringId) {
        binding.accountLoginServerSideErrorMessage.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            binding.accountLoginServerSideErrorMessage.setText(resStringId);
        }
    }

    @Override
    public void enableLoginButton(boolean enable) {
        binding.accountLoginLoginButton.setEnabled(enable);
    }

    @Override
    public void showEmailValidationError(boolean show) {
        binding.accountLoginEmailContainer.setError(getActivity().getString(R.string.invalid_email_error));
        binding.accountLoginEmailContainer.setErrorEnabled(show);
    }

    @Override
    public void finishSuccessLoginActivity() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void finishSuccessBusinessLoginActivity() {
        getActivity().setResult(RESULT_BUSINESS_LOGIN_SUCCESS);
        getActivity().finish();
    }

    @Override
    public void finishLoginActivity() {
        getActivity().setResult(Activity.RESULT_FIRST_USER);
        getActivity().finish();
    }

    @NotNull
    @Override
    public Observable<Unit> onForgotPasswordClick() {
        return RxView.clicks(getActivity().findViewById(R.id.account_login_forgot_password_label));
    }

    @NotNull
    @Override
    public Observable<Unit> onMoreAboutBusinessBookingClick() {
        return RxView.clicks(binding.accountLoginMoreAboutBusinessBooker);
    }

    @NotNull
    @Override
    public Observable<Unit> onContinueGuestClick() {
        return binding.cabLoginContinueGuestButton.onClickOnInternetAvailable();
    }

    @Override
    public void goToWebLink(@NonNull String webUrl) {
        getActivity().startActivity(IntentUtils.createWebLinkIntent(webUrl));
    }

    @NotNull
    @Override
    public Observable<LoginDataInput> onLogInButtonClick() {
        return binding.accountLoginLoginButton.onClickOnInternetAvailable()
                .map(__ -> LoginDataInput.create(binding.accountLoginEmailContainer.isErrorEnabled(),
                        binding.accountLoginEmailInput.getText().toString(),
                        binding.accountLoginPasswordInput.getText().toString(),
                        binding.loginTabs.getSelectedTabPosition() == BUSINESS_BOOKER_LOGIN_TAB_POSITION));
    }

    @Override
    public void startResetPasswordActivity() {
        getActivity().startActivityForResult(
                ResetPasswordActivity.createIntent(getActivity(),
                        binding.loginTabs.getSelectedTabPosition() == BUSINESS_BOOKER_LOGIN_TAB_POSITION),
                ResetPasswordActivity.RESET_PASSWORD_RESULT_REQUEST_CODE);
    }

    @Override
    public void showResetPasswordConfirmation(@NonNull String email) {
        binding.accountLoginResetPassConfirmContainer.setVisibility(View.VISIBLE);
        binding.accountLoginResetPassMessage.setText(getActivity().getString(R.string.reset_password_email_confirmation_message, email));
    }

    @Override
    public void showLoginLoading(boolean show) {
        binding.accountLoginLoginButton.setLoadingState(show);
    }

    @Override
    public void enableTabs(boolean enable, int selectedTabPos) {
        if (enable) {
            binding.loginTabs.setVisibility(View.VISIBLE);
            binding.loginTabs.selectTab(binding.loginTabs.getTabAt(selectedTabPos), true);
        } else {
            binding.loginTabs.setVisibility(View.GONE);
        }
    }

    @NotNull
    @Override
    public Observable<Integer> onTabSelected() {
        return RxTabLayout.selections(binding.loginTabs).map(TabLayout.Tab::getPosition);
    }

    public void showDiscardBookingAlert() {
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.login_discard_booking_alert_title)
                .setMessage(R.string.login_discard_booking_alert_message)
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }


}
