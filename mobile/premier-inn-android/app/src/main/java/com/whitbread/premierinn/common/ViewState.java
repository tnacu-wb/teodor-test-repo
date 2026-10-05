package com.whitbread.premierinn.common;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;


@AutoValue
public abstract class ViewState<T> {

    public abstract Type type();

    @Nullable
    public abstract T data();

    @Nullable
    public abstract Throwable error();

    public static <T> ViewState<T> idle() {
        return create(Type.Idle, null, null);
    }

    public static <T> ViewState<T> inFlight() {
        return create(Type.InFlight, null, null);
    }

    public static <T> ViewState<T> resultOk(T data) {
        return create(Type.ResultOk, data, null);
    }

    public static <T> ViewState<T> error(Throwable throwable) {
        return create(Type.Error, null, throwable);
    }

    private static <T> ViewState<T> create(Type action, T data, Throwable throwable) {
        return new AutoValue_ViewState<T>(action, data, throwable);
    }

    public enum Type {
        Idle,
        InFlight,
        ResultOk,
        Error
    }
}
