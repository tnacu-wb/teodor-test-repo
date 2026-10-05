package com.whitbread.premierinn.common.rx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposables;

/**
 * This code was been found here
 * @link https://github.com/ReactiveX/RxJava/issues/4937
 */
public final class RxBroadcastReceiver implements ObservableOnSubscribe<Intent> {

    public static Observable<Intent> create(Context context, IntentFilter intentFilter) {
        return Observable.create(new RxBroadcastReceiver(context, intentFilter));
    }

    private final Context context;
    private final IntentFilter intentFilter;

    private RxBroadcastReceiver(Context context, IntentFilter intentFilter) {
        this.context = context;
        this.intentFilter = intentFilter;
    }

    @Override
    public void subscribe(ObservableEmitter<Intent> emitter) throws Exception {
        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                emitter.onNext(intent);
            }
        };
        context.registerReceiver(broadcastReceiver, intentFilter);
        emitter.setDisposable(Disposables.fromRunnable(() -> context.unregisterReceiver(broadcastReceiver)));
    }
}
