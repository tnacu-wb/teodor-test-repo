package com.whitbread.premierinn.plantrip;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type;
import static com.whitbread.premierinn.common.utils.LocationUtils.GOOGLE_MAPS_URL;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import com.whitbread.premierinn.api.response.availability.Coordinates;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.common.utils.LocationProvider;
import com.whitbread.premierinn.common.utils.LocationUtils;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.data.hotel.mapper.HotelMappersKt;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain;
import com.whitbread.premierinn.domain.common.hoteldetails.usecase.GraphQLHotelDetailsUseCase;
import com.whitbread.premierinn.domain.hotel.entity.Hotel;
import java.util.Locale;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;

@ActivityRetainedScoped
public class PlanTripPresenter extends Presenter<PlanTripPresenter.View> {

    private String hotelCode;
    private final LocationProvider locationProvider;
    private final CompositeDisposable viewDisposable;
    private final TrackingAnalytics analytics;
    private final DeviceLocaleProvider deviceLocaleProvider;
    private final GraphQLHotelDetailsUseCase graphQLHotelDetailsUseCase;
    private Observable<HotelInformationDomain> hotelInformationDomainObservable;

    @Inject
    public PlanTripPresenter(
                             @NonNull CompositeDisposable compositeDisposable,
                             @NonNull GraphQLHotelDetailsUseCase graphQLHotelDetailsUseCase,
                             @NonNull LocationProvider provider,
                             @NonNull TrackingAnalytics analytics,
                             @NonNull DeviceLocaleProvider deviceLocaleProvider) {
        this.viewDisposable = compositeDisposable;
        this.graphQLHotelDetailsUseCase = graphQLHotelDetailsUseCase;
        this.locationProvider = provider;
        this.analytics = analytics;
        this.deviceLocaleProvider = deviceLocaleProvider;
    }

    public void initParams(@NonNull String hotelCode) {
        this.hotelCode = hotelCode;
    }

    @Override
    public void onAttachView(View view) {
            String deviceLanguage = deviceLocaleProvider.getDeviceLanguage();
            Locale deviceLocale = deviceLocaleProvider.getDeviceLocale();
            String country = deviceLocaleProvider.getCountryIfRegion(deviceLocale).toLowerCase(deviceLocale);
            hotelInformationDomainObservable = graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(
                            country, hotelCode, deviceLanguage)
                    .toObservable().cache();
            viewDisposable.add(view.onDirectionClicked()
                    .flatMap(__ -> hotelInformationDomainObservable)
                    .map(hotelInfo -> String.format(Locale.ENGLISH, GOOGLE_MAPS_URL,
                            hotelInfo.getCoordinates().getLatitude(), hotelInfo.getCoordinates().getLongitude()))
                    .subscribe(view::startUriActivity));

            viewDisposable.add(view.onLocationPermissionRequested()
                    .flatMap(__ -> locationProvider.locationUpdateObservable())
                    .flatMap(location -> hotelInformationDomainObservable, Pair::new)
                    .subscribe(pair -> processLocation(view, pair), throwable -> view.finishWithMessage())
            );

            viewDisposable.add(hotelInformationDomainObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(hotelInfo -> processHotelInfo(view, hotelInfo),
                            throwable -> view.finishWithMessage()
                    ));
        analytics.track(ScreenState.PLAN_YOUR_TRIP, Type.MY_PREMIER_INN);
    }

    private void processLocation(@NonNull View view, @NonNull Pair<Location, HotelInformationDomain> pair) {
        if (isViewAttached()) {
            Location location = LocationUtils.create(pair.second.getCoordinates().getLatitude(),
                    pair.second.getCoordinates().getLongitude());
            view.setDistanceFromUser(LocationUtils.distanceInMiles(pair.first, location));
        }
    }

    private void processHotelInfo(@NonNull View view, @NonNull HotelInformationDomain hotelInfo) {
        if (isViewAttached()) {
            Coordinates coordinates = Coordinates.create(hotelInfo.getCoordinates().getLatitude(),
                    hotelInfo.getCoordinates().getLongitude());
            view.setHotelCoordinates(coordinates, HotelMappersKt.mapToBrand(hotelInfo.getBrand()));

            if (hotelInfo.getContactDetails() != null) {
                view.setTelephone(hotelInfo.getContactDetails().getPhone());
            }

            String addressLine2 = hotelInfo.getAddress().getAddressLine2() + ", " + hotelInfo.getAddress().getAddressLine3();
            view.setAddress(hotelInfo.getAddress().getAddressLine1(), addressLine2);

            view.setDirectionsInfo(hotelInfo.getDirections());
            view.setParkingInfo(hotelInfo.getParkingDescription());
            view.setHotelName(hotelInfo.getName());
        }
    }

    @Override
    public void onDestroy() {
        if (viewDisposable.size() > 0) {
            viewDisposable.clear();
        }
    }

    interface View extends PresenterView {
        void setHotelCoordinates(Coordinates hotelCoordinates, Hotel.Brand hotelBrand);

        Observable<Unit> onDirectionClicked();

        Observable<Boolean> onLocationPermissionRequested();

        void setAddress(@NonNull String line1, @NonNull String line2);

        void setTelephone(@NonNull String telephone);

        void setDirectionsInfo(@NonNull String direction);

        void setParkingInfo(@NonNull String parking);

        void startUriActivity(@NonNull String uri);

        void setDistanceFromUser(float distanceInMiles);

        void setHotelName(String name);

        void finishWithMessage();
    }
}
