package com.whitbread.premierinn.search;

import androidx.annotation.NonNull;

import com.whitbread.premierinn.api.response.search.SearchItemInput;
import com.whitbread.premierinn.common.AsyncResult;
import com.whitbread.premierinn.common.AsyncResultKt;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.common.utils.LocationProvider;
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem;
import com.whitbread.premierinn.domain.search.usecase.CheckIfRecentsExist;
import com.whitbread.premierinn.domain.search.usecase.GetDefaultSearchItems;
import com.whitbread.premierinn.domain.search.usecase.GetSearchItemSorted;
import com.whitbread.premierinn.domain.search.usecase.RemoveAllRecentSearchItems;
import com.whitbread.premierinn.search.adapter.adapteritem.CurrentLocationSearchItemUiModel;
import com.whitbread.premierinn.search.adapter.adapteritem.EndOfResultsUiModel;
import com.whitbread.premierinn.search.adapter.adapteritem.HeaderUiModel;
import com.whitbread.premierinn.search.adapter.adapteritem.SearchAdapterItem;
import com.whitbread.premierinn.search.adapter.adapteritem.SearchItemUiModel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.SEARCH;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.SEARCH_NO_RESULTS_FOUND;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.LOOK_TO_BOOK;
import static com.whitbread.premierinn.common.utils.StringUtils.isBlank;
import static com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem.Type;
import static com.whitbread.premierinn.search.adapter.adapteritem.HeaderUiModel.HeaderType.RECENT_SEARCHES;
import static com.whitbread.premierinn.search.adapter.adapteritem.HeaderUiModel.HeaderType.TOP_DESTINATIONS;

@ActivityRetainedScoped
public class SearchPresenter extends Presenter<SearchPresenter.View> {

    private final GetSearchItemSorted getSearchItemSorted;
    private final GetDefaultSearchItems getDefaultSearchItems;
    private final CheckIfRecentsExist checkIfRecentsExist;
    private final RemoveAllRecentSearchItems removeAllRecentSearchItems;
    private final TrackingAnalytics analytics;
    private final CompositeDisposable viewCompositeDisposable;
    private final LocationProvider locationProvider;
    private final SearchMessageProvider searchMessageProvider;

    @Inject
    public SearchPresenter(@NonNull CheckIfRecentsExist checkIfRecentsExist,
                           @NonNull GetSearchItemSorted getSearchItemSorted,
                           @NonNull GetDefaultSearchItems getDefaultSearchItems,
                           @NonNull RemoveAllRecentSearchItems removeAllRecentSearchItems,
                           @NonNull CompositeDisposable compositeDisposable,
                           @NonNull LocationProvider locationProvider,
                           @NonNull SearchMessageProvider searchMessageProvider,
                           @NonNull TrackingAnalytics trackingAnalytics) {
        this.checkIfRecentsExist = checkIfRecentsExist;
        this.getSearchItemSorted = getSearchItemSorted;
        this.getDefaultSearchItems = getDefaultSearchItems;
        this.removeAllRecentSearchItems = removeAllRecentSearchItems;
        this.viewCompositeDisposable = compositeDisposable;
        this.locationProvider = locationProvider;
        this.analytics = trackingAnalytics;
        this.searchMessageProvider = searchMessageProvider;
    }

    @Override
    public void onAttachView(View view) {
        analytics.trackContentSquare(SEARCH, LOOK_TO_BOOK);
        viewCompositeDisposable.add(
                view.onSearchInput()
                        .map(String::trim)
                        .doOnNext(query -> view.showClearSearch(query.length() > 0))
                        .distinctUntilChanged()
                        .debounce(200, TimeUnit.MILLISECONDS)
                        .filter(allowIfQueryIsEmptyOrValidLength())
                        .switchMap(query -> {
                                    if (isBlank(query)) {
                                        return AsyncResultKt.mapToAsyncResult(
                                                getDefaultSearchItems.execute().toObservable()
                                                .compose(toDataStateUiModelWhenNoQuery())
                                                .subscribeOn(Schedulers.io()));
                                    } else {
                                        return AsyncResultKt.mapToAsyncResult(
                                                getSearchItemSorted.execute(query).toObservable()
                                                .compose(toDataStateUiModelForQuery(query))
                                                .subscribeOn(Schedulers.io()));
                                    }
                                }
                        )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(state -> bindSearchItemsStateToUi(state, view))
        );

        viewCompositeDisposable.add(view.onSearchLocationClickPermissionsAccepted()
                .flatMap(accepted -> AsyncResultKt.mapToAsyncResult(locationProvider.locationUpdateObservable2()
                        .map(location -> new SearchSuggetionItem(searchMessageProvider.getMyLocationSearchText(), location,
                                null, Type.LOCATION))
                        .map(new SearchItemMapper())))
                .subscribe(state -> bindLocationStateToUi(state, view))
        );

        viewCompositeDisposable.add(view.onSearchItemClick()
                .map(new SearchItemMapper())
                .subscribe(view::returnResult));

        viewCompositeDisposable.add(view.onRecentSearchClearClick()
                .observeOn(Schedulers.io())
                .doOnNext(__ -> removeAllRecentSearchItems.execute())
                .flatMap(__ -> AsyncResultKt.mapToAsyncResult(getDefaultSearchItems.execute().toObservable()
                        .compose(toDataStateUiModelWhenNoQuery())
                        .subscribeOn(Schedulers.io())))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(state -> bindSearchItemsStateToUi(state, view))
        );
    }

