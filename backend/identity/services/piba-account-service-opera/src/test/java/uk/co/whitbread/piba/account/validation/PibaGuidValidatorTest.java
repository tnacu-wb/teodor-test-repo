package uk.co.whitbread.piba.account.validation;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.piba.account.exception.PibaGuidException;
import uk.co.whitbread.piba.account.model.PibaTetheredGuidResponse;
import uk.co.whitbread.piba.account.util.TestUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PibaGuidValidatorTest {

    @InjectMocks
    PibaGuidValidator pibaGuidValidator;

    @Mock
    Validator validator;

    @Test
    void validateSuccess(){
        PibaTetheredGuidResponse pibaTetheredGuidResponse = new TestUtil().createPibaTetheredGuidResponse("128", "4");
        pibaGuidValidator.validate(pibaTetheredGuidResponse);
        verify(validator, times(1)).validate(pibaTetheredGuidResponse);
    }

    @Test
    void validateNull() {
        assertThrows(PibaGuidException.class, () -> pibaGuidValidator.validate(null));
    }

    @Test
    void validateMultiple() {
        List<PibaTetheredGuidResponse> multiResponseGuids = new TestUtil().createPibaTetheredGuidResponseMultiple("ABC", "12");
        pibaGuidValidator.validateMultiple(multiResponseGuids);
        verify(validator, times(1)).validate(multiResponseGuids);
    }

    @Test
    void validateMultipleNull() {
        assertThrows(PibaGuidException.class, () -> pibaGuidValidator.validateMultiple(null));
    }

    @Test
    void validateMultipleEmptyGuids() {
        List<PibaTetheredGuidResponse> multiResponseGuids = new TestUtil().createPibaTetheredGuidResponseMultipleEmptyGuids("121", "11");
        assertThrows(PibaGuidException.class, () -> pibaGuidValidator.validateMultiple(multiResponseGuids));
    }
}