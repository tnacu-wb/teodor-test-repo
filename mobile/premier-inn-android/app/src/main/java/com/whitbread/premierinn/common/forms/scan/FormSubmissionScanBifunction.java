package com.whitbread.premierinn.common.forms.scan;

import com.whitbread.premierinn.common.forms.FormInputErrorMessageProvider;
import com.whitbread.premierinn.common.forms.FormUiModel;
import com.whitbread.premierinn.common.forms.result.FormResult;
import com.whitbread.premierinn.common.forms.result.ResetInputErrorResult;
import com.whitbread.premierinn.common.forms.result.Result;
import com.whitbread.premierinn.common.forms.result.SubmitFormResult;

import io.reactivex.functions.BiFunction;

import static com.whitbread.premierinn.common.forms.FormUiModel.State.FORM_UPDATE;

public class FormSubmissionScanBifunction implements BiFunction<FormUiModel, Result, FormUiModel> {
   private FormInputErrorMessageProvider formInputErrorMessageProvider;
    public FormSubmissionScanBifunction(FormInputErrorMessageProvider formInputErrorMessageProvider) {
        this.formInputErrorMessageProvider = formInputErrorMessageProvider;
    }

    @Override
    public FormUiModel apply(FormUiModel oldState, Result result) {
        if (result instanceof SubmitFormResult) {
            SubmitFormResult submitResult = (SubmitFormResult) result;
            if (submitResult.state() == SubmitFormResult.State.IN_FLIGHT) {
                return FormUiModel.inProgress();
            }

            if (submitResult.state() == SubmitFormResult.State.VALIDATION_FAILED) {
                return FormUiModel.updateForm(submitResult.inputs(), formInputErrorMessageProvider);
            }

            if (submitResult.state() == SubmitFormResult.State.SUCCESS) {
                return FormUiModel.success();
            }

            if (submitResult.state() == SubmitFormResult.State.FAILED) {
                return FormUiModel.error(submitResult.errorMessage());
            }
        }

        if (result instanceof ResetInputErrorResult && oldState.state() == FORM_UPDATE) {
            ResetInputErrorResult resetInputResult = (ResetInputErrorResult) result;
            return FormUiModel.resetInputError(resetInputResult.inputId(), oldState.inputStates());
        }

        if (result instanceof FormResult) {
            FormResult formResult = (FormResult) result;
            return FormUiModel.builder()
                    .state(FORM_UPDATE)
                    .inputStates(formResult.inputs()).build();
        }

        return FormUiModel.idle();
    }
}