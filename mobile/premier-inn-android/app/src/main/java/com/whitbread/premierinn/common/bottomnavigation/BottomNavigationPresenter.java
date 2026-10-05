package com.whitbread.premierinn.common.bottomnavigation;

import androidx.annotation.NonNull;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

@ActivityRetainedScoped
class BottomNavigationPresenter extends Presenter<BottomNavigationPresenter.View> {

    private final CompositeDisposable viewCompositeDisposable;

    @Inject
    BottomNavigationPresenter(@NonNull CompositeDisposable compositeDisposable) {
        viewCompositeDisposable = compositeDisposable;
    }

    @Override
    public void onAttachView(View view) {
        viewCompositeDisposable.add(view.onSearchItemClicked().subscribe(__ -> view.startLandingActivity()));
        viewCompositeDisposable.add(view.onMyBookingsItemClicked().subscribe(__ -> view.startMyBookingsActivity()));
        viewCompositeDisposable.add(view.onMyAccountItemClicked().subscribe(__ -> view.startMyAccountActivity()));
    }

    @Override
    public void onDetachView() {
        viewCompositeDisposable.clear();
    }

    interface View extends PresenterView {
        void startLandingActivity();

        void startMyBookingsActivity();

        void startMyAccountActivity();

        Observable<Unit> onSearchItemClicked();

        Observable<Unit> onMyBookingsItemClicked();

        Observable<Unit> onMyAccountItemClicked();
    }
}
