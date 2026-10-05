package com.whitbread.premierinn.gdpr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.common.contentSquare.CSQOptInHelper;
import com.whitbread.premierinn.databinding.ActivityGdprDataUseBinding;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GdprDataUseActivity extends BasePresenterActivity<GdprDataUsePresenter.View,
        ActivityGdprDataUseBinding, GdprDataUsePresenter> implements GdprDataUsePresenter.View {

    public static final int REQUEST_CODE = 678;
    @Inject
    CSQOptInHelper cSQOptInHelper;

    @Inject
    GdprDataUsePresenter presenter;


    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, GdprDataUseActivity.class);
    }

    @NonNull
    @Override
    protected ActivityGdprDataUseBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityGdprDataUseBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();
        presenter.attachView(this);
    }

    @Override
    public void showContent(String htmlContent) {
        binding.gdprDataUseText.setText(Html.fromHtml(htmlContent));
        binding.gdprDataUseText.setMovementMethod(new LinkMovementMethod());
    }

    private void setupToolbar() {
        setToolbar(getString(R.string.gdpr_use_data_title), true);
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_close));
    }

    @Override
    protected GdprDataUsePresenter.@NotNull View provideView() {
        return this;
    }

    @Override
    protected @NotNull GdprDataUsePresenter createPresenter() {
        return presenter;
    }
}
