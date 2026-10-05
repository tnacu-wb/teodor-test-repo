package com.whitbread.premierinn.plantrip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivityPlanTripBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlanTripActivity extends BasePresenterActivity<PlanTripPresenter.View,
        ActivityPlanTripBinding, PlanTripPresenter> {

    private static final String HOTEL_CODE_INPUT = "hotel_code_input";
    @Inject
    PlanTripPresenter presenter;

    public static void start(@NonNull Context context, @NonNull String hotelCode) {
        Intent starter = new Intent(context, PlanTripActivity.class);
        starter.putExtra(HOTEL_CODE_INPUT, hotelCode);
        context.startActivity(starter);
    }

    @NonNull
    @Override
    protected ActivityPlanTripBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityPlanTripBinding.inflate(inflater);
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
    protected PlanTripPresenter.@NotNull View provideView() {
        return new PlanTripViewContainer(this, binding);
    }

    @Override
    protected @NotNull PlanTripPresenter createPresenter() {
        presenter.initParams(Objects.requireNonNull(getIntent().getStringExtra(HOTEL_CODE_INPUT)));
        return presenter;
    }
}
