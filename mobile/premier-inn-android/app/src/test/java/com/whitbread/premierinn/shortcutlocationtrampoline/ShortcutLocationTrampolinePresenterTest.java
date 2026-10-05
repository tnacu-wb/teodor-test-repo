package com.whitbread.premierinn.shortcutlocationtrampoline;


import android.location.Location;

import com.whitbread.premierinn.common.utils.LocationProvider;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn;
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository;
import com.whitbread.premierinn.searchresults.SearchResultsInput;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.LocalDate;

import io.reactivex.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

@RunWith(MockitoJUnitRunner.class)
public class ShortcutLocationTrampolinePresenterTest {

    @Mock
    LocationProvider locationProviderMock;
    @Mock
    ShortcutLocationTrampolinePresenter.View viewMock;
    @Mock
    Location locationMock;

    @Mock
    IsFeatureOn isFeatureOn;

    @Mock
    DeviceLocaleProvider deviceLocaleProvider;

    private ShortcutLocationTrampolinePresenter presenter;

    @Before
    public void setup() {
        presenter = new ShortcutLocationTrampolinePresenter(locationProviderMock, isFeatureOn, deviceLocaleProvider);
        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_USE_GRAPHQL_AVAILABILITIES)).thenReturn(true);
        when(deviceLocaleProvider.getDeviceLocale()).thenReturn(Locale.UK);
        when(deviceLocaleProvider.getDeviceLanguage()).thenReturn("en");
    }

    @Test
    public void testGetLocationSuccessful() {
        double latitude = 102;
        double longitude = 102;
        when(viewMock.onLocationPermissionRequested()).thenReturn(Observable.just(true));
        when(locationProviderMock.locationUpdateObservable()).thenReturn(Observable.just(locationMock));
        when(locationMock.getLatitude()).thenReturn(latitude);
        when(locationMock.getLongitude()).thenReturn(longitude);

        presenter.attachView(viewMock);

        SearchResultsInput searchResultsInput = SearchResultsInput.builderWithDefaults()
                .placeName("My location")
                .latitude((float) latitude)
                .longitude((float) longitude)
                .arrivalDate(LocalDate.now())
                .departureDate(LocalDate.now().plusDays(1)).build();

        verify(viewMock).startSearchResultsActivity(searchResultsInput, true, "gb", "en");
    }

    @Test
    public void testGetLocationError() {
        when(viewMock.onLocationPermissionRequested()).thenReturn(Observable.just(false));
        presenter.attachView(viewMock);

        verify(viewMock).closeScreenWithError();
    }
}
