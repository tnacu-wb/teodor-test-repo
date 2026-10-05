package com.whitbread.premierinn.findbooking;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.TaskStackBuilder;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.jakewharton.rxrelay2.Relay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.bookingdetails.BookingDetailsActivity;
import com.whitbread.premierinn.calendar.dialogcalendar.CalendarDialogActivity;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.format.DateFormat;
import com.whitbread.premierinn.common.format.FormatExtensionsKt;
import com.whitbread.premierinn.common.utils.StringUtils;
import com.whitbread.premierinn.databinding.ActivityFindBookingBinding;
import com.whitbread.premierinn.mybookings.MyBookingsActivity;

import org.threeten.bp.LocalDate;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import kotlin.Unit;

public class FindBookingViewContainer implements FindBookingPresenter.View {

    private final Relay<LocalDate> calendarDayRelay;
    private ActivityFindBookingBinding binding;
    private BaseActivity activity;

    public FindBookingViewContainer(@NonNull BaseActivity activity,
                                    @NonNull Relay<LocalDate> calendarDayRelay,
                                    ActivityFindBookingBinding binding) {
        this.activity = activity;
        this.calendarDayRelay = calendarDayRelay;
        this.binding = binding;
        activity.setToolbar(activity.getString(R.string.find_booking), true);
        activity.setKeyboardVisibilityListener(binding.llFindBookingRoot);
    }

    @Override
    public Observable<Unit> onClickArrivalDate() {
        return RxView.clicks(binding.etFindBookingArrivalDate).throttleFirst(1, TimeUnit.SECONDS);
    }

    @Override
    public void startCalendarActivity(@NonNull LocalDate rangeStartDate, @NonNull LocalDate selectedDate) {
        activity.startActivityForResult(CalendarDialogActivity.createIntent(activity, rangeStartDate, selectedDate),
                CalendarDialogActivity.ACTIVITY_RESULT_REQUEST_CODE);
    }

    @Override
    public Observable<LocalDate> onArrivalDateSelected() {
        return calendarDayRelay;
    }

    @Override
    public void showArrivalDate(@NonNull LocalDate selectedDate) {
        binding.etFindBookingArrivalDate.setText(FormatExtensionsKt.format(selectedDate, DateFormat.SLASHED_DAY_MONTH_YEAR));
    }

    @Override
    public Observable<Unit> onFindBookingClick() {
        return RxView.clicks(binding.cabFindBookingFindBooking);
    }

    @Override
    public Observable<String> onReferenceBookingChanged() {
        return RxTextView.textChanges(activity.findViewById(R.id.et_find_booking_booking_reference)).map(CharSequence::toString);
    }

    @Override
    public Observable<String> onLastNameChanged() {
        return RxTextView.textChanges(activity.findViewById(R.id.et_find_booking_last_name)).map(CharSequence::toString);
    }

    @Override
    public void showLoading(boolean show) {
        binding.cabFindBookingFindBooking.setLoadingState(show);
    }

    @Override
    public void showErrorFetchingBooking() {
        binding.rlFindBookingErrorGetBooking.setVisibility(View.VISIBLE);
        binding.findBookingGetBookingErrorText.setText(R.string.find_booking_error_get_booking);
    }

    @Override
    public void showErrorAlreadyImportedBooking() {
        binding.rlFindBookingErrorGetBooking.setVisibility(View.VISIBLE);
        binding.findBookingGetBookingErrorText.setText(R.string.find_booking_error_already_imported);
    }

    @Override
    public void showErrorPastBookingImportAttempt() {
        binding.rlFindBookingErrorGetBooking.setVisibility(View.VISIBLE);
        binding.findBookingGetBookingErrorText.setText(R.string.find_booking_error_past_booking);
    }

    @Override
    public void showErrorUnableToRetrieveBooking() {
        binding.rlFindBookingErrorGetBooking.setVisibility(View.VISIBLE);
        binding.findBookingGetBookingErrorText.setText(R.string.find_booking_error_unable_to_retrieve_booking);
    }

    @Override
    public void showBusinessBookingImportError() {
        binding.rlFindBookingErrorGetBooking.setVisibility(View.VISIBLE);
        binding.findBookingGetBookingErrorText.setText(R.string.find_booking_error_get_business_booking);
    }

    @Override
    public void goToBookingsScreen(boolean isDeeplinkedActivity) {
        if (isDeeplinkedActivity) {
            activity.startActivity(MyBookingsActivity.createIntent(activity, true));
        } else {
            activity.setResult(Activity.RESULT_OK, new Intent());
        }
        activity.finish();
    }


    @Override
    public void goToBookingDetailsScreen(@NonNull String bookingReference,
                                         @NonNull String lastName,
                                         @NonNull LocalDate arrivalDate,
                                         @NonNull String uuidBasketReference,
                                         @NonNull String token,
                                         boolean isDeeplinkedActivity) {
        if (isDeeplinkedActivity) {
            TaskStackBuilder.create(activity)
                    .addNextIntent(
                            MyBookingsActivity.createIntent(activity, true)
                    )
                    .addNextIntent(
                            BookingDetailsActivity.createIntent(activity, bookingReference,
                                    uuidBasketReference, null, null,
                                    null, token, arrivalDate.toString(), lastName,
                                    false, "", "", true)
                    )
                    .startActivities();
        } else {
            activity.startActivity(BookingDetailsActivity.createIntent(activity, bookingReference,
                    uuidBasketReference, null, null, null,
                    token, arrivalDate.toString(), lastName, false, "", "", true)
            );
        }
        activity.finish();
    }

    @Override
    public void showBookingRefError(boolean show) {
        binding.tilFindBookingBookingReference.setError(activity.getString(R.string.find_booking_error_booking_ref));
        binding.tilFindBookingBookingReference.setErrorEnabled(show);
    }

    @Override
    public void showLastNameError(boolean show) {
        binding.tilFindBookingLastName.setError(activity.getString(R.string.find_booking_error_last_name));
        binding.tilFindBookingLastName.setErrorEnabled(show);
    }

    @Override
    public void showArrivalDateError(boolean show) {
        binding.tilFindBookingArrivalDate.setError(activity.getString(R.string.find_booking_arrival_date));
        binding.tilFindBookingArrivalDate.setErrorEnabled(show);
    }

    @Override
    public void updateScreenFields(@NonNull FindBookingInput findBookingInput) {
        String date = findBookingInput.getArrivalDate();
        String displayDate = StringUtils.EMPTY_STRING;
        if (!date.isBlank()) {
            try {
                LocalDate localDate = FormatExtensionsKt.toLocalDate(date);
                displayDate = FormatExtensionsKt.format(localDate, DateFormat.SLASHED_DAY_MONTH_YEAR);
            } catch (Exception ignored) {
            }

            binding.etFindBookingArrivalDate.setText(displayDate);
        }
        binding.etFindBookingBookingReference.setText(findBookingInput.getBookingReference());
            binding.etFindBookingLastName.setText(findBookingInput.getLastName());
    }
}
