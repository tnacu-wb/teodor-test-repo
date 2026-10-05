package com.whitbread.premierinn.summarybreakdown;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivitySummaryBreakdownBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SummaryBreakdownActivity extends BasePresenterActivity<SummaryBreakdownPresenter.View,
        ActivitySummaryBreakdownBinding, SummaryBreakdownPresenter> {
    static final String SUMMARY_BREAKDOWN_INPUT = "summary_breakdown_input";

    @Inject
    SummaryBreakdownPresenter presenter;

    public static void start(@NonNull Context context, SummaryBreakdownInput summaryBreakdownInput) {
        Intent starter = new Intent(context, SummaryBreakdownActivity.class);
        starter.putExtra(SUMMARY_BREAKDOWN_INPUT, summaryBreakdownInput);
        context.startActivity(starter);
    }

    @NonNull
    @Override
    protected ActivitySummaryBreakdownBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivitySummaryBreakdownBinding.inflate(inflater);
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
    protected SummaryBreakdownPresenter.@NotNull View provideView() {
        return new SummaryBreakdownViewContainer(this, binding);
    }

    @Override
    protected @NotNull SummaryBreakdownPresenter createPresenter() {
        presenter.initParams(Objects.requireNonNull(getIntent().getParcelableExtra(SummaryBreakdownActivity.SUMMARY_BREAKDOWN_INPUT)));
        return presenter;
    }
}
