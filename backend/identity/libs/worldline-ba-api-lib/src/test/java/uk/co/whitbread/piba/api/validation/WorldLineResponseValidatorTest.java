package uk.co.whitbread.piba.api.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.piba.api.exception.PibaException;
import worldline.mst.bsm.api.b2b.pi.data.ErrorInfoType;
import worldline.mst.bsm.api.b2b.pi.data.ResponseType;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorldLineResponseValidatorTest {
    private WorldLineResponseValidator sut;

    @BeforeEach
    void setUp()  {
        sut = new WorldLineResponseValidator();
    }

    @Test
    void validate_shouldHandleNullInput() {
        //Then
        assertThatThrownBy(() -> sut.validate(null))
                .isInstanceOf(PibaException.class)
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
        response.setResultCode("ValidationError");
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
