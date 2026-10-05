package uk.co.whitbread.business.tether.validation;

import java.util.Optional;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.business.tether.exception.BusinessTetherException;
import uk.co.whitbread.piba.api.exception.PibaException;
import worldline.mst.bsm.api.b2b.pi.data.ErrorInfoType;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUserResponse;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUserResponseType;
import worldline.mst.bsm.api.b2b.pi.data.NewSessionType;
import worldline.mst.bsm.api.b2b.pi.data.RefreshSessionResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetherByAccountNumberResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetherByAccountNumberResponseType;
import worldline.mst.bsm.api.b2b.pi.data.TetherByCardNumberResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetherByCardNumberResponseType;
import worldline.mst.bsm.api.b2b.pi.data.TetherDetailsType;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorldLineTetherResponseValidatorTest {
    private WorldLineTetherResponseValidator sut;

    @BeforeEach
    public void setUp() {
        sut = new WorldLineTetherResponseValidator();
    }

    @Test
    public void login_shouldHandleNullInput() {
        //When
        Optional<String> errorMessage = sut.validate((LoginTetheredUserResponse)null);

        //Then
        assertTrue(errorMessage.isPresent());
        MatcherAssert.assertThat(errorMessage.get(), is(equalTo(WorldLineTetherResponseValidator.ERROR_EMPTY_RESPONSE)));
    }

    @Test
    public void refresh_shouldHandleNullInput() {
        //When
        Optional<String> errorMessage = sut.validate((RefreshSessionResponse) null);

        //Then
        assertTrue(errorMessage.isPresent());
        MatcherAssert.assertThat(errorMessage.get(), is(equalTo(WorldLineTetherResponseValidator.ERROR_EMPTY_RESPONSE)));
    }


    @Test
    public void login_shouldExtractErrorMessage() {
        //Given
        LoginTetheredUserResponse response = new LoginTetheredUserResponse();
        LoginTetheredUserResponseType innerResponse = new LoginTetheredUserResponseType();

        ErrorInfoType errorInfoType = new ErrorInfoType();
        errorInfoType.setInfo("Invalid login");
        errorInfoType.setErrorCode("AuthenticationError");
        innerResponse.getErrors().add(errorInfoType);
        response.setResponse(innerResponse);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        assertTrue(errorMessage.isPresent());
        MatcherAssert.assertThat(errorMessage.get(), is(equalTo("AuthenticationError")));
    }

    @Test
    public void login_shouldHandleNullErrorDetails() {
        //Given
        LoginTetheredUserResponse response = new LoginTetheredUserResponse();
        LoginTetheredUserResponseType innerResponse = new LoginTetheredUserResponseType();
        response.setResponse(innerResponse);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        MatcherAssert.assertThat(errorMessage.isPresent(), is(false));
    }

    @Test
    public void login_shouldHandleEmptyErrorDetails() {
        //Given
        LoginTetheredUserResponse response = new LoginTetheredUserResponse();
        LoginTetheredUserResponseType innerResponse = new LoginTetheredUserResponseType();
        innerResponse.setNewSession(new NewSessionType());

        ErrorInfoType errorInfoType = new ErrorInfoType();
        errorInfoType.setInfo("");
        errorInfoType.setErrorCode("");
        innerResponse.getErrors().add(errorInfoType);
        response.setResponse(innerResponse);

        //When
        Optional<String> errorMessage = sut.validate(response);

        //Then
        MatcherAssert.assertThat(errorMessage.isPresent(), is(false));

    }

    @Test
    public void validateTetherByAccount_ThrowsExceptionIfResponseIsnull(){
        assertThatThrownBy(() -> sut.validate((TetherByAccountNumberResponse) null))
                .isInstanceOf(BusinessTetherException.class)
                .hasMessageContaining(WorldLineTetherResponseValidator.ERROR_EMPTY_RESPONSE);
    }

    @Test
    public void validateTetherByAccount_ThrowsExceptionIfResponseTypeIsNull(){
        TetherByAccountNumberResponse response = new TetherByAccountNumberResponse();

        assertThatThrownBy(() -> sut.validate(response))
                .isInstanceOf(BusinessTetherException.class)
                .hasMessageContaining(WorldLineTetherResponseValidator.ERROR_EMPTY_RESPONSE);
    }
    
    @Test
    public void validateTetherByAccount_ThrowsExceptionIfResultCodeIsNotNull(){
        TetherByAccountNumberResponse response = new TetherByAccountNumberResponse();
        TetherByAccountNumberResponseType responseType= new TetherByAccountNumberResponseType();
        ErrorInfoType errorInfoType = new ErrorInfoType();
        errorInfoType.setInfo("");
        errorInfoType.setErrorCode("Fail");
        responseType.setResultCode("ValidationError");
        responseType.getErrors().add(errorInfoType);
        TetherDetailsType tetherDetails= new TetherDetailsType();
        tetherDetails.setTetheredUserGuid("6f3a0338-24b9-474b-b961-6ef9d07145ae");
        responseType.setTetherDetails(tetherDetails);
        response.setResponse(responseType);

        assertThatThrownBy(() -> sut.validate(response))
                .isInstanceOf(PibaException.class)
                .hasMessageContaining("Fail");
    }

    @Test
    public void validateTetherByCard_ThrowsExceptionIfResponseIsnull(){
        assertThatThrownBy(() -> sut.validate((TetherByCardNumberResponse) null))
                .isInstanceOf(BusinessTetherException.class)
                .hasMessageContaining(WorldLineTetherResponseValidator.ERROR_EMPTY_RESPONSE);
    }

    @Test
    public void validateTetherByCard_ThrowsExceptionIfResponseTypeIsNull(){
        TetherByCardNumberResponse response = new TetherByCardNumberResponse();

        assertThatThrownBy(() -> sut.validate(response))
                .isInstanceOf(BusinessTetherException.class)
                .hasMessageContaining(WorldLineTetherResponseValidator.ERROR_EMPTY_RESPONSE);
    }

    @Test
    public void validateTetherByCard_ThrowsExceptionIfResultCodeIsNotNull(){
        TetherByCardNumberResponse response = new TetherByCardNumberResponse();
        TetherByCardNumberResponseType responseType= new TetherByCardNumberResponseType();
        ErrorInfoType errorInfoType = new ErrorInfoType();
        errorInfoType.setInfo("");
        errorInfoType.setErrorCode("Fail");
        responseType.setResultCode("ValidationError");
        responseType.getErrors().add(errorInfoType);
        TetherDetailsType tetherDetails= new TetherDetailsType();
        tetherDetails.setTetheredUserGuid("6f3a0338-24b9-474b-b961-6ef9d07145ae");
        responseType.setTetherDetails(tetherDetails);
        response.setResponse(responseType);

        assertThatThrownBy(() -> sut.validate(response))
                .isInstanceOf(PibaException.class)
                .hasMessageContaining("Fail");
    }
}
