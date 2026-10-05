package com.whitbread.premierinn.mealpreferences;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jakewharton.rxbinding3.view.RxView;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.databinding.ActivityMealPreferencesBinding;
import com.whitbread.premierinn.login.LoginActivity;
import com.whitbread.premierinn.login.Screen;
import com.whitbread.premierinn.login.ScreenType;

import java.util.List;

import io.reactivex.Observable;
import kotlin.Unit;

public class MealPreferencesViewContainer extends ViewContainer implements MealPreferencesPresenter.View {

    private final ActivityMealPreferencesBinding binding;
    private BreakfastOptionsListAdapter breakfastOptionsAdapter;


    public MealPreferencesViewContainer(@NonNull BaseActivity activity, ActivityMealPreferencesBinding binding) {
        super(activity);
        this.binding = binding;

        binding.mealPreferencesBreakfastList.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
    }

    @Override
    public void showBreakfastOptions(@NonNull List<BreakfastRadioButtonInput> breakfastContents) {
        binding.mealPreferencesBreakfastList.addItemDecoration(new MealPreferenceDividerItemDecorator());

        breakfastOptionsAdapter = new BreakfastOptionsListAdapter(breakfastContents);
        binding.mealPreferencesBreakfastList.setAdapter(breakfastOptionsAdapter);
    }

    @Override
    public void selectBreakfastOption(@NonNull String upsellItemCode) {
        breakfastOptionsAdapter.selectItem(upsellItemCode);
    }

    @Override
    public void showButtonLoading(boolean show) {
        binding.mealPreferencesSaveChangesButton.setLoadingState(show);
    }

    @Override
    public void showError(@NonNull String errorMessageUpdateCustomer) {
        Toast.makeText(getActivity(), errorMessageUpdateCustomer, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void goToBookingsPreferences() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void showPageLoading(boolean show) {
        binding.mealPreferencesPageLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.mealPreferencesScreenWrapper.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public Observable<Unit> onSaveChangesClick() {
        return RxView.clicks(binding.mealPreferencesSaveChangesButton);
    }

    @Override
    public Observable<BreakfastRadioButtonInput> getOptionSelected() {
        return Observable.just(breakfastOptionsAdapter.getSelectedItem());
    }

    @Override
    public void showForceLoginMessage() {
        Toast.makeText(getActivity(), R.string.force_login_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startLogInActivity() {
        getActivity().startActivity(LoginActivity.createIntent(getActivity(),
                new Screen(ScreenType.ACCOUNT_LOGIN.name(), AnalyticsConstants.ScreenState.AUTHENTICATION_ERROR_LOG_IN)));
    }
}
