package com.whitbread.premierinn.common.forms.observabletransformer;

import com.whitbread.premierinn.common.Validator;
import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.action.SubmitFormAction;
import com.whitbread.premierinn.common.forms.result.SubmitFormResult;
import com.whitbread.premierinn.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.whitbread.premierinn.common.forms.Input.ValidationType.CARD_DATE;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.CHECKED;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.COMPANY_NAME;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.COMPANY_NAME_SPECIAL;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.EMAIL;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.FIRST_NAME;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.GERMAN_POSTCODE;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.LAST_NAME;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.PASSWORD;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.PASSWORDS_DONT_MATCH;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.PHONE;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.REQUIRED;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.UK_POSTCODE;

public class SubmitFormActionTransformer implements ObservableTransformer<SubmitFormAction, SubmitFormResult> {

    private final ObservableTransformer<List<Input>, SubmitFormResult> apiTransformer;

    public SubmitFormActionTransformer(ObservableTransformer<List<Input>, SubmitFormResult> apiTransformer) {
        this.apiTransformer = apiTransformer;
    }

    @Override
    public ObservableSource<SubmitFormResult> apply(Observable<SubmitFormAction> actions) {
        return actions.flatMap(action -> {
            List<InputState> formFailedInputs = getFormFailedInputs(action.inputs());
            if (formFailedInputs.isEmpty()) {
                return Observable.just(action.inputs())
                        .compose(apiTransformer)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .startWith(SubmitFormResult.inFlight());
            } else {
                return Observable.fromIterable(formFailedInputs)
                        .distinct(InputState::id)
                        .toList().toObservable()
                        .map(SubmitFormResult::formValidationFailed)
                        .startWith(SubmitFormResult.idle());
            }
        });
    }

    static List<InputState> getFormFailedInputs(List<Input> formInputs) {
        List<InputState> resultList = new ArrayList<>();
        for (Input input : formInputs) {
            Input.ValidationType failedValidationType = getFailedValidationType(input);
            if (failedValidationType != null) {
                resultList.add(InputState.failed(input.id(), failedValidationType));
            }
        }
        return resultList;
    }

    //too simplistic maybe validation needs to be in a separate class
    static Input.ValidationType getFailedValidationType(Input input) {
        for (Input.ValidationType type : input.validationTypes()) {
            if (type == REQUIRED) {
                if (StringUtils.isBlank(input.value().toString())) {
                    return REQUIRED;
                }
            }
            if (type == EMAIL) {
                return Validator.isEmailValid(input.value().toString()) ? null : EMAIL;
            }
            if (type == CARD_DATE) {
                return Validator.isCardDate(input.value().toString()) ? null : CARD_DATE;
            }
            if (type == PHONE) {
                return Validator.isPhoneNumberValid(input.value().toString()) ? null : PHONE;
            }
            if (type == PASSWORD) {
                return Validator.isPasswordValid(input.value().toString()) ? null : PASSWORD;
            }
            if (type == PASSWORDS_DONT_MATCH) {
                return Validator.doPasswordsMatch(input.value().toString()) ? null : PASSWORDS_DONT_MATCH;
            }
            if (type == CHECKED) {
                return Validator.isChecked((Boolean) input.value()) ? null : CHECKED;
            }
            if (type == FIRST_NAME) {
                return Validator.isFirstNameValid(input.value().toString()) ? null : FIRST_NAME;
            }
            if (type == LAST_NAME) {
                return Validator.isLastNameValid(input.value().toString()) ? null : LAST_NAME;
            }
            if (type == GERMAN_POSTCODE) {
                return Validator.isGermanPostcodeValid(input.value().toString()) ? null : GERMAN_POSTCODE;
            }
            if (type == UK_POSTCODE) {
                return Validator.isUkPostcodeValid(input.value().toString()) ? null : UK_POSTCODE;
            }
            if (type == COMPANY_NAME) {
                if (Validator.isCompanyNameLengthValid(input.value().toString())) {
                    if (Validator.isCompanyNameValid(input.value().toString())) {
                        return null;
                    } else {
                        return COMPANY_NAME_SPECIAL;
                    }
                } else {
                    return COMPANY_NAME;
                }
            }
        }
        return null;
    }
}
