package uk.co.whitbread.piba.registration.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.piba.api.exception.PibaException;
import uk.co.whitbread.piba.registration.exception.PibaRegistrationException;
import worldline.mst.bsm.api.b2b.pi.data.ErrorInfoType;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticateResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticateResponseType;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class WorldLineRegistrationAuthenticateResponseValidatorTest {
    public static final String ERROR_EMPTY_RESPONSE = "Worldline returned empty response.";

    @InjectMocks
    private WorldLineRegistrationResponseValidator objectUnderTest;

    @Test
    void validateThrowsExceptionIfResponseIsnull(){
        assertThatThrownBy(() -> objectUnderTest.validate((RegistrationAuthenticateResponse) null))
                .isInstanceOf(PibaRegistrationException.class)
                .hasMessageContaining(ERROR_EMPTY_RESPONSE);
    }

    @Test
    void validateThrowsExceptionIfRegistrationAuthenticateResponseTypeIsNull(){
        RegistrationAuthenticateResponse registrationAuthenticateResponse = new RegistrationAuthenticateResponse();

        assertThatThrownBy(() -> objectUnderTest.validate(registrationAuthenticateResponse))
                .isInstanceOf(PibaRegistrationException.class)
                .hasMessageContaining(ERROR_EMPTY_RESPONSE);
    }

    @Test
    void validateReturnsEmptyIfRegistrationResultCodeIsNull(){
        RegistrationAuthenticateResponse registrationAuthenticateResponse = new RegistrationAuthenticateResponse();
        RegistrationAuthenticateResponseType registrationAuthenticateResponseType= new RegistrationAuthenticateResponseType();
        registrationAuthenticateResponse.setResponse(registrationAuthenticateResponseType);
        assertThatCode(()->objectUnderTest.validate(registrationAuthenticateResponse)).doesNotThrowAnyException();

    }

    @Test
    void validateThrowsExceptionIfRegistrationResultCodeIsNotNull(){
        RegistrationAuthenticateResponse registrationAuthenticateResponse = new RegistrationAuthenticateResponse();
        RegistrationAuthenticateResponseType registrationAuthenticateResponseType= new RegistrationAuthenticateResponseType();
        registrationAuthenticateResponseType.setResultCode("ValidationError");
        ErrorInfoType errorInfoType = new ErrorInfoType();
        errorInfoType.setErrorCode("RegistrationFailAccountHolderAlreadyRegistered");

        registrationAuthenticateResponseType.getErrors().add(errorInfoType);
        registrationAuthenticateResponse.setResponse(registrationAuthenticateResponseType);
        assertThatThrownBy(() -> objectUnderTest.validate(registrationAuthenticateResponse))
                .isInstanceOf(PibaException.class)
                .hasMessageContaining("RegistrationFailAccountHolderAlreadyRegistered");
    }
}
