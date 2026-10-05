package com.whitbread.premierinn.common.service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.whitbread.premierinn.common.rx.RxBroadcastReceiver;
import com.whitbread.premierinn.common.utils.NetworkUtils;

import io.reactivex.Observable;

public class OfflineDetectorService {

    private Observable<Intent> networkObservable;
    private Context context;

    public OfflineDetectorService(Context context) {
        this.networkObservable = RxBroadcastReceiver.create(context, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        this.context = context;
    }

    public Observable<Boolean> getNetworkObservable() {
        return networkObservable
                .map(o -> NetworkUtils.hasInternetConnection(context));
    }
}
