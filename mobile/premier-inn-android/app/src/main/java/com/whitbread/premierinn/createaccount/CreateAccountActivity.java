package com.whitbread.premierinn.createaccount;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.databinding.ActivityCreateAccountBinding;
import com.whitbread.premierinn.postcodefinder.ParcelableAddress;
import com.whitbread.premierinn.postcodefinder.PostcodeFinderActivity;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreateAccountActivity extends BasePresenterActivity<CreateAccountPresenter.View,
        ActivityCreateAccountBinding, CreateAccountPresenter> {

    public static final int ACTIVITY_RESULT_REQUEST_CODE = 789;
    private final BehaviorRelay<ParcelableAddress> postcodeAddressRelay = BehaviorRelay.create();

    @Inject
    CreateAccountPresenter presenter;

    @Inject
    DeviceLocaleProvider deviceLocaleProvider;

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, CreateAccountActivity.class);
    }

    @NonNull
    @Override
    protected ActivityCreateAccountBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityCreateAccountBinding.inflate(inflater);
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
                    postcodeAddressRelay.accept(postcodeAddressSelected);
                }
            }
        }
    }

    @Override
    protected CreateAccountPresenter.@NotNull View provideView() {
        return new CreateAccountViewContainer(this, postcodeAddressRelay, binding);
    }

    @Override
    protected @NotNull CreateAccountPresenter createPresenter() {
        return presenter;
    }
}
