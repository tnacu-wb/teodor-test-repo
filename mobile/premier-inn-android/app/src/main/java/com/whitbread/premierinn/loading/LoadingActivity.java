package com.whitbread.premierinn.loading;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Action.CIOL_DEEPLINK_ACTION;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.CIOL_DEEPLINKING;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.CIOL_FLOW;
import static com.whitbread.premierinn.common.deeplink.DeeplinkMapperKt.CIOL_BUNDLE;
import static com.whitbread.premierinn.common.deeplink.DeeplinkMapperKt.CIOL_DESTINATION;
import static com.whitbread.premierinn.common.deeplink.DeeplinkMapperKt.HDP_BUNDLE;
import static com.whitbread.premierinn.common.deeplink.DeeplinkMapperKt.HDP_DESTINATION;
import static com.whitbread.premierinn.common.deeplink.DeeplinkMapperKt.HOME_BUNDLE;
import static com.whitbread.premierinn.common.deeplink.DeeplinkMapperKt.HOME_DESTINATION;
import static com.whitbread.premierinn.common.deeplink.DeeplinkMapperKt.MY_ACCOUNT_EMPLOYEE_OFFER;
import static com.whitbread.premierinn.common.deeplink.DeeplinkMapperKt.SRP_BUNDLE;
import static com.whitbread.premierinn.common.deeplink.DeeplinkMapperKt.SRP_DESTINATION;
import static com.whitbread.premierinn.common.utils.StringUtils.EMPTY_STRING;
import static com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.formattedCheckInDay;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.deeplink.DeepLink;
import com.appsflyer.deeplink.DeepLinkResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.account.AccountActivity;
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData;
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsModel;
import com.whitbread.premierinn.common.activity.BasePresenterActivity;
import com.whitbread.premierinn.common.appsflyer.AppsFlyerHelper;
import com.whitbread.premierinn.common.deeplink.DeepLinkDataUrl;
import com.whitbread.premierinn.common.deeplink.DeeplinkContent;
import com.whitbread.premierinn.common.deeplink.model.HotelDetailsDeeplinkModel;
import com.whitbread.premierinn.common.deeplink.model.SearchResultDeeplinkModel;
import com.whitbread.premierinn.common.pushNotification.PushNotificationFactory;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.data.common.DataExtensionsKt;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.databinding.ActivityLoadingScreenBinding;
import com.whitbread.premierinn.findbooking.FindBookingActivity;
import com.whitbread.premierinn.findbooking.FindBookingInput;
import com.whitbread.premierinn.gdpr.GdprComposeActivityKt;
import com.whitbread.premierinn.hoteldetails.HotelDetailsActivity;
import com.whitbread.premierinn.hoteldetails.HotelDetailsInput;
import com.whitbread.premierinn.landing.LandingActivityIntent;
import com.whitbread.premierinn.landing.model.LandingDeeplinkModel;
import com.whitbread.premierinn.landing.model.LandingInputModel;
import com.whitbread.premierinn.notifications.NotificationsActivity;
import com.whitbread.premierinn.notifications.NotificationsInfo;
import com.whitbread.premierinn.searchresults.SearchResultsActivityKt;
import com.whitbread.premierinn.searchresults.SearchResultsInput;
import com.whitbread.premierinn.utils.ExtensionFunctionsKt;
import org.jetbrains.annotations.NotNull;
import org.threeten.bp.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

