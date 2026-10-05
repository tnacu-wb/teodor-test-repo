package com.whitbread.premierinn.editpaymentmethods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.customer.MappersKt;
import com.whitbread.premierinn.api.response.customer.ParcelableCustomer;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivityEditPaymentMethodsBinding;
import com.whitbread.premierinn.domain.customer.entity.Customer;
import com.whitbread.premierinn.postcodefinder.ParcelableAddress;
import com.whitbread.premierinn.postcodefinder.ParcelableMappersKt;
import com.whitbread.premierinn.postcodefinder.PostcodeFinderActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditPaymentMethodsActivity extends BasePresenterActivity<EditPaymentMethodsPresenter.View,
        ActivityEditPaymentMethodsBinding, EditPaymentMethodsPresenter> {

    public static final int ACTIVITY_RESULT_REQUEST_CODE = 645;
    private static final String CUSTOMER_KEY = "customer";
    public static final int SAVED_CARD_REQUEST_CODE = 907;
    public static final int ACTIVITY_RESULT_REQUEST_CODE_SAVE_CARD = 908;

    @Inject
    EditPaymentMethodsPresenter editPaymentMethodsPresenter;
    private Customer customer;

    public static Intent createIntent(@NonNull Context context, @NonNull ParcelableCustomer customer) {
        Intent intent = new Intent(context, EditPaymentMethodsActivity.class);
        intent.putExtra(CUSTOMER_KEY, customer);
        return intent;
    }

    @NonNull
    @Override
    protected ActivityEditPaymentMethodsBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityEditPaymentMethodsBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbar(getString(R.string.payment_methods_label), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_privacy_policy_badge, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PostcodeFinderActivity.POSTCODE_ADDRESS_RESULT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    ParcelableAddress billingAddress = data.getParcelableExtra(PostcodeFinderActivity.SELECTED_POSTCODE_ADDRESS_KEY);
                    if (billingAddress != null && editPaymentMethodsPresenter != null) {
                        editPaymentMethodsPresenter.setBillingAddress(ParcelableMappersKt.toAddress(billingAddress));
                    } else {
                        Log.w(this.getLocalClassName(), "billingAddress is null");
                    }
                }
                break;
            case SAVED_CARD_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            default: break;
        }
    }

    @Override
    protected EditPaymentMethodsPresenter.@NotNull View provideView() {
        return new EditPaymentMethodsViewContainer(
                this,
                customer.hasPaymentCardDetails(),
                binding
        );
    }

    @Override
    protected @NotNull EditPaymentMethodsPresenter createPresenter() {
        customer = MappersKt.toDomain(Objects.requireNonNull(getIntent().getParcelableExtra(CUSTOMER_KEY)));
        editPaymentMethodsPresenter.initParams(customer);
        return editPaymentMethodsPresenter;
    }
}