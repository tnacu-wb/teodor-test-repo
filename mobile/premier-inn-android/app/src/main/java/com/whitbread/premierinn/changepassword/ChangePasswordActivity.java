package com.whitbread.premierinn.changepassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivityChangePasswordBinding;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChangePasswordActivity extends BasePresenterActivity<ChangePasswordPresenter.View,
        ActivityChangePasswordBinding, ChangePasswordPresenter> {

    public static final int ACTIVITY_RESULT_REQUEST_CODE = 852;

    @Inject
    ChangePasswordPresenter presenter;

    public static Intent createIntent(@NonNull BaseActivity activity) {
        return new Intent(activity, ChangePasswordActivity.class);
    }

    @NonNull
    @Override
    protected ActivityChangePasswordBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityChangePasswordBinding.inflate(inflater);
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
    protected ChangePasswordPresenter.@NotNull View provideView() {
        return new ChangePasswordViewContainer(this, binding);
    }

    @Override
    protected @NotNull ChangePasswordPresenter createPresenter() {
        return presenter;
    }
}
