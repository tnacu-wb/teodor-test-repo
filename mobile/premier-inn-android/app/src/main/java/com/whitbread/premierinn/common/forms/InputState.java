package com.whitbread.premierinn.common.forms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.utils.StringUtils;

@AutoValue
public abstract class InputState {

    public abstract int id();

    public abstract State state();

    @Nullable
    public abstract String errorMessage();

    @Nullable
    public abstract Input.ValidationType failedValidationType();

    @Nullable
    public abstract Object value();


    public static InputState idle(int id) {
        return builder()
                .id(id)
                .state(State.IDLE)
                .errorMessage(StringUtils.EMPTY_STRING)
                .build();
    }

    public static InputState valid(InputState input) {
        return input.toBuilder()
                .state(State.VALID)
                .errorMessage(StringUtils.EMPTY_STRING)
                .build();
    }

    public static InputState failed(int id, @NonNull Input.ValidationType type) {
        return builder()
                .id(id)
                .state(State.FAILED)
                .failedValidationType(type)
                .build();
    }

    public static InputState value(int id, @Nullable String value) {
        return builder()
                .id(id)
                .state(State.VISIBLE)
                .value(value).build();
    }

    public static InputState withError(InputState input, String errorMessage) {
        return input.toBuilder().errorMessage(errorMessage).state(State.FAILED).build();
    }

    public static InputState noError(InputState input) {
        InputState.Builder inputStateBuilder = input.toBuilder().errorMessage(StringUtils.EMPTY_STRING);
        if (input.state() == State.FAILED) {
            inputStateBuilder.state(State.VALID);
        }
        return inputStateBuilder.build();
    }

    public enum State {
        IDLE, VALID, FAILED, INVISIBLE, VISIBLE
    }

    public static Builder builder() {
        return new AutoValue_InputState.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(int id);

        public abstract Builder state(InputState.State state);

        public abstract Builder errorMessage(String errorMessage);

        public abstract Builder failedValidationType(Input.ValidationType failedValidationType);

        public abstract Builder value(Object value);

        public abstract InputState build();
    }

    public abstract InputState.Builder toBuilder();
}