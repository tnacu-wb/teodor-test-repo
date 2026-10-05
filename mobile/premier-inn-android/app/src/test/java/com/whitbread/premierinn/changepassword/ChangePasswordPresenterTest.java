package com.whitbread.premierinn.changepassword;

import static com.whitbread.premierinn.common.forms.Input.ValidationType.PASSWORD;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.REQUIRED;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.forms.FormInputErrorMessageProvider;
import com.whitbread.premierinn.common.forms.FormUiModel;
import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.action.Action;
import com.whitbread.premierinn.common.forms.action.SubmitFormAction;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerPassword;
import com.whitbread.premierinn.utils.RxJavaTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

@RunWith(MockitoJUnitRunner.class)
public class ChangePasswordPresenterTest {

    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();

    @Mock
    ChangePasswordPresenter.View view;
    @Mock
    UpdateCustomerPassword updateCustomerPassword;
    @Mock
    TrackingAnalytics trackingAnalytics;
    @Mock
    LogService crashlyticsLogger;
    @Mock
    FormInputErrorMessageProvider formInputErrorMessageProvider;

    private ChangePasswordPresenter presenter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Before
    public void setup() {
        when(view.getActions()).thenReturn(Observable.never());
        presenter = new ChangePasswordPresenter(updateCustomerPassword, compositeDisposable,
                trackingAnalytics, crashlyticsLogger, formInputErrorMessageProvider);
    }

    @Test
    public void testLifeCycle() {
        assertFalse(presenter.isViewAttached());
        assertEquals(0, compositeDisposable.size());

        presenter.attachView(view);
        assertTrue(presenter.isViewAttached());
        assertEquals(1, compositeDisposable.size());

        presenter.detachView();
        assertFalse(presenter.isViewAttached());
        assertEquals(0, compositeDisposable.size());
    }

    @Test
    public void onGetActionsSuccess_updateExpected() {
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
        Action action = SubmitFormAction.create(inputList);

        when(updateCustomerPassword.invoke(any(UpdateCustomerPassword.Params.class))).thenReturn(Completable.complete());
        when(view.getActions()).thenReturn(Observable.just(action));

        presenter.attachView(view);

        verify(view).update(FormUiModel.idle());
        verify(view).update(FormUiModel.success());
    }

    @Test
    public void onGetActionsSuccess_errorFormUpdateExpected() {
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
        Action action = SubmitFormAction.create(inputList);

        when(updateCustomerPassword.invoke(any(UpdateCustomerPassword.Params.class))).thenReturn(Completable.error(new Exception()));
        when(view.getActions()).thenReturn(Observable.just(action));

        presenter.attachView(view);

        verify(view).update(FormUiModel.idle());
        verify(view).update(FormUiModel.error(null));
    }

    @Test
    public void onGetActionsSuccess_validationFailedUpdateExpected() {
        String newPassword = "1234";
        String confirmPassword = "";
        List<Input> inputList = Arrays.asList(
                Input.builder()
                        .id(R.id.change_password_new_password_input)
                        .value(newPassword)
                        .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED, Input.ValidationType.PASSWORD)).build(),
                Input.builder()
                        .id(R.id.change_password_confirm_password_input)
                        .value(confirmPassword)
                        .validationTypes(EnumSet.of(Input.ValidationType.REQUIRED, Input.ValidationType.PASSWORDS_DONT_MATCH)).build());
        Action action = SubmitFormAction.create(inputList);

        List<InputState> expectedInputErrors = Arrays.asList(
                InputState.failed(R.id.change_password_new_password_input, PASSWORD),
                InputState.failed(R.id.change_password_confirm_password_input, REQUIRED));


        when(view.getActions()).thenReturn(Observable.just(action));

        presenter.attachView(view);

        verify(view, times(2)).update(FormUiModel.idle());

        verify(view).update(FormUiModel.updateForm(expectedInputErrors, formInputErrorMessageProvider));
    }

    @Test
    public void onGetActionsSuccess_noHotelAccountServiceMethodMocked_genericErrorExpected() {
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
        Action action = SubmitFormAction.create(inputList);

        when(view.getActions()).thenReturn(Observable.just(action));

        presenter.attachView(view);

        verify(view).update(FormUiModel.idle());
        verify(view).showGenericError();
    }
}
