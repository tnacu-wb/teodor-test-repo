package com.whitbread.premierinn.common.forms.result;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.forms.InputState;

import java.util.List;

/**
 *
 */
@AutoValue
public abstract class SubmitFormResult implements Result {

    public abstract State state();

    @Nullable
    public abstract List<InputState> inputs();

    @Nullable
    public abstract String errorMessage();

    public static SubmitFormResult formValidationFailed(List<InputState> inputs) {
        return create(State.VALIDATION_FAILED, inputs);
    }

    public static SubmitFormResult inFlight() {
        return create(State.IN_FLIGHT, null);
    }

    public static SubmitFormResult serverSuccess() {
        return create(State.SUCCESS, null);
    }

    public static SubmitFormResult serverError() {
        return create(State.FAILED, null);
    }

    public static SubmitFormResult serverError(@NonNull String errorMessage) {
        return create(State.FAILED, null, errorMessage);
    }

    public static SubmitFormResult idle() {
        return create(State.IDLE, null);
    }

    private static SubmitFormResult create(State state, List<InputState> failedInputs, String errorMessage) {
        return new AutoValue_SubmitFormResult(state, failedInputs, errorMessage);
    }

    private static SubmitFormResult create(State state, List<InputState> failedInputs) {
        return new AutoValue_SubmitFormResult(state, failedInputs, null);
    }

    public enum State {
        VALIDATION_FAILED, IN_FLIGHT, SUCCESS, FAILED, IDLE
    }
}