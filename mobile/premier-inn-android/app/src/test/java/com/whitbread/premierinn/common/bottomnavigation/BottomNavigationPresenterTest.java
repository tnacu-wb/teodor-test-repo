package com.whitbread.premierinn.common.bottomnavigation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BottomNavigationPresenterTest {

    @Mock
    BottomNavigationPresenter.View viewMock;

    private final CompositeDisposable viewCompositeDisposable = new CompositeDisposable();
    private BottomNavigationPresenter presenter;

    @Before
    public void onSetup() {
        presenter = new BottomNavigationPresenter(viewCompositeDisposable);
        when(viewMock.onMyBookingsItemClicked()).thenReturn(Observable.never());
        when(viewMock.onSearchItemClicked()).thenReturn(Observable.never());
        when(viewMock.onMyAccountItemClicked()).thenReturn(Observable.never());
    }

    @Test
    public void myBookingsItemClickedTest() {
        when(viewMock.onMyBookingsItemClicked()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(viewMock);
        verify(viewMock).startMyBookingsActivity();
    }

    @Test
    public void searchItemClickedTest() {
        when(viewMock.onSearchItemClicked()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(viewMock);
        verify(viewMock).startLandingActivity();
    }

    @Test
    public void myAccountItemClickedTest() {
        when(viewMock.onMyAccountItemClicked()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(viewMock);
        verify(viewMock).startMyAccountActivity();
    }

    @Test
    public void testLifecycle() {
        assertFalse(presenter.isViewAttached());
        assertEquals(viewCompositeDisposable.size(), 0);
        presenter.attachView(viewMock);
        assertTrue(presenter.isViewAttached());
        assertEquals(viewCompositeDisposable.size(), 3);
        presenter.detachView();
        assertFalse(presenter.isViewAttached());
        assertEquals(viewCompositeDisposable.size(), 0);
        presenter.destroy();
    }
}
