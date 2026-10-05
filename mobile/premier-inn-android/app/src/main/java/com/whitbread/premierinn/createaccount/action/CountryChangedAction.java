package com.whitbread.premierinn.createaccount.action;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.forms.action.Action;

@AutoValue
public abstract class CountryChangedAction implements Action {

    public abstract String countryCode();

    public static CountryChangedAction create(@NonNull String countryCode) {
        return new AutoValue_CountryChangedAction(countryCode);
    }
}
