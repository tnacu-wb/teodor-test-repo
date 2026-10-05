package com.whitbread.premierinn.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivitySearchBinding;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchActivity extends BasePresenterActivity<SearchPresenter.View, ActivitySearchBinding, SearchPresenter> {

    private ViewGroup noInternetMessageHolder;

    public static final String SELECTED_SEARCH_ITEM = "selected_search_item";

    @Inject
    SearchPresenter presenter;

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, SearchActivity.class);
    }

    @NonNull
    @Override
    protected ActivitySearchBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivitySearchBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        noInternetMessageHolder = binding.noInternetMessageHolder;

        postponeEnterTransition();
        final View decor = getWindow().getDecorView();
        decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                decor.getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return true;
            }
        });
    }

    @Override
    protected ViewGroup getParentViewForInternetInfoBar() {
        return noInternetMessageHolder;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public @NotNull SearchPresenter createPresenter() {
        return presenter;
    }

    @Override
    protected @NotNull SearchViewContainer provideView() {
        return new SearchViewContainer(this, binding);
    }
}
