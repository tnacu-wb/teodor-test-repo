package com.whitbread.premierinn.common.forms;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.Validator;

import java.util.List;

import io.reactivex.Observable;

/**
 *
 */
@AutoValue
public abstract class FormUiModel {

    public abstract State state();

    @Nullable
    public abstract List<InputState> inputStates();

    @Nullable
    public abstract String errorMessage();

    public static FormUiModel idle() {
        return idle(null);
    }

    public static FormUiModel idle(@Nullable List<InputState> inputStates) {
        return builder()
                .inputStates(inputStates)
                .state(State.IDLE)
                .build();
    }

    public static FormUiModel success() {
        return builder()
                .state(State.SUCCESS)
                .build();
    }

    public static FormUiModel error() {
        return builder()
                .state(State.ERROR)
                .build();
    }

    public static FormUiModel error(String errorMessage) {
        return builder()
                .state(State.ERROR)
                .errorMessage(errorMessage)
                .build();
    }

    public static FormUiModel inProgress() {
        return builder()
                .state(State.IN_PROGRESS)
                .build();
    }
    public static FormUiModel updateForm(List<InputState> states, FormInputErrorMessageProvider formInputErrorMessageProvider) {
        return builder()
                .inputStates(inputStates(states, formInputErrorMessageProvider))
                .state(State.FORM_UPDATE)
                .build();
    }

    public static FormUiModel resetInputError(int inputId, List<InputState> oldStates) {
        return builder()
                .inputStates(updateInputStates(inputId, oldStates))
                .state(State.FORM_UPDATE)
                .build();
    }

    private static List<InputState> updateInputStates(int id, List<InputState> oldStates) {
        return Observable.concat(
                Observable.fromCallable(() -> InputState.idle(id)),
                Observable.fromIterable(oldStates))
                .distinct(InputState::id)
                .toList()
                .blockingGet();
    }

    public static Builder builder() {
        return new AutoValue_FormUiModel.Builder();
    }

    public enum State {
        IDLE, IN_PROGRESS, SUCCESS, ERROR, FORM_UPDATE
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder state(State state);

        public abstract Builder inputStates(List<InputState> inputStates);

        public abstract Builder errorMessage(String errorMessage);

        public abstract FormUiModel build();
    }

    public static List<InputState> inputStates(List<InputState> inputs, FormInputErrorMessageProvider formInputErrorMessageProvider) {
                return Observable.fromIterable(inputs)
                        .map(input -> {
                            if (input.failedValidationType() == null) {
                                return InputState.noError(input);
                            } else {
                                return InputState.withError(input,
                                        provideErrorMessage(input.failedValidationType(), formInputErrorMessageProvider));
                            }
                        })
                        .toList()
                        .blockingGet();
            }

    public static String provideErrorMessage(Input.ValidationType type, FormInputErrorMessageProvider formInputErrorMessageProvider) {
        return switch (type) {
            case REQUIRED -> formInputErrorMessageProvider.getFieldRequiresString();
            case EMAIL -> formInputErrorMessageProvider.getEmailInvalidString();
            case PHONE -> formInputErrorMessageProvider.getPhoneNumberInvalidString();
            case CARD_DATE -> formInputErrorMessageProvider.getDateInvalidString();
            case CARD_NUMBER -> formInputErrorMessageProvider.getEnterValidCardString();
            case PASSWORD -> formInputErrorMessageProvider.getPasswordInvalidString();
            case PASSWORDS_DONT_MATCH ->
                    formInputErrorMessageProvider.getPasswordDoNotMatchString();
            case FIRST_NAME ->
                    formInputErrorMessageProvider.getFirstNameInvalidString() + Validator.MAX_FIRST_NAME_LENGTH;
            case LAST_NAME ->
                    formInputErrorMessageProvider.getLastNameInvalidString() + Validator.MAX_LAST_NAME_LENGTH;
            case GERMAN_POSTCODE, UK_POSTCODE -> formInputErrorMessageProvider.getPostcodeInvalidString();
            case COMPANY_NAME -> formInputErrorMessageProvider.getCompanyNameInvalidString();
            case COMPANY_NAME_SPECIAL ->
                    formInputErrorMessageProvider.getCompanyNameInvalidSpecialCharacter();
            default -> null;
        };
    }
}