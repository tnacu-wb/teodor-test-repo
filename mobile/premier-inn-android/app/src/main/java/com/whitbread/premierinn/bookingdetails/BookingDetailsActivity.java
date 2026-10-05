package com.whitbread.premierinn.bookingdetails;

import static com.whitbread.premierinn.ciol.CheckInOnlineActivityKt.CHECK_IN_ONLINE_ACTIVITY_REQUEST_KEY;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.amend.amendguestsrooms.ParcelableAmendTotal;
import com.whitbread.premierinn.ciol.CheckOutConfirmationActivity;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivityBookingDetailsBinding;
import com.whitbread.premierinn.mybookings.MyBookingsActivity;

import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookingDetailsActivity extends
        BasePresenterActivity<BookingDetailsPresenter.View, ActivityBookingDetailsBinding, BookingDetailsPresenter> {

    private static final String BOOKING_DETAILS_REF_KEY = "BOOKING_DETAILS_REF_KEY";
    private static final String UUID_BASKET_REF_KEY = "UUID_BASKET_REF_KEY";
    private static final String OPERA_TOKEN = "OPERA_CANCEL_BOOKING_TOKEN";
    private static final String BOOKING_DETAILS_EMAIL_KEY = "BOOKING_DETAILS_EMAIL_KEY";
    private static final String BOOKING_DETAILS_PRICE = "BOOKING_DETAILS_PRICE";
    private static final String BOOKING_DETAILS_ACCOUNT_RESPONSE_KEY = "BOOKING_DETAILS_ACCOUNT_RESPONSE_KEY";
    private static final String BOOKING_DETAILS_LAST_NAME_KEY = "BOOKING_DETAILS_LAST_NAME_KEY";
    private static final String BOOKING_DETAILS_ARRIVAL_DATE_KEY = "BOOKING_DETAILS_ARRIVAL_DATE_KEY";
    private static final String BOOKING_DETAILS_PUSH_TRIGGER_KEY = "BOOKING_DETAILS_PUSH_TRIGGER_KEY";
    private static final String BOOKING_DETAILS_PUSH_TYPE = "BOOKING_DETAILS_PUSH_TYPE";
    private static final String BOOKING_DETAILS_TRACKING_CODE = "BOOKING_DETAILS_TRACKING_CODE";
    private static final String SHOW_IMPORT_SUCCESS_MESSAGE = "SHOW_IMPORT_SUCCESS_MESSAGE";
    public static final int BOOKING_COMPLETE_REQUEST_CODE = 200;
    public static final int CANCEL_BOOKING_REQUEST = 300;
    public static final int AMEND_BOOKING_REQUEST = 400;
    public static final int NON_AMEND_BOOKING_REQUEST = 500;

    @Inject
    BookingDetailsPresenter presenter;

    public static Intent createIntent(@NonNull Context context,
                                      @NonNull String bookingReference,
                                      @Nullable String uuidBasketReference,
                                      @Nullable String emailAddress,
                                      @Nullable ParcelableAmendTotal amendedTotal,
                                      @Nullable String accountResponse,
                                      String token,
                                      String arrivalDate,
                                      String lastName,
                                      boolean isPushTrigger,
                                      String pushType,
                                      String trackingCode) {
        return createIntent(context, bookingReference, uuidBasketReference, emailAddress,
                amendedTotal, accountResponse, token, arrivalDate, lastName, isPushTrigger, pushType, trackingCode, false);
    }

    public static Intent createIntent(@NonNull Context context,
                                      @NonNull String bookingReference,
                                      @Nullable String uuidBasketReference,
                                      @Nullable String emailAddress,
                                      @Nullable ParcelableAmendTotal amendedTotal,
                                      @Nullable String accountResponse,
                                      String token,
                                      String arrivalDate,
                                      String lastName,
                                      boolean isPushTrigger,
                                      String pushType,
                                      String trackingCode,
                                      boolean showImportSuccessMessage) {
        Intent starter = new Intent(context, BookingDetailsActivity.class);
        starter.putExtra(BOOKING_DETAILS_REF_KEY, bookingReference);
        starter.putExtra(UUID_BASKET_REF_KEY, uuidBasketReference != null ? uuidBasketReference : "");
        starter.putExtra(BOOKING_DETAILS_EMAIL_KEY, emailAddress);
        starter.putExtra(BOOKING_DETAILS_PRICE, amendedTotal);
        starter.putExtra(BOOKING_DETAILS_ACCOUNT_RESPONSE_KEY, accountResponse);
        starter.putExtra(OPERA_TOKEN, token != null ? token : "");
        starter.putExtra(BOOKING_DETAILS_LAST_NAME_KEY, lastName);
        starter.putExtra(BOOKING_DETAILS_ARRIVAL_DATE_KEY, arrivalDate);
        starter.putExtra(BOOKING_DETAILS_PUSH_TRIGGER_KEY, isPushTrigger);
        starter.putExtra(BOOKING_DETAILS_PUSH_TYPE, pushType);
        starter.putExtra(BOOKING_DETAILS_TRACKING_CODE, trackingCode);
        starter.putExtra(SHOW_IMPORT_SUCCESS_MESSAGE, showImportSuccessMessage);

        return starter;
    }

    @NonNull
    @Override
    protected ActivityBookingDetailsBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityBookingDetailsBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra(SHOW_IMPORT_SUCCESS_MESSAGE, false)) {
            Toast.makeText(this, getString(R.string.my_bookings_success_import), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_privacy_policy_badge, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CANCEL_BOOKING_REQUEST, AMEND_BOOKING_REQUEST, NON_AMEND_BOOKING_REQUEST:
                    presenter.scrollToBanner();
                    reattachPresenterWithFreshData();
                    break;
                default:
                    break;
            }
        }
        if (requestCode == CHECK_IN_ONLINE_ACTIVITY_REQUEST_KEY || requestCode == CheckOutConfirmationActivity.REQUEST_CODE) {
            // Check-in/out completed, reattach presenter to fetch fresh data
            reattachPresenterWithFreshData();
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onBackPressed() {
        startActivity(MyBookingsActivity.createIntent(this, false));
        finish();
        super.onBackPressed();
    }

    private void reattachPresenterWithFreshData() {
        if (presenter != null) {
            presenter.refreshBookingData();
            presenter.attachView(provideView());
        }
    }

    @Override
    protected BookingDetailsPresenter.@NotNull View provideView() {
        return new BookingDetailsViewContainer(this, binding);
    }

    @Override
    protected @NotNull BookingDetailsPresenter createPresenter() {
        presenter.initParams(
                getIntent().getStringExtra(BOOKING_DETAILS_REF_KEY),
                getIntent().getStringExtra(UUID_BASKET_REF_KEY),
                getIntent().getStringExtra(OPERA_TOKEN),
                getIntent().getStringExtra(BOOKING_DETAILS_LAST_NAME_KEY),
                getIntent().getStringExtra(BOOKING_DETAILS_ARRIVAL_DATE_KEY),
                getIntent().getBooleanExtra(BOOKING_DETAILS_PUSH_TRIGGER_KEY, false),
                getIntent().getStringExtra(BOOKING_DETAILS_PUSH_TYPE),
                getIntent().getStringExtra(BOOKING_DETAILS_EMAIL_KEY),
                getIntent().getStringExtra(BOOKING_DETAILS_ACCOUNT_RESPONSE_KEY),
                getIntent().getParcelableExtra(BOOKING_DETAILS_PRICE),
                getIntent().getStringExtra(BOOKING_DETAILS_TRACKING_CODE)
        );
        return presenter;
    }
}