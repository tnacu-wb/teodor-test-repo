package com.whitbread.premierinn.reviewbooking;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.whitbread.premierinn.additionalinformation.AdditionalInformationActivityKt.CHANGE_BUTTON_KEY;
import static com.whitbread.premierinn.data.common.Constants.EMPTY_STRING;
import static com.whitbread.premierinn.reviewbooking.ReviewBookActivity.GOOGLE_PAY_REQUEST;
import static com.whitbread.premierinn.reviewbooking.ReviewBookActivity.THREE_C_P_REQUEST;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.braintreepayments.api.BraintreeClient;
import com.braintreepayments.api.PayPalClient;
import com.braintreepayments.api.PayPalDataCollector;
import com.braintreepayments.api.PayPalVaultRequest;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.BuildConfig;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.bookingdetails.BookingDetailsActivity;
import com.whitbread.premierinn.common.PaymentMethodType;
import com.whitbread.premierinn.common.PaymentTimingChoice;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.format.PriceFormat;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.common.utils.HtmlUtils;
import com.whitbread.premierinn.common.utils.IntentUtils;
import com.whitbread.premierinn.common.view.SummaryExpensesView;
import com.whitbread.premierinn.common.view.TotalPriceView;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.databinding.ActivityReviewBookingBinding;
import com.whitbread.premierinn.databinding.ViewTotalPriceBinding;
import com.whitbread.premierinn.domain.common.Address;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.countries.entity.CountryDomain;
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain;
import com.whitbread.premierinn.editguest.EditGuestActivity;
import com.whitbread.premierinn.editguest.EditGuestInput;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;
import com.whitbread.premierinn.hoteldetails.DonationsInput;
import com.whitbread.premierinn.landing.LandingActivityIntent;
import com.whitbread.premierinn.paymentbreakdown.PaymentBreakdownActivity;
import com.whitbread.premierinn.paymentbreakdown.PaymentBreakdownInput;
import com.whitbread.premierinn.paymentdetails.PaymentDetailsInput;
import com.whitbread.premierinn.reviewbooking.view.DonationView;
import com.whitbread.premierinn.threeCp.ThreeCpActivity;
import com.whitbread.premierinn.threeCp.ThreeCpCustomTabActivity;
import com.whitbread.premierinn.threeCp.ThreeCpInput;

import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;


public class ReviewBookingViewContainer extends ViewContainer implements ReviewBookingPresenter.View {

    private final ActivityReviewBookingBinding binding;
    private final ViewTotalPriceBinding mergedTotalPriceBinding;
    private final Observable<Boolean> cardVerificationOkOpera;
    private final Observable<ReviewBookingInput> inputUpdate;
    private final Observable<EditGuestInput> guestDetailsUpdate;
    private Observable<Address> paymentAddressUpdate;
    private ValueAnimator animator;

    private PayPalClient payPalClient;
    private PayPalDataCollector payPalDataCollector;

    private String deviceDataForPayPal;

    private int originalConfirmButtonWidth;
    private int originalConfirmButtonHeight;

    public ReviewBookingViewContainer(@NonNull BaseActivity activity,
                                      ActivityReviewBookingBinding binding,
                                      @NonNull Observable<Boolean> cardVerificationOkOpera,
                                      @NonNull Observable<ReviewBookingInput> inputUpdate,
                                      @NonNull Observable<EditGuestInput> guestDetailsInput,
                                      @NonNull Observable<Address> paymentAddress) {
        super(activity);
        this.binding = binding;
        activity.setToolbar(getActivity().getString(R.string.review_booking_title), true);
        this.mergedTotalPriceBinding = ViewTotalPriceBinding.bind(binding.getRoot());
        this.cardVerificationOkOpera = cardVerificationOkOpera;
        this.inputUpdate = inputUpdate;
        this.guestDetailsUpdate = guestDetailsInput;
        this.paymentAddressUpdate = paymentAddress;

        getActivity().setKeyboardVisibilityListener(binding.reviewBookingRootContainer);

        binding.reviewBookingTermsAndConditionsLabel.setText(HtmlUtils
                .parseTags((getActivity().getString(R.string.review_booking_terms_and_conditions_label_html,
                 getActivity().getString(R.string.terms_conditions_web_url)))));
        binding.reviewBookingTermsAndConditionsLabel.setMovementMethod(LinkMovementMethod.getInstance());

        binding.reviewBookingDonationView.enableAnimation(binding.viewKonfetti);
        setupTotalAmountAnimator();
        initConfirmButtonDimensions();
    }

