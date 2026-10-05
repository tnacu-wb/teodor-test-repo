package com.whitbread.premierinn.postcodefinder;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.whitbread.premierinn.domain.common.Address;
import com.whitbread.premierinn.domain.common.AddressShort;
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.FormattedAddressDomain;
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PartialAddressDomain;
import com.whitbread.premierinn.domain.graphql.guestDetails.usecase.GraphQLGuestDetailsUseCase;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PartialAddressRequestBody;
import com.whitbread.premierinn.utils.RxJavaTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

@RunWith(MockitoJUnitRunner.class)
public class PostcodeFinderPresenterTest {

    private final String postcode = "EC1N 2TD";

    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();

    @Mock PostcodeFinderPresenter.View view;
    @Mock GraphQLGuestDetailsUseCase graphQLGuestDetailsUseCase;
    @Mock AddressShort addressShort;
    @Mock PartialAddressDomain partialAddressDomain;
    @Mock Address address;
    @Mock FormattedAddressDomain formattedAddress;

    private PostcodeFinderPresenter presenter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Before
    public void onSetup() {
        presenter = new PostcodeFinderPresenter(graphQLGuestDetailsUseCase, compositeDisposable);
        when(view.onAddressClick()).thenReturn(Observable.never());
        when(view.onPostCodeEntered()).thenReturn(Observable.never());
        when(view.onManualAddressClicked()).thenReturn(Observable.never());
    }

    @Test
    public void testOnCorrectPostcodeChangedOpera() {
        presenter = new PostcodeFinderPresenter(graphQLGuestDetailsUseCase, compositeDisposable);
        String postcode = "EC1N2TD";
        when(graphQLGuestDetailsUseCase.getPartialAddress(new PartialAddressRequestBody(postcode)))
                .thenReturn(Single.just((partialAddressDomain)));
        when(partialAddressDomain.getPartialAddress())
                .thenReturn(Collections.singletonList(addressShort));
        when(view.onPostCodeEntered()).thenReturn(Observable.just(postcode));

        presenter.attachView(view);

        verify(view).showResults(Collections.singletonList(addressShort));
        verify(view).showLoading();
    }

    @Test
    public void testPostcodeChangedLessThan6Chars() {
        String postcode = "EC1N";
        when(view.onPostCodeEntered()).thenReturn(Observable.just(postcode));

        presenter.attachView(view);

        verify(view, times(2)).showDefaultScreen();
    }

    @Test
    public void testAttachView() {
        presenter.attachView(view);
        verify(view).showDefaultScreen();
    }

    @Test
    public void testLifeCycle() {
        assertFalse(presenter.isViewAttached());
        presenter.attachView(view);
        assertTrue(presenter.isViewAttached());
        presenter.detachView();
        assertFalse(presenter.isViewAttached());
        presenter.destroy();
    }

    @Test
    public void testOnAddressItemClickGetFullAddressOpera() {
        presenter = new PostcodeFinderPresenter(graphQLGuestDetailsUseCase, compositeDisposable);

        when(view.onAddressClick()).thenReturn(Observable.just(addressShort));
        when(graphQLGuestDetailsUseCase.getFormattedAddress(addressShort.getId()))
                .thenReturn(Single.just(formattedAddress));

        presenter.attachView(view);

        verify(view).selectAddress(formattedAddress.getFormattedAddress());
    }

    @Test
    public void testOnManualAddressClicked() {
        when(view.onManualAddressClicked()).thenReturn(Observable.just(Unit.INSTANCE));

        presenter.attachView(view);

        verify(view).showManualAddressInput();
    }
}
