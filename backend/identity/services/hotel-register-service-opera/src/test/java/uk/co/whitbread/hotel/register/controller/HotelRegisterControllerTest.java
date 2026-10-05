package uk.co.whitbread.hotel.register.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.common.validators.password.PasswordByConfigValidator;
import uk.co.whitbread.hotel.captcha.exception.CaptchaVerificationException;
import uk.co.whitbread.hotel.captcha.service.CaptchaService;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.hotel.register.model.CustomerResponse;
import uk.co.whitbread.hotel.register.properties.RegisterProperties;
import uk.co.whitbread.hotel.register.service.HotelRegisterService;
import uk.co.whitbread.hotel.register.validation.BusinessCustomerValidator;

import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HotelRegisterControllerTest {

    public static final String PASSWORD = "password-test";
    public static final String DEFAULT_LANGUAGE = "en";
    @Mock
    private HotelRegisterService registerService;

    @Mock
    private CaptchaService captchaService;

    @Mock
    private RegisterProperties registerProperties;

    @Mock
    private BusinessCustomerValidator businessCustomerValidator;

    @Mock
    private PasswordByConfigValidator passwordByConfigValidatorMock;

    @InjectMocks
    private HotelRegisterController underTest;

    @BeforeEach
    public void setUp() {
        when(passwordByConfigValidatorMock.isValid(eq(PASSWORD), anyString(), any(Consumer.class))).thenReturn(true);
    }

    @Test
    public void nullCaptchaShouldCallServiceAndReturnCreatedStatus() {
        Customer payload = new Customer();
        payload.setPassword(PASSWORD);
        payload.setCaptcha(null);

        ResponseEntity<CustomerResponse> responseEntity = underTest.createCustomer(payload, DEFAULT_LANGUAGE,false);

        verify(registerService).createCustomer(payload, DEFAULT_LANGUAGE, false);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void InvalidCaptchaShouldThrowException() {
        Customer payload = new Customer();
        payload.setCaptcha("invalid");

        when(captchaService.isValid(anyString())).thenReturn(false);

        assertThrows(CaptchaVerificationException.class, () -> underTest.createCustomer(payload, "gb", false));
    }

    @Test
    public void ValidCaptchaShouldCallServiceAndReturnCreatedStatus() {
        Customer payload = new Customer();
        payload.setCaptcha("valid");

        when(captchaService.isValid(anyString())).thenReturn(true);
        ResponseEntity<CustomerResponse> responseEntity = underTest.createCustomer(payload, DEFAULT_LANGUAGE, false);

        verify(registerService).createCustomer(payload, DEFAULT_LANGUAGE, false);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    void shouldEnforcePasswordPolicyWhenCreatingCustomer() {
        // Given
        Customer payload = new Customer();
        payload.setPassword("ValidPassword123!");
        payload.setCaptcha(null);

        when(passwordByConfigValidatorMock.isValid(eq(payload.getPassword()), anyString(), any(Consumer.class)))
            .thenReturn(true);

        // When
        underTest.createCustomer(payload, DEFAULT_LANGUAGE, false);

        // Then
        verify(passwordByConfigValidatorMock).isValid(eq(payload.getPassword()), anyString(), any(Consumer.class));
    }

}
