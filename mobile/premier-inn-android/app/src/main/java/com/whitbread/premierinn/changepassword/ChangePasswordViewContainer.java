package com.whitbread.premierinn.changepassword;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.jakewharton.rxbinding3.view.RxView;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.forms.FormUiModel;
import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.action.Action;
import com.whitbread.premierinn.common.forms.action.SubmitFormAction;
import com.whitbread.premierinn.databinding.ActivityChangePasswordBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import io.reactivex.Observable;

import static android.app.Activity.RESULT_OK;

class ChangePasswordViewContainer implements ChangePasswordPresenter.View {

    private ActivityChangePasswordBinding binding;
    private ChangePasswordActivity activity;

    ChangePasswordViewContainer(
            @NonNull ChangePasswordActivity changePasswordActivity, ActivityChangePasswordBinding binding) {
        this.activity = changePasswordActivity;
        this.binding = binding;
        activity.setToolbar(activity.getString(R.string.change_password_label), true);
    }

    @Override
    public void update(FormUiModel uiModel) {
        switch (uiModel.state()) {
            case IN_PROGRESS:
                binding.changePasswordSaveChangesButton.setLoadingState(true);
                break;
            case ERROR:
                String errorMessage = uiModel.errorMessage() != null
                        ? uiModel.errorMessage()
                        : activity.getString(R.string.generic_error_message_with_try_again_later);
                binding.changePasswordSaveChangesButton.setLoadingState(false);
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
                break;
            case SUCCESS:
                binding.changePasswordSaveChangesButton.setLoadingState(false);
                activity.setResult(RESULT_OK);
                activity.finish();
                break;
            case FORM_UPDATE:
                binding.changePasswordSaveChangesButton.setLoadingState(false);
                for (InputState inputState : uiModel.inputStates()) {
                    switch (inputState.id()) {
                        case R.id.change_password_new_password_input:
                            binding.changePasswordNewPasswordInput.setError(inputState.errorMessage());
                            binding.changePasswordNewPasswordInput.setErrorEnabled(
                                    inputState.state() == InputState.State.FAILED);
                            break;
                        case R.id.change_password_confirm_password_input:
                            binding.changePasswordConfirmPasswordInput.setError(inputState.errorMessage());
                            binding.changePasswordConfirmPasswordInput.setErrorEnabled(
                                    inputState.state() == InputState.State.FAILED);
                            break;
                        default:
                            break;
                    }
                }
            default:
                break;
        }
    }

    @Override
    public Observable<Action> getActions() {
        return Observable.merge(getSubmitEvents(), getOngoingEvents());
    }

    @Override
    public void showGenericError() {
        Toast.makeText(activity, activity.getString(R.string.generic_error_message_with_try_again_later), Toast.LENGTH_LONG).show();
    }

    private Observable<Action> getOngoingEvents() {
        return Observable.merge(
                binding.changePasswordNewPasswordInput.resetEvent(),
                binding.changePasswordConfirmPasswordInput.resetEvent());
    }

    private Observable<SubmitFormAction> getSubmitEvents() {
        return RxView.clicks(binding.changePasswordSaveChangesButton).map(__ -> SubmitFormAction.create(
                new ArrayList<>(Arrays.asList(
                        Input.builder()
                                .id(binding.changePasswordNewPasswordInput.getId())
                                .value(binding.changePasswordNewPasswordInput.getInputText())
                                .validationTypes(EnumSet.of(
                                        Input.ValidationType.REQUIRED,
                                        Input.ValidationType.PASSWORD)).build(),
                        Input.builder()
                                .id(binding.changePasswordConfirmPasswordInput.getId())
                                .value(binding.changePasswordConfirmPasswordInput.getInputText())
                                .validationTypes(EnumSet.of(
                                        Input.ValidationType.REQUIRED,
                                        Input.ValidationType.PASSWORDS_DONT_MATCH)).build()))));
    }
}
