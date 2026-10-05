package com.whitbread.premierinn.common.forms.action;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.forms.Input;

import java.util.List;

/**
 *
 */
@AutoValue
public abstract class SubmitFormAction implements Action {

    public abstract List<Input> inputs();

    public static SubmitFormAction create(List<Input> inputs) {
        return new AutoValue_SubmitFormAction(inputs);
    }

}