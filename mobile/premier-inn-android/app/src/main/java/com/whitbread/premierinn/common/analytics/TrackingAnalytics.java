package com.whitbread.premierinn.common.analytics;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Action;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Value;
import static com.whitbread.premierinn.common.format.DateFormat.HOUR_AND_MINUTES_AND_SECONDS;
import static com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils.format;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adobe.marketing.mobile.MobileCore;
import com.contentsquare.android.Contentsquare;
import com.contentsquare.android.api.model.CustomVar;
import com.contentsquare.android.api.model.Transaction;
import com.google.gson.Gson;
import com.whitbread.premierinn.BuildConfig;
import com.whitbread.premierinn.api.response.ErrorBody;
import com.whitbread.premierinn.businessbooker.domain.customer.repository.BusinessCustomerRepository;
import com.whitbread.premierinn.common.contentSquare.CSQScreenNameMapper;
import com.whitbread.premierinn.common.utils.StringUtils;
import com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils;
import com.whitbread.premierinn.domain.customer.entity.Customer;
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository;

import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Stream;

import javax.inject.Inject;
import retrofit2.Response;

public class TrackingAnalytics {

    private final Gson gson;
    private final CustomerRepository customerRepository;
    private final BusinessCustomerRepository businessCustomerRepository;
    private CSQScreenNameMapper screenNameMapper;

    @Inject
    public TrackingAnalytics(
            @NonNull CustomerRepository customerRepository,
            @NonNull BusinessCustomerRepository businessCustomerRepository,
            @NonNull Gson gson,
            @NonNull CSQScreenNameMapper screenNameMapper) {
        this.gson = gson;
        this.customerRepository = customerRepository;
        this.businessCustomerRepository = businessCustomerRepository;
        this.screenNameMapper = screenNameMapper;
    }

    // Track with common context Data
    public void track(@NonNull String screenName, @NonNull String stateType) {
        trackState(screenName, constructCommonContextData(stateType), constructCommonCustomCSQVars(screenName, stateType));
    }

    // Track ContentSquare only (no Adobe Analytics)
    public void trackContentSquare(@NonNull String screenName, @NonNull String stateType) {
        CustomVar[] customCSQVars = constructCommonCustomCSQVars(screenName, stateType);
        sendToContentSquare(screenName, customCSQVars);
    }

    // Track Adobe Analytics only (no ContentSquare)
    public void trackAdobe(@NonNull String screenName, @NonNull String stateType) {
        sendToAdobe(screenName, constructCommonContextData(stateType));
    }

    // Track without custom CSQ variables (used for back navigation)
    private void trackWithoutCustomVars(@NonNull String screenName, @NonNull String stateType) {
        trackState(screenName, constructCommonContextData(stateType), null);
    }

    // Track with common context Data merged with analytics context Data
    public void track(@NonNull String screenName, @NonNull AnalyticsData data) {
        trackState(screenName,
                mergeWithCommonContextDataIfExist(data.contextData()),
                mergeWithCommonCustomCSQVarsIfExist(screenName,
                        !data.customCSQVars().isEmpty()
                                ? data.customCSQVars().toArray(new CustomVar[0])
                                : null));
    }

    // Track action with common context Data merged with analytics context Data
    public void trackAction(@NonNull String action, @NonNull AnalyticsData data) {
        MobileCore.trackAction(format(appBaseInfo(action)), mergeWithCommonContextDataIfExist(data.contextData()));
    }

    private Map<String, String> mergeWithCommonContextDataIfExist(@Nullable Map<String, String> contextData) {
        if (contextData != null) {
            Map<String, String> mergedContextData = constructCommonContextData(null);
            mergedContextData.putAll(contextData);
            return mergedContextData;
        }
        return null;
    }

    private CustomVar[] mergeWithCommonCustomCSQVarsIfExist(@NonNull String screenName, @Nullable CustomVar[] customCSQVars) {
        CustomVar[] commonCustomCSQVars = constructCommonCustomCSQVars(screenName, null);
        return Stream.concat(
                        commonCustomCSQVars == null ? Stream.empty() : Stream.of(commonCustomCSQVars),
                        customCSQVars == null ? Stream.empty() : Stream.of(customCSQVars)
                )
                .toArray(CustomVar[]::new);
    }

    // Helper method to send tracking data to Adobe Analytics
    private void sendToAdobe(@NonNull String screenName, @Nullable Map<String, String> contextData) {
        MobileCore.trackState(format(appBaseInfo(screenName)), contextData);
    }

    // Helper method to send tracking data to ContentSquare
    private void sendToContentSquare(@NonNull String screenName, @Nullable CustomVar[] customCSQVars) {
        if (customCSQVars != null) {
            Contentsquare.send(format(appBaseInfo(screenName)), customCSQVars);
        } else {
            Contentsquare.send(format(appBaseInfo(screenName)));
        }
    }

    private void trackState(@NonNull String screenName, @Nullable Map<String, String> contextData,
                            @Nullable CustomVar[] customCSQVars) {
        sendToAdobe(screenName, contextData);
        sendToContentSquare(screenName, customCSQVars);
    }

    public void trackError(@NonNull ErrorBody errorBody) {
        Map<String, String> contextData = new HashMap<>();
        if (errorBody.errorMessage() != null) {
            contextData.put(Key.ERROR_MESSAGE, errorBody.errorMessage());
        }
        if (errorBody.code() != 0) {
            contextData.put(Key.ERROR_CODE, String.valueOf(errorBody.code()));
        }
        MobileCore.trackAction(format(appBaseInfo(Action.ERROR)), contextData);
    }

