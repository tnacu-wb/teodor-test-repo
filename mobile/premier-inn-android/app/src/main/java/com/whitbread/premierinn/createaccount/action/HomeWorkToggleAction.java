package com.whitbread.premierinn.createaccount.action;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.forms.action.Action;
import com.whitbread.premierinn.common.view.ToggleButtonView;

@AutoValue
public abstract class HomeWorkToggleAction implements Action {

    public abstract ToggleButtonView.State homeWorkState();

    public static HomeWorkToggleAction create(@NonNull ToggleButtonView.State state) {
        return new AutoValue_HomeWorkToggleAction(state);
    }
}
