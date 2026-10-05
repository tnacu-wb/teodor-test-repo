package com.whitbread.premierinn.postcodefinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.domain.common.AddressShort;

import java.util.List;

@AutoValue
public abstract class PostcodeState {

    public static PostcodeState create(@NonNull Action action) {
        return create(action, null);
    }

    public static PostcodeState create(@NonNull Action action, @Nullable List<AddressShort> addresses) {
        return new AutoValue_PostcodeState(action, addresses);
    }

    public abstract Action action();

    @Nullable
    public abstract List<AddressShort> addresses();

    public enum Action {
        SHOW_LOADING,
        SHOW_NO_RESULT,
        SHOW_RESULT,
        SHOW_DEFAULT_SCREEN
    }
}
