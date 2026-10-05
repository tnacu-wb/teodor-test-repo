package com.whitbread.premierinn.mealpreferences;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.databinding.ActivityMealPreferencesBinding;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MealPreferencesActivity extends BasePresenterActivity<MealPreferencesPresenter.View,
        ActivityMealPreferencesBinding, MealPreferencesPresenter> {

    public static final int MEAL_PREFERENCES_RESULT_KEY = 6478;
    @Inject
    MealPreferencesPresenter presenter;

    @NonNull
    @Override
    protected ActivityMealPreferencesBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityMealPreferencesBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbar(getString(R.string.meal_preferences_label), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_privacy_policy_badge, menu);
        return true;
    }

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, MealPreferencesActivity.class);
    }

    @Override
    protected MealPreferencesPresenter.@NotNull View provideView() {
        return new MealPreferencesViewContainer(this, binding);
    }

    @Override
    protected @NotNull MealPreferencesPresenter createPresenter() {
        return presenter;
    }
}
