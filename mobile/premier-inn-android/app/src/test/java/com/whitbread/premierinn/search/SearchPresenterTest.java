package com.whitbread.premierinn.search;

import androidx.annotation.NonNull;

import com.whitbread.premierinn.api.response.availability.Coordinates;
import com.whitbread.premierinn.api.response.search.SearchItemInput;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.utils.LocationProvider;
import com.whitbread.premierinn.domain.search.entity.Location;
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem;
import com.whitbread.premierinn.domain.search.usecase.CheckIfRecentsExist;
import com.whitbread.premierinn.domain.search.usecase.GetDefaultSearchItems;
import com.whitbread.premierinn.domain.search.usecase.GetSearchItemSorted;
import com.whitbread.premierinn.domain.search.usecase.RemoveAllRecentSearchItems;
import com.whitbread.premierinn.search.adapter.adapteritem.CurrentLocationSearchItemUiModel;
import com.whitbread.premierinn.search.adapter.adapteritem.EndOfResultsUiModel;
import com.whitbread.premierinn.search.adapter.adapteritem.HeaderUiModel;
import com.whitbread.premierinn.search.adapter.adapteritem.SearchItemUiModel;
import com.whitbread.premierinn.utils.RxJavaTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.SEARCH;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.SEARCH_NO_RESULTS_FOUND;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.LOOK_TO_BOOK;
import static com.whitbread.premierinn.common.utils.StringUtils.EMPTY_STRING;
import static com.whitbread.premierinn.search.adapter.adapteritem.HeaderUiModel.HeaderType.HOTELS;
import static com.whitbread.premierinn.search.adapter.adapteritem.HeaderUiModel.HeaderType.PLACES;
import static com.whitbread.premierinn.search.adapter.adapteritem.HeaderUiModel.HeaderType.RECENT_SEARCHES;
import static com.whitbread.premierinn.search.adapter.adapteritem.HeaderUiModel.HeaderType.TOP_DESTINATIONS;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SearchPresenterTest {

    @Rule public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();

    @Mock private SearchPresenter.View view;
    @Mock private CheckIfRecentsExist checkIfRecentsExist;
    @Mock private GetSearchItemSorted getSearchItemSorted;
    @Mock private GetDefaultSearchItems getDefaultSearchItems;
    @Mock private RemoveAllRecentSearchItems removeAllRecentSearchItems;
    @Mock private LocationProvider locationProvider;
    @Mock private SearchMessageProvider searchMessageProvider;
    @Mock private TrackingAnalytics trackingAnalytics;

    private SearchPresenter presenter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Before
    public void onSetup() {
        presenter = new SearchPresenter(checkIfRecentsExist, getSearchItemSorted, getDefaultSearchItems, removeAllRecentSearchItems,
                compositeDisposable, locationProvider, searchMessageProvider, trackingAnalytics);

        stubOutAllStreams();
    }

    @Test
    public void givenAnEmptyInputString_View_Displays_TopDestinationItems() {
        //given
        when(view.onSearchInput()).thenReturn(Observable.just(EMPTY_STRING));

        //when
        when(getDefaultSearchItems.execute()).thenReturn(Single.just(Arrays.asList(LONDON_LOCATION, BRISTOL_LOCATION)));
        when(checkIfRecentsExist.execute()).thenReturn(Boolean.FALSE);

        presenter.onAttachView(view);

        //then
        verify(view).showLoading(true);

        verify(view).displayResults(Arrays.asList(
                CURRENT_LOCATION_HEADER,
                TOP_DESTINATION_HEADER,
                SearchItemUiModel.create(EMPTY_STRING, LONDON_LOCATION),
                SearchItemUiModel.create(EMPTY_STRING, BRISTOL_LOCATION)));

        verify(view).showLoading(false);
    }

    @Test
    public void givenAnEmptyInputString_View_Displays_RecentItems() {
        //given
        when(view.onSearchInput()).thenReturn(Observable.just(EMPTY_STRING));

        //when
        when(getDefaultSearchItems.execute()).thenReturn(Single.just(Arrays.asList(LONDON_LOCATION, BRISTOL_LOCATION)));
        when(checkIfRecentsExist.execute()).thenReturn(Boolean.TRUE);

        presenter.onAttachView(view);

        //then
        verify(view).showLoading(true);

        verify(view).displayResults(Arrays.asList(
                CURRENT_LOCATION_HEADER,
                RECENT_SEARCHES_HEADER,
                SearchItemUiModel.create(EMPTY_STRING, LONDON_LOCATION),
                SearchItemUiModel.create(EMPTY_STRING, BRISTOL_LOCATION)));

        verify(view).showLoading(false);
        verify(view).showClearSearch(false);

        verifyNoOtherViewInteractions(view);
    }

    @Test
    public void givenAValidInputString_View_Displays_Places_And_Hotels() {
        //given
        final String queryInput = "Lon";
        when(view.onSearchInput()).thenReturn(Observable.just(queryInput));

        //when
        when(getSearchItemSorted.execute(queryInput)).thenReturn(Single.just(Arrays.asList(LONDON_LOCATION, KINGS_CROSS_HOTEL,
                HOLBORN_HOTEL)));

        presenter.onAttachView(view);

        //then
        verify(view).showLoading(true);

        verify(view).displayResults(Arrays.asList(
                SearchItemUiModel.create(queryInput, LONDON_LOCATION),
                SearchItemUiModel.create(queryInput, KINGS_CROSS_HOTEL),
                SearchItemUiModel.create(queryInput, HOLBORN_HOTEL),
                new EndOfResultsUiModel()
        ));

        verify(view).showLoading(false);
        verify(trackingAnalytics).trackAdobe(SEARCH, LOOK_TO_BOOK);
    }

    @Test
    public void givenAValidInputString_View_Displays_Just_Places() {
        //given
        final String queryInput = "Lon";
        when(view.onSearchInput()).thenReturn(Observable.just(queryInput));

        //when
        when(getSearchItemSorted.execute(queryInput)).thenReturn(Single.just(Collections.singletonList(LONDON_LOCATION)));

        presenter.onAttachView(view);

        //then
        verify(view).showLoading(true);

        verify(view).displayResults(Arrays.asList(
                SearchItemUiModel.create(queryInput, LONDON_LOCATION),
                new EndOfResultsUiModel()
        ));

        verify(view).showLoading(false);
        verify(trackingAnalytics).trackAdobe(SEARCH, LOOK_TO_BOOK);
    }

    @Test
    public void givenAValidInputString_View_Displays_Just_Hotels() {
        //given
        final String queryInput = "Lon";
        when(view.onSearchInput()).thenReturn(Observable.just(queryInput));

        //when
        when(getSearchItemSorted.execute(queryInput)).thenReturn(Single.just(Arrays.asList(KINGS_CROSS_HOTEL,
                HOLBORN_HOTEL)));

        presenter.onAttachView(view);

        //then
        verify(view).showLoading(true);

        verify(view).displayResults(Arrays.asList(
                SearchItemUiModel.create(queryInput, KINGS_CROSS_HOTEL),
                SearchItemUiModel.create(queryInput, HOLBORN_HOTEL),
                new EndOfResultsUiModel()
        ));

        verify(view).showLoading(false);
        verify(trackingAnalytics).trackAdobe(SEARCH, LOOK_TO_BOOK);
    }

    @Test
    public void givenFewInvalidInputs_Loading_Not_Shown() {
        //given
        when(view.onSearchInput()).thenReturn(Observable.fromArray("L", "Lo", "Lo", "Lo ", "Lo  "));

        //when
        presenter.onAttachView(view);

        //then
        verify(view, times(0)).showLoading(true);
    }

    @Test
    public void givenFewValidInputs_View_Displays_Results() {
        final String firstQueryInput = "London";
        final String secondQueryInput = "London Eye";

        //given
        when(view.onSearchInput()).thenReturn(Observable.fromArray("London", "London Eye"));

        //when
        when(getSearchItemSorted.execute("London")).thenReturn(Single.just(Arrays.asList(LONDON_LOCATION)));
        when(getSearchItemSorted.execute("London Eye")).thenReturn(Single.just(Arrays.asList(LONDON_LOCATION, LONDON_HOTEL)));

        presenter.onAttachView(view);

        //then
        verify(view, times(2)).showLoading(true);

        verify(view).displayResults(Arrays.asList(
                SearchItemUiModel.create(firstQueryInput, LONDON_LOCATION),
                new EndOfResultsUiModel()
        ));

        verify(view).displayResults(Arrays.asList(
                SearchItemUiModel.create(secondQueryInput, LONDON_LOCATION),
                SearchItemUiModel.create(secondQueryInput, LONDON_HOTEL),
                new EndOfResultsUiModel()
        ));

        verify(view, times(2)).showLoading(false);
        verify(trackingAnalytics, times(2)).trackAdobe(SEARCH, LOOK_TO_BOOK);
    }

    @Test
    public void givenAnInput_And_No_results_View_Displays_Empty_list() {
        //given
        when(view.onSearchInput()).thenReturn(Observable.just("Ldasdas"));
        when(searchMessageProvider.getNoSearchResultsMessage(any())).thenReturn("No results");

        //when
        when(getSearchItemSorted.execute("Ldasdas")).thenReturn(Single.just(Collections.emptyList()));
        presenter.onAttachView(view);

        //then
        verify(view).showLoading(true);
        verify(view).displayResults(Collections.emptyList());
        verify(view).showNoResultsFoundMessage("No results");
        verify(view).showLoading(false);
        verify(trackingAnalytics).trackAdobe(SEARCH_NO_RESULTS_FOUND, LOOK_TO_BOOK);
    }

    @Test
    public void givenAnInput_And_Exception_Occurs_View_Displays_Error() {
        //given
        when(view.onSearchInput()).thenReturn(Observable.just("London/"));
        when(searchMessageProvider.getSearchResultsErrorMessage()).thenReturn("Error occurred");

        //when
        when(getSearchItemSorted.execute("London/")).thenReturn(Single.error(new Exception()));
        presenter.onAttachView(view);

        //then
        verify(view).showLoading(true);
        verify(view).showErrorOccurred("Error occurred");
        verify(view).showLoading(false);
    }

    @Test
    public void givenAnItemIsClicked_View_Starts_CriteriaActivity() {
        //given
        when(view.onSearchItemClick()).thenReturn(Observable.just(LONDON_HOTEL));

        //when
        presenter.onAttachView(view);

        //then
        verify(view).returnResult(new SearchItemMapper().apply(LONDON_HOTEL));

        verifyNoOtherViewInteractions(view);
    }

    @Test
    public void givenClearLinkIsClicked_ItemsAreCleared_And_View_Displays_TopDestinations() {
        //given
        when(view.onRecentSearchClearClick()).thenReturn(Observable.just(new Object()));

        //when
        when(getDefaultSearchItems.execute()).thenReturn(Single.just(Arrays.asList(LONDON_LOCATION, BRISTOL_LOCATION)));

        presenter.onAttachView(view);

        //then
        verify(removeAllRecentSearchItems).execute();

        verify(view).showLoading(true);

        verify(view).displayResults(Arrays.asList(
                CURRENT_LOCATION_HEADER,
                TOP_DESTINATION_HEADER,
                SearchItemUiModel.create(EMPTY_STRING, LONDON_LOCATION),
                SearchItemUiModel.create(EMPTY_STRING, BRISTOL_LOCATION)));

        verify(view).showLoading(false);

        verifyNoOtherViewInteractions(view);
    }

    @Test
    public void givenCurrentLocationHeaderIsClicked_View_Starts_CriteriaActivity() {
        //given
        when(view.onSearchLocationClickPermissionsAccepted()).thenReturn(Observable.just(new Object()));

        //when
        when(locationProvider.locationUpdateObservable2()).thenReturn(Observable.just(new Location(1.0, 1.0)));
        when(searchMessageProvider.getMyLocationSearchText()).thenReturn("My Location");
        presenter.onAttachView(view);

        //then
        verify(view).showLoading(true);
        verify(view).returnResult(SearchItemInput.builder()
                .searchText("My Location")
                .location(Coordinates.create(1.0F, 1.0F))
                .build());
        verify(view).showLoading(false);

        verifyNoOtherViewInteractions(view);
    }

    @Test
    public void givenCurrentLocationHeaderIsClicked_And_ExceptionOccurred() {
        //given
        when(view.onSearchLocationClickPermissionsAccepted()).thenReturn(Observable.just(new Object()));

        //when
        when(locationProvider.locationUpdateObservable2()).thenReturn(Observable.error(new Exception()));

        presenter.onAttachView(view);

        //then
        verify(view).showLoading(true);
        verify(view).showLoading(false);

        verifyNoOtherViewInteractions(view);
    }

    @Test
    public void whenSearchQueryIsEmptyThenClearButtonGetsHidden() {
        when(view.onSearchInput()).thenReturn(Observable.just(""));
        presenter.onAttachView(view);
        verify(view).showClearSearch(false);
    }

    @Test
    public void whenSearchQueryIsNonEmptyThenClearButtonGetsShown() {
        when(view.onSearchInput()).thenReturn(Observable.just("L"));
        presenter.onAttachView(view);
        verify(view).showClearSearch(true);
    }

    private void stubOutAllStreams() {
        when(view.onSearchInput()).thenReturn(Observable.never());
        when(view.onRecentSearchClearClick()).thenReturn(Observable.never());
        when(view.onSearchLocationClickPermissionsAccepted()).thenReturn(Observable.never());
        when(view.onSearchItemClick()).thenReturn(Observable.never());
    }

    private void verifyNoOtherViewInteractions(@NonNull SearchPresenter.View view) {
        verify(view).onSearchInput();
        verify(view).onSearchLocationClickPermissionsAccepted();
        verify(view).onSearchItemClick();
        verify(view).onRecentSearchClearClick();

        verifyNoMoreInteractions(view);
    }

    @Test
    public void testPresenterLifeCycle() {
        when(view.onSearchInput()).thenReturn(Observable.empty());
        assertFalse(presenter.isViewAttached());
        assertEquals(0, compositeDisposable.size());
        presenter.attachView(view);
        assertTrue(presenter.isViewAttached());
        assertEquals(4, compositeDisposable.size());
        presenter.detachView();
        assertFalse(presenter.isViewAttached());
        assertEquals(0, compositeDisposable.size());
        presenter.destroy();
    }

    static final SearchSuggetionItem LONDON_LOCATION = new SearchSuggetionItem("London",
            new com.whitbread.premierinn.domain.search.entity.Location(1.0, 1.0),
            "", SearchSuggetionItem.Type.LOCATION);

    static final SearchSuggetionItem LONDON_HOTEL = new SearchSuggetionItem("London Eye Hotel",
            new com.whitbread.premierinn.domain.search.entity.Location(1.0, 1.0),
            "", SearchSuggetionItem.Type.PI_HOTEL);

    static final SearchSuggetionItem BRISTOL_LOCATION = new SearchSuggetionItem("Bristol",
            new com.whitbread.premierinn.domain.search.entity.Location(1.0, 1.0),
            "", SearchSuggetionItem.Type.LOCATION);

    static final SearchSuggetionItem KINGS_CROSS_HOTEL = new SearchSuggetionItem("KingsCross Hotel",
            new com.whitbread.premierinn.domain.search.entity.Location(1.0, 1.0),
            "", SearchSuggetionItem.Type.PI_HOTEL);

    static final SearchSuggetionItem HOLBORN_HOTEL = new SearchSuggetionItem("Holborn Hotel",
            new com.whitbread.premierinn.domain.search.entity.Location(1.0, 1.0),
            "", SearchSuggetionItem.Type.HUB_HOTEL);

    static final HeaderUiModel PLACES_HEADER = HeaderUiModel.create(PLACES);
    static final HeaderUiModel HOTELS_HEADER = HeaderUiModel.create(HOTELS);
    static final HeaderUiModel TOP_DESTINATION_HEADER = HeaderUiModel.create(TOP_DESTINATIONS);
    static final HeaderUiModel RECENT_SEARCHES_HEADER = HeaderUiModel.create(RECENT_SEARCHES);
    static final CurrentLocationSearchItemUiModel CURRENT_LOCATION_HEADER = CurrentLocationSearchItemUiModel.create();
}
