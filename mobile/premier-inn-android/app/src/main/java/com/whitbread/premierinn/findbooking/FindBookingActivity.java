package com.whitbread.premierinn.findbooking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.Relay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.calendar.dialogcalendar.CalendarDialogActivity;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivityFindBookingBinding;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.LocalDate;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FindBookingActivity extends BasePresenterActivity<FindBookingPresenter.View,
        ActivityFindBookingBinding, FindBookingPresenter> {

    public static final int ACTIVITY_RESULT_REQUEST_CODE = 123;
    public static final String FIND_BOOKING_INPUT = "find_booking_input";

    @Inject
    FindBookingPresenter presenter;

    private final Relay<LocalDate> calendarDayRelay = BehaviorRelay.create();

    public static Intent createIntent(@NonNull Context context, FindBookingInput findBookingInput) {
        Intent intent = new Intent(context, FindBookingActivity.class);
        intent.putExtra(FIND_BOOKING_INPUT, findBookingInput);
        return intent;
    }

    @NonNull
    @Override
    protected ActivityFindBookingBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityFindBookingBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_privacy_policy_badge, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == CalendarDialogActivity.ACTIVITY_RESULT_REQUEST_CODE && resultCode == RESULT_OK
                && intent != null && intent.getSerializableExtra(CalendarDialogActivity.CHOSEN_DATE) != null) {
            calendarDayRelay.accept((LocalDate) intent.getSerializableExtra(CalendarDialogActivity.CHOSEN_DATE));
        }
    }

    @Override
    protected FindBookingPresenter.@NotNull View provideView() {
        return new FindBookingViewContainer(this, calendarDayRelay, binding);
    }

    @Override
    protected @NotNull FindBookingPresenter createPresenter() {
        presenter.initParams(getIntent().getParcelableExtra(FIND_BOOKING_INPUT));
        return presenter;
    }

}
