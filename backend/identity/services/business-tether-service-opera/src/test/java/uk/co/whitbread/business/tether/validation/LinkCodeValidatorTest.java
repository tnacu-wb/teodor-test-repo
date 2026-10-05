package uk.co.whitbread.business.tether.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.business.tether.properties.ValidatorProperties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LinkCodeValidatorTest {

    private LinkCodeValidator validator;

    @Mock
    private ValidatorProperties validatorProperties;

    @BeforeEach
    public void setUp() {
        validator = new LinkCodeValidator(validatorProperties);
        when(validatorProperties.getLength()).thenReturn(11);
    }

    @Test
    public void shouldReturnTrueWhenLinkCodeLengthIsExactly11() {
        assertTrue(validator.isValid("qwe-qwe-qwe", null));
    }

    @Test
    public void shouldReturnFalseWhenLinkCodeLengthIsLessThan11() {
        assertFalse(validator.isValid("qwe-qwe-qw", null));
    }
}