@AndroidEntryPoint
public class LoadingActivity extends BasePresenterActivity<LoadingPresenter.View, ActivityLoadingScreenBinding, LoadingPresenter>
        implements LoadingPresenter.View {

    private static final int GDPR_REQUEST_CODE = 43879;
    private static final int NOTIFICATIONS_REQUEST_CODE = 43880;
    private static final String NOTIFICATION_KEY_GOOGLE = "google";
    private static final String NOTIFICATION_KEY_GCM = "gcm";

    private final Relay<Object> gdprPrivacyPolicyAccepted = PublishRelay.create();
    private final Relay<Object> notificationMessageAccepted = PublishRelay.create();
    private final Relay<String> appsFlyerDeeplink = PublishRelay.create();
    private String deeplinkData = EMPTY_STRING;
    private Map<String, String> pushNotificationData = Collections.emptyMap();

    @Inject LogService logService;
    @Inject LoadingPresenter presenter;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @NonNull
    @Override
    protected ActivityLoadingScreenBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityLoadingScreenBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        processIntent();
        super.onCreate(savedInstanceState);
        checkForGooglePlayServices();
    }


    private void processIntent() {
        Intent intent = getIntent();
        boolean isDeeplinkIntent = ExtensionFunctionsKt.isDeeplinkIntent(intent);
        boolean isPushNotification = ExtensionFunctionsKt.isNotificationIntent(intent);
        if (isDeeplinkIntent) {
            loadDeeplink(intent);
        } else if (isPushNotification) {
            loadPushNotification(intent);
        } else {
            deeplinkData = DeepLinkDataUrl.INSTANCE.getUrl();
            pushNotificationData = new HashMap<>();
        }
    }

    private void loadPushNotification(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            pushNotificationData = bundleToMap(extras);
        } else {
            pushNotificationData = new HashMap<>();
        }
    }

    private void loadDeeplink(Intent intent) {
        Uri appLinkData = intent.getData();
        if (appLinkData != null) {
            deeplinkData = appLinkData.toString();
        } else {
            deeplinkData = EMPTY_STRING;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processIntent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    public Map<String, String> bundleToMap(Bundle extras) {
        Map<String, String> map = new HashMap<>();

        Set<String> keySet = extras.keySet();
        for (String key : keySet) {
            if (!key.contains(NOTIFICATION_KEY_GOOGLE) && !key.contains(NOTIFICATION_KEY_GCM)) {
                map.put(key, extras.getString(key));
            }
        }

        return map;
    }

    private void checkForGooglePlayServices() {
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        logService.log("Play Services OKAY");
                    } else {
                        logService.logException(Objects.requireNonNull(task.getException()), "Play Services version NOT OKAY");
                    }
                });
    }

    @Override
    public void startHomeActivity() {
        startActivity(LandingActivityIntent.INSTANCE.create(this));
        finish();
    }

    @Override
    public void loading() {
        binding.progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void startGDPRActivity() {
        DeepLinkDataUrl.INSTANCE.setUrl(deeplinkData);
        startActivity(GdprComposeActivityKt.createIntent(this));
        finish();
    }

    public void startNotificationActivity(NotificationsInfo notificationsInfo) {
        startActivityForResult(NotificationsActivity.createIntent(this, notificationsInfo, false),
                NOTIFICATIONS_REQUEST_CODE);
    }

    @Override
    public Observable<Object> gdprPrivacyPolicyAccepted() {
        return gdprPrivacyPolicyAccepted;
    }

    @Override
    public Observable<Object> notificationMessageAccepted() {
        return notificationMessageAccepted;
    }

    @Override
    public Single<String> getDeeplink() {
        return Single.just(deeplinkData);
    }

    @Override
    public Single<Map<String, String>> getPushNotificationData() {
        return Single.just(pushNotificationData);
    }

    @Override
    public Observable<String> getAppsFlyerDeeplink() {
        return appsFlyerDeeplink;
    }

    @Override
    public void initAppsFlyerAndCheckForDeferredDeeplink() {
        if (!AppsFlyerHelper.INSTANCE.isInitialized()) {
            AppsFlyerHelper.INSTANCE.getAppsFlyerLibInstance().subscribeForDeepLink(deepLinkResult -> {
                AppsFlyerHelper.INSTANCE.getAppsFlyerLibInstance().unregisterConversionListener();
                DeepLinkResult.Status dlStatus = deepLinkResult.getStatus();
                DeepLink deepLinkObj = deepLinkResult.getDeepLink();

                if (dlStatus != DeepLinkResult.Status.FOUND || deepLinkObj == null) {
                    startHomeActivity();
                } else if (Boolean.TRUE.equals(deepLinkObj.isDeferred())) {
                    try {
                        appsFlyerDeeplink.accept(deepLinkObj.getDeepLinkValue());
                    } catch (Exception e) {
                        startHomeActivity();
                    }
                }
            });

            AppsFlyerConversionListener conversionListener =  new AppsFlyerConversionListener() {
                @Override
                public void onConversionDataSuccess(Map<String, Object> conversionDataMap) {
                    AppsFlyerHelper.INSTANCE.getAppsFlyerLibInstance().unregisterConversionListener();
                    startHomeActivity();
                }

                @Override
                public void onConversionDataFail(String errorMessage) {
                    AppsFlyerHelper.INSTANCE.getAppsFlyerLibInstance().unregisterConversionListener();
                    startHomeActivity();
                }

                @Override
                public void onAppOpenAttribution(Map<String, String> attributionData) {
                    // do nothing
                }

                @Override
                public void onAttributionFailure(String errorMessage) {
                    // do nothing
                }
            };
            AppsFlyerHelper.INSTANCE.initAppsFlyer(this, conversionListener);
        } else {
            startHomeActivity();
        }
    }

    public void showEnableEmployeeOfferPopUp() {
        new AlertDialog.Builder(this, R.style.PurpleDialog)
                .setTitle(getString(R.string.home_employee_offer_pop_up_title))
                .setMessage(getString(R.string.home_employee_offer_pop_up_message))
                .setNegativeButton(R.string.home_employee_offer_cancel, (dialog, i) -> {
                    dialog.dismiss();
                    startHomeActivity();
                })
                .setPositiveButton(R.string.home_employee_offer_enable, (dialog, i) -> {
                    goToMyAccount();
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    @Override
    public void showForceUpdate() {
        startActivity(ForceUpdateActivity.createIntent(this));
        finish();
    }

    @Override
    public void showBartDowntimeScreen() {
        startActivity(BartDowntimeActivity.createIntent(this));
        finish();
    }

    @Override
    public void handlePushNotification(PushNotificationFactory factory, Map<String, String> data) {
        Intent intent = factory.createIntent(this, data);
        startActivity(intent);
        finish();
    }

    private void goToMyAccount() {
        AccountActivity.startForEmployeeOffer(this, true);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GDPR_REQUEST_CODE:
                    gdprPrivacyPolicyAccepted.accept(new Object());
                    break;
                case NOTIFICATIONS_REQUEST_CODE:
                    notificationMessageAccepted.accept(new Object());
                    break;
                default:
                    // Nothing to do
            }
        } else if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {
                case GDPR_REQUEST_CODE:
                case NOTIFICATIONS_REQUEST_CODE:
                    finish();
                    break;
                default:
                    // Nothing to do
            }
        }
    }

    @Override
    public void navigateDeeplink(DeeplinkContent deeplinkContent) {
        String destination = deeplinkContent.getDestination();
        switch (destination) {
            case CIOL_DESTINATION:
                FindBookingInput input = deeplinkContent.getBundleData().getParcelable(CIOL_BUNDLE);
                if (input != null) {
                    String checkInDay = formattedCheckInDay(DataExtensionsKt.toLocalDate(input.getArrivalDate()));
                    analytics.track(
                            CIOL_DEEPLINKING,
                            new CiolAnalyticsData(
                                    new CiolAnalyticsModel(
                                            input.getBookingReference(),
                                            CIOL_DEEPLINK_ACTION,
                                            input.getArrivalDate().replace("-", "/"),
                                            EMPTY_STRING,
                                            checkInDay != null ? checkInDay : EMPTY_STRING,
                                            true
                                    ),
                                    CIOL_FLOW,
                                    false,
                                    EMPTY_STRING,
                                    input.getCampaignModel())
                    );
                    startActivity(FindBookingActivity.createIntent(this, input));
                    finish();
                }
                break;
            case SRP_DESTINATION:
                DeviceLocaleProvider deviceLocaleProvider = new DeviceLocaleProvider(this);
                SearchResultDeeplinkModel searchDeeplink = deeplinkContent.getBundleData().getParcelable(SRP_BUNDLE);

                if (searchDeeplink != null) {
                    LocalDate arrivalDate = (searchDeeplink.getYear() > 0 && searchDeeplink.getMonth() > 0 && searchDeeplink.getDay() > 0)
                            ? LocalDate.of(searchDeeplink.getYear(), searchDeeplink.getMonth(), searchDeeplink.getDay()) : LocalDate.now();

                    SearchResultsInput searchInput =
                            SearchResultsInput.builder()
                                    .numRooms(searchDeeplink.getRooms())
                                    .adults(searchDeeplink.getAdults())
                                    .children(searchDeeplink.getChildren())
                                    .infants(searchDeeplink.getInfants())
                                    .cots(searchDeeplink.getCots())
                                    .roomTypeCodes(searchDeeplink.getRoomsType())
                                    .arrivalDate(arrivalDate)
                                    .departureDate(arrivalDate.plusDays(searchDeeplink.getNights()))
                                    .latitude(0f)
                                    .longitude(0f)
                                    .placeName(searchDeeplink.getPlaceName())
                                    .campaignModel(searchDeeplink.getCampaignModel())
                                    .build();

                    Intent searchIntent = SearchResultsActivityKt.createSearchResultIntent(this,
                            searchInput, null, null, false, searchDeeplink.getPlaceId(),
                            deviceLocaleProvider.getDeviceLocale().getCountry().toLowerCase(),
                            deviceLocaleProvider.getDeviceLanguage());
                    startActivity(searchIntent);
                    finish();
                }
                break;

            case HDP_DESTINATION:
                HotelDetailsDeeplinkModel hdpModel = deeplinkContent.getBundleData().getParcelable(HDP_BUNDLE);
                if (hdpModel != null) {
                    LocalDate arrivalDate = LocalDate.of(hdpModel.getYear(), hdpModel.getMonth(), hdpModel.getDay());
                    SearchResultsInput searchResultsInput =
                            SearchResultsInput.builder()
                                    .numRooms(hdpModel.getRooms())
                                    .adults(hdpModel.getAdults())
                                    .children(hdpModel.getChildren())
                                    .infants(hdpModel.getInfants())
                                    .cots(hdpModel.getCots())
                                    .roomTypeCodes(hdpModel.getRoomsType())
                                    .arrivalDate(arrivalDate)
                                    .departureDate(arrivalDate.plusDays(hdpModel.getNights()))
                                    .latitude(0f)
                                    .longitude(0f)
                                    .placeName(EMPTY_STRING)
                                    .build();
                    Intent intent = HotelDetailsActivity.createIntent(
                            this,
                            HotelDetailsInput.builder()
                                    .hotelCode(hdpModel.getHotelCode())
                                    .brand(hdpModel.getHotelBrand())
                                    .distanceFromSearchedLocation(0f)
                                    .cameFromMapView(false)
                                    .searchResultsInput(searchResultsInput)
                                    .campaignModel(hdpModel.getCampaignModel())
                                    .build()
                    );
                    startActivity(intent);
                    finish();
                }
                break;

            case MY_ACCOUNT_EMPLOYEE_OFFER:
                showEnableEmployeeOfferPopUp();
                break;
            case HOME_DESTINATION:
                LandingDeeplinkModel landingDeeplinkModel = deeplinkContent.getBundleData().getParcelable(HOME_BUNDLE);

                if (landingDeeplinkModel != null && landingDeeplinkModel.getPromoCodeInput() != null) {
                    startActivity(LandingActivityIntent.INSTANCE.create(this,
                            new LandingInputModel(
                                    landingDeeplinkModel.getPromoCodeInput(),
                                    null,
                                    null,
                                    landingDeeplinkModel.getCampaignModel()
                            )
                    ));
                    finish();
                } else {
                    startHomeActivity();
                }
                break;
            default: startHomeActivity();
        }
    }

    @Override
    protected @NotNull LoadingPresenter createPresenter() {
        return presenter;
    }

    @Override
    protected LoadingPresenter.@NotNull View provideView() {
        return this;
    }
}