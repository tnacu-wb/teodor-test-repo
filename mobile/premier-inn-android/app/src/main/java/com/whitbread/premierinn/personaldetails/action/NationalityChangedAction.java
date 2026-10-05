package com.whitbread.premierinn.personaldetails.action;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.forms.action.Action;
import com.whitbread.premierinn.domain.countries.entity.CountryDomain;

@AutoValue
public abstract class NationalityChangedAction implements Action {

    @Nullable
    public abstract CountryDomain country();

    public static NationalityChangedAction create(@Nullable CountryDomain country) {
        return new AutoValue_NationalityChangedAction(country);
    }
}