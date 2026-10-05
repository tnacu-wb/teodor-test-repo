package com.whitbread.premierinn.gdpr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivityGdprBinding;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GdprActivity extends BasePresenterActivity<GdprPresenter.View, ActivityGdprBinding, GdprPresenter> {

    @Inject
    GdprPresenter presenter;

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, GdprActivity.class);
    }

    private RxPermissions rxPermissions;

    @NonNull
    @Override
    protected ActivityGdprBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityGdprBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxPermissions = new RxPermissions(this);
        requestNotificationPermission();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rxPermissions.request(Manifest.permission.POST_NOTIFICATIONS).subscribe();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GdprDataUseActivity.REQUEST_CODE) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    protected GdprPresenter.@NotNull View provideView() {
        return new GdprViewContainer(this, binding);
    }

    @Override
    protected @NotNull GdprPresenter createPresenter() {
        return presenter;
    }
}