    @Override
    public Context getContext() {
        return getActivity().getBaseContext();
    }

    private void setupTotalAmountAnimator() {
        final float originalTextSize = mergedTotalPriceBinding.totalBookingPrice.getTextSize();
        final float maxTextSize = originalTextSize * 1.5f;
        animator = ValueAnimator.ofFloat(originalTextSize, maxTextSize, originalTextSize);
        long animationDuration = 400; // Milliseconds
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(animationDuration);

        animator.addUpdateListener(valueAnimator -> {
            float textSize = (float) valueAnimator.getAnimatedValue();
            mergedTotalPriceBinding.totalBookingPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        });
    }

    @Override
    public Observable<ReviewBookingInput> onInputUpdated() {
        return inputUpdate;
    }

    @Override
    public Observable<EditGuestInput> onGuestDetailsUpdated() {
        return guestDetailsUpdate;
    }

    @Override
    public Observable<Address> onPaymentAddressPostcodeFinderEntry() {
        return paymentAddressUpdate;
    }

    @Override
    public void showPriceIncludesTaxesAndFeesMessage(boolean show) {
        binding.reviewBookingExpensesTotalPriceContainer.showPriceIncludesTaxesAndFeesMessage(show);
    }

    @Override
    public Observable<Unit> onPaymentBreakdownClick() {
        return RxView.clicks(binding.reviewBookingPaymentBreakdownLabel);
    }

    @Override
    public void startPaymentBreakdownActivity(@NonNull PaymentBreakdownInput paymentBreakdownInput) {
        PaymentBreakdownActivity.start(getActivity(), paymentBreakdownInput);
    }

