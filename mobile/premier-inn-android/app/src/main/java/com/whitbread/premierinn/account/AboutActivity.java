package com.whitbread.premierinn.account;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.StringResourceProvider;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.databinding.ActivityAboutBinding;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AboutActivity extends BaseActivity<ActivityAboutBinding> {

    @Inject StringResourceProvider stringResourceProvider;
    @Inject LogService logService;
    @Inject TrackingAnalytics analytics;

    @NonNull
    @Override
    protected ActivityAboutBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityAboutBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.toolbar;
    }

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbar(getString(R.string.my_account_about), true);
        try {
            binding.tvAboutVersionName.setText(stringResourceProvider.getVersionLabel());
        } catch (Exception e) {
            binding.tvAboutVersionName.setVisibility(View.GONE);
            logService.logException(e, null);
        }

        analytics.track(ScreenState.ABOUT, Type.MY_PREMIER_INN);
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, AboutActivity.class);
    }
}