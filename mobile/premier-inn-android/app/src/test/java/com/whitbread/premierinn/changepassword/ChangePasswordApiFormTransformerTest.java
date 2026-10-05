package com.whitbread.premierinn.changepassword;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.result.SubmitFormResult;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerPassword;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@RunWith(MockitoJUnitRunner.class)
public class ChangePasswordApiFormTransformerTest {

    @Mock UpdateCustomerPassword updateCustomerPassword;
    @Mock LogService crashlyticsLogger;

    private ChangePasswordApiFormTransformer transformer;

    @Test
    public void requestSuccessful_resultSuccessExpected() {
        String newPassword = "newPassword1";
        String confirmPassword = "newPassword1";
        List<Input> inputList = Arrays.asList(
                Input.builder()
                        .id(R.id.change_password_new_password_input)
                        .value(newPassword)
                        .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED, Input.ValidationType.PASSWORD)).build(),
                Input.builder()
                        .id(R.id.change_password_confirm_password_input)
                        .value(confirmPassword)
                        .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED, Input.ValidationType.PASSWORDS_DONT_MATCH)).build());

        when(updateCustomerPassword.invoke(any(UpdateCustomerPassword.Params.class)))
                .thenReturn(Completable.complete());
        transformer = new ChangePasswordApiFormTransformer(updateCustomerPassword, crashlyticsLogger);

        Observable.just(inputList)
                .compose(transformer)
                .test()
                .assertValues(SubmitFormResult.inFlight(), SubmitFormResult.serverSuccess());
    }

    @Test
    public void requestNotSuccessful_resultErrorExpected() {
        String newPassword = "newPassword1";
        String confirmPassword = "confirmPassword1";
        List<Input> inputList = Arrays.asList(
                Input.builder()
                        .id(R.id.change_password_new_password_input)
                        .value(newPassword)
                        .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED, Input.ValidationType.PASSWORD)).build(),
                Input.builder()
                        .id(R.id.change_password_confirm_password_input)
                        .value(confirmPassword)
                        .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED, Input.ValidationType.PASSWORDS_DONT_MATCH)).build());

        when(updateCustomerPassword.invoke(any(UpdateCustomerPassword.Params.class)))
                .thenReturn(Completable.error(new Exception()));
        transformer = new ChangePasswordApiFormTransformer(updateCustomerPassword, crashlyticsLogger);

        Observable.just(inputList)
                .compose(transformer)
                .test()
                .assertValues(SubmitFormResult.inFlight(), SubmitFormResult.serverError(null));
    }
}