    @Override
    public void display(@NonNull ReviewBookModel reviewBookModel, @NonNull CardSummaryModel cardSummaryModel,
                        boolean isBusinessCustomer, int numNights, @NonNull String rateName,
                        boolean isHub, @Nullable String formattedBaseRate, @Nullable String promotionCode,
                        @Nullable String promoTag, CompositeDisposable viewCompositeDisposable,
                        List<CountryDomain> listOfCountries, boolean featurePaypal, boolean featureGooglePay) {
        binding.reviewBookingHotelImage.load(reviewBookModel.getHotelImageUrl());
        binding.reviewBookingHotelNameLabel.setText(reviewBookModel.getHotelName());
        String guests = getActivity().getResources().getQuantityString(R.plurals.guests, reviewBookModel.getGuests(),
                reviewBookModel.getGuests());
        String rooms = getActivity().getResources().getQuantityString(R.plurals.rooms, reviewBookModel.getRooms(),
                reviewBookModel.getRooms());
        String datesGuestsRooms = getActivity().getString(R.string.review_booking_dates_guests_rooms,
                reviewBookModel.getArrivalDateFormatted(), reviewBookModel.getDepartureDateFormatted(), guests, rooms);
        binding.reviewBookingDatesGuestsRoomsLabel.setText(datesGuestsRooms);
        binding.reviewBookingPriceLabel.setText(PriceFormat.format(reviewBookModel.getTotalPrice().getAmount(),
                reviewBookModel.getTotalPrice().getCurrency(), reviewBookModel.getDeviceLocaleProvider()));

        // Card Summary
        binding.paymentComponent.loadPaymentContent(reviewBookModel, isBusinessCustomer, viewCompositeDisposable, listOfCountries,
                featurePaypal, featureGooglePay);

        binding.reviewBookingExpensesTotalPriceContainer.setDonation(reviewBookModel.getDonation(),
                reviewBookModel.getDeviceLocaleProvider(), reviewBookModel.getDonationPledgeString());

        //Total Section
        SummaryExpensesView hotelExpenseView = binding.reviewBookingExpensesTotalPriceContainer.getHotelExpenseView();

        String hotelTitle = getActivity().getString(R.string.hotel_stay_label);
        hotelExpenseView.setTitle(hotelTitle);
        binding.reviewBookingExpensesTotalPriceContainer.showFormattedHotelStayDescription(
                numNights, reviewBookModel.getRooms(), reviewBookModel.getAllRoomsAreAccessible());
        hotelExpenseView.setPrice(reviewBookModel.getHotelStayCost());
        hotelExpenseView.setStrikethroughPrice(formattedBaseRate, promotionCode, promoTag);

        TotalPriceView totalPriceView = binding.reviewBookingExpensesTotalPriceContainer.getTotalPriceContainer();
        totalPriceView.setBookingTotal(reviewBookModel.getTotalPrice(), reviewBookModel.getDeviceLocaleProvider());
        totalPriceView.setRateName(rateName);

        binding.reviewBookingAdditionalInformationEditButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra(CHANGE_BUTTON_KEY, true);
            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();
        });
    }

    @Override
    public void setGuestDetails(@NonNull ReviewBookingInput reviewBookingInput) {
        PaymentDetailsInput paymentDetailsInput = reviewBookingInput.paymentDetailsInput();
        List<GuestDetailsFormDataInput> guestData = paymentDetailsInput.guestDetailsList();

        binding.reviewBookingGuestDetailsNameHeader.setText(getActivity()
                .getString(R.string.review_booking_name, paymentDetailsInput.bookerDetails().title(),
                paymentDetailsInput.bookerDetails().firstName(), paymentDetailsInput.bookerDetails().lastName()));
        binding.reviewBookingGuestDetailsEmailHeader.setText(paymentDetailsInput.bookerDetails().email());
        binding.reviewBookingGuestDetailsRooms.setGuestDetailsFormDataInput(guestData);
    }

    @Override
    public void setAdditionalInformation(List<AdditionalInformation> additionalInformation) {

            binding.reviewBookingAdditionalInformationContainer.setVisibility(VISIBLE);
            for (AdditionalInformation item : additionalInformation) {
                TextView question = new TextView(getContext());
                question.setText(item.getQuestion());
                question.setTextAppearance(getContext(), R.style.Body);
                question.setPadding(0, 16, 0, 5);
                binding.reviewBookingAdditionalInformationLl.addView(question);

                TextView answer = new TextView(getContext());
                answer.setText(item.getAnswer());
                answer.setTextAppearance(getContext(), R.style.Body);
                answer.setPadding(0, 5, 0, 30);
                binding.reviewBookingAdditionalInformationLl.addView(answer);
            }
    }

    @Override
    public void setDonation(@NonNull PriceDomain donation,
                            @NonNull PriceDomain totalPrice,
                            boolean animateChange,
                            @NonNull DeviceLocaleProvider deviceLocaleProvider,
                            String donationPledgeString) {
        binding.reviewBookingExpensesTotalPriceContainer.setDonation(donation, deviceLocaleProvider, donationPledgeString);
        binding.reviewBookingPriceLabel.setText(PriceFormat.format(totalPrice.getAmount(),
                totalPrice.getCurrency(), deviceLocaleProvider));
        TotalPriceView totalPriceView = binding.reviewBookingExpensesTotalPriceContainer.getTotalPriceContainer();
        totalPriceView.setBookingTotal(totalPrice, deviceLocaleProvider);
        if (animateChange) {
            animateTotalPriceChange();
        }
    }

    @Override
    public void showBookerAsOnlyGuest() {
        binding.reviewBookingGuestDetailsHeader.setText(R.string.lead_guest_details);
        binding.reviewBookingGuestDetailsLeadGuestLabel.setVisibility(GONE);
        binding.reviewBookingGuestDetailsRooms.setVisibility(GONE);

    }

    @Override
    public void showBookerAsOneOfManyGuests() {
        binding.reviewBookingGuestDetailsHeader.setText(R.string.lead_guest_details);
        binding.reviewBookingGuestDetailsLeadGuestLabel.setVisibility(GONE);
        binding.reviewBookingGuestDetailsNameHeader.setVisibility(GONE);
        binding.reviewBookingGuestDetailsEmailHeader.setVisibility(GONE);
    }

    @Override
    public void hideLeadGuest() {
        binding.reviewBookingGuestDetailsLayout.setVisibility(GONE);
    }

    @Override
    public void showChildrenBreakfastExpense(ReviewBookingInput input, int numberOfNights) {
        binding.reviewBookingExpensesTotalPriceContainer.showChildrenBreakfastExpense(input, numberOfNights);
    }

    @Override
    public Observable<Unit> onGuestDetailsEditClick() {
        return RxView.clicks(binding.reviewBookingGuestDetailsEditButton);
    }

    @Override
    public Observable<Unit> onAdditionalInformationEditClick() {
        return RxView.clicks(binding.reviewBookingAdditionalInformationEditButton);
    }

    @Override
    public void startEditGuestActivity(@NonNull EditGuestInput editGuestInput) {
        EditGuestActivity.startForResult(getActivity(), editGuestInput);
    }

    @Override
    public Observable<Unit> onConfirmBookingClick() {
        return binding.reviewBookingConfirmButton.onClickOnInternetAvailable();
    }

    @Override
    public Observable<Unit> onPayPalButtonClick() {
        return RxView.clicks(binding.reviewBookingPaypalButtonLayout);
    }

    @Override
    public void startMyBookingsActivity(@NonNull String bookingReference,
                                        @Nullable String uuidBookingReference,
                                        @NonNull String bookerEmail,
                                        @Nullable String accountResponse,
                                        String token) {
        getActivity().startActivityForResult(BookingDetailsActivity.createIntent(getActivity(),
                        bookingReference, uuidBookingReference, bookerEmail, null, accountResponse, token,
                        EMPTY_STRING, EMPTY_STRING, false, EMPTY_STRING, EMPTY_STRING),
                BookingDetailsActivity.BOOKING_COMPLETE_REQUEST_CODE);
    }

    @Override
    public void showPaymentError() {
        showLoading(false);
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.review_booking_payment_failed_title)
                .setMessage(R.string.review_booking_payment_failed_description)
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @Override
    public void showPaymentComponentValidationError() {
        binding.scrollViewContainer.smoothScrollTo(0, ((binding.paymentComponent.getHeight() / 5) * 4));
    }

    @Override
    public void showGenericError() {
        showLoading(false);
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.review_booking_booking_failed_title)
                .setMessage(R.string.search_results_error)
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @Override
    public void storageFailedError() {
        showLoading(false);
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.review_booking_error_title)
                .setMessage(R.string.review_booking_storage_failed_description)
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @Override
    public void showMessageandTakeUserBackToPreviousScreen() {
        showLoading(false);
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.generic_error_title)
                .setMessage(R.string.generic_error_message_with_try_again_later)
                .setPositiveButton(R.string.review_booking_error_close, (dialog, i) -> {
                    dialog.dismiss();
                    getActivity().finish();
                })
                .create()
                .show();
    }

    @Override
    public void showMessageWhenPollingStatusIsPendingOrApiDown(String email) {
        showLoading(false);
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.review_booking_error_title)
                .setMessage(getActivity().getString(R.string.review_booking_polling_pending_message, email))
                .setPositiveButton(R.string.review_booking_error_close, (dialog, i) -> {
                    startHomeActivity();
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @Override
    public void showMessageWhenBasketPollingStatusIsPending(String email) {
        showLoading(false);
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.review_booking_error_title)
                .setMessage(getActivity().getString(R.string.review_booking_polling_pending_message, email))
                .setPositiveButton(R.string.review_booking_error_close, (dialog, i) -> {
                    startHomeActivity();
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @Override
    public void showMessageWhenConfirmationSpinnerIsCancelledOrFailed() {
        showLoading(false);
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.review_booking_error_title)
                .setMessage(getActivity().getString(R.string.payment_refund_error))
                .setPositiveButton(R.string.review_booking_error_close, (dialog, i) -> {
                    startHomeActivity();
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    void startHomeActivity() {
        getActivity().startActivity(LandingActivityIntent.INSTANCE.create(getActivity()));
        getActivity().finish();
    }

    @Override
    public void showThreeCPiPageLaunchFailError() {
        showLoading(false);
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.review_booking_iPage_failed_title)
                .setMessage(R.string.search_results_error)
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @Override
    public void showUnexpectedError(@NonNull String error) {
        showLoading(false);
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.review_booking_booking_failed_title)
                .setMessage(error)
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }


    @Override
    public void show3CPaymentFraudCheckError(@NonNull String error, @NonNull String phoneNumber) {
        showLoading(false);
        new AlertDialog.Builder(getActivity(), R.style.PurpleDialog)
                .setTitle(R.string.review_booking_booking_failed_title)
                .setMessage(error)
                .setNegativeButton(android.R.string.ok, (dialog, i) -> {
                    dialog.dismiss();
                })
                .setPositiveButton(R.string.call_us, (dialog, i) -> {
                    makePhoneCall(phoneNumber);
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @Override
    public void loadThreeCpIPage(@NonNull ThreeCpInput input, String selectedPaymentCardType, String paymentTimingSelection) {
        if (Objects.equals(selectedPaymentCardType, PaymentMethodType.GP.name())) {
            getActivity().startActivityForResult(ThreeCpCustomTabActivity.createThreeCpIntentForGPay(getActivity(),
                            input),
                    GOOGLE_PAY_REQUEST);
        } else {
            getActivity().startActivityForResult(ThreeCpActivity.createThreeCpIntent(getActivity(),
                            input, selectedPaymentCardType, paymentTimingSelection),
                    THREE_C_P_REQUEST);
        }
    }

    @Override
    public Observable<Boolean> onCardVerificationOkOpera() {
        return cardVerificationOkOpera;
    }

    @Override
    public void showPrivacyPolicy(@NonNull String htmlContent) {
        binding.reviewBookingPrivacyPolicyLabel.setText(Html.fromHtml(htmlContent));
        binding.reviewBookingPrivacyPolicyLabel.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void showBreakfastExpenseWithOnlyAdultsPaying(DeviceLocaleProvider deviceLocaleProvider,
                                                         List<UpsellItem> selectedUpsellItems, int numNights) {
        binding.reviewBookingExpensesTotalPriceContainer.showBreakfastExpenseWithOnlyAdultsPaying(selectedUpsellItems,
                numNights, deviceLocaleProvider);
    }

    @Override
    public void setOtherExtras(List<ExtrasItemDomain> selectedExtras, DeviceLocaleProvider deviceLocaleProvider) {
        binding.reviewBookingExpensesTotalPriceContainer.setOtherExtras(selectedExtras, deviceLocaleProvider);
    }

    @Override
    public void showLoading(boolean value) {
        binding.reviewBookingConfirmButton.setLoadingState(value);
    }

    public void showPaypalButtonLoading(boolean loadingState) {
        binding.reviewBookingPaypalButtonTxt1.setVisibility(loadingState ? View.GONE : View.VISIBLE);
        binding.reviewBookingPaypalButtonTxt2.setVisibility(loadingState ? View.GONE : View.VISIBLE);
        binding.reviewBookingPaypalButtonImageView.setVisibility(loadingState ? View.GONE : View.VISIBLE);
        binding.reviewBookingPaypalButtonLoading.setVisibility(loadingState ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setPayNowInTotalPriceViewMessage(boolean value) {
        TotalPriceView totalPriceView = binding.reviewBookingExpensesTotalPriceContainer.getTotalPriceContainer();
        totalPriceView.setBookingPriceLabel(value ? getActivity().getString(R.string.review_booking_pay_now_label)
                : getActivity().getString(R.string.review_booking_pay_on_arrival_label));
    }

    public void makePhoneCall(String number) {
        Intent callIntent = IntentUtils.createTelephoneIntent(number);
        if (IntentUtils.checkIntentResolvedActivity(getActivity(), callIntent)) {
            getActivity().startActivity(callIntent);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), R.string.phone_call_action_not_supported, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Observable<DonationView.DonationType> donationButtonClicks() {
        return binding.reviewBookingDonationView.buttonClicks();
    }

    @Override
    public void setUpDonations(DonationsInput input) {
        binding.reviewBookingDonationView.donations().accept(input);
    }

    @Override
    public void setDonationInfo(String donationTitle,
                                String donationImageUrl,
                                String donationDescription) {
        binding.reviewBookingDonationView.setDonationHeading(donationTitle);
        binding.reviewBookingDonationView.setDonationImage(donationImageUrl);
        binding.reviewBookingDonationView.setDonationMessage(HtmlUtils.parseTags(donationDescription).toString());
    }

    @Override
    public void setConfirmButtonText(boolean createAccount) {
        binding.reviewBookingConfirmButton.setText(getActivity().getString(createAccount
                ? R.string.review_booking_confirm_booking_and_create_account
                : R.string.review_booking_confirm_booking));
    }

    @Override
    public void setThreeCpConfirmButtonText(PaymentRadioButtonView selectedPaymentType,
                                            boolean createAccount) {
        if (selectedPaymentType.getPaymentCardDetailsInput() == null) {
            binding.reviewBookingConfirmButton.setText(getActivity().getString(createAccount
                    ? R.string.review_booking_enter_card_details_and_create_account
                    : R.string.review_booking_enter_card_details));
        } else {
            binding.reviewBookingConfirmButton.setText(getActivity().getString(createAccount
                    ? R.string.review_booking_continue_to_final_step_and_create_account
                    : R.string.review_booking_continue_to_final_step));
        }
    }

    private void initConfirmButtonDimensions() {
        originalConfirmButtonWidth = binding.reviewBookingConfirmButton.getLayoutParams().width;
        originalConfirmButtonHeight = binding.reviewBookingConfirmButton.getLayoutParams().height;
    }
    @Override
    public void togglePayPalButton(boolean showPayPalButton) {
        if (showPayPalButton) {
            binding.reviewBookingConfirmButton.setVisibility(View.INVISIBLE);
            binding.reviewBookingConfirmButton.setEnabled(false);
            binding.reviewBookingConfirmButton.getLayoutParams().width = 0;
            binding.reviewBookingConfirmButton.getLayoutParams().height = 0;
            binding.reviewBookingPaypalButtonLayout.setVisibility(View.VISIBLE);
        } else {
            binding.reviewBookingConfirmButton.setVisibility(View.VISIBLE);
            binding.reviewBookingConfirmButton.setEnabled(true);
            binding.reviewBookingConfirmButton.getLayoutParams().width = originalConfirmButtonWidth;
            binding.reviewBookingConfirmButton.getLayoutParams().height = originalConfirmButtonHeight;
            binding.reviewBookingPaypalButtonLayout.setVisibility(GONE);
        }
    }

    @Override
    public void showAmendAndCancellationMsg(@NonNull String message) {
        binding.bookingInfoBanner.setVisibility(VISIBLE);
        binding.bookingInfoBanner.setText(message);
    }

    @Override
    public PublishRelay<PaymentRadioButtonView> onPaymentTypeSelectionChanged() {
        return binding.paymentComponent.getPaymentDetails();
    }

    @Override
    public PublishRelay<PaymentTimingChoice> onPaymentTimingSelectionChange() {
        return binding.paymentComponent.getPaymentTimingRelay();
    }

    @Override
    public void setDonationOptionVisible(boolean isVisible) {
        binding.reviewBookingDonationView.setVisibility(isVisible ? VISIBLE : GONE);
    }

    @Override
    public void animateTotalPriceChange() {
        animator.end();
        animator.start();
    }
    @Override
    public void setPaymentAddressFields(Address address) {
        binding.paymentComponent.updateAddress(address);
    }

    @Override
    public Boolean isPaymentComponentReadyForSubmission() {
        return binding.paymentComponent.isPaymentComponentValid();
    }

    @Override
    public Boolean isBusinessCustomerWithNoCardsAllowed() {
        return binding.paymentComponent.isBusinessUserWithNoCardsAssigned();
    }

    @Override
    public Boolean isDinnerBudgetForSubmission() {
        return binding.paymentComponent.isDinnerBudgetValid();
    }

    @Override
    public PaymentComponentView getPaymentComponent() {
        return binding.paymentComponent;
    }

    @Override
    public void showLoadingSpinner(Boolean show, Boolean forPaypal, String message) {
        loadingSpinnerSetup();
        binding.translucentLoading.translucentConstraintLayout.setVisibility(show ? VISIBLE : GONE);
        binding.translucentLoading.loadingSpinnerInfoText.setVisibility(show ? VISIBLE : GONE);
        binding.translucentLoading.loadingSpinnerInfoText.setText(forPaypal
                ? getActivity().getString(R.string.initiate_paypal_payment_spinner_message) : message);
    }

    public void loadingSpinnerSetup() {
        binding.translucentLoading.translucentConstraintLayout.setClickable(true);
        binding.translucentLoading.translucentConstraintLayout
                .setBackgroundColor(getActivity().getResources().getColor(R.color.base_black_70, null));
        binding.translucentLoading.spinnerBackground.setBackground(null);
        binding.translucentLoading.loadingSpinner.setPadding(0, 0, 0, 0);
        binding.translucentLoading.loadingSpinner.getIndeterminateDrawable().mutate().setColorFilter(
                getActivity().getResources().getColor(R.color.white, null), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void initPaypalClent(String paypalClientToken) {
        BraintreeClient braintreeClient = new BraintreeClient(
                getContext(),
                paypalClientToken,
                BuildConfig.APPLICATION_ID + ".review.braintree");
        payPalClient = new PayPalClient(getActivity(), braintreeClient);
        payPalDataCollector = new PayPalDataCollector(braintreeClient);
    }

    @Override
    public PayPalClient getPayPalClient() {
        return payPalClient;
    }

    @Override
    public void myTokenizePayPalAccountWithVaultMethod() {
        PayPalVaultRequest request = new PayPalVaultRequest();
        request.setBillingAgreementDescription("Your agreement description");
        payPalClient.tokenizePayPalAccount(getActivity(), request);
    }

    @Override
    public void collectDeviceDataForPayPal() {
        payPalDataCollector.collectDeviceData(getActivity(), (deviceData, error) -> {
            if (error != null || deviceData == null) {
                deviceDataForPayPal = "";
            } else {
                deviceDataForPayPal = deviceData;
            }
        });
    }

    @Override
    public void showInfoBoxForBBAllowancesIfApplicable(boolean cnpRequired, boolean dinnerAllowed, boolean carParkingAllowed) {
        if (!cnpRequired || (!dinnerAllowed && !carParkingAllowed)) {
            binding.businessBookerBookingAllowanceInfobox.setVisibility(View.GONE);
            return;
        }

        binding.businessBookerBookingAllowanceInfobox.setText(
                binding.getRoot().getContext().getString(
                        dinnerAllowed && carParkingAllowed
                                ? R.string.review_booking_bb_parking_dinner_allowance
                                : dinnerAllowed
                                ? R.string.review_booking_bb_dinner_allowance
                                : R.string.review_booking_bb_parking_allowance
                )
        );
        binding.businessBookerBookingAllowanceInfobox.setVisibility(View.VISIBLE);
    }

    @Override
    public String getDeviceDataForPayPal() {
        return deviceDataForPayPal;
    }

}