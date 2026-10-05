package com.whitbread.premierinn.common;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ActivityResultMessage<T> {
    public abstract int requestCode();

    public abstract int resultCode();

    @Nullable
    public abstract Intent intent();

    public abstract Class activityType();

    public T getData(@NonNull String key) {
        return intent() != null ? intent().getParcelableExtra(key) : null;
    }

    public static ActivityResultMessage create(int requestCode, int resultCode, Intent intent, Class type) {
        return new AutoValue_ActivityResultMessage(requestCode, resultCode, intent, type);
    }
}
