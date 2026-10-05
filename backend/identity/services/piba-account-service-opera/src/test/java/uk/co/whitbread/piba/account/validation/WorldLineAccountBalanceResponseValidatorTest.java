package uk.co.whitbread.piba.account.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.piba.account.exception.PibaAccountException;
import uk.co.whitbread.piba.api.exception.PibaException;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalancesResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalancesResponseType;
import worldline.mst.bsm.api.b2b.pi.data.ErrorInfoType;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class WorldLineAccountBalanceResponseValidatorTest {

    public static final String ERROR_EMPTY_RESPONSE = "Worldline returned empty response.";

    @InjectMocks
    private WorldLineAccountResponseValidator objectUnderTest;

    @Test
    void validateThrowsExceptionIfResponseIsnull(){
        assertThatThrownBy(() -> objectUnderTest.validate((CustomerAccountViewCurrentBalancesResponse) null))
                .isInstanceOf(PibaAccountException.class)
                .hasMessageContaining(ERROR_EMPTY_RESPONSE);
    }

    @Test
    void validateThrowsExceptionIfRegistrationAuthenticateResponseTypeIsNull(){
        CustomerAccountViewCurrentBalancesResponse response = new CustomerAccountViewCurrentBalancesResponse();

        assertThatThrownBy(() -> objectUnderTest.validate(response))
                .isInstanceOf(PibaAccountException.class)
                .hasMessageContaining(ERROR_EMPTY_RESPONSE);
    }

    @Test
    void validateReturnsEmptyIfRegistrationResultCodeIsNull(){
        CustomerAccountViewCurrentBalancesResponse response = new CustomerAccountViewCurrentBalancesResponse();
        CustomerAccountViewCurrentBalancesResponseType responseType= new CustomerAccountViewCurrentBalancesResponseType();
        response.setResponse(responseType);

        assertThatCode(()->objectUnderTest.validate(response)).doesNotThrowAnyException();
    }

    @Test
    void validateThrowsExceptionIfRegistrationResultCodeIsNotNull(){
        CustomerAccountViewCurrentBalancesResponse response = new CustomerAccountViewCurrentBalancesResponse();
        CustomerAccountViewCurrentBalancesResponseType responseType= new CustomerAccountViewCurrentBalancesResponseType();
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
