package uk.co.whitbread.piba.registration.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.piba.api.exception.PibaException;
import uk.co.whitbread.piba.registration.exception.PibaRegistrationException;
import worldline.mst.bsm.api.b2b.pi.data.ErrorInfoType;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfoResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfoResponseType;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class WorldLineRegistrationResponseValidatorTest {
    public static final String ERROR_EMPTY_RESPONSE = "Worldline returned empty response.";

    @InjectMocks
    private WorldLineRegistrationResponseValidator objectUnderTest;

    @Test
    void validateThrowsExceptionIfResponseIsnull(){

        assertThatThrownBy(() -> objectUnderTest.validate((RegistrationGetInfoResponse) null))
                .isInstanceOf(PibaRegistrationException.class)
                .hasMessageContaining(ERROR_EMPTY_RESPONSE);
    }

    @Test
    void validateThrowsExceptionIfRegistrationGetInfoResponseTypeIsNull(){
        RegistrationGetInfoResponse registrationGetInfoResponse = new RegistrationGetInfoResponse();

        assertThatThrownBy(() -> objectUnderTest.validate(registrationGetInfoResponse))
                .isInstanceOf(PibaRegistrationException.class)
                .hasMessageContaining(ERROR_EMPTY_RESPONSE);
    }

    @Test
    void validateReturnsEmptyIfRegistrationResultCodeIsNull(){
        RegistrationGetInfoResponse registrationGetInfoResponse = new RegistrationGetInfoResponse();
        RegistrationGetInfoResponseType registrationGetInfoResponseType= new RegistrationGetInfoResponseType();
        registrationGetInfoResponse.setResponse(registrationGetInfoResponseType);
        assertThatCode(()->objectUnderTest.validate(registrationGetInfoResponse)).doesNotThrowAnyException();
    }

    @Test
    void validateThrowsExceptionIfRegistrationResultCodeIsNotNull(){
        RegistrationGetInfoResponse registrationGetInfoResponse = new RegistrationGetInfoResponse();
        RegistrationGetInfoResponseType registrationGetInfoResponseType= new RegistrationGetInfoResponseType();
        registrationGetInfoResponseType.setResultCode("ValidationError");
        ErrorInfoType errorInfoType = new ErrorInfoType();
        errorInfoType.setErrorCode("RegistrationFailAccountHolderAlreadyRegistered");
        errorInfoType.setInfo("RegistrationFailAccountHolderAlreadyRegistered");
        registrationGetInfoResponseType.getErrors().add(errorInfoType);
        registrationGetInfoResponse.setResponse(registrationGetInfoResponseType);
        assertThatThrownBy(() -> objectUnderTest.validate(registrationGetInfoResponse))
                .isInstanceOf(PibaException.class)
                .hasMessageContaining("RegistrationFailAccountHolderAlreadyRegistered");
    }
}
