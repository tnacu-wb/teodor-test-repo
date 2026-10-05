package com.whitbread.premierinn.hoteldetails;

import static com.whitbread.premierinn.common.Constants.RESULT_BUSINESS_LOGIN_SUCCESS;
import static com.whitbread.premierinn.summary.SummaryActivityKt.EXTRA_BASKET_REFERENCE_V2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.whitbread.premierinn.calendar.maincalendar.HomeCalendarActivity;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.databinding.ActivityHotelDetailsBinding;
import com.whitbread.premierinn.domain.common.RoomCriteria;
import com.whitbread.premierinn.landing.LandingActivityIntent;
import com.whitbread.premierinn.login.LoginActivity;
import com.whitbread.premierinn.roomcriteria.MappersKt;
import com.whitbread.premierinn.roomcriteria.ParcelableRooms;
import com.whitbread.premierinn.roomcriteria.RoomCriteriaActivity;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.LocalDate;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HotelDetailsActivity extends BasePresenterActivity<HotelDetailsPresenter.View,
        ActivityHotelDetailsBinding, HotelDetailsPresenter> {

    public static final int RESULT_REQUEST_CODE = 555;
    public static final int REQUEST_CODE_SUMMARY_SCREEN = 556;
    public static final int REQUEST_CODE_CALENDAR_SCREEN = 557;
    public static final int REQUEST_CODE_ROOMS_SCREEN = 558;
    static final String HOTEL_DETAILS_INPUT = "hotel_details_input";

    private HotelDetailsViewContainer view;
    @Inject HotelDetailsPresenter presenter;
    @Inject LogService logService;

    public static Intent createIntent(Context context, HotelDetailsInput hotelDetailsInput) {
        Intent intent = new Intent(context, HotelDetailsActivity.class);
        intent.putExtra(HOTEL_DETAILS_INPUT, hotelDetailsInput);
        return intent;
    }

    @NonNull
    @Override
    protected ActivityHotelDetailsBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityHotelDetailsBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view.addCommonSubscriptions(presenter.bindAllCommonViewSubscriptions(view));
        bindAllBookingAvailabilitySubscriptions();
    }

    private void bindAllBookingAvailabilitySubscriptions() {
        view.addBookingSubscriptions(
                presenter.bindAllBookingAvailabilityViewSubscriptions(view)
        );
    }

    private void bindAllSubscriptions() {
        view.addCommonSubscriptions(presenter.bindAllCommonViewSubscriptions(view));
        bindAllBookingAvailabilitySubscriptions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.updatedCustomerLoggedIn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view.clearCommonSubscriptions();
        view.clearBookingSubscriptions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUEST_CODE_SUMMARY_SCREEN:
                if (resultCode == RESULT_OK) {
                    String basketReference = intent.getStringExtra(EXTRA_BASKET_REFERENCE_V2);
                    view.addCommonSubscriptions(presenter.bindReleaseBooking(view, basketReference));
                }
                break;
            case REQUEST_CODE_CALENDAR_SCREEN:
                if (resultCode == RESULT_OK) {
                    LocalDate arrival =
                            (LocalDate) intent.getSerializableExtra(HomeCalendarActivity.CALENDAR_SELECTED_ARRIVAL);
                    LocalDate departure =
                            (LocalDate) intent.getSerializableExtra(HomeCalendarActivity.CALENDAR_SELECTED_DEPARTURE);
                    view.addCommonSubscriptions(presenter.bindHotelAvailability(view, arrival, departure, null));
                }
                break;
            case REQUEST_CODE_ROOMS_SCREEN:
                if (resultCode == RESULT_OK) {
                    ParcelableRooms parcelableRooms = intent.getParcelableExtra(RoomCriteriaActivity.ROOM_CRITERIA_SELECTION);
                    List<RoomCriteria> roomsCriteria = MappersKt.toRoomCriteriaList(parcelableRooms);
                    view.addCommonSubscriptions(presenter.bindHotelAvailability(view, null, null, roomsCriteria));
                }
                break;
            case LoginActivity.ACTIVITY_RESULT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                   view.startGuestDetailsActivity(presenter.getBookingFlowInput());
                } else if (resultCode == RESULT_BUSINESS_LOGIN_SUCCESS) {
                    startActivity(LandingActivityIntent.INSTANCE.create(this));
                    finish();
                } else if (resultCode == RESULT_FIRST_USER) {
                    view.startGuestDetailsActivity(presenter.getBookingFlowInput());
                }
            default:
        }
    }

    public void reBindBookingAvailabilitySubscriptions() {
        view.clearBookingSubscriptions();
        bindAllBookingAvailabilitySubscriptions();
    }

    public void rebindAllSubscriptions() {
        view.clearCommonSubscriptions();
        view.clearBookingSubscriptions();
        bindAllSubscriptions();
    }

    @Override
    protected HotelDetailsPresenter.@NotNull View provideView() {
        view = new HotelDetailsViewContainer(this, binding, logService);
        return view;
    }

    @Override
    protected @NotNull HotelDetailsPresenter createPresenter() {
        presenter.initParams(Objects.requireNonNull(getIntent().getParcelableExtra(HOTEL_DETAILS_INPUT)));
        return presenter;
    }
}