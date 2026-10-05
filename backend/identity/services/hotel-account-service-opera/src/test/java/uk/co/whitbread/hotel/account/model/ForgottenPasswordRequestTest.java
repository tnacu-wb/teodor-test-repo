package uk.co.whitbread.hotel.account.model;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.account.config.TestConfig;
import uk.co.whitbread.hotel.account.config.WhitelistProperties;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class ForgottenPasswordRequestTest {

    @Autowired
    private Validator validator;

    @MockitoBean
    WhitelistProperties whitelistProperties;

    @Test
    void shouldValidateForgottenPasswordRequestWithErrors() {
        //Given
        ForgottenPasswordRequest request = new ForgottenPasswordRequest();
        request.setUrl("http://www.invalid.com");

        when(whitelistProperties.getRegex()).thenReturn(Collections.emptyList());

        //When
        List<String> validationMessages = validator.validate(request).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(2));
        assertThat(validationMessages, containsInAnyOrder("username must not be blank","url must be on white list"));
    }

    @Test
    void shouldValidateForgottenPasswordRequestWhenValid() {
        //Given
        ForgottenPasswordRequest request = new ForgottenPasswordRequest();
        request.setUsername("userId");
        request.setUrl("http://www.premierinn.com");


        when(whitelistProperties.getRegex()).thenReturn(Collections.singletonList(".*\\.premierinn\\.com"));

        //When
        List<String> validationMessages = validator.validate(request).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(0));

    }

    @Test
    void shouldValidateForgottenPasswordRequestWhenValidAndUrlNotSet() {
        //Given
        ForgottenPasswordRequest request = new ForgottenPasswordRequest();
        request.setUsername("userId");


        when(whitelistProperties.getRegex()).thenReturn(Collections.singletonList(".*\\.premierinn\\.com"));

        //When
        List<String> validationMessages = validator.validate(request).stream().
                map(constraint -> constraint.getPropertyPath() + " " + constraint.getMessage()).collect(toList());

        //Then
        assertThat(validationMessages, hasSize(0));

    }

}
