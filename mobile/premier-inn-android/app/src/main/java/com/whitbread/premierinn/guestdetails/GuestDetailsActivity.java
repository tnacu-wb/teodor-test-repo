package com.whitbread.premierinn.guestdetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.BookingFlowInput;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.databinding.ActivityGuestDetailsBinding;
import com.whitbread.premierinn.postcodefinder.ParcelableAddress;
import com.whitbread.premierinn.postcodefinder.PostcodeFinderActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GuestDetailsActivity extends BasePresenterActivity<GuestDetailsPresenter.View,
        ActivityGuestDetailsBinding, GuestDetailsPresenter> {

    private static final String EXTRA_BOOKING_FLOW_INPUT = "EXTRA_BOOKING_FLOW_INPUT";

    @Inject
    GuestDetailsPresenter guestDetailsPresenter;

    @Inject
    DeviceLocaleProvider deviceLocaleProvider;

    public static Intent createIntent(@NonNull Context context, @NonNull BookingFlowInput input) {
        Intent starter = new Intent(context, GuestDetailsActivity.class);
        starter.putExtra(EXTRA_BOOKING_FLOW_INPUT, input);
        return starter;
    }

    @NonNull
    @Override
    protected ActivityGuestDetailsBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityGuestDetailsBinding.inflate(inflater);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PostcodeFinderActivity.POSTCODE_ADDRESS_RESULT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ParcelableAddress postcodeAddressSelected = data.getParcelableExtra(PostcodeFinderActivity.SELECTED_POSTCODE_ADDRESS_KEY);
                if (postcodeAddressSelected != null) {
                    guestDetailsPresenter.displayAddress(postcodeAddressSelected);
                }
            }
        }
    }

    @Override
    protected GuestDetailsPresenter.@NotNull View provideView() {
        return new GuestDetailsViewContainer(this, deviceLocaleProvider, binding);
    }

    @Override
    protected @NotNull GuestDetailsPresenter createPresenter() {
        guestDetailsPresenter.initParams(Objects.requireNonNull(getIntent().getParcelableExtra(EXTRA_BOOKING_FLOW_INPUT)));
        return guestDetailsPresenter;
    }
}