    @NonNull
    private Predicate<String> allowIfQueryIsEmptyOrValidLength() {
        return query -> isBlank(query) || query.length() > 2;
    }

    private ObservableTransformer<List<SearchSuggetionItem>, SearchItemsWithoutQuery> toDataStateUiModelWhenNoQuery() {
        return upstream -> upstream
                .compose(toUiModelItemsWithHeader(checkIfRecentsExist.execute() ? RECENT_SEARCHES : TOP_DESTINATIONS));
    }

    private ObservableTransformer<List<SearchSuggetionItem>, SearchItemsWithoutQuery> toUiModelItemsWithHeader(
            HeaderUiModel.HeaderType headerType) {
        return upstream -> upstream.map(searchSuggestionItems -> {
            List<SearchAdapterItem<?>> results = new ArrayList<>();
            results.add(CurrentLocationSearchItemUiModel.create());
            if (searchSuggestionItems.size() > 0) {
                results.add(HeaderUiModel.create(headerType));
                for (SearchSuggetionItem item : searchSuggestionItems) {
                    results.add(SearchItemUiModel.create("", item));
                }
            }
            return new SearchItemsWithoutQuery(results);
        });
    }

    private ObservableTransformer<List<SearchSuggetionItem>, SearchItemsForQuery> toDataStateUiModelForQuery(@NonNull String query) {
        return upstream -> upstream
                .compose(toUiModelItemsForQuery(query));
    }

    private ObservableTransformer<List<SearchSuggetionItem>, SearchItemsForQuery> toUiModelItemsForQuery(@NonNull String query) {
        return upstream -> upstream.map(searchSuggestionItems -> {
            List<SearchAdapterItem<?>> results = new ArrayList<>();
            if (searchSuggestionItems.size() > 0) {
                for (SearchSuggetionItem item : searchSuggestionItems) {
                    results.add(SearchItemUiModel.create(query, item));
                }
                results.add(new EndOfResultsUiModel());
            }
            return new SearchItemsForQuery(query, results);
        });
    }

    private void bindSearchItemsStateToUi(@NonNull AsyncResult state, @NonNull View view) {
        if (state instanceof AsyncResult.Loading) {
            view.showLoading(true);
        } else if (state instanceof AsyncResult.Success) {
            view.showLoading(false);
            Object searchResults = ((AsyncResult.Success) state).getData();
            if (searchResults instanceof SearchItemsForQuery) {
                SearchItemsForQuery searchItemsForQuery = (SearchItemsForQuery) searchResults;
                List<SearchAdapterItem> results = new ArrayList<>(searchItemsForQuery.getResults());
                view.displayResults(results);

                if (!results.isEmpty()) {
                    analytics.trackAdobe(SEARCH, LOOK_TO_BOOK);
                } else {
                    view.showNoResultsFoundMessage(searchMessageProvider.getNoSearchResultsMessage(searchItemsForQuery.getQuery()));
                    analytics.trackAdobe(SEARCH_NO_RESULTS_FOUND, LOOK_TO_BOOK);
                }
            } else if (searchResults instanceof SearchItemsWithoutQuery) {
                SearchItemsWithoutQuery searchItemsWithoutQuery = (SearchItemsWithoutQuery) searchResults;
                List<SearchAdapterItem> results = new ArrayList<>(searchItemsWithoutQuery.getResults());
                view.displayResults(results);
            }
        } else if (state instanceof AsyncResult.Error) {
            view.showErrorOccurred(searchMessageProvider.getSearchResultsErrorMessage());
            view.showLoading(false);
        }
    }

    private void bindLocationStateToUi(@NonNull AsyncResult state, @NonNull View view) {
            if (state instanceof AsyncResult.Loading) {
                view.showLoading(true);
            } else if (state instanceof AsyncResult.Success) {
                view.showLoading(false);
                view.returnResult((SearchItemInput) ((AsyncResult.Success) state).getData());
            } else if (state instanceof AsyncResult.Error) {
                view.showLoading(false);
            }
    }

    @Override
    public void onDetachView() {
        if (viewCompositeDisposable.size() > 0) {
            viewCompositeDisposable.clear();
        }
    }

    @Override
    protected void onDestroy() {
    }

    public interface View extends PresenterView {
        void displayResults(@NonNull List<SearchAdapterItem> searchAdapterItems);

        void showNoResultsFoundMessage(@NonNull String message);

        void showErrorOccurred(@NonNull String message);

        void showLoading(boolean show);

        void showClearSearch(boolean show);

        void returnResult(@NonNull SearchItemInput searchItem);

        Observable<String> onSearchInput();

        Observable<Object> onRecentSearchClearClick();

        Observable<Object> onSearchLocationClickPermissionsAccepted();

        Observable<SearchSuggetionItem> onSearchItemClick();
    }
}