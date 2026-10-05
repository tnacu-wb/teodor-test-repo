package com.whitbread.premierinn.mybookings;

import static com.whitbread.premierinn.ciol.CheckInOnlineActivityKt.CHECK_IN_ONLINE_ACTIVITY_REQUEST_KEY;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.bottomnavigation.BottomNavigationActivity;
import com.whitbread.premierinn.databinding.ActivityMyBookingsBinding;
import com.whitbread.premierinn.findbooking.FindBookingActivity;
import com.whitbread.premierinn.login.LoginActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyBookingsActivity extends BottomNavigationActivity<ActivityMyBookingsBinding> {

    public static final int BOOKING_DETAILS_REQUEST_CODE = 150;
    public static final String IS_DEEPLINKED_ACTIVITY = "is_deeplinked_activity";

    @Inject
    MyBookingsPresenter presenter;

    private final Relay<Object> bookingCanceledRelay = PublishRelay.create();
    private final Relay<Object> importedBookingRelay = PublishRelay.create();
    private final PublishRelay<Object> loginRelay = PublishRelay.create();

    private MyBookingsViewContainer myBookingsViewContainer;

    public static Intent createIntent(@NonNull Context context, boolean isDeeplinkedActivity) {
        Intent intent = new Intent(context, MyBookingsActivity.class);
        intent.putExtra(IS_DEEPLINKED_ACTIVITY, isDeeplinkedActivity);
        return intent;
    }

    public static Intent createIntentClearTask(@NonNull Context context) {
        Intent intent = new Intent(context, MyBookingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @NonNull
    @Override
    protected ActivityMyBookingsBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityMyBookingsBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isDeeplinkedActivity = getIntent().getBooleanExtra(IS_DEEPLINKED_ACTIVITY, false);

        myBookingsViewContainer = new MyBookingsViewContainer(this,
                bookingCanceledRelay, importedBookingRelay, loginRelay, binding);

        if (isDeeplinkedActivity) {
            importedBookingRelay.accept(new Object());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case BOOKING_DETAILS_REQUEST_CODE:
                    bookingCanceledRelay.accept(new Object());
                    break;
                case FindBookingActivity.ACTIVITY_RESULT_REQUEST_CODE:
                    importedBookingRelay.accept(new Object());
                    break;
                case LoginActivity.ACTIVITY_RESULT_REQUEST_CODE:
                    loginRelay.accept(new Object());
                    break;
                default:
                    // Nothing to do
            }
        }
        if (requestCode == CHECK_IN_ONLINE_ACTIVITY_REQUEST_KEY) {
            recreate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onDetachView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attachView(myBookingsViewContainer);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Bottom Navigation View
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected int getSelectedMenuItem() {
        return R.id.bottom_navigation_my_bookings;
    }
}
