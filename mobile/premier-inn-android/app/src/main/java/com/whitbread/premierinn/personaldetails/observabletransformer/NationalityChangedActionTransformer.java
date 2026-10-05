package com.whitbread.premierinn.personaldetails.observabletransformer;

import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.result.FormResult;
import com.whitbread.premierinn.personaldetails.action.NationalityChangedAction;

import java.util.Collections;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

public class NationalityChangedActionTransformer implements ObservableTransformer<NationalityChangedAction, FormResult> {
    @Override
    public ObservableSource<FormResult> apply(Observable<NationalityChangedAction> upstream) {
        return upstream.map(country -> country.country() != null && country.country().getRequiresPassportInfo())
                .map(passportRequired -> {
                    if (passportRequired) {
                        return FormResult.create(Collections.singletonList(displayPassportFieldsStates()));
                    } else {
                        return FormResult.create(Collections.singletonList(removePassportFieldsStates()));
                    }
                });
    }

    private InputState removePassportFieldsStates() {
        return InputState.builder()
                .id(com.whitbread.premierinn.R.id.personal_details_passport_number)
                .failedValidationType(Input.ValidationType.NONE)
                .value("")
                .state(InputState.State.INVISIBLE).build();
    }

    private InputState displayPassportFieldsStates() {
        return InputState.builder()
                .id(com.whitbread.premierinn.R.id.personal_details_passport_number)
                .failedValidationType(Input.ValidationType.REQUIRED)
                .state(InputState.State.VISIBLE).build();
    }
}