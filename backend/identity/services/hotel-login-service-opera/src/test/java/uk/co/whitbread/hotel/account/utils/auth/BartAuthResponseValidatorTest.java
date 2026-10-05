package uk.co.whitbread.hotel.account.utils.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.bart.auth0.api.ErrorDetails2;
import uk.co.whitbread.bart.auth0.api.InitSessionResponse;
import uk.co.whitbread.bart.auth0.api.InitSessionResponse2;
import uk.co.whitbread.bart.booking.api.ClearSessionRequestResponse;
import uk.co.whitbread.bart.booking.api.ClearSessionResponse;
import uk.co.whitbread.bart.business.api.ArrayOferrorError;
import uk.co.whitbread.bart.business.api.Error;
import uk.co.whitbread.bart.business.api.UserLoginResponse;
import uk.co.whitbread.bart.business.api.UserLoginResponse2;
import uk.co.whitbread.bart.business.auth0.api.ArrayOferrorError2;
import uk.co.whitbread.bart.business.auth0.api.Error2;
import uk.co.whitbread.bart.registeredguest.api.ErrorDetails;
import uk.co.whitbread.bart.registeredguest.api.ForgottenPasswordRequestResponse;
import uk.co.whitbread.bart.registeredguest.api.ForgottenPasswordResponse;
import uk.co.whitbread.bart.registeredguest.api.RegisteredGuestLoginRequestResponse;
import uk.co.whitbread.bart.registeredguest.api.RegisteredGuestLoginResponse;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BartAuthResponseValidatorTest {

    private static final String BART_EMPTY_RESPONSE_ERROR = "Bart returned empty response.";
    private static final String ERROR_DESCRIPTION = "Error";
    public static final String ERROR_CODE = "Error code";
    @Mock
    private ArrayOferrorError2 arrayOferrorError2;
    private BartAuthResponseValidator sut;

    @BeforeEach
    public void setUp() {
        sut = new BartAuthResponseValidator();
    }

    @Test
    public void forgottenPassword_shouldHandleNullInput() {
        //Given
        ForgottenPasswordRequestResponse response = null;

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(BART_EMPTY_RESPONSE_ERROR)));
    }

    @Test
    public void forgottenPassword_shouldHandleNullInnerObject() {
        //Given
        ForgottenPasswordRequestResponse response = new ForgottenPasswordRequestResponse();
        response.setForgottenPasswordRequestResult(null);
        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(BART_EMPTY_RESPONSE_ERROR)));
    }

    @Test
    public void forgottenPassword_shouldExtractErrorCode() {
        //Given
        ForgottenPasswordRequestResponse response = new ForgottenPasswordRequestResponse();
        ForgottenPasswordResponse innerResponse = new ForgottenPasswordResponse();
        response.setForgottenPasswordRequestResult(innerResponse);
        innerResponse.setErrorDetail(null);

        innerResponse.setForgottenPasswordError("Error");

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo("Error")));
    }

    @Test
    public void forgottenPassword_shouldExtractErrorMessage() {
        //Given
        ForgottenPasswordRequestResponse response = new ForgottenPasswordRequestResponse();
        ForgottenPasswordResponse innerResponse = new ForgottenPasswordResponse();
        response.setForgottenPasswordRequestResult(innerResponse);
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage(ERROR_DESCRIPTION);
        innerResponse.setErrorDetail(errorDetails);
        innerResponse.setForgottenPasswordError(ERROR_CODE);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(ERROR_DESCRIPTION)));
    }

    @Test
    public void forgottenPassword_shouldExtractErrorCodeWithMessageEmpty() {
        //Given
        ForgottenPasswordRequestResponse response = new ForgottenPasswordRequestResponse();
        ForgottenPasswordResponse innerResponse = new ForgottenPasswordResponse();
        response.setForgottenPasswordRequestResult(innerResponse);
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage("");
        innerResponse.setErrorDetail(errorDetails);

        innerResponse.setForgottenPasswordError(ERROR_CODE);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(ERROR_CODE)));
    }

    @Test
    public void forgottenPassword_shouldHandleNullErrorDetails() {
        //Given
        ForgottenPasswordRequestResponse response = new ForgottenPasswordRequestResponse();
        ForgottenPasswordResponse innerResponse = new ForgottenPasswordResponse();
        response.setForgottenPasswordRequestResult(innerResponse);
        innerResponse.setSuccess(true);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(false));
    }

    @Test
    public void forgottenPassword_shouldHandleEmptyErrorDetails() {
        //Given
        ForgottenPasswordRequestResponse response = new ForgottenPasswordRequestResponse();
        ForgottenPasswordResponse innerResponse = new ForgottenPasswordResponse();
        response.setForgottenPasswordRequestResult(innerResponse);
        innerResponse.setSuccess(true);
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage("");
        innerResponse.setErrorDetail(errorDetails);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(false));
    }

    @Test
    public void forgottenPassword_shouldHandleUnsuccessfulResponse() {
        //Given
        ForgottenPasswordRequestResponse response = new ForgottenPasswordRequestResponse();
        ForgottenPasswordResponse innerResponse = new ForgottenPasswordResponse();
        response.setForgottenPasswordRequestResult(innerResponse);
        innerResponse.setSuccess(false);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo("Reset password attempt was not successful")));
    }

    @Test
    public void login_shouldHandleNullInput() {
        //Given
        RegisteredGuestLoginRequestResponse response = null;

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(BART_EMPTY_RESPONSE_ERROR)));
    }

    @Test
    public void login_shouldHandleInnerNullInput() {
        //Given
        RegisteredGuestLoginRequestResponse response = new RegisteredGuestLoginRequestResponse();
        response.setRegisteredGuestLoginRequestResult(null);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(BART_EMPTY_RESPONSE_ERROR)));
    }

    @Test
    public void login_shouldExtractErrorCode() {
        //Given
        RegisteredGuestLoginRequestResponse response = new RegisteredGuestLoginRequestResponse();
        RegisteredGuestLoginResponse result = new RegisteredGuestLoginResponse();
        result.setErrorDetail(null);
        result.setLoginRegisteredGuestError(ERROR_CODE);
        response.setRegisteredGuestLoginRequestResult(result);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(ERROR_CODE)));
    }

    @Test
    public void login_shouldExtractErrorMessage() {
        //Given
        RegisteredGuestLoginRequestResponse response = new RegisteredGuestLoginRequestResponse();
        RegisteredGuestLoginResponse result = new RegisteredGuestLoginResponse();
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage(ERROR_DESCRIPTION);
        result.setErrorDetail(errorDetails);
        result.setLoginRegisteredGuestError(ERROR_CODE);
        response.setRegisteredGuestLoginRequestResult(result);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(ERROR_DESCRIPTION)));
    }

    @Test
    public void login_shouldHandleEmptyErrors() {
        //Given
        RegisteredGuestLoginRequestResponse response = new RegisteredGuestLoginRequestResponse();
        response.setRegisteredGuestLoginRequestResult(new RegisteredGuestLoginResponse());

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(false));
    }

    @Test
    public void login_bb_shouldHandleNullInput() {
        //Given
        UserLoginResponse response = null;

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(BART_EMPTY_RESPONSE_ERROR)));
    }

    @Test
    public void login_bb_shouldHandleInnerNullInput() {
        //Given
        UserLoginResponse response = new UserLoginResponse();
        response.setUserLoginResult(null);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(BART_EMPTY_RESPONSE_ERROR)));
    }

    @Test
    public void login_bb_shouldExtractErrorCode() {
        //Given
        UserLoginResponse response = new UserLoginResponse();
        UserLoginResponse2 result = new UserLoginResponse2();

        result.setErrors(new ArrayOferrorError());
        Error error = new Error();
        error.setErrorCode("Error code");
        error.setErrorDescription(null);
        result.getErrors().getError().add(error);

        response.setUserLoginResult(result);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(ERROR_CODE)));
    }

    @Test
    public void login_bb_shouldExtractErrorMessage() {
        //Given
        UserLoginResponse response = new UserLoginResponse();
        UserLoginResponse2 result = new UserLoginResponse2();

        result.setErrors(new ArrayOferrorError());
        Error error = new Error();
        error.setErrorCode(ERROR_CODE);
        error.setErrorDescription(ERROR_DESCRIPTION);
        result.getErrors().getError().add(error);

        response.setUserLoginResult(result);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(ERROR_DESCRIPTION)));
    }

    @Test
    public void login_bb_shouldHandleEmptyErrors() {
        //Given
        UserLoginResponse response = new UserLoginResponse();
        response.setUserLoginResult(new UserLoginResponse2());

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(false));
    }

    @Test
    public void logout_shouldHandleNullInput() {
        //Given
        ClearSessionRequestResponse response = null;

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(BART_EMPTY_RESPONSE_ERROR)));
    }

    @Test
    public void logout_shouldHandleInnerNullInput() {
        //Given
        ClearSessionRequestResponse response = new ClearSessionRequestResponse();
        response.setClearSessionRequestResult(null);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(BART_EMPTY_RESPONSE_ERROR)));
    }

    @Test
    public void logout_shouldExtractErrorMessage() {
        //Given
        ClearSessionRequestResponse response = new ClearSessionRequestResponse();
        ClearSessionResponse response2 = new ClearSessionResponse();

        uk.co.whitbread.bart.booking.api.ErrorDetails errorDetails = new uk.co.whitbread.bart.booking.api.ErrorDetails();
        errorDetails.setErrorMessage(ERROR_DESCRIPTION);
        response2.setErrorDetail(errorDetails);
        response.setClearSessionRequestResult(response2);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(ERROR_DESCRIPTION)));
    }

    @Test
    public void logout_shouldExtractSessionErrorMessage() {
        //Given
        ClearSessionRequestResponse response = new ClearSessionRequestResponse();
        ClearSessionResponse response2 = new ClearSessionResponse();
        response2.setClearSessionError(ERROR_DESCRIPTION);
        response.setClearSessionRequestResult(response2);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(true));
        assertThat(errorMessage.get(), is(equalTo(ERROR_DESCRIPTION)));
    }

    @Test
    public void initSession_pi_should_validate() {
        //Given
        InitSessionResponse initSessionResponse = new InitSessionResponse();
        InitSessionResponse2 initSessionResult = new InitSessionResponse2();
        initSessionResponse.setInitSessionResult(initSessionResult);

        //When
        Optional<String> errorMessage = sut.validate(initSessionResponse);

        //Then
        assertThat(errorMessage, is(equalTo(Optional.empty())));
    }

    @Test
    public void initSession_pi_should_extractErrorCode() {
        //Given
        InitSessionResponse initSessionResponse = new InitSessionResponse();
        InitSessionResponse2 initSessionResult = new InitSessionResponse2();
        initSessionResponse.setInitSessionResult(initSessionResult);
        initSessionResult.setInitError(ERROR_CODE);

        //When
        Optional<String> errorMessage = sut.validate(initSessionResponse);

        //Then
        assertThat(errorMessage, is(equalTo(Optional.of(ERROR_CODE))));
    }

    @Test
    public void initSession_pi_should_extractErrorMessage() {
        //Given
        InitSessionResponse initSessionResponse = new InitSessionResponse();
        InitSessionResponse2 initSessionResult = new InitSessionResponse2();
        initSessionResponse.setInitSessionResult(initSessionResult);
        initSessionResult.setInitError(ERROR_CODE);
        ErrorDetails2 errorDetails = new ErrorDetails2();
        errorDetails.setErrorMessage(ERROR_DESCRIPTION);
        initSessionResult.setErrorDetail(errorDetails);

        //When
        Optional<String> errorMessage = sut.validate(initSessionResponse);

        //Then
        assertThat(errorMessage, is(equalTo(Optional.of(ERROR_DESCRIPTION))));
    }

    @Test
    public void initSession_pi_should_handleNullInput() {
        //Given
        InitSessionResponse initSessionResponse = null;

        //When
        Optional<String> errorMessage = sut.validate(initSessionResponse);

        //Then
        assertThat(errorMessage, is(equalTo(Optional.of(BART_EMPTY_RESPONSE_ERROR))));
    }

    @Test
    public void initSession_pi_should_handleNullInnerObject() {
        //Given
        InitSessionResponse initSessionResponse = new InitSessionResponse();

        //When
        Optional<String> errorMessage = sut.validate(initSessionResponse);

        //Then
        assertThat(errorMessage, is(equalTo(Optional.of(BART_EMPTY_RESPONSE_ERROR))));
    }

    @Test
    public void initSession_bb_should_validate() {
        //Given
        uk.co.whitbread.bart.business.auth0.api.InitSessionResponse initSessionResponse =
                new uk.co.whitbread.bart.business.auth0.api.InitSessionResponse();
        uk.co.whitbread.bart.business.auth0.api.InitSessionResponse2 initSessionResult =
                new uk.co.whitbread.bart.business.auth0.api.InitSessionResponse2();
        initSessionResponse.setInitSessionResult(initSessionResult);

        //When
        Optional<String> errorMessage = sut.validate(initSessionResponse);

        //Then
        assertThat(errorMessage, is(equalTo(Optional.empty())));
    }

    @Test
    public void initSession_bb_should_extractErrorMessage() {
        //Given
        uk.co.whitbread.bart.business.auth0.api.InitSessionResponse initSessionResponse =
                new uk.co.whitbread.bart.business.auth0.api.InitSessionResponse();
        uk.co.whitbread.bart.business.auth0.api.InitSessionResponse2 initSessionResult =
                new uk.co.whitbread.bart.business.auth0.api.InitSessionResponse2();
        initSessionResponse.setInitSessionResult(initSessionResult);
        Error2 error2 = new Error2();
        error2.setErrorDescription(ERROR_CODE);
        when(arrayOferrorError2.getError()).thenReturn(List.of(error2));
        initSessionResult.setErrors(arrayOferrorError2);

        //When
        Optional<String> errorMessage = sut.validate(initSessionResponse);

        //Then
        assertThat(errorMessage, is(equalTo(Optional.of(ERROR_CODE))));
    }

    @Test
    public void initSession_bb_should_handleNullInput() {
        //Given
        uk.co.whitbread.bart.business.auth0.api.InitSessionResponse initSessionResponse = null;

        //When
        Optional<String> errorMessage = sut.validate(initSessionResponse);

        //Then
        assertThat(errorMessage, is(equalTo(Optional.of(BART_EMPTY_RESPONSE_ERROR))));
    }

    @Test
    public void initSession_bb_should_handleNullInnerObject() {
        //Given
        uk.co.whitbread.bart.business.auth0.api.InitSessionResponse initSessionResponse =
                new uk.co.whitbread.bart.business.auth0.api.InitSessionResponse();

        //When
        Optional<String> errorMessage = sut.validate(initSessionResponse);

        //Then
        assertThat(errorMessage, is(equalTo(Optional.of(BART_EMPTY_RESPONSE_ERROR))));
    }
}