    public void trackError(String code, String errorMessage, String screenName, String keyPollingError) {
        Map<String, String> contextData = new HashMap<>();
        contextData.put(Key.ERROR_MESSAGE, errorMessage);
        contextData.put(Key.ERROR_CODE, code);
        contextData.put(keyPollingError, "true");

        MobileCore.trackAction(format(appBaseInfo(Action.ERROR)) + " " + screenName, contextData);
    }

    public void trackError(String screenName, String keyPollingError) {
        Map<String, String> contextData = new HashMap<>();
        contextData.put(keyPollingError, "true");

        MobileCore.trackAction(format(appBaseInfo(Action.ERROR)) + " " + screenName, contextData);
    }

    public void trackError(@NonNull Response failedResponse) {
        ErrorBody errorBody = ErrorBody.fromResponse(failedResponse, gson);
        trackError(errorBody);
    }

    private Map<String, String> constructCommonContextData(@Nullable String stateType) {
        Map<String, String> contextData = new HashMap<>();
        contextData.put(Key.ENVIRONMENT, BuildConfig.FLAVOR.equals("stage") ? Value.VARIANT_DEBUG : Value.VARIANT_PRODUCTION);
        contextData.put(Key.TIME, LocalTime.now().format(DateTimeFormatter.ofPattern((HOUR_AND_MINUTES_AND_SECONDS.toPattern()))));
        contextData.put(Key.TIME_ZONE, TimeZone.getDefault().getID());
        contextData.put(Key.LANGUAGE, Locale.getDefault().getLanguage());

        final Customer savedCustomer = customerRepository.getCustomerFromSharedPref();
        String userId = savedCustomer.getGuestHistoryNumber();
        if (savedCustomer.getCustomerAccountID() != null
                && !savedCustomer.getCustomerAccountID().isEmpty()) {
            userId = savedCustomer.getCustomerAccountID();
        }
        contextData.put(Key.USER_ID, userId);
        contextData.put(
                Key.LOGIN,
                !StringUtils.isBlank(customerRepository.getLoggedInCustomerEmail())
                        || !StringUtils.isBlank(businessCustomerRepository.getLoggedInBusinessEmail())
                        ? Value.LOGGED_IN
                        : Value.LOGGED_OUT
        );
        if (!StringUtils.isBlank(stateType)) {
            contextData.put(Key.SCREEN_TYPE, format(appBaseInfo(stateType)));
        }

        if (savedCustomer.getBusiness() != null) {
            contextData.put(Key.BUSINESS_USER_LEVEL_ID, Value.USER_LEVEL_BUSINESS_BOOKER);
            contextData.put(Key.BUSINESS_COMPANY_ID_KEY, savedCustomer.getCompanyId());
        }

        return contextData;
    }

    public CustomVar[] constructCommonCustomCSQVars(@Nullable String screenName, @Nullable String stateType) {
        CustomVar customVar1 = screenName == null ? null : new CustomVar(20, Key.PAGE_NAME, format(appBaseInfo(screenName)));
        CustomVar customVar2 = stateType == null ? null : new CustomVar(2, Key.SCREEN_TYPE, format(appBaseInfo(stateType)));
        CustomVar customVar3 = new CustomVar(1, Key.LOGIN, !StringUtils.isBlank(customerRepository.getLoggedInCustomerEmail())
                ? Value.LOGGED_IN : Value.LOGGED_OUT);

        return Stream.of(customVar1, customVar2, customVar3)
                .filter(Objects::nonNull)
                .toArray(CustomVar[]::new);
    }

    public void activityOnResume(@NonNull Activity activity) {
        MobileCore.setApplication(activity.getApplication());
        MobileCore.lifecycleStart(null);
    }

    public void activityOnPause() {
        MobileCore.lifecyclePause();
    }

    public String appBaseInfo(@NonNull String screenName) {
        String language = Locale.getDefault().getLanguage().equals("de") ? "DE" : "UK";
        if (customerRepository.getCustomerFromSharedPref().getBusiness() != null) {
            return String.format("PB:%s: %s", language, screenName);
        } else {
            return String.format("PI:%s: %s", language, screenName);
        }
    }

    /**
     * Track screen view for back navigation.
     * Converts Activity class name to ScreenState constant and tracks with full formatting.
     * Called from BaseActivity when user navigates back to a previous screen.
     * Note: Does not send customCSQVars to ContentSquare (only sends screen name).
     *
     * @param activity The current Activity (destination of back navigation)
     */
    public void trackActivityBackNavigation(@NonNull Activity activity) {
        String className = activity.getClass().getSimpleName();
        String screenState = screenNameMapper.map(className);
        String stateType = TrackingAnalyticsUtils.getStateType(activity.getClass().getCanonicalName());

        // Use trackWithoutCustomVars to avoid sending customCSQVars on back navigation
        trackWithoutCustomVars(screenState, stateType);
    }

    /**
     * Track screen view for fragment back navigation.
     * Converts Fragment class name to ScreenState constant and tracks with full formatting.
     * Called from BaseFragment when user navigates back to a previous fragment.
     * Note: Does not send customCSQVars to ContentSquare (only sends screen name).
     *
     * @param fragment The current Fragment (destination of back navigation)
     */
    public void trackFragmentBackNavigation(@NonNull Fragment fragment) {
        String className = fragment.getClass().getSimpleName();
        String screenState = screenNameMapper.map(className);
        String stateType = TrackingAnalyticsUtils.getStateType(fragment.getClass().getCanonicalName());

        // Use trackWithoutCustomVars to avoid sending customCSQVars on back navigation
        trackWithoutCustomVars(screenState, stateType);
    }

    public void trackCSQTransaction(Float amount, String currency, String id) {
        Contentsquare.send(Transaction.Companion.builder(amount, currency).id(id).build());
    }
}