package com.whitbread.premierinn.common.forms.result;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.forms.InputState;

import java.util.List;

@AutoValue
public abstract class FormResult implements Result {

    public abstract List<InputState> inputs();

    public static FormResult create(List<InputState> inputStates) {
        return new AutoValue_FormResult(inputStates);
    }
}
