package com.whitbread.premierinn.paymentmethods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.databinding.ActivityPaymentMethodsBinding;
import com.whitbread.premierinn.editpaymentmethods.EditPaymentMethodsActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PaymentMethodsActivity extends BaseActivity<ActivityPaymentMethodsBinding> {

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, PaymentMethodsActivity.class);
    }

    @Inject
    PaymentMethodsPresenter presenter;
    private PaymentMethodsViewContainer container;

    private Relay<Object> cardUpdatedRelay;

    @NonNull
    @Override
    protected ActivityPaymentMethodsBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityPaymentMethodsBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbar(getString(R.string.payment_methods_label), true);
        cardUpdatedRelay = PublishRelay.create();
        presenter.initParams(cardUpdatedRelay);
        container = new PaymentMethodsViewContainer(this, binding);
        presenter.attachView(container);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EditPaymentMethodsActivity.ACTIVITY_RESULT_REQUEST_CODE && resultCode == RESULT_OK) {
            cardUpdatedRelay.accept(new Object());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_privacy_policy_badge, menu);
        return true;
    }
}