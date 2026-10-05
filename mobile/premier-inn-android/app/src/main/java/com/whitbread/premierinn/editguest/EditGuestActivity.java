package com.whitbread.premierinn.editguest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivityEditGuestBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditGuestActivity extends BasePresenterActivity<EditGuestPresenter.View,
        ActivityEditGuestBinding, EditGuestPresenter> {

    public static final int EDIT_GUEST_REQUEST_CODE = 111;
    public static final String EDIT_GUEST_INPUT_KEY = "edit_guest_input_key";

    @Inject
    EditGuestPresenter presenter;

    public static void startForResult(@NonNull Activity activity, @NonNull EditGuestInput editGuestInput) {
        Intent intent = new Intent(activity, EditGuestActivity.class);
        intent.putExtra(EDIT_GUEST_INPUT_KEY, editGuestInput);
        activity.startActivityForResult(intent, EDIT_GUEST_REQUEST_CODE);
    }

    @NonNull
    @Override
    protected ActivityEditGuestBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityEditGuestBinding.inflate(inflater);
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_down);
    }

    @Override
    protected EditGuestPresenter.@NotNull View provideView() {
        return new EditGuestViewContainer(this, binding);
    }

    @Override
    protected @NotNull EditGuestPresenter createPresenter() {
        presenter.initParams(Objects.requireNonNull(getIntent().getParcelableExtra(EDIT_GUEST_INPUT_KEY)));
        return presenter;
    }
}
