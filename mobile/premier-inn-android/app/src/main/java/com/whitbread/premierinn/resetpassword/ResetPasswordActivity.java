package com.whitbread.premierinn.resetpassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivityResetPasswordBinding;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ResetPasswordActivity extends BasePresenterActivity<ResetPasswordPresenter.View,
        ActivityResetPasswordBinding, ResetPasswordPresenter> {

    public static final int RESET_PASSWORD_RESULT_REQUEST_CODE = 1;
    public static final String RESULT_INTENT_EMAIL_KEY = "result_intent_email_key";
    public static final String EXTRA_BUSINESS_BOOKER = "EXTRA_BUSINESS_BOOKER";

    @Inject
    ResetPasswordPresenter presenter;

    public static Intent createIntent(@NonNull Context context, boolean businessBooker) {
        return new Intent(context, ResetPasswordActivity.class).putExtra(EXTRA_BUSINESS_BOOKER, businessBooker);
    }

    @NonNull
    @Override
    protected ActivityResetPasswordBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityResetPasswordBinding.inflate(inflater);
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
    protected ResetPasswordPresenter.@NotNull View provideView() {
        return new ResetPasswordViewContainer(this, getIntent().getBooleanExtra(EXTRA_BUSINESS_BOOKER, false), binding);
    }

    @Override
    protected @NotNull ResetPasswordPresenter createPresenter() {
        return presenter;
    }
}
