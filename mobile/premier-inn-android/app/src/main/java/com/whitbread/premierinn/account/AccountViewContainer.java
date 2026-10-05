package com.whitbread.premierinn.account;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidadvance.topsnackbar.TopSnackbar;
import com.businessbooker.paymentmethods.BusinessBookerPaymentMethodsActivity;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.customer.ParcelableCustomer;
import com.whitbread.premierinn.bookingpreferences.BookingPreferencesActivity;
import com.whitbread.premierinn.changepassword.ChangePasswordActivity;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.common.utils.IntentUtils;
import com.whitbread.premierinn.createaccount.CreateAccountActivity;
import com.whitbread.premierinn.databinding.ActivityMyAccountBinding;
import com.whitbread.premierinn.editpaymentmethods.EditPaymentMethodsActivity;
import com.whitbread.premierinn.gdpr.GdprDataUseActivity;
import com.whitbread.premierinn.login.LoginActivity;
import com.whitbread.premierinn.login.Screen;
import com.whitbread.premierinn.login.ScreenType;
import com.whitbread.premierinn.newsletterpreferences.NewsletterPreferencesActivity;
import com.whitbread.premierinn.paymentmethods.PaymentMethodsActivity;
import com.whitbread.premierinn.personaldetails.PersonalDetailsActivity;
import com.whitbread.premierinn.utils.BrowserUtilsKt;

import io.reactivex.Observable;
import kotlin.Pair;
import kotlin.Unit;

public class AccountViewContainer extends ViewContainer implements AccountPresenter.View {

    private final ActivityMyAccountBinding binding;

    private final PublishRelay<Object> createAccountRelay;
    private final PublishRelay<Object> loginRelay;
    private final PublishRelay<Object> refreshRelay;
    private final PublishRelay<Object> changePasswordRelay;
    private final PublishRelay<Object> saveCardRelay;
    private final PublishRelay<Boolean> employeeOfferRelay;

    public AccountViewContainer(@NonNull BaseActivity activity, PublishRelay<Object> createAccountRelay,
                                PublishRelay<Object> loginRelay, PublishRelay<Object> refreshRelay,
                                PublishRelay<Object> changePasswordRelay,
                                PublishRelay<Object> saveCardRelay,
                                PublishRelay<Boolean> employeeOfferRelay,
                                ActivityMyAccountBinding binding) {
        super(activity);
        this.createAccountRelay = createAccountRelay;
        this.loginRelay = loginRelay;
        this.refreshRelay = refreshRelay;
        this.changePasswordRelay = changePasswordRelay;
        this.saveCardRelay = saveCardRelay;
        this.employeeOfferRelay = employeeOfferRelay;
        this.binding = binding;
        activity.setToolbar(activity.getString(R.string.my_account_title), false);
    }

    @Override
    public void showWelcomeToAccountBanner(String firstname) {
        String bannerMessage = getActivity().getString(R.string.my_account_welcome_banner, firstname);
        TopSnackbar.make(binding.coordinator, bannerMessage, TopSnackbar.LENGTH_LONG).show();
    }

    @Override
    public void showBannerSuccessfulPasswordChange() {
        String bannerMessage = getActivity().getString(R.string.my_account_password_change_success);
        TopSnackbar.make(binding.coordinator, bannerMessage, TopSnackbar.LENGTH_LONG).show();
    }

    @Override
    public Observable<Unit> onLogInClick() {
        return RxView.clicks(binding.cabMyAccountLogIn);
    }

    @Override
    public Observable<Object> onAccountCreated() {
        return createAccountRelay;
    }

    @Override
    public Observable<Object> onLoginSuccessful() {
        return loginRelay;
    }

    @Override
    public Observable<Object> onSetImmediately() {
        return Observable.just(new Object());
    }

    @Override
    public Observable<Unit> onLogOutClick() {
        return RxView.clicks(binding.myAccountLogOutButton);
    }

    @Override
    public Observable<Unit> onCreateAccountClick() {
        return RxView.clicks(binding.myAccountCreateAccountButton);
    }

    @Override
    public Observable<Object> onChangePasswordSuccessfull() {
        return changePasswordRelay;
    }

    @NonNull
    @Override
    public Observable<Object> onSaveCardSuccessful() {
        return saveCardRelay;
    }

    @Override
    public void showBannerForSuccessfulSaveCard() {
        String savedCardBannerMessage = getActivity().getString(R.string.save_card_success_message);
        TopSnackbar.make(binding.coordinator, savedCardBannerMessage, TopSnackbar.LENGTH_SHORT).show();
    }

