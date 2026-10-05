package com.whitbread.premierinn.personaldetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.customer.MappersKt;
import com.whitbread.premierinn.api.response.customer.ParcelableCustomer;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.databinding.ActivityPersonalDetailsBinding;
import com.whitbread.premierinn.postcodefinder.ParcelableAddress;
import com.whitbread.premierinn.postcodefinder.PostcodeFinderActivity;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PersonalDetailsActivity extends BaseActivity<ActivityPersonalDetailsBinding> {

    public static final int ACTIVITY_RESULT_REQUEST_CODE = 9053;

    @Inject
    PersonalDetailsPresenter presenter;

    @Inject
    DeviceLocaleProvider deviceLocaleProvider;

    private PersonalDetailsViewContainer container;

    private final BehaviorRelay<ParcelableAddress> postcodeAddressRelay = BehaviorRelay.create();
    private static final String CUSTOMER_KEY = "customer";

    public static Intent createIntent(@NonNull Context context, @NonNull ParcelableCustomer customer) {
        Intent intent = new Intent(context, PersonalDetailsActivity.class);
        intent.putExtra(CUSTOMER_KEY, customer);
        return intent;
    }

    @NonNull
    @Override
    protected ActivityPersonalDetailsBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityPersonalDetailsBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParcelableCustomer customerDetails = Objects.requireNonNull(getIntent().getParcelableExtra(CUSTOMER_KEY));
        presenter.initParams(MappersKt.toDomain(customerDetails));
        container = new PersonalDetailsViewContainer(this, postcodeAddressRelay, deviceLocaleProvider, binding);
        presenter.attachView(container);
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
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        if (isFinishing()) {
            presenter.destroy();
        }
    }

}