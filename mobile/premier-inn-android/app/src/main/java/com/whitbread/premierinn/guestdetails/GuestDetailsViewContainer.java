package com.whitbread.premierinn.guestdetails;

import static com.whitbread.premierinn.common.view.GuestDetailsTripTypeViewKt.TRIP_TYPE_BUSINESS;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jakewharton.rxbinding3.widget.RxCompoundButton;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.AddressField;
import com.whitbread.premierinn.common.AddressFormDataOutput;
import com.whitbread.premierinn.common.ToastUtil;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.view.ToggleButtonView;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.databinding.ActivityGuestDetailsBinding;
import com.whitbread.premierinn.domain.countries.entity.CountryDomain;
import com.whitbread.premierinn.guestdetails.adapter.RoomGuestDetailsData;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput;
import com.whitbread.premierinn.login.LoginActivity;
import com.whitbread.premierinn.login.Screen;
import com.whitbread.premierinn.login.ScreenType;
import com.whitbread.premierinn.reviewbooking.ReviewBookActivity;
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.Observable;
import kotlin.Unit;

public class GuestDetailsViewContainer implements GuestDetailsPresenter.View {

    private ActivityGuestDetailsBinding binding;
    private GuestDetailsActivity activity;

    public GuestDetailsViewContainer(
            @NonNull GuestDetailsActivity activity,
            @NonNull DeviceLocaleProvider deviceLocaleProvider,
            ActivityGuestDetailsBinding binding) {
        this.activity = activity;
        this.binding = binding;

        activity.setKeyboardVisibilityListener(binding.llGuestDetailsRoot);

        // Set default marketing toggle state: ON for UK/English, OFF for German
        binding.sbGuestDetailsMarketingDetails.setChecked(!deviceLocaleProvider.isLanguageGerman());
    }

    @Override
    public void setupToolbar() {
        activity.setToolbar(activity.getString(R.string.guest_details_toolbar_title), true);
    }

    @Override
    public Observable<GuestDetailsFormDataOutput> getBookerForm() {
        return binding.gdfvGuestDetailsFormBookerDetails.getFormWithTextAndFocus();
    }

    @Override
    public void setBookerDetails(GuestDetailsFormDataInput bookerDetails) {
        binding.gdfvGuestDetailsFormBookerDetails.setValue(bookerDetails, false);
    }

    @Override
    public void showBookerValidationError(boolean showError, GuestDetailsFormDataOutput.Form form) {
        binding.gdfvGuestDetailsFormBookerDetails.showFormValidationError(showError, form);
    }

    @Override
    public Observable<RoomGuestDetailsData> getRoomFormWithTextAndFocus() {
        return binding.rgdvGuestDetailsRoom.getFormWithTextAndFocus();
    }

    @Override
    public void setupRoomFormList(List<GuestDetailsFormDataInput> guestDetailsFormDataInputList, boolean bookerIsStayingAndValid) {
        binding.rgdvGuestDetailsRoom.setup(guestDetailsFormDataInputList, false, bookerIsStayingAndValid);
    }

