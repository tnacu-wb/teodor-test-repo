package com.whitbread.premierinn.criteria.view;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.criteria.NumberSelectorMaxNumberInfo;

@AutoValue
public abstract class NumberSelectorViewInput implements Parcelable {

    public static NumberSelectorViewInput createDefault(int value) {
        return new AutoValue_NumberSelectorViewInput(value, null, false, true);
    }

    public static NumberSelectorViewInput createNoError(NumberSelectorViewInput input) {
        return input.toBuilder()
                .maxNumberInfo(null).build();
    }

    public abstract int value();

    @Nullable
    public abstract NumberSelectorMaxNumberInfo maxNumberInfo();

    public abstract boolean minusEnabled();

    public abstract boolean plusEnabled();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract NumberSelectorViewInput.Builder value(int number);
        public abstract NumberSelectorViewInput.Builder minusEnabled(boolean minusEnabled);
        public abstract NumberSelectorViewInput.Builder plusEnabled(boolean plusEnabled);
        public abstract NumberSelectorViewInput.Builder maxNumberInfo(@Nullable NumberSelectorMaxNumberInfo maxNumberInfo);
        public abstract NumberSelectorViewInput build();
    }

    public static NumberSelectorViewInput.Builder builder() {
        return new AutoValue_NumberSelectorViewInput.Builder()
                .maxNumberInfo(null)
                .minusEnabled(false)
                .plusEnabled(false);
    }

    public abstract NumberSelectorViewInput.Builder toBuilder();

}
