package com.whitbread.premierinn.postcodefinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivityPostcodeFinderBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PostcodeFinderActivity extends BasePresenterActivity<PostcodeFinderPresenter.View,
        ActivityPostcodeFinderBinding, PostcodeFinderPresenter> {

    public static final int POSTCODE_ADDRESS_RESULT_REQUEST_CODE = 1;
    public static final String SELECTED_POSTCODE_ADDRESS_KEY = "selected_postcode_address_key";
    private static final String KEY_POSTCODE_TEXT = "key_postcode_text";
    @Inject
    PostcodeFinderPresenter presenter;

    public static void start(@NonNull Context context, @NonNull Activity activity,
                             @NonNull String postcode) {
        Intent intent = new Intent(context, PostcodeFinderActivity.class);
        intent.putExtra(KEY_POSTCODE_TEXT, postcode);
        activity.startActivityForResult(intent, POSTCODE_ADDRESS_RESULT_REQUEST_CODE);
    }

    @NonNull
    @Override
    protected ActivityPostcodeFinderBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityPostcodeFinderBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected PostcodeFinderPresenter.@NotNull View provideView() {
        return new PostcodeFinderViewContainer(this, binding);
    }

    @Override
    protected @NotNull PostcodeFinderPresenter createPresenter() {
        presenter.initParams(Objects.requireNonNull(getIntent().getStringExtra(KEY_POSTCODE_TEXT)));
        return presenter;
    }
}