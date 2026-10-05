package com.whitbread.premierinn.shortcutlocationtrampoline;

import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivitySearchResultsShortcutLoadingLocationBinding;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShortcutLocationTrampolineActivity extends BasePresenterActivity<ShortcutLocationTrampolinePresenter.View,
        ActivitySearchResultsShortcutLoadingLocationBinding, ShortcutLocationTrampolinePresenter> {

    @Inject
    ShortcutLocationTrampolinePresenter presenter;

    @NonNull
    @Override
    protected ActivitySearchResultsShortcutLoadingLocationBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivitySearchResultsShortcutLoadingLocationBinding.inflate(inflater);
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
    protected ShortcutLocationTrampolinePresenter.@NotNull View provideView() {
        return new ShortcutLocationTrampolineViewContainer(this);
    }

    @Override
    protected @NotNull ShortcutLocationTrampolinePresenter createPresenter() {
        return presenter;
    }
}
