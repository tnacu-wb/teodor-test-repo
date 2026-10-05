package com.whitbread.premierinn.common.bottomnavigation;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jakewharton.rxbinding3.view.RxMenuItem;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.account.AccountActivity;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.landing.LandingActivityIntent;
import com.whitbread.premierinn.mybookings.MyBookingsActivity;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

public abstract class BottomNavigationActivity<T extends ViewBinding> extends BaseActivity<T> implements BottomNavigationPresenter.View {

    private BottomNavigationView bnvBottomNavigation;
    private BottomNavigationPresenter presenter;

    @NonNull
    protected abstract T inflateBinding(@NonNull LayoutInflater inflater);

    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
        presenter = new BottomNavigationPresenter(new CompositeDisposable());
        presenter.attachView(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.detachView();
            presenter.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        T binding = inflateBinding(getLayoutInflater());
        View viewWithNavigation = wrapWithBottomNavigation(binding.getRoot());
        setBinding(binding, viewWithNavigation);

        bnvBottomNavigation = findViewById(R.id.bnv_activity_bottom_navigation);
    }

    private View wrapWithBottomNavigation(View view) {
        ViewGroup parentActivityLayout = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_bottom_navigation, null);
        parentActivityLayout.addView(view, 0);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        layoutParams.addRule(RelativeLayout.ABOVE, R.id.v_bottom_navigation_line_separator);
        view.setLayoutParams(layoutParams);

        return parentActivityLayout;
    }

    @Override
    public Observable<Unit> onSearchItemClicked() {
        return RxMenuItem.clicks(bnvBottomNavigation.getMenu().findItem(R.id.bottom_navigation_search));
    }

    @Override
    public Observable<Unit> onMyBookingsItemClicked() {
        return RxMenuItem.clicks(bnvBottomNavigation.getMenu().findItem(R.id.bottom_navigation_my_bookings));
    }

    @Override
    public Observable<Unit> onMyAccountItemClicked() {
        return RxMenuItem.clicks(bnvBottomNavigation.getMenu().findItem(R.id.bottom_navigation_account));
    }

    @Override
    public void startLandingActivity() {
        if (getSelectedMenuItem() != R.id.bottom_navigation_search) {
            startActivity(LandingActivityIntent.INSTANCE.create(this));
            finish();
        }
    }

    @Override
    public void startMyBookingsActivity() {
        if (getSelectedMenuItem() != R.id.bottom_navigation_my_bookings) {
            startActivity(MyBookingsActivity.createIntent(this, false));
            finish();
        }
    }

    @Override
    public void startMyAccountActivity() {
        if (getSelectedMenuItem() != R.id.bottom_navigation_account) {
            AccountActivity.start(this);
            finish();
        }
    }

    @IdRes
    protected abstract int getSelectedMenuItem();

    private void updateNavigationBarState() {
        Menu menu = bnvBottomNavigation.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == getSelectedMenuItem()) {
                item.setChecked(true);
                break;
            }
        }
    }
}
