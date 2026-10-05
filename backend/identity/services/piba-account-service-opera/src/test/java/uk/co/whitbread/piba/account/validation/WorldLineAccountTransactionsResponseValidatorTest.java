package uk.co.whitbread.piba.account.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.piba.account.exception.PibaAccountException;
import uk.co.whitbread.piba.api.exception.PibaException;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewTransactionsResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewTransactionsResponseType;
import worldline.mst.bsm.api.b2b.pi.data.ErrorInfoType;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class WorldLineAccountTransactionsResponseValidatorTest {

    public static final String ERROR_EMPTY_RESPONSE = "Worldline returned empty response.";

    @InjectMocks
    private WorldLineAccountResponseValidator objectUnderTest;

    @Test
    void validateThrowsExceptionIfResponseIsnull(){
        assertThatThrownBy(() -> objectUnderTest.validate((CustomerAccountViewTransactionsResponse) null))
                .isInstanceOf(PibaAccountException.class)
                .hasMessageContaining(ERROR_EMPTY_RESPONSE);
    }

    @Test
    void validateThrowsExceptionIfRegistrationAuthenticateResponseTypeIsNull(){
        CustomerAccountViewTransactionsResponse response = new CustomerAccountViewTransactionsResponse();

        assertThatThrownBy(() -> objectUnderTest.validate(response))
                .isInstanceOf(PibaAccountException.class)
                .hasMessageContaining(ERROR_EMPTY_RESPONSE);
    }

    @Test
    void validateReturnsEmptyIfRegistrationResultCodeIsNull(){
        CustomerAccountViewTransactionsResponse response = new CustomerAccountViewTransactionsResponse();
        CustomerAccountViewTransactionsResponseType responseType= new CustomerAccountViewTransactionsResponseType();
        response.setResponse(responseType);

        assertThatCode(()->objectUnderTest.validate(response)).doesNotThrowAnyException();

    }

    @Test
    void validateThrowsExceptionIfRegistrationResultCodeIsNotNull(){
        CustomerAccountViewTransactionsResponse response = new CustomerAccountViewTransactionsResponse();
        CustomerAccountViewTransactionsResponseType responseType= new CustomerAccountViewTransactionsResponseType();
        responseType.setResultCode("ValidationError");
        ErrorInfoType errorInfoType = new ErrorInfoType();
        errorInfoType.setErrorCode("Fail");

        responseType.getErrors().add(errorInfoType);
        response.setResponse(responseType);
        assertThatThrownBy(() -> objectUnderTest.validate(responseType))
                .isInstanceOf(PibaException.class)
                .hasMessageContaining("Fail");
    }
}
