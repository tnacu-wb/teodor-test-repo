package uk.co.whitbread.piba.registration.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.piba.api.exception.PibaException;
import uk.co.whitbread.piba.registration.exception.PibaRegistrationException;
import worldline.mst.bsm.api.b2b.pi.data.ErrorInfoType;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmitResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmitResponseType;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class WorldLineRegistrationSubmitResponseValidatorTest {
    public static final String ERROR_EMPTY_RESPONSE = "Worldline returned empty response.";

    @InjectMocks
    private WorldLineRegistrationResponseValidator objectUnderTest;

    @Test
    void validateThrowsExceptionIfResponseIsnull(){
        assertThatThrownBy(() -> objectUnderTest.validate((RegistrationSubmitResponse) null))
                .isInstanceOf(PibaRegistrationException.class)
                .hasMessageContaining(ERROR_EMPTY_RESPONSE);
    }

    @Test
    void validateThrowsExceptionIfRegistrationSubmitResponseTypeIsNull(){
        RegistrationSubmitResponse registrationSubmitResponse = new RegistrationSubmitResponse();

        assertThatThrownBy(() -> objectUnderTest.validate(registrationSubmitResponse))
                .isInstanceOf(PibaRegistrationException.class)
                .hasMessageContaining(ERROR_EMPTY_RESPONSE);
    }

    @Test
    void validateReturnsEmptyIfRegistrationResultCodeIsNull(){
        RegistrationSubmitResponse registrationSubmitResponse = new RegistrationSubmitResponse();
        RegistrationSubmitResponseType registrationSubmitResponseType= new RegistrationSubmitResponseType();
        registrationSubmitResponse.setResponse(registrationSubmitResponseType);

        assertThatCode(()->objectUnderTest.validate(registrationSubmitResponse)).doesNotThrowAnyException();
    }

    @Test
    void validateThrowsExceptionIfRegistrationResultCodeIsNotNull(){
        RegistrationSubmitResponse registrationSubmitResponse = new RegistrationSubmitResponse();
        RegistrationSubmitResponseType registrationSubmitResponseType= new RegistrationSubmitResponseType();
        registrationSubmitResponseType.setResultCode("ValidationError");
        ErrorInfoType errorInfoType = new ErrorInfoType();
        errorInfoType.setErrorCode("RegistrationFailAccountHolderAlreadyRegistered");
        errorInfoType.setInfo("ValidationError");
        registrationSubmitResponseType.getErrors().add(errorInfoType);
        registrationSubmitResponse.setResponse(registrationSubmitResponseType);
        assertThatThrownBy(() -> objectUnderTest.validate(registrationSubmitResponse))
                .isInstanceOf(PibaException.class)
                .hasMessageContaining("RegistrationFailAccountHolderAlreadyRegistered");
    }
}
