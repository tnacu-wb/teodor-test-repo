package com.whitbread.premierinn.reviewbooking;

import static com.whitbread.premierinn.common.Constants.EXTRA_CARD_TYPE;
import static com.whitbread.premierinn.common.Constants.EXTRA_THREE_C_P;
import static com.whitbread.premierinn.common.Constants.PAYMENT_SUCCESS;
import static com.whitbread.premierinn.threeCp.ThreeCpCustomTabActivityKt.EXTRA_GPAY_RESP;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.bookingdetails.BookingDetailsActivity;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivityReviewBookingBinding;
import com.whitbread.premierinn.domain.common.Address;
import com.whitbread.premierinn.editguest.EditGuestActivity;
import com.whitbread.premierinn.editguest.EditGuestInput;
import com.whitbread.premierinn.mybookings.MyBookingsActivity;
import com.whitbread.premierinn.postcodefinder.ParcelableAddress;
import com.whitbread.premierinn.postcodefinder.ParcelableMappersKt;
import com.whitbread.premierinn.postcodefinder.PostcodeFinderActivity;

import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ReviewBookActivity extends BasePresenterActivity<ReviewBookingPresenter.View,
        ActivityReviewBookingBinding, ReviewBookingPresenter> {

    public static final int THREE_C_P_REQUEST = 807;
    public static final int GOOGLE_PAY_REQUEST = 919;
    private static final String REVIEW_BOOKING_INPUT = "review_booking_input";
    @Inject
    ReviewBookingPresenter reviewBookingPresenter;
    private PublishRelay<Boolean> cardVerificationOkOperaRelay = PublishRelay.create();
    private BehaviorRelay<ReviewBookingInput> inputUpdateRelay = BehaviorRelay.create();
    private PublishRelay<EditGuestInput> guestDetailsUpdateRelay = PublishRelay.create();
    private BehaviorRelay<Address> paymentAddressUpdatedRelay = BehaviorRelay.create();

    public static Intent createIntent(@NonNull Context context,
                                      @NonNull ReviewBookingInput reviewBookingInput) {
        Intent starter = new Intent(context, ReviewBookActivity.class);
        starter.putExtra(REVIEW_BOOKING_INPUT, reviewBookingInput);

        return starter;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @NonNull
    @Override
    protected ActivityReviewBookingBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityReviewBookingBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ReviewBookingInput reviewBookingInput = getIntent().getParcelableExtra(REVIEW_BOOKING_INPUT);

        if (reviewBookingInput == null) {
            super.onCreate(savedInstanceState);
            Toast.makeText(this, R.string.generic_error_message_with_try_again, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        inputUpdateRelay.accept(reviewBookingInput);
        super.onCreate(savedInstanceState);
    }
    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.toolbar_privacy_policy_badge, menu);
            return true;
        }

        @Override
        protected void onResume() {
            reviewBookingPresenter.clearPollingDisposables();
            super.onResume();
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            if (requestCode == BookingDetailsActivity.BOOKING_COMPLETE_REQUEST_CODE) {
                startActivity(MyBookingsActivity.createIntentClearTask(this));
            }
            // TODO Show an error to user here if not handled previously
            return;
        }

        if (data != null) {
            String gPayData = data.getStringExtra(EXTRA_GPAY_RESP);
            if (gPayData != null) {
                cardVerificationOkOperaRelay.accept(true);
            }

            String paymentCardType = data.getStringExtra(EXTRA_CARD_TYPE);
            if (paymentCardType != null) {
                reviewBookingPresenter.setPaymentCardType(paymentCardType);
            }

            switch (requestCode) {
                case EditGuestActivity.EDIT_GUEST_REQUEST_CODE:
                    EditGuestInput editGuestInput = data.getParcelableExtra(EditGuestActivity.EDIT_GUEST_INPUT_KEY);
                    guestDetailsUpdateRelay.accept(editGuestInput);
                    break;
                case THREE_C_P_REQUEST:
                    String success = data.getStringExtra(EXTRA_THREE_C_P);
                    if (success != null && success.equals(PAYMENT_SUCCESS)) {
                        cardVerificationOkOperaRelay.accept(true);
                    }
                    break;
                case PostcodeFinderActivity.POSTCODE_ADDRESS_RESULT_REQUEST_CODE:
                    ParcelableAddress postcodeAddressSelected =
                            data.getParcelableExtra(PostcodeFinderActivity.SELECTED_POSTCODE_ADDRESS_KEY);
                    if (postcodeAddressSelected != null) {
                        paymentAddressUpdatedRelay.accept(ParcelableMappersKt.toAddress(postcodeAddressSelected));
                    }
                default:
            }
        }
    }

    @Override
    protected ReviewBookingPresenter.@NotNull View provideView() {
        return new ReviewBookingViewContainer(this, binding,
                cardVerificationOkOperaRelay, inputUpdateRelay,
                guestDetailsUpdateRelay, paymentAddressUpdatedRelay);
    }

    @Override
    protected @NotNull ReviewBookingPresenter createPresenter() {
        return reviewBookingPresenter;
    }
}
