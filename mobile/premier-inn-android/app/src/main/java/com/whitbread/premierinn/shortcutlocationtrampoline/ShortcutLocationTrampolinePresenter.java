package com.whitbread.premierinn.shortcutlocationtrampoline;

import androidx.annotation.NonNull;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.common.utils.LocationProvider;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn;
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository;
import com.whitbread.premierinn.searchresults.SearchResultsInput;
import org.threeten.bp.LocalDate;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

@ActivityRetainedScoped
public class ShortcutLocationTrampolinePresenter extends Presenter<ShortcutLocationTrampolinePresenter.View> {

    private final LocationProvider locationProvider;
    private final IsFeatureOn isFeatureOn;
    private final DeviceLocaleProvider deviceLocaleProvider;
    private Disposable disposable;
    private boolean useGraphQlAvailabilities;

    @Inject
    public ShortcutLocationTrampolinePresenter(
            @NonNull LocationProvider locationProvider,
            IsFeatureOn isFeatureOn,
            @NonNull DeviceLocaleProvider deviceLocaleProvider
    ) {
        this.locationProvider = locationProvider;
        this.isFeatureOn = isFeatureOn;
        this.deviceLocaleProvider = deviceLocaleProvider;
    }

    @Override
    protected void onAttachView(View view) {
        useGraphQlAvailabilities = isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_USE_GRAPHQL_AVAILABILITIES);
        disposable = view.onLocationPermissionRequested()
                .flatMap(__ -> locationProvider.locationUpdateObservable())
                .subscribe(location -> {
                            SearchResultsInput searchResultsInput = SearchResultsInput.builderWithDefaults()
                                    .placeName("My location")
                                    .latitude((float) location.getLatitude())
                                    .longitude((float) location.getLongitude())
                                    .arrivalDate(LocalDate.now())
                                    .departureDate(LocalDate.now().plusDays(1)).build();

                            view.startSearchResultsActivity(searchResultsInput, useGraphQlAvailabilities,
                                    deviceLocaleProvider.getDeviceLocale().getCountry().toLowerCase(),
                                    deviceLocaleProvider.getDeviceLanguage());
                        },
                        throwable -> view.closeScreenWithError());
    }

    @Override
    protected void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDestroy();
    }

    public interface View extends PresenterView {
        Observable<Boolean> onLocationPermissionRequested();

        void startSearchResultsActivity(
                @NonNull SearchResultsInput searchResultsInput,
                boolean useGraphQlAvailabilities,
                String country,
                String language);

        void closeScreenWithError();
    }
}
