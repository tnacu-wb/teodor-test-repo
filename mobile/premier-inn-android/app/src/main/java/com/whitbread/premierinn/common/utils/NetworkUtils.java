package com.whitbread.premierinn.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;

public class NetworkUtils {

    private NetworkUtils() {
        throw new AssertionError("no instances allowed");
    }

    public static boolean hasInternetConnection(@NonNull Context context) {
        NetworkInfo activeNetwork = ((ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
