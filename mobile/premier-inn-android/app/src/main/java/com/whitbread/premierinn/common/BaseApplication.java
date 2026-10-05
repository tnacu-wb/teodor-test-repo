package com.whitbread.premierinn.common;

import static com.whitbread.premierinn.BuildConfig.ADOBE_APP_ID;
import static com.whitbread.premierinn.BuildConfig.STAGING;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.provider.FontRequest;
import androidx.emoji2.text.EmojiCompat;
import androidx.emoji2.text.FontRequestEmojiCompatConfig;

import com.adobe.marketing.mobile.Analytics;
import com.adobe.marketing.mobile.Assurance;
import com.adobe.marketing.mobile.Audience;
import com.adobe.marketing.mobile.CampaignClassic;
import com.adobe.marketing.mobile.Extension;
import com.adobe.marketing.mobile.Identity;
import com.adobe.marketing.mobile.Lifecycle;
import com.adobe.marketing.mobile.LoggingMode;
import com.adobe.marketing.mobile.MobileCore;
import com.adobe.marketing.mobile.Places;
import com.adobe.marketing.mobile.Signal;
import com.adobe.marketing.mobile.Target;
import com.adobe.marketing.mobile.UserProfile;
import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.whitbread.premierinn.BuildConfig;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.pushNotification.PiFirebaseMessagingService;
import com.whitbread.premierinn.common.service.LogService;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseApplication extends Application {

    public abstract void initSdks();

    @Override
    public void onCreate() {
        if (ProcessPhoenix.isPhoenixProcess(this)) {
            return;
        }

        // Skip full initialization for Akamai WebView process
        if (isAkamaiWebViewProcess()) {
            super.onCreate();
            return;
        }

        initAdobe();

        initEmojiCompat();

        initJava8TimeBackport();

        setRxAndroidAsyncScheduler();

        super.onCreate();

        subscribeToFcmTopic();

        initCrashlytics();

        initSdks();

        makeWebViewDebuggableIn(STAGING);

        // Disabling night mode as we're using DayNight theme which is causing issues
        // - this is quick solution untilwe come back to changing the theme
        // we're overriding in themes.xml
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // TODO removeGlideCache() can be removed after all users are on v3.13 or later
        removeGlideCache();

        prepareNotificationChannel();
        setupRxDefaultErrorHandler();
    }

    // this code is to clean up images cached by Glide in old versions of the app (v3.12 and older)
    // images are supposed to be cached by OkHttpClient instead
    private void removeGlideCache() {
        Completable.fromAction(() -> Glide.get(this).clearDiskCache())
                .subscribeOn(Schedulers.io())
                .doOnError(e -> new LogService().logException(e, e.getMessage()))
                .subscribe();
    }

    private void initJava8TimeBackport() {
        AndroidThreeTen.init(this);
    }

    private void setRxAndroidAsyncScheduler() {
        // Why this? https://medium.com/@ZacSweers/rxandroids-new-async-api-4ab5b3ad3e93
        Scheduler androidAsyncScheduler = AndroidSchedulers.from(Looper.getMainLooper(), true);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> androidAsyncScheduler);
        RxAndroidPlugins.setMainThreadSchedulerHandler(__ -> androidAsyncScheduler);
    }

    private void setupRxDefaultErrorHandler() {
        // set default Rx error handler only for Production in order to avoid potential onErrorNotImplemented crashes for live users
        // for Staging let app crash if Rx error not handled in order to be caught during development and handled properly
        if (!STAGING) {
            RxJavaPlugins.setErrorHandler(throwable -> FirebaseCrashlytics.getInstance().recordException(throwable));
        }
    }

    private void initEmojiCompat() {
        FontRequest fontRequest = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs);
        EmojiCompat.init(new FontRequestEmojiCompatConfig(getApplicationContext(), fontRequest));
    }

    private void initCrashlytics() {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
    }

    protected void subscribeToFcmTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.FCM_TOPIC_REMOTE_CONFIG_PURGE);
    }

    private void makeWebViewDebuggableIn(boolean stage) {
        //https://developers.google.com/web/tools/chrome-devtools/remote-debugging/webviews
        if (stage) {
            WebView.setWebContentsDebuggingEnabled(stage);
        }
    }

    public void initAdobe() {
        MobileCore.setApplication(this);
        MobileCore.configureWithAppID(ADOBE_APP_ID);
        MobileCore.setLogLevel(LoggingMode.DEBUG);

        try {
            List<Class<? extends Extension>> extensions = Arrays.asList(
                    UserProfile.EXTENSION,
                    Audience.EXTENSION,
                    CampaignClassic.EXTENSION,
                    Places.EXTENSION,
                    Target.EXTENSION,
                    Analytics.EXTENSION,
                    Identity.EXTENSION,
                    Lifecycle.EXTENSION,
                    Signal.EXTENSION,
                    Assurance.EXTENSION
            );

            MobileCore.registerExtensions(extensions, o -> {
                Log.d(getClass().getSimpleName(), "Adobe Experience Platform Mobile SDK is initialized");
            });
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Failed to initialize Adobe SDK", e);
        }
    }

    private void prepareNotificationChannel() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(
                new NotificationChannel(
                        PiFirebaseMessagingService.NOTIFICATIONS_CHANNEL_ID,
                        getString(R.string.notification_channel_name),
                        NotificationManager.IMPORTANCE_HIGH
                )
        );
    }

    private boolean isAkamaiWebViewProcess() {
        String processName = getCurrentProcessName();
        return processName != null && processName.contains("com.akamai.webview.process");
    }

    private String getCurrentProcessName() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            return Application.getProcessName();
        }
        return null;
    }

}
