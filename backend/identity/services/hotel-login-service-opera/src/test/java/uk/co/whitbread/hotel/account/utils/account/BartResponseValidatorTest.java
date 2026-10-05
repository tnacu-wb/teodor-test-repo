package uk.co.whitbread.hotel.account.utils.account;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.bart.registeredguest.api.ChangePasswordRequestResponse;
import uk.co.whitbread.bart.registeredguest.api.ChangePasswordResponse;
import uk.co.whitbread.bart.registeredguest.api.ErrorDetails;
import uk.co.whitbread.bart.registeredguest.api.FutureStaysRequestResponse;
import uk.co.whitbread.bart.registeredguest.api.FutureStaysResponse2;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@MockitoSettings(strictness = Strictness.LENIENT)
public class BartResponseValidatorTest {

    @InjectMocks
    private BartResponseValidator sut;

    @Test
    public void changePassword_shouldHandleNullInput() throws Exception {
        //Given
        ChangePasswordRequestResponse response = null;

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.get(), is(equalTo("Bart returned empty response.")));
    }

    @Test
    public void changePassword_shouldHandleNullInnerObject() throws Exception {
        //Given
        ChangePasswordRequestResponse response = new ChangePasswordRequestResponse();
        response.setChangePasswordRequestResult(null);
        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.get(), is(equalTo("Bart returned empty response.")));
    }

    @Test
    public void changePassword_shouldExtractErrorCode() throws Exception {
        //Given
        ChangePasswordRequestResponse response = new ChangePasswordRequestResponse();
        ChangePasswordResponse innerResponse = new ChangePasswordResponse();
        response.setChangePasswordRequestResult(innerResponse);
        innerResponse.setErrorDetail(null);

        innerResponse.setPasswordChangedError("Error");

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.get(), is(equalTo("Error")));
    }

    @Test
    public void changePassword_shouldExtractErrorMessage() throws Exception {
        //Given
        ChangePasswordRequestResponse response = new ChangePasswordRequestResponse();
        ChangePasswordResponse innerResponse = new ChangePasswordResponse();
        response.setChangePasswordRequestResult(innerResponse);
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage("errorMessage");
        innerResponse.setErrorDetail(errorDetails);
        innerResponse.setPasswordChangedError("Error");

        //When
        Optional<String> errorMessage  = sut.validate(response);

        //Then
        assertThat(errorMessage.get(), is(equalTo("errorMessage")));
    }

    @Test
    public void changePassword_shouldExtractErrorCodeWithMessageEmpty() throws Exception {
        //Given
        ChangePasswordRequestResponse response = new ChangePasswordRequestResponse();
        ChangePasswordResponse innerResponse = new ChangePasswordResponse();
        response.setChangePasswordRequestResult(innerResponse);
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage("");
        innerResponse.setErrorDetail(errorDetails);

        innerResponse.setPasswordChangedError("Error");

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.get(), is(equalTo("Error")));
    }

    @Test
    public void changePassword_shouldHandleNullErrorDetails() throws Exception {
        //Given
        ChangePasswordRequestResponse response = new ChangePasswordRequestResponse();
        ChangePasswordResponse innerResponse = new ChangePasswordResponse();
        response.setChangePasswordRequestResult(innerResponse);
        innerResponse.setPasswordChanged(true);

        //When
        Optional<String> errorMessage  = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(false));
    }

    @Test
    public void changePassword_shouldHandleEmptyErrorDetails() throws Exception {
        //Given
        ChangePasswordRequestResponse response = new ChangePasswordRequestResponse();
        ChangePasswordResponse innerResponse = new ChangePasswordResponse();
        response.setChangePasswordRequestResult(innerResponse);
        innerResponse.setPasswordChanged(true);
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage("");
        innerResponse.setErrorDetail(errorDetails);

        //When
        Optional<String> errorMessage  = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(false));
    }

    @Test
    public void changePassword_shouldHandleUnsuccessfulResponse() throws Exception {
        //Given
        ChangePasswordRequestResponse response = new ChangePasswordRequestResponse();
        ChangePasswordResponse innerResponse = new ChangePasswordResponse();
        response.setChangePasswordRequestResult(innerResponse);
        innerResponse.setPasswordChanged(false);

        //When
        Optional<String> errorMessage  = sut.validate(response);

        //Then
        assertThat(errorMessage.get(), is(equalTo("Change password attempt was not successful")));
    }

    @Test
    public void futureStays_shouldHandleNullInput() throws Exception {
        //Given
        FutureStaysRequestResponse response = null;

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.get(), is(equalTo("Bart returned empty response.")));
    }

    @Test
    public void futureStays_shouldHandleNullInnerObject() throws Exception {
        //Given
        FutureStaysRequestResponse response = new FutureStaysRequestResponse();
        response.setFutureStaysRequestResult(null);
        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.get(), is(equalTo("Bart returned empty response.")));
    }

    @Test
    public void futureStays_shouldExtractErrorCode() throws Exception {
        //Given
        FutureStaysRequestResponse response = new FutureStaysRequestResponse();
        FutureStaysResponse2 innerResponse = new FutureStaysResponse2();
        response.setFutureStaysRequestResult(innerResponse);
        innerResponse.setErrorDetail(null);

        innerResponse.setFutureStaysError("Error");

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.get(), is(equalTo("Error")));
    }

    @Test
    public void futureStays_shouldExtractErrorMessage() throws Exception {
        //Given
        FutureStaysRequestResponse response = new FutureStaysRequestResponse();
        FutureStaysResponse2 innerResponse = new FutureStaysResponse2();
        response.setFutureStaysRequestResult(innerResponse);
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage("errorMessage");
        innerResponse.setErrorDetail(errorDetails);
        innerResponse.setFutureStaysError("Error");

        //When
        Optional<String> errorMessage  = sut.validate(response);

        //Then
        assertThat(errorMessage.get(), is(equalTo("errorMessage")));
    }

    @Test
    public void futureStays_shouldExtractErrorCodeWithMessageEmpty() throws Exception {
        //Given
        FutureStaysRequestResponse response = new FutureStaysRequestResponse();
        FutureStaysResponse2 innerResponse = new FutureStaysResponse2();
        response.setFutureStaysRequestResult(innerResponse);
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage("");
        innerResponse.setErrorDetail(errorDetails);

        innerResponse.setFutureStaysError("Error");

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertThat(errorMessage.get(), is(equalTo("Error")));
    }

    @Test
    public void futureStays_shouldHandleNullErrorDetails() throws Exception {
        //Given
        FutureStaysRequestResponse response = new FutureStaysRequestResponse();
        FutureStaysResponse2 innerResponse = new FutureStaysResponse2();
        response.setFutureStaysRequestResult(innerResponse);
        innerResponse.setErrorDetail(null);

        //When
        Optional<String> errorMessage  = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(false));
    }

    @Test
    public void futureStays_shouldHandleEmptyErrorDetails() throws Exception {
        //Given
        FutureStaysRequestResponse response = new FutureStaysRequestResponse();
        FutureStaysResponse2 innerResponse = new FutureStaysResponse2();
        response.setFutureStaysRequestResult(innerResponse);
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage("");
        innerResponse.setErrorDetail(errorDetails);

        //When
        Optional<String> errorMessage  = sut.validate(response);

        //Then
        assertThat(errorMessage.isPresent(), is(false));
    }

}