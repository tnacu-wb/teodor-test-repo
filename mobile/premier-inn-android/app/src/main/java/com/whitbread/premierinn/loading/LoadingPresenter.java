package com.whitbread.premierinn.loading;

import static com.whitbread.premierinn.common.deeplink.DeeplinkMapperKt.HDP_BUNDLE;
import static com.whitbread.premierinn.data.common.Constants.DEFAULT_MAX_ARRIVAL_DATE;
import static com.whitbread.premierinn.data.common.Constants.DEFAULT_MAX_NIGHTS_INN_BUSINESS;
import static com.whitbread.premierinn.data.common.Constants.DEFAULT_MAX_NIGHTS_LEISURE;
import static com.whitbread.premierinn.data.common.Constants.DEFAULT_MAX_ROOMS_AMEND;
import static com.whitbread.premierinn.data.common.Constants.DEFAULT_MAX_ROOMS_INN_BUSINESS;
import static com.whitbread.premierinn.data.common.Constants.DEFAULT_MAX_ROOMS_LEISURE;
import static com.whitbread.premierinn.domain.common.Constants.EMPTY_STRING_DOMAIN;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager;
import com.whitbread.premierinn.common.AsyncResult;
import com.whitbread.premierinn.common.AsyncResultKt;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.FirebaseLogger;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.deeplink.DeeplinkContent;
import com.whitbread.premierinn.common.deeplink.DeeplinkMapper;
import com.whitbread.premierinn.common.deeplink.DeeplinkMapperKt;
import com.whitbread.premierinn.common.deeplink.model.HotelDetailsDeeplinkModel;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.common.pushNotification.PushNotificationFactory;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager;
import com.whitbread.premierinn.di.IoScheduler;
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn;
import com.whitbread.premierinn.domain.graphql.hdp.usecase.GraphQLHDPUseCase;
import com.whitbread.premierinn.domain.resource.usecase.BartDownMaintenanceRequired;
import com.whitbread.premierinn.domain.resource.usecase.ForceUpdateRequired;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;
import com.whitbread.premierinn.notifications.NotificationsInfo;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@ActivityRetainedScoped
public class LoadingPresenter extends Presenter<LoadingPresenter.View> {
    private final ForceUpdateRequired forceUpdateRequired;
    private final SimplePersistenceManager persistenceManager;
    private final TrackingAnalytics analytics;
    private final LogService logService;
    private final IsFeatureOn isFeatureRequired;
    private final GetStringResource getStringResource;
    private final Scheduler scheduler;
    private final BartDownMaintenanceRequired bartDownMaintenanceRequired;
    private final GraphQLHDPUseCase graphQLHDPUseCase;
    private final DeviceLocaleProvider deviceLocaleProvider;
    private final DeeplinkMapper deeplinkMapper;
    private final PushNotificationFactory pushNotificationFactory;
    private final boolean isInnBusinessUser;
    private final BusinessPersistenceManager businessPersistenceManager;
    private final FirebaseLogger firebaseLogger;
    private boolean bartDown;
    private String deeplinkData;
    private Map<String, String> pushNotificationData;

    private final CompositeDisposable compositeDisposable;

    private static final long FORCE_UPDATE_TIMEOUT_IN_MS = 3000L;

    @Inject
    public LoadingPresenter(@NonNull ForceUpdateRequired forceUpdateRequired,
                            @NonNull BartDownMaintenanceRequired bartDownMaintenanceRequired,
                            @NonNull GraphQLHDPUseCase graphQLHDPUseCase,
                            @NonNull DeviceLocaleProvider deviceLocaleProvider,
                            @NonNull DeeplinkMapper deeplinkMapper,
                            @NonNull PushNotificationFactory pushNotificationFactory,
                            @NonNull SimplePersistenceManager persistenceManager,
                            @NonNull BusinessPersistenceManager businessPersistenceManager,
                            @NonNull CompositeDisposable compositeDisposable,
                            @NonNull TrackingAnalytics trackingAnalytics,
                            @NonNull FirebaseLogger firebaseLogger,
                            @NonNull LogService logService,
                            @NonNull IsFeatureOn isFeatureRequired,
                            @NonNull GetStringResource getStringResource,
                            @IoScheduler @NonNull Scheduler scheduler) {
        this.forceUpdateRequired = forceUpdateRequired;
        this.bartDownMaintenanceRequired = bartDownMaintenanceRequired;
        this.graphQLHDPUseCase = graphQLHDPUseCase;
        this.deviceLocaleProvider = deviceLocaleProvider;
        this.deeplinkMapper = deeplinkMapper;
        this.pushNotificationFactory = pushNotificationFactory;
        this.persistenceManager = persistenceManager;
        this.businessPersistenceManager = businessPersistenceManager;
        this.analytics = trackingAnalytics;
        this.firebaseLogger = firebaseLogger;
        this.logService = logService;
        this.isFeatureRequired = isFeatureRequired;
        this.getStringResource = getStringResource;
        this.scheduler = scheduler;
        this.compositeDisposable = compositeDisposable;
        this.isInnBusinessUser = !businessPersistenceManager.getBusinessCustomerEmail().equals(EMPTY_STRING_DOMAIN);
    }

    @Override
    public void onAttachView(View view) {
        analytics.track(AnalyticsConstants.ScreenState.LOADING, AnalyticsConstants.Type.HOME);
        pushNotificationData = view.getPushNotificationData().blockingGet();
        deeplinkData = view.getDeeplink().blockingGet();
        compositeDisposable.add(AsyncResultKt.mapToAsyncResult(
                        bartDownMaintenanceRequired.execute().subscribeOn(Schedulers.io()).toObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(state -> {
                    if (state instanceof AsyncResult.Loading) {
                        view.loading();
                    } else {
                        if (state instanceof AsyncResult.Success) {
                            bartDown = (boolean) ((AsyncResult.Success) state).getData();
                            if (bartDown) {
                                view.showBartDowntimeScreen();
                            } else {
                                checkForForceUpdate(view);
                            }
                        } else if (state instanceof AsyncResult.Error) {
                            logService.logException(((AsyncResult.Error) state).getError(), "error");
                            proceedToNextScreen(view);
                        }
                    }
                })
        );

        compositeDisposable.add(view.gdprPrivacyPolicyAccepted().subscribe(__ -> view.startHomeActivity()));

        compositeDisposable.add(view.notificationMessageAccepted().subscribe(__ -> view.startHomeActivity()));

        compositeDisposable.add(view.getAppsFlyerDeeplink().subscribe(appsFlyerDeeplink -> processDeeplinkData(view, appsFlyerDeeplink)));

        if (isInnBusinessUser) {
            businessPersistenceManager.saveValuesForBusinessRulesInnBusiness(
                    DEFAULT_MAX_NIGHTS_INN_BUSINESS,
                    DEFAULT_MAX_ROOMS_INN_BUSINESS,
                    DEFAULT_MAX_ARRIVAL_DATE);
        } else {
            persistenceManager.saveValuesForBusinessRulesLeisure(
                    DEFAULT_MAX_NIGHTS_LEISURE,
                    DEFAULT_MAX_ROOMS_LEISURE,
                    DEFAULT_MAX_ROOMS_AMEND,
                    DEFAULT_MAX_ARRIVAL_DATE);
        }
        persistenceManager.clearSelectedHomeScreenCriteria();

    }

    private void checkForForceUpdate(View view) {
            compositeDisposable.add(
                    AsyncResultKt.mapToAsyncResult(
                            forceUpdateRequired.execute()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .timeout(FORCE_UPDATE_TIMEOUT_IN_MS, TimeUnit.MILLISECONDS, scheduler)
                                    .toObservable()
                            )
                            .flatMap(state -> {
                                if (state instanceof AsyncResult.Loading) {
                                    return Observable.just(LoadingNavigation.Loading.INSTANCE);
                                } else {
                                    if (state instanceof AsyncResult.Success) {
                                        boolean forceUpdateRequired = (boolean) ((AsyncResult.Success) state).getData();
                                        if (forceUpdateRequired) {
                                            return Observable.just(LoadingNavigation.ForceUpdate.INSTANCE);
                                        } else {
                                            return Observable.just(LoadingNavigation.ForceUpdateNotRequired.INSTANCE);
                                        }
                                    } else if (state instanceof AsyncResult.Error) {
                                        logService.logException(((AsyncResult.Error) state).getError(), "error");
                                        return Observable.just(LoadingNavigation.Proceed.INSTANCE);
                                    }
                                }
                                return Observable.just(LoadingNavigation.Proceed.INSTANCE);
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(navigation -> {
                                        if (navigation instanceof LoadingNavigation.Loading) {
                                            view.loading();
                                        } else if (navigation instanceof LoadingNavigation.Proceed) {
                                            proceedToNextScreen(view);
                                        } else if (navigation instanceof LoadingNavigation.ForceUpdate) {
                                            view.showForceUpdate();
                                        } else if (navigation instanceof LoadingNavigation.ForceUpdateNotRequired) {
                                            if (pushNotificationData != null && !pushNotificationData.isEmpty()) {
                                                view.handlePushNotification(pushNotificationFactory, pushNotificationData);
                                            } else if (deeplinkData != null && !deeplinkData.isBlank()) {
                                                processDeeplinkData(view, deeplinkData);
                                            } else {
                                                proceedToNextScreen(view);
                                            }
                                        }
                                    })
            );
    }

    private void processDeeplinkData(View view, String deeplink) {
        DeeplinkContent content = deeplinkMapper.mapUrl(deeplink);
        if (!persistenceManager.hasAcceptedPrivacyPolicy()) {
            view.startGDPRActivity();
        } else {
            if (!content.getDestination().equals(DeeplinkMapperKt.HDP_DESTINATION)) {
                view.navigateDeeplink(content);
                return;
            }

            HotelDetailsDeeplinkModel model = content.getBundleData().getParcelable(HDP_BUNDLE);
            if (model == null || !model.getHotelCode().isEmpty()) {
                view.navigateDeeplink(content);
                return;
            }

            compositeDisposable.add(
                    getHotelInfoBySlug(content, model).subscribe(view::navigateDeeplink)
            );
        }

    }

    private Observable<DeeplinkContent> getHotelInfoBySlug(DeeplinkContent deeplinkContent, HotelDetailsDeeplinkModel model) {
        return graphQLHDPUseCase
                .fetchHotelInfoBySlug(
                        model.getSlug(),
                        deviceLocaleProvider.getDeviceLocale().getCountry().toLowerCase(),
                        deviceLocaleProvider.getDeviceLanguage()
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(hotelInformationSlugDomain -> {
                    Bundle newBundle = new Bundle();
                    newBundle.putParcelable(
                            HDP_BUNDLE,
                            model.copy(
                                    hotelInformationSlugDomain.getHotelId(),
                                    model.getDay(),
                                    model.getMonth(),
                                    model.getYear(),
                                    model.getNights(),
                                    model.getChildren(),
                                    model.getCots(),
                                    model.getAdults(),
                                    model.getInfants(),
                                    model.getRoomsType(),
                                    hotelInformationSlugDomain.getBrand(),
                                    model.getCampaignId(),
                                    model.getRooms(),
                                    model.getSlug(),
                                    model.getCampaignModel()
                            )
                    );
                    return deeplinkContent.copy(
                            deeplinkContent.getDeeplinkStatus(),
                            deeplinkContent.getDestination(),
                            newBundle,
                            deeplinkContent.getParams()
                    );
                }).toObservable();
    }

    private void proceedToNextScreen(@NonNull View view) {
        if (persistenceManager.hasAcceptedPrivacyPolicy()) {
            view.initAppsFlyerAndCheckForDeferredDeeplink();
        } else {
            view.startGDPRActivity();
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }

    public interface View extends PresenterView {
        void startHomeActivity();

        void startGDPRActivity();

        void startNotificationActivity(NotificationsInfo notificationsInfo);

        void loading();

        Observable<Object> gdprPrivacyPolicyAccepted();

        Observable<Object> notificationMessageAccepted();

        Single<String> getDeeplink();

        void navigateDeeplink(DeeplinkContent deeplinkContent);

        Single<Map<String, String>> getPushNotificationData();

        void showForceUpdate();

        void showBartDowntimeScreen();

        void handlePushNotification(PushNotificationFactory factory, Map<String, String> data);

        void initAppsFlyerAndCheckForDeferredDeeplink();

        Observable<String> getAppsFlyerDeeplink();
    }
}
