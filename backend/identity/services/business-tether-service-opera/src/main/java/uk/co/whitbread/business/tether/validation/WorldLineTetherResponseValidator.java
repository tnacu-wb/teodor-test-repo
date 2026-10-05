package uk.co.whitbread.business.tether.validation;

import java.util.Objects;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.business.tether.exception.BusinessTetherException;
import uk.co.whitbread.piba.api.exception.PibaException;
import uk.co.whitbread.piba.api.validation.WorldLineResponseValidator;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUserResponse;
import worldline.mst.bsm.api.b2b.pi.data.RefreshSessionResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetherByAccountNumberResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetherByCardNumberResponse;

@Component
@Slf4j
public class WorldLineTetherResponseValidator extends WorldLineResponseValidator{
    public static final String ERROR_EMPTY_RESPONSE = "Worldline returned empty response.";
    
    public Optional<String> validate(LoginTetheredUserResponse response) {
        if (response == null || response.getResponse() == null) {
            return Optional.of(ERROR_EMPTY_RESPONSE);
        }
        try {
            validate(response.getResponse());
        } catch (PibaException exception) {
            return Optional.of(exception.getMessage());
        }
        
        return Optional.empty();
    }
    
    public Optional<String> validate(RefreshSessionResponse response) {
        if (response == null || response.getResponse() == null) {
            return Optional.of(ERROR_EMPTY_RESPONSE);
        }
        try {
            validate(response.getResponse());
        } catch (PibaException exception) {
            return Optional.of(exception.getMessage());
        }
        return Optional.empty();
    }
    
    public void validate(TetherByAccountNumberResponse response) {
        Optional.ofNullable(response)
                .filter(tetherResponse -> Objects.nonNull(tetherResponse.getResponse()))
                .ifPresentOrElse(tetherResponse -> validate(tetherResponse.getResponse()),
                        () -> throwBusinessTetherException(ERROR_EMPTY_RESPONSE));
    }
    
    
    public void validate(TetherByCardNumberResponse response) {
        Optional.ofNullable(response)
                .filter(tetherResponse -> Objects.nonNull(tetherResponse.getResponse()))
                .ifPresentOrElse(tetherResponse -> validate(tetherResponse.getResponse()),
                        () -> throwBusinessTetherException(ERROR_EMPTY_RESPONSE));
    }
    
    private void throwBusinessTetherException(String errorMessage) {
        throw new BusinessTetherException(errorMessage);
    }
}