    @Override
    public Observable<Object> onRefresh() {
        return refreshRelay;
    }

    @Override
    public void startEditPaymentMethodsActivity(@NonNull ParcelableCustomer customer) {
        getActivity().startActivityForResult(EditPaymentMethodsActivity.createIntent(getActivity(), customer),
                EditPaymentMethodsActivity.ACTIVITY_RESULT_REQUEST_CODE_SAVE_CARD);
    }

    @Override
    public void startBusinessBookerPaymentMethodsActivity() {
        getActivity().startActivity(BusinessBookerPaymentMethodsActivity.createIntent(getActivity()));
    }

    @Override
    public void showForceLoginMessage() {
        Toast.makeText(getActivity(), R.string.force_login_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startPersonalDetailsActivity(@NonNull ParcelableCustomer customer) {
        getActivity().startActivityForResult(PersonalDetailsActivity.createIntent(getActivity(), customer),
                PersonalDetailsActivity.ACTIVITY_RESULT_REQUEST_CODE);
    }

    @Override
    public void startChangePasswordActivity() {
        getActivity().startActivityForResult(ChangePasswordActivity.createIntent(getActivity()),
                ChangePasswordActivity.ACTIVITY_RESULT_REQUEST_CODE);
    }

    @Override
    public void startNewsletterUpdatesActivity(String userEmailAddress) {
        Intent intent = NewsletterPreferencesActivity.createIntent(getActivity(), userEmailAddress);
        getActivity().startActivity(intent);
    }

    @Override
    public void startBookingPreferencesActivity() {
        getActivity().startActivity(BookingPreferencesActivity.createIntent(getActivity()));
    }

    @Override
    public void startCreateAccountActivity() {
        getActivity().startActivityForResult(
                CreateAccountActivity.createIntent(getActivity()), CreateAccountActivity.ACTIVITY_RESULT_REQUEST_CODE);
    }

    @Override
    public void startPaymentMethodsActivity() {
        getActivity().startActivity(PaymentMethodsActivity.createIntent(getActivity()));
    }

    @Override
    public void startLogInActivity() {
        getActivity().startActivityForResult(LoginActivity.createIntent(getActivity(),
                new Screen(ScreenType.ACCOUNT_LOGIN.name(), AnalyticsConstants.ScreenState.ACCOUNT_LOG_IN)),
                LoginActivity.ACTIVITY_RESULT_REQUEST_CODE);
    }

    @Override
    public void showLoading(boolean show) {
        binding.myAccountLoginMessage.setVisibility(show ? View.GONE : View.VISIBLE);
        binding.cabMyAccountLogIn.setVisibility(show ? View.GONE : View.VISIBLE);
        binding.myAccountCreateAccountButton.setVisibility(show ? View.GONE : View.VISIBLE);
        binding.loadingSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void hideBookingPreferenceAndNewsletter(boolean isInnBusinessUser) {
        binding.bookingPreferencesAndNewsletterContainer.setVisibility(isInnBusinessUser ? View.GONE : View.VISIBLE);
    }

    @Override
    public void addLink(Pair<String, String> link) {
        View childView = getActivity().findViewById(R.id.rl_dynamic_canvas);

        childView.setVisibility(View.VISIBLE);
        TextView textView = childView.findViewById(R.id.button_text);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setText(link.getFirst());

        childView.setOnClickListener(v -> goToWebLink(link.getSecond()));
    }

    @Override
    public void showLogoutLoading(boolean show) {
        binding.myAccountLogOutButton.setLoadingState(show);
    }

    @Override
    public void openHowWeUseYourDataDialog() {
        getActivity().startActivityForResult(GdprDataUseActivity.createIntent(getActivity()), GdprDataUseActivity.REQUEST_CODE);
    }

    @Override
    public Observable<Unit> onPersonalDetailsClick() {
        return RxView.clicks(getActivity().findViewById(R.id.my_account_my_details));
    }

    @Override
    public Observable<Unit> onPaymentMethodsClick() {
        return RxView.clicks(getActivity().findViewById(R.id.my_account_payment_methods));
    }

    @Override
    public Observable<Unit> onChangePasswordClick() {
        return RxView.clicks(getActivity().findViewById(R.id.my_account_change_password));
    }

    @Override
    public Observable<Unit> onBookingPreferencesClick() {
        return RxView.clicks(getActivity().findViewById(R.id.my_account_booking_preferences));
    }

    @Override
    public Observable<Unit> onNewsletterUpdatesClick() {
        return RxView.clicks(getActivity().findViewById(R.id.my_account_newsletters));
    }

    @Override
    public Observable<Unit> onTermsConditionsClick() {
        return RxView.clicks(getActivity().findViewById(R.id.rl_my_account_terms_conditions));
    }

    @Override
    public Observable<Unit> onPrivacyPolicyClick() {
        return RxView.clicks(getActivity().findViewById(R.id.rl_my_account_privacy_policy));
    }

    @Override
    public Observable<Unit> onHowWeUseYourDataClick() {
        return RxView.clicks(getActivity().findViewById(R.id.rl_my_account_gdpr));
    }

    @Override
    public Observable<Unit> onAboutClick() {
        return RxView.clicks(getActivity().findViewById(R.id.rl_my_account_about));
    }

    @Override
    public Observable<Unit> onContactClick() {
        return RxView.clicks(getActivity().findViewById(R.id.rl_my_account_contact_us));
    }

    @Override
    public Observable<Unit> onFaqClick() {
        return RxView.clicks(getActivity().findViewById(R.id.rl_my_account_faq));
    }

    @Override
    public Observable<Unit> onSendFeedbackClick() {
        return RxView.clicks(getActivity().findViewById(R.id.rl_my_account_send_feedback));
    }

    @Override
    public void goToWebLink(@NonNull String webUrl) {
        getActivity().startActivity(IntentUtils.createWebLinkIntent(webUrl));
    }

    @Override
    public void render(boolean loggedIn, @Nullable String firstName, @Nullable String lastName,
                       @Nullable String email, boolean businessBooker) {
        binding.llMyAccountLogIn.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        binding.llMyAccountInfo.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        binding.myAccountLoggedInOptions.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        binding.myAccountMyDetails.setVisibility(loggedIn && !businessBooker ? View.VISIBLE : View.GONE);
        binding.myAccountLogOutButton.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        binding.tvMyAccountName.setText(String.format(getActivity().getString(R.string.two_string_placeholder), firstName, lastName));
        binding.tvMyAccountEmail.setText(email);
        binding.tvBbLabel.setVisibility(businessBooker ? View.VISIBLE : View.GONE);
    }

    @Override
    public void startAboutActivity() {
        getActivity().startActivity(AboutActivity.createIntent(getActivity()));
    }

    @Override
    public void startEmailActivity(@NonNull String receiverAddress, @NonNull String subject, @NonNull String body) {
        getActivity().startActivity(IntentUtils.createEmailIntent(getActivity().getApplicationContext(),
                receiverAddress, subject, body));
    }

    @Override
    public void showGenericError() {
        Toast.makeText(getActivity(), R.string.generic_error_description, Toast.LENGTH_LONG).show();
    }

    @Override
    public void hidePaymentMethods(boolean hide) {
        binding.myAccountPaymentMethods.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    @Override
    public void openCustomTab(@NonNull String webUrl) {
        BrowserUtilsKt.openUrlWithFallback(getActivity().getApplicationContext(), webUrl);
    }

    @Override
    public void showEmployeeOffer() {
        binding.scMyAccountEnableEmployeeOffer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmployeeOffer() {
        binding.scMyAccountEnableEmployeeOffer.setVisibility(View.GONE);
    }

    @Override
    public void setChangedListenerEmployeeOffer() {
        binding.scMyAccountEnableEmployeeOffer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            employeeOfferRelay.accept(isChecked);
        });
    }

    @NonNull
    @Override
    public Observable<Boolean> isEmployeeOfferToggleEnabled() {
        return employeeOfferRelay;
    }

    @Override
    public void setToggleStateForEmployeeOffer(boolean checked) {
        binding.scMyAccountEnableEmployeeOffer.setChecked(checked);
    }

    public void openUrl(@NonNull String url) {
        BrowserUtilsKt.openUrlWithFallback(getActivity(), url);
    }

    @Override
    public void openTermsConditionsUrl() {
        openUrl(getActivity().getString(R.string.terms_conditions_web_url));
    }

    @Override
    public void openPrivacyPolicyUrl() {
        openUrl(getActivity().getString(R.string.privacy_policy_web_url));
    }

    @Override
    public void openFaqUrl() {
        openUrl(getActivity().getString(R.string.faq_web_url));
    }

    @Override
    public void openContactUrl() {
        openUrl(getActivity().getString(R.string.contact_us_web_url));
    }
}