    @Override
    public void updateRoomFormList(GuestDetailsFormDataInput guestDetailsFormDataInput, int position) {
        binding.rgdvGuestDetailsRoom.update(guestDetailsFormDataInput, position);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // GuestDetailsPresenter.View Address Form
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Observable<AddressFormDataOutput> getAddressFormWithTextAndFocus() {
        return binding.afvGuestDetailsAddressForm.getFormWithTextAndFocus();
    }

    @Override
    public Observable<Unit> onClickEnterAddressManual() {
        return binding.afvGuestDetailsAddressForm.onClickEnterManualAddress();
    }

    @Override
    public void showAddressValidationError(boolean showError, AddressFormDataOutput.Form form) {
        binding.afvGuestDetailsAddressForm.showValidationError(showError, form);
    }

    @Override
    public void setAddressFields(@NonNull AddressField addressField) {
        binding.afvGuestDetailsAddressForm.setAddressFields(addressField);
    }

    @Override
    public void showCompanyAddress(boolean show) {
        binding.afvGuestDetailsAddressForm.setCompanyVisibility(show);
    }

    @Override
    public void showManualAddressSection(boolean show) {
        binding.afvGuestDetailsAddressForm.showManualAddressSection(show);
    }

    @Override
    public void enableContinueButton(boolean show) {
        binding.ctaGuestDetailsContinue.setEnabled(show);
    }

    @Override
    public void showPrivacyGenericFooterContent(@NonNull String htmlContent) {
        binding.guestDetailsGdprBottomInfo.setHtmlText(htmlContent);
    }

    @Override
    public void showFindAddressButton(boolean show) {
        binding.afvGuestDetailsAddressForm.showFindAddressButton(show);
    }

    @Override
    public void showPostcode(boolean show) {
        binding.afvGuestDetailsAddressForm.showPostcode(show);
    }

    @Override
    public Observable<ToggleButtonView.State> onToggleButtonAddressChange() {
        return binding.tbvGuestDetailsAddressHomeWork.getClick();
    }

    @Override
    public Observable<Unit> onClickContinueButton() {
        return binding.ctaGuestDetailsContinue.onClickOnInternetAvailable();
    }

    @Override
    public Observable<Boolean> onCheckNotStaying() {
        return RxCompoundButton.checkedChanges(binding.scGuestDetailsBookingNotStaying);

    }

    @Override
    public Observable<Boolean> onMarketingOptIn() {
        return RxCompoundButton.checkedChanges(binding.sbGuestDetailsMarketingDetails);
    }

    @Override
    public void showProgressLoading(boolean value) {
        binding.flProgressBarContainer.setVisibility(value ? View.VISIBLE : View.GONE);
        binding.svGuestDetailsFormContainer.setVisibility(value ? View.GONE : View.VISIBLE);
    }

    @Override
    public void startReviewBookActivity(@NonNull ReviewBookingInput input) {
        activity.startActivity(ReviewBookActivity.createIntent(activity, input));
    }

    @Override
    public void setContinueButtonTextLastStep() {
        binding.ctaGuestDetailsContinue.setText(activity.getString(R.string.payment_details_continue_last_step_button));
    }

    @Override
    public void setContinueAndCreateAccountStep() {
        binding.ctaGuestDetailsContinue.setText(activity.getString(R.string.button_continue_create_account));
    }

    @Override
    public void loadSharingGuestDetailsPrivacyMessage(@NonNull String htmlContent) {
        binding.guestDetailsGdprSharingGuestInfo.setHtmlText(htmlContent);
    }

    @Override
    public void showSharingGuestDetailsPrivacyMessage(boolean state) {
        binding.guestDetailsGdprSharingGuestInfo.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showForceLoginMessage() {
        Toast.makeText(activity, R.string.force_login_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startLogInActivity() {
        activity.startActivity(LoginActivity.createIntent(activity,
                new Screen(ScreenType.ACCOUNT_LOGIN.name(), AnalyticsConstants.ScreenState.AUTHENTICATION_ERROR_LOG_IN)));
        activity.finish();
    }

    @Override
    public boolean isBusinessTrip() {
        return binding.tripTypeSectionView.getTripTypeSelection() == TRIP_TYPE_BUSINESS;
    }

    @Override
    public int getTripTypeSelection() {
        return binding.tripTypeSectionView.getTripTypeSelection();
    }

    @Override
    public void showCityTaxAlertBanner(@Nullable String message) {
        if (message == null) {
            binding.guestDetailsCityTaxPermanentBanner.setVisibility(View.GONE);
        } else {
            binding.guestDetailsCityTaxPermanentBanner.setVisibility(View.VISIBLE);
            binding.guestDetailsCityTaxPermanentBanner.setText(message);
        }
    }

    @Override
    public void showCityTaxInfoBanner(@Nullable String message) {
        if (message == null) {
            binding.guestDetailsCityTaxToggleBanner.setVisibility(View.GONE);
        } else {
            binding.guestDetailsCityTaxToggleBanner.setVisibility(View.VISIBLE);
            binding.guestDetailsCityTaxToggleBanner.setText(message);
        }
    }

    @Override
    public void showLoading(boolean value) {
        binding.ctaGuestDetailsContinue.setLoadingState(value);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // General
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setIsStayingState(boolean isStaying) {
        binding.scGuestDetailsBookingNotStaying.setChecked(!isStaying);
    }

    @Override
    public void selectHomeAddress() {
        binding.tbvGuestDetailsAddressHomeWork.setState(ToggleButtonView.State.LEFT);
    }

    @Override
    public void selectWorkAddress() {
        binding.tbvGuestDetailsAddressHomeWork.setState(ToggleButtonView.State.RIGHT);
    }

    @Override
    public void setHomeWorkToggle(boolean isWork) {
        if (isWork) {
            binding.tbvGuestDetailsAddressHomeWork.setState(ToggleButtonView.State.RIGHT);
        } else {
            binding.tbvGuestDetailsAddressHomeWork.setState(ToggleButtonView.State.LEFT);
        }
    }

    @Override
    public void addCountries(@NonNull List<CountryDomain> countries) {
        binding.afvGuestDetailsAddressForm.setCountries(countries);
    }

    @Override
    public void setAddressCountrySelection(int countryPosition) {
        binding.afvGuestDetailsAddressForm.setCountrySelection(countryPosition);
    }

    @Override
    public void setIsBusinessSelection(boolean isBusinessAddressSelected) {
        binding.afvGuestDetailsAddressForm.isAddressBusinessSelected(isBusinessAddressSelected);
    }

    @NonNull
    @Override
    public Observable<Integer> onTripTypeChange() {
        return binding.tripTypeSectionView.observeSelection();
    }

    @Override
    public void updateMarketingToggle(boolean isChecked) {
        binding.sbGuestDetailsMarketingDetails.setChecked(isChecked);
    }

    @Override
    public void displayMarketingOption() {
        binding.llGuestDetailsEmailMarketing.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMarketingOption() {
        binding.llGuestDetailsEmailMarketing.setVisibility(View.GONE);
    }

    @Override
    public void showCreateAccount(@NonNull boolean isCustomerLoggedInState) {
        //binding.guestDetailsCreateAccountView.setVisibility(isCustomerLoggedInState ? GONE : VISIBLE);
    }

    @NotNull
    @Override
    public Observable<Boolean> isAcceptablePassword() {
        return binding.guestDetailsCreateAccountView.isAcceptablePassword();
    }

    @NotNull
    @Override
    public Observable<String> getCreateAccountPassword() {
        return binding.guestDetailsCreateAccountView.getPassword();
    }

    @Override
    public void paymentMethodsOrBookingConfUnavailable() {
        new ToastUtil(activity).showLong(activity.getString(R.string.generic_error_message_with_try_again_later));
    }

    @Override
    public void showToastGraphQlError(String queryName) {
        Toast.makeText(activity.getApplicationContext(), "GraphQL Server error for : " + queryName, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void showToastGenericError() {
        Toast.makeText(activity, activity.getString(R.string.generic_error_description), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showTripTypeSelectionError() {
        binding.tripTypeSectionView.showSelectionError();
    }
}