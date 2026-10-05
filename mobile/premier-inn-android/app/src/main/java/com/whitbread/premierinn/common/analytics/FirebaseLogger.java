package com.whitbread.premierinn.common.analytics;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseLogger {

    private final FirebaseAnalytics firebaseAnalytics;

    public FirebaseLogger(@NonNull FirebaseAnalytics firebaseAnalytics) {
        this.firebaseAnalytics = firebaseAnalytics;
    }

    public void logEvent(@NonNull String eventName) {
        firebaseAnalytics.logEvent(eventName, new Bundle());
    }

    public void logEvent(@NonNull String eventName, @NonNull FirebaseParams params) {
        firebaseAnalytics.logEvent(eventName, params.toBundle());
    }

    public static class Event {
        public static final String APP_RATING = "app_rating";
        public static final String APP_RATING_CANCELLATION = "app_rating_cancellation";
        public static final String CIOL_ATTEMPT = "ciol_attempt";
        public static final String CIOL_SUCCESS = "ciol_success";
        public static final String CIOL_FAILURE = "ciol_failure";
        public static final String CIOL_FAILURE_RESERVATION_LOCKED = "ciol_failure_reservation_locked";
        public static final String LEGACY_AVAILABILITIES_CALL = "legacy_availabilities_call";
        public static final String GRAPHQL_AVAILABILITIES_CALL = "graphQL_availabilities_call";

    }
}