package com.whitbread.premierinn.resetpassword;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.databinding.ActivityResetPasswordBinding;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import io.reactivex.Observable;

public class ResetPasswordViewContainer implements ResetPasswordPresenter.View {

    private boolean businessBooker;
    private ActivityResetPasswordBinding binding;
    private BaseActivity activity;

    public ResetPasswordViewContainer(
            @NonNull BaseActivity activity, boolean businessBooker, ActivityResetPasswordBinding binding) {
        this.activity = activity;
        this.binding = binding;
        this.businessBooker = businessBooker;
        activity.setToolbar(activity.getString(R.string.reset_password_reset_password_label), true);
    }

    @Override
    public Observable<ResetPasswordPresenter.UsernameFormInput> onEmailChanged() {
        return RxTextView.textChanges(binding.etResetPasswordEmail)
                .map(charSequence -> ResetPasswordPresenter.UsernameFormInput.create(
                        binding.tilResetPasswordEmail.isErrorEnabled(), charSequence.toString()));
    }

    @Override
    public Observable<Pair<String, Boolean>> onResetPasswordClick() {
        return RxView.clicks(binding.cabResetPasswordResetPassword).map(__ -> Pair.create(
                binding.etResetPasswordEmail.getText().toString(), businessBooker));
    }

    @Override
    public void showRequestFailedMessage() {
        Toast.makeText(activity, activity.getString(R.string.reset_password_request_failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void resetSuccessful(@NonNull String email) {
        Intent intent = new Intent();
        intent.putExtra(ResetPasswordActivity.RESULT_INTENT_EMAIL_KEY, email);
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }

    @Override
    public void showWrongUsernameMessage() {
        binding.llResetPasswordNotRegisteredMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmailErrorValidation(boolean show) {
        binding.tilResetPasswordEmail.setError(activity.getString(R.string.invalid_email_error));
        binding.tilResetPasswordEmail.setErrorEnabled(show);
    }
}
