package com.whitbread.premierinn.calendar.dialogcalendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.DialogCalendarBinding;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.LocalDate;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CalendarDialogActivity extends BasePresenterActivity<CalendarPresenter.View, DialogCalendarBinding, CalendarPresenter> {

    public static final String CALENDAR_DIALOG_INPUT = "calendar_dialog_input";
    public static final int ACTIVITY_RESULT_REQUEST_CODE = 1489;
    public static final String CHOSEN_DATE = "chosen_date";

    @Inject
    CalendarPresenter presenter;

    public static Intent createIntent(@NonNull Context context, @NonNull LocalDate rangeStartDate, @NonNull LocalDate selectedDate) {
        Intent intent = new Intent(context, CalendarDialogActivity.class);
        return intent.putExtra(CALENDAR_DIALOG_INPUT, CalendarDialogInput.create(rangeStartDate, selectedDate));
    }

    @NonNull
    @Override
    protected DialogCalendarBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return DialogCalendarBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected CalendarPresenter.@NotNull View provideView() {
        return new CalendarViewContainer(this,
                ContextCompat.getDrawable(this, R.drawable.ic_calendar_left),
                ContextCompat.getDrawable(this, R.drawable.ic_calendar_right), binding);
    }

    @Override
    protected @NotNull CalendarPresenter createPresenter() {
        presenter.initParams(Objects.requireNonNull(getIntent().getParcelableExtra(CALENDAR_DIALOG_INPUT)));
        return presenter;
    }
}
