package com.whitbread.premierinn.paymentbreakdown;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivityPaymentBreakdownBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PaymentBreakdownActivity extends BasePresenterActivity<PaymentBreakdownPresenter.View,
        ActivityPaymentBreakdownBinding, PaymentBreakdownPresenter> {

    @Inject
    PaymentBreakdownPresenter presenter;

    private static final String PAYMENT_BREAKDOWN_INPUT = "payment_breakdown_input";

    public static void start(@NonNull Context context, @NonNull PaymentBreakdownInput paymentBreakdownInput) {
        Intent intent = new Intent(context, PaymentBreakdownActivity.class);
        intent.putExtra(PAYMENT_BREAKDOWN_INPUT, paymentBreakdownInput);
        context.startActivity(intent);
    }

    @NonNull
    @Override
    protected ActivityPaymentBreakdownBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityPaymentBreakdownBinding.inflate(inflater);
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
    protected PaymentBreakdownPresenter.@NotNull View provideView() {
        return new PaymentBreakdownViewContainer(this, binding);
    }

    @Override
    protected @NotNull PaymentBreakdownPresenter createPresenter() {
        presenter.initParams(Objects.requireNonNull(getIntent().getParcelableExtra(PAYMENT_BREAKDOWN_INPUT)));
        return presenter;
    }
}
