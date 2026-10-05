package com.whitbread.premierinn.bookingpreferences;

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
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivityBookingPreferencesBinding;
import com.whitbread.premierinn.mealpreferences.MealPreferencesActivity;
import com.whitbread.premierinn.roompreferences.RoomPreferencesActivity;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BookingPreferencesActivity extends BasePresenterActivity<BookingPreferencesPresenter.View,
        ActivityBookingPreferencesBinding, BookingPreferencesPresenter> {

    @Inject
    BookingPreferencesPresenter presenter;
    private final Relay<Object> bookingPrefChangeRelay = BehaviorRelay.create();

    public static Intent createIntent(Context context) {
        return new Intent(context, BookingPreferencesActivity.class);
    }

    @NonNull
    @Override
    protected ActivityBookingPreferencesBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityBookingPreferencesBinding.inflate(inflater);
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
        if (resultCode == RESULT_OK && (requestCode == MealPreferencesActivity.MEAL_PREFERENCES_RESULT_KEY
                || requestCode == RoomPreferencesActivity.ROOM_PREFERENCES_RESULT_KEY)) {
            bookingPrefChangeRelay.accept(new Object());
        }
    }

    @Override
    protected BookingPreferencesPresenter.@NotNull View provideView() {
        return new BookingPreferencesViewContainer(this, bookingPrefChangeRelay, binding);
    }

    @Override
    protected @NotNull BookingPreferencesPresenter createPresenter() {
        return presenter;
    }
}