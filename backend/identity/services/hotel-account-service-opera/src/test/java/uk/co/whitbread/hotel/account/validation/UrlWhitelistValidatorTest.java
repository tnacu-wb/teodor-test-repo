package uk.co.whitbread.hotel.account.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.account.config.WhitelistProperties;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlWhitelistValidatorTest {
    
    @Mock
    WhitelistProperties whitelistProperties;

    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @InjectMocks
    UrlWhitelistValidator urlWhitelistValidator;

    @Test
    void isValidWhenUrlIsNull() {

        boolean result = urlWhitelistValidator.isValid(null, constraintValidatorContext);
        assertThat("Nul URL", result, is(true));

        verify(whitelistProperties, never()).getRegex();
    }

    @Test
    void isValidWhenUrlIsWhiteListed() {

        when(whitelistProperties.getRegex()).thenReturn(Collections.singletonList(".*\\.premierinn\\.com"));

        boolean result = urlWhitelistValidator.isValid("http://www.premierinn.com", constraintValidatorContext);
        assertThat("Whitelisted Url", result, is(true));

        verify(whitelistProperties).getRegex();
    }

    @Test
    void isValidWhenUrlIsNotWhiteListed() {

        when(whitelistProperties.getRegex()).thenReturn(Collections.emptyList());

        boolean result = urlWhitelistValidator.isValid("http://www.premierinn.com", constraintValidatorContext);
        assertThat("Whitelisted Url", result, is(false));

        verify(whitelistProperties).getRegex();
    }

    @Test
    void isValidWhenNoWhitelist() {

        when(whitelistProperties.getRegex()).thenReturn(null);

        boolean result = urlWhitelistValidator.isValid("http://www.premierinn.com", constraintValidatorContext);
        assertThat("Whitelisted Url", result, is(false));

        verify(whitelistProperties).getRegex();
    }

    @Test
    void isValidWhenUrlIsInvalid() {

        when(whitelistProperties.getRegex()).thenReturn(Collections.singletonList(".*\\.premierinn\\.com"));

        boolean result = urlWhitelistValidator.isValid("malformedurl", constraintValidatorContext);
        assertThat("", result, is(false));

        verify(whitelistProperties).getRegex();
    }

}
