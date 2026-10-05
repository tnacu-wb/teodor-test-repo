package com.whitbread.premierinn.common.forms.action;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ClickAction implements Action {

    public abstract int id();

    public static ClickAction create(int id) {
        return new AutoValue_ClickAction(id);
    }
}
