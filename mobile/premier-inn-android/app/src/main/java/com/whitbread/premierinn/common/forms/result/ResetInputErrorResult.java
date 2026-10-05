package com.whitbread.premierinn.common.forms.result;

import com.google.auto.value.AutoValue;

/**
 *
 */
@AutoValue
public abstract class ResetInputErrorResult implements Result {
    public abstract int inputId();

    public static ResetInputErrorResult create(int inputId) {
        return new AutoValue_ResetInputErrorResult(inputId);
    }
}