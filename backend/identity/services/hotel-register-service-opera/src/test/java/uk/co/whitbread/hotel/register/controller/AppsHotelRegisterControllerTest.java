package uk.co.whitbread.hotel.register.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
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
import uk.co.whitbread.hotel.register.model.AppsCustomer;
import uk.co.whitbread.hotel.register.model.CustomerResponse;
import uk.co.whitbread.hotel.register.service.HotelRegisterService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AppsHotelRegisterControllerTest {

  private static final String PASSWORD = "password-test";
  private static final String DEFAULT_LANGUAGE = "en";
  @Mock
  private HotelRegisterService registerService;

  @Mock
  private CaptchaService captchaService;

  @Mock
  private PasswordByConfigValidator passwordByConfigValidatorMock;

  @InjectMocks
  private AppsHotelRegisterController underTest;

  @BeforeEach
  void setUp() {
    when(passwordByConfigValidatorMock.isValid(eq(PASSWORD), anyString(),
        any(Consumer.class))).thenReturn(true);
  }

  @Test
  void nullCaptchaShouldCallServiceAndReturnCreatedStatus() {
    AppsCustomer payload = new AppsCustomer();
    payload.setPassword(PASSWORD);
    payload.setCaptcha(null);

    ResponseEntity<CustomerResponse> responseEntity = underTest.registerAccount(payload,
        DEFAULT_LANGUAGE);

    verify(registerService).registerAccount(payload, DEFAULT_LANGUAGE);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
  }

  @Test
  void InvalidCaptchaShouldThrowException() {
    AppsCustomer payload = new AppsCustomer();
    payload.setCaptcha("invalid");

    when(captchaService.isValid(anyString())).thenReturn(false);

    assertThrows(CaptchaVerificationException.class,
        () -> underTest.registerAccount(payload, "gb"));
  }


  @Test
  void ValidCaptchaShouldCallServiceAndReturnCreatedStatus() {
    AppsCustomer payload = new AppsCustomer();
    payload.setCaptcha("valid");

    when(captchaService.isValid(anyString())).thenReturn(true);
    ResponseEntity<CustomerResponse> responseEntity = underTest.registerAccount(payload,
        DEFAULT_LANGUAGE);

    verify(registerService).registerAccount(payload, DEFAULT_LANGUAGE);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
  }

  @Test
  void shouldEnforcePasswordPolicyWhenCreatingCustomer() {
    // Given
    AppsCustomer payload = new AppsCustomer();
    payload.setPassword("ValidPassword123!");
    payload.setCaptcha(null);

    when(passwordByConfigValidatorMock.isValid(eq(payload.getPassword()), anyString(),
        any(Consumer.class)))
        .thenReturn(true);

    // When
    underTest.registerAccount(payload, DEFAULT_LANGUAGE);

    // Then
    verify(passwordByConfigValidatorMock).isValid(eq(payload.getPassword()), anyString(),
        any(Consumer.class));
  }
}
