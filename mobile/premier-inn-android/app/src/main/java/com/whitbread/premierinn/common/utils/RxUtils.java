package com.whitbread.premierinn.common.utils;

import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;

import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;


public final class RxUtils {

    private RxUtils() {
        throw new AssertionError("no instances allowed");
    }

    @SuppressWarnings("unchecked")
    public static <T> ObservableTransformer<T, T> observableLogCurrentThead(String flag) {
        return upstream ->
                upstream.doOnNext(__ ->
                        Log.w("RX-Log", flag + " " + upstream + " -> " + Thread.currentThread() + ""));
    }

    @SuppressWarnings("unchecked")
    public static <T> SingleTransformer<T, T> singleLogCurrentThead(String flag) {
        return upstream ->
                upstream.doOnSubscribe(__ ->
                        Log.w("RX-Log", flag + " " + upstream + " -> " + Thread.currentThread() + ""));
    }


    public static <T> ObservableTransformer<Object, T> mapViewTagToTypeIfItIsAllowed(@NonNull View view, Class<T> clasz) {
        return upstream -> upstream
                .filter(__ -> view.getTag() != null && clasz.isInstance(view.getTag()))
                .map(it -> view.getTag())
                .cast(clasz);
    }
}