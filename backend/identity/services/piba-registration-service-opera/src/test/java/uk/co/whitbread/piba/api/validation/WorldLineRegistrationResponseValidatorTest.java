package uk.co.whitbread.piba.api.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.piba.api.exception.PibaException;
import uk.co.whitbread.piba.registration.exception.PibaRegistrationException;
import uk.co.whitbread.piba.registration.validation.WorldLineRegistrationResponseValidator;
import worldline.mst.bsm.api.b2b.pi.data.*;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorldLineRegistrationResponseValidatorTest {
    private WorldLineRegistrationResponseValidator sut;

    @BeforeEach
    public void setUp()  {
        sut = new WorldLineRegistrationResponseValidator();
    }

    @Test
    void validate_shouldHandleNullInput() {
        assertThatThrownBy(() -> sut.validate((RegistrationGetInfoResponse) null))
                .isInstanceOf(PibaRegistrationException.class)
                .hasMessageContaining(WorldLineResponseValidator.ERROR_EMPTY_RESPONSE);
    }

    @Test
    void validateRegistrationGetInfoResponse_shouldHandleNullInput() {
        assertThatThrownBy(() -> sut.validate((RegistrationGetInfoResponse) null))
                .isInstanceOf(PibaRegistrationException.class)
                .hasMessageContaining(WorldLineResponseValidator.ERROR_EMPTY_RESPONSE);
    }

    @Test
    void validateRegistrationAuthenticateResponse_shouldHandleNullInput() {
        assertThatThrownBy(() -> sut.validate((RegistrationAuthenticateResponse) null))
                .isInstanceOf(PibaRegistrationException.class)
                .hasMessageContaining(WorldLineResponseValidator.ERROR_EMPTY_RESPONSE);
    }

    @Test
    void validateRegistrationSubmitResponse_shouldHandleNullInput() {
        assertThatThrownBy(() -> sut.validate((RegistrationSubmitResponse) null))
                .isInstanceOf(PibaRegistrationException.class)
                .hasMessageContaining(WorldLineResponseValidator.ERROR_EMPTY_RESPONSE);
    }

    @Test
    void validate_shouldExtractErrorMessage() {
        //Given
        ResponseType response = new ResponseType();

        ErrorInfoType errorInfoType = new ErrorInfoType();
        errorInfoType.setInfo("Error message");
        errorInfoType.setErrorCode("ErrorCode");
        response.getErrors().add(errorInfoType);

        //Then
        assertThatThrownBy(() -> sut.validate(response))
                .isInstanceOf(PibaException.class)
                .hasMessageContaining("ErrorCode");

    }

    @Test
    void validate_shouldHandleNullErrorDetails() {
        //Given
        ResponseType response = new ResponseType();

        //Then
        assertThatCode(()->sut.validate(response)).doesNotThrowAnyException();
    }

}
