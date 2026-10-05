// Approach inspired from
// https://handstandsam.com/2018/02/11/kotlin-buildsrc-for-better-gradle-dependency-management/
object Versions {

    const val kotlin = "2.0.20"
    const val gradle = "8.13.0" //AGP version

    const val androidMinSdk = 28
    const val androidTargetSdk = 35
    const val androidCompileSdk = 35

    // AndroidX Library Versions
    const val androidxCore = "1.13.1"
    const val androidxAppCompat = "1.6.1"
    const val androidxAnnotation = "1.7.1"
    const val androidxFragment = "1.6.2"
    const val androidxLifecycle = "2.8.7"
    const val androidxWebkit = "1.11.0"
    const val androidxConstraintLayout = "2.1.4"

    const val androidxBrowser = "1.8.0" // customTab // TODO: Check for updates

    const val androidxSecurity = "1.1.0-alpha03"
    const val androidxIdentityCredential = "1.0.0-alpha02"

    const val emoji2 = "1.4.0"

    const val materialLibrary = "1.12.0"

    const val googlePlayServicesLocation = "18.0.0"
    const val googlePlayServicesMaps = "19.2.0"
    const val room = "2.6.1"

    //Libraries
    const val dagger = "2.53"
    const val okHttp = "4.10.0"
    const val retrofit = "2.11.0"
    const val rxJava = "2.2.21"
    const val autoValue = "1.6.6"
    const val autoValueGson = "1.0.0"
    const val autoValueParcel = "0.2.8"

    const val gson = "2.9.0"

    const val contentSquare = "0.8.0"

    const val javaxAnnotation = "1.0"
    const val javaxAnnotationApi = "10.0-b28"

    const val coroutines = "1.8.1"

    //Testing
    const val jUnit = "4.13.2"
    const val jUnit5 = "5.14.1"
    const val jUnitPlatform = "1.14.1"
    const val mockito = "5.16.1"
    const val mockitoKotlin = "5.4.0"
    const val espresso = "3.6.1"
    const val turbine = "1.2.0"
    const val mockK = "1.13.14"
    const val robolectric = "4.14"
    const val androidxTestCore = "1.6.1"
    const val androidxTestRunner = "1.2.1"
    const val androidxTestOrchestrator = "1.5.0"

    const val archCoreTesting = "2.2.0"


    const val leakCanary = "1.6.3"

    const val glide = "4.13.2"
    const val glideCompose = "1.0.0-beta01"

    const val zxing = "3.5.0"
    const val hilt = "2.56"
    const val androidxHilt = "1.2.0"
    const val composeBOM = "2025.11.01"

    const val firebaseBOM = "33.5.1"
}

object Plugins {

    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val gradle = "com.android.tools.build:gradle:${Versions.gradle}"

    const val firebase_crashlytics = "com.google.firebase:firebase-crashlytics-gradle:3.0.2"
    const val firebase_performance = "com.google.firebase:perf-plugin:2.0.2"
    const val google_services = "com.google.gms:google-services:4.4.4"

    const val dexcount = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:4.0.0"
    const val gradle_versions = "com.github.ben-manes:gradle-versions-plugin:0.53.0"
    const val dynatrace_versions = "com.dynatrace.tools.android:gradle-plugin:8.+"
    const val hilt_android = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"
    const val compose_versions = "org.jetbrains.kotlin:compose-compiler-gradle-plugin:${Versions.kotlin}"
}

object Libs {
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    const val kotlinTestlib = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
    const val jetbrainsAnnotations = "org.jetbrains:annotations:16.0.3"

    const val supportAppCompat = "androidx.appcompat:appcompat:${Versions.androidxAppCompat}"
    const val supportAnnotations = "androidx.annotation:annotation:${Versions.androidxAnnotation}"
    const val supportDesign = "com.google.android.material:material:${Versions.materialLibrary}"
    const val supportWebkit = "androidx.webkit:webkit:${Versions.androidxWebkit}"
    const val supportCardview = "androidx.cardview:cardview:1.0.0"
    const val supportGridlayout = "androidx.gridlayout:gridlayout:1.0.0"
    const val supportConstraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.androidxConstraintLayout}"

    const val auth0 = "com.auth0.android:auth0:1.24.1"

    // AndroidX Core & Fragment
    const val androidktx = "androidx.core:core-ktx:${Versions.androidxCore}"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.androidxFragment}"

    const val emoji2 = "androidx.emoji2:emoji2:${Versions.emoji2}"
    const val emoji2Views = "androidx.emoji2:emoji2-views:${Versions.emoji2}"
    const val emoji2ViewsHelper = "androidx.emoji2:emoji2-views-helper:${Versions.emoji2}" // TODO: Delete unused

    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
    const val rxRelay = "com.jakewharton.rxrelay2:rxrelay:2.1.1"
    const val rxBindings = "com.jakewharton.rxbinding3:rxbinding:3.0.0"
    const val rxBindingMaterial = "com.jakewharton.rxbinding3:rxbinding-material:3.0.0"
    const val rxLocation = "com.patloew.rxlocation:rxlocation:1.0.5"
    const val rxPermissions = "com.github.tbruyelle:rxpermissions:0.11"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"

    const val javaxAnnotation = "javax.annotation:jsr250-api:${Versions.javaxAnnotation}"
    const val javaxAnnotationApi = "org.glassfish:javax.annotation:${Versions.javaxAnnotationApi}"
    const val javaxInject = "javax.inject:javax.inject:1"
    
    const val playServicesLocation = "com.google.android.gms:play-services-location:${Versions.googlePlayServicesLocation}"
    const val playServicesMaps = "com.google.android.gms:play-services-maps:${Versions.googlePlayServicesMaps}"

    //Firebase BOM
    const val firebaseBOM = "com.google.firebase:firebase-bom:${Versions.firebaseBOM}"
    const val firebaseRemoteConfig = "com.google.firebase:firebase-config"
    const val firebaseMessaging = "com.google.firebase:firebase-messaging"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics"
    const val firebaseAnalytics =  "com.google.firebase:firebase-analytics"
    const val firebasePerformance = "com.google.firebase:firebase-perf"

    const val customTab = "androidx.browser:browser:${Versions.androidxBrowser}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompose = "com.github.bumptech.glide:compose:${Versions.glideCompose}"
    const val glideOkhttpIntegration = "com.github.bumptech.glide:okhttp3-integration:${Versions.glide}"
    const val glideOkhttpIntegrationAnnotation = "com.github.bumptech.glide:annotations:${Versions.glide}"
    const val glideOkhttpIntegrationAnnotationProcessor = "com.github.bumptech.glide:compiler:${Versions.glide}"
    const val androidSVG = "com.caverock:androidsvg:1.4"

    const val calendarView = "com.prolificinteractive:material-calendarview:1.4.3"
    const val newCalendarView = "com.github.kizitonwose:CalendarView:0.2.9"
    const val flexboxLayout = "com.google.android.flexbox:flexbox:3.0.0"
    const val securePreferences = "com.scottyab:secure-preferences-lib:0.1.7"

    //Deprecated as Android is moving towards DataStore, but as we still use SharedPreferences
    const val securePreferencesGoogle = "androidx.security:security-crypto:${Versions.androidxSecurity}"
    const val identityCredentialGoogle = "androidx.security:security-identity-credential:${Versions.androidxIdentityCredential}"

    const val processPhoenix = "com.jakewharton:process-phoenix:2.0.0"
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
    const val leakCanaryNoOp = "com.squareup.leakcanary:leakcanary-android-no-op:${Versions.leakCanary}"

    const val confetti = "nl.dionsegijn:konfetti:1.3.2"

    const val adapterDelegate = "com.hannesdorfmann:adapterdelegates4-kotlin-dsl:4.3.0"

    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val room = "androidx.room:room-runtime:${Versions.room}"
    const val roomRxJava = "androidx.room:room-rxjava2:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"

    // Android ViewModel & Lifecycle - https://developer.android.com/jetpack/androidx/releases/lifecycle
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.androidxLifecycle}"
    const val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.androidxLifecycle}"
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidxLifecycle}"
    const val lifecycleCommon = "androidx.lifecycle:lifecycle-common-java8:${Versions.androidxLifecycle}" // Delete
    const val lifecycleProcess = "androidx.lifecycle:lifecycle-process:${Versions.androidxLifecycle}" // Delete


    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitScalars = "com.squareup.retrofit2:converter-scalars:${Versions.retrofit}"
    const val retrofitGsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val retrofitRxAdapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    const val okhttp3Logging = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"

    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val apacheCommons = "org.apache.commons:commons-lang3:3.7"
    const val apacheIO = "commons-io:commons-io:2.8.0"

    const val autoValue = "com.google.auto.value:auto-value:${Versions.autoValue}"
    const val autoValueGson = "com.ryanharter.auto.value:auto-value-gson:${Versions.autoValueGson}"
    const val autoValueGsonRuntime = "com.ryanharter.auto.value:auto-value-gson-runtime:${Versions.autoValueGson}"
    const val autoValueParcel = "com.ryanharter.auto.value:auto-value-parcel:${Versions.autoValueParcel}"

    // Adobe analytics
    // Important, Tom has suggested to use dynamic versioning as hardcoding the versions
    // may break some functionalities of adobe
    const val adobeBOM = "com.adobe.marketing.mobile:sdk-bom:3.+"
    const val adobeUserProfile = "com.adobe.marketing.mobile:userprofile"
    const val adobeCore = "com.adobe.marketing.mobile:core"
    const val adobeAnalytics = "com.adobe.marketing.mobile:analytics"
    const val adobeAudience = "com.adobe.marketing.mobile:audience"
    const val adobeTarget = "com.adobe.marketing.mobile:target"
    const val adobeCampaign = "com.adobe.marketing.mobile:campaignclassic"
    const val adobePlaces = "com.adobe.marketing.mobile:places"
    const val adobeAssurance = "com.adobe.marketing.mobile:assurance" //Griffon rebranded
    const val adobeSignal = "com.adobe.marketing.mobile:signal" // Required by the Core SDK
    const val adobeIdentity = "com.adobe.marketing.mobile:identity" // Required by the Core SDK
    const val adobeLifecycle = "com.adobe.marketing.mobile:lifecycle" // Required by the Core SDK

    // Appsflyer
    const val appsFlyer = "com.appsflyer:af-android-sdk:6.13.0"
    const val installReferer = "com.android.installreferrer:installreferrer:2.2"

    // Hilt
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}" // Required by the Core SDK
    const val hiltCompiler = "com.google.dagger:hilt-compiler:${Versions.hilt}" // Required by the Core SDK
    const val androidxHiltAndroid = "androidx.hilt:hilt-work:${Versions.androidxHilt}" // Required by the Core SDK
    const val androidxHiltCompiler = "androidx.hilt:hilt-compiler:${Versions.androidxHilt}" // Required by the Core SDK

    const val androidxActivity = "androidx.activity:activity-ktx:1.9.0" // Required by the Core SDK
    const val androidxFragment = "androidx.fragment:fragment-ktx:1.6.2" // Required by the Core SDK
    const val androidxViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0" // Required by the Core SDK

    //PayPal
    const val payPal = "com.braintreepayments.api:paypal:4.51.0"
    const val payPalDataCollector = "com.braintreepayments.api:paypal-data-collector:4.51.0"

    // Coroutines
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    const val coroutinesRxJava = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${Versions.coroutines}"

    //QR Code
    const val zxingCore = "com.google.zxing:core:${Versions.zxing}"
    const val zxingJavaSe = "com.google.zxing:javase:${Versions.zxing}"

    const val contentSquare = "com.contentsquare.android:sdk:${Versions.contentSquare}"

    // Turbine - Testing Kotlin StateFlow
    const val turbine = "app.cash.turbine:turbine:${Versions.turbine}"
    const val jUnit = "junit:junit:${Versions.jUnit}"
    const val jUnit5 = "org.junit.jupiter:junit-jupiter:${Versions.jUnit5}"
    const val jUnitVintageEngine = "org.junit.vintage:junit-vintage-engine:${Versions.jUnit5}"
    const val jUnitPlatformLauncher = "org.junit.platform:junit-platform-launcher:${Versions.jUnitPlatform}"
    const val mockito = "org.mockito:mockito-core:${Versions.mockito}"
    const val mockitoKotlin = "org.mockito.kotlin:mockito-kotlin:${Versions.mockitoKotlin}"
    const val mockK = "io.mockk:mockk:${Versions.mockK}"
    const val jUnitParams = "pl.pragmatists:JUnitParams:1.1.1"
    const val thruth = "com.google.truth:truth:1.0"
    const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    const val okhttp3MockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.okHttp}"
    const val guava = "com.google.guava:guava:22.0"
    const val roomTesting = "androidx.room:room-testing:${Versions.room}"
    const val archCoreTesting = "androidx.arch.core:core-testing:${Versions.archCoreTesting}" //TODO if upgraded to 2.1.0 - issue needed to be resolved with lint
    const val androidXTestCore = "androidx.test:core:${Versions.androidxTestCore}"
    const val androidTestRunner = "androidx.test.ext:junit:${Versions.androidxTestRunner}"
    const val espressoOrchestrator = "androidx.test:orchestrator:${Versions.androidxTestOrchestrator}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val espressoIntents = "androidx.test.espresso:espresso-intents:${Versions.espresso}"
    const val espressoContrib = "androidx.test.espresso:espresso-contrib:${Versions.espresso}"
    const val restMock = "com.github.andrzejchm.RESTMock:android:0.4.1"
    const val okhttp3IdlingResource = "com.jakewharton.espresso:okhttp3-idling-resource:1.0.0"
    const val rx2Idler = "com.squareup.rx.idler:rx2-idler:0.9.1"
    const val falcon = "com.jraska:falcon:2.2.0"

    const val threetenabp = "com.jakewharton.threetenabp:threetenabp:1.2.1"
    const val threetenbp = "org.threeten:threetenbp:1.4.0" // should only be used in JVM Testing

    //Compose BOM
    const val composeBOM = "androidx.compose:compose-bom:${Versions.composeBOM}"
    const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling"
    const val composeUi = "androidx.compose.ui:ui"
    const val coposeUiGraphics = "androidx.compose.ui:ui-graphics"
    const val composeUiTest = "androidx.compose.ui:ui-test-manifest"
    const val composeMaterialDesign = "androidx.compose.material3:material3"
    const val composeMaterialIconsCore = "androidx.compose.material:material-icons-core"
    const val composeActivity = "androidx.activity:activity-compose"
    const val composeSaveable = "androidx.compose.runtime:runtime-saveable"

}