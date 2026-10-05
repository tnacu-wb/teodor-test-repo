package com.whitbread.premierinn.common.forms.action;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ResetInputErrorAction implements Action {

    public abstract int inputId();

    public static ResetInputErrorAction create(int inputId) {
        return new AutoValue_ResetInputErrorAction(inputId);
    }

}