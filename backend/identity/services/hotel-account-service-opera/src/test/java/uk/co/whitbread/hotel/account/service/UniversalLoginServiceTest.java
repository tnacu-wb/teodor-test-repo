package uk.co.whitbread.hotel.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.hotel.account.model.PasswordResetResponse;
import uk.co.whitbread.hotel.account.model.SendEmailRequest;
import uk.co.whitbread.hotel.account.properties.Auth0Properties;
import uk.co.whitbread.hotel.account.service.auth0.Auth0Service;
import uk.co.whitbread.shared.azureemail.model.AccountBlocked;
import uk.co.whitbread.shared.azureemail.model.AccountRegistration;
import uk.co.whitbread.shared.azureemail.model.ChangePasswordConfirmation;
import uk.co.whitbread.shared.azureemail.model.OtpPasswordReset;
import uk.co.whitbread.shared.azureemail.service.GenericEmailService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UniversalLoginServiceTest {

  private static final String TEST_EMAIL = "test@example.com";
  private static final String TEST_FIRST_NAME = "John";
  private static final String TEST_LAST_NAME = "Doe";
  private static final String TEST_LANGUAGE = "en";
  private static final String TEST_URL = "https://example.com/reset";
  private static final String TEST_OTP = "123456";

  @Mock
  private GenericEmailService genericEmailService;

  @Mock
  private Auth0Service auth0LeisureService;

  @Mock
  private Auth0Properties auth0Properties;

  @InjectMocks
  private UniversalLoginService universalLoginService;

  @BeforeEach
  void setUp() {
    doNothing().when(genericEmailService).sendAccountBlockedEmail(any(AccountBlocked.class));
    doNothing().when(genericEmailService).sendAccountRegistrationEmail(any(AccountRegistration.class));
    doNothing().when(genericEmailService).sendChangePasswordConfirmationEmail(any(ChangePasswordConfirmation.class));
    doNothing().when(genericEmailService).sendOtpPasswordResetEmail(any(OtpPasswordReset.class));
    when(auth0Properties.getB2cConnection()).thenReturn("test-connection");
  }

  @Test
  void shouldSendAccountRegistrationEmail_WithAllParameters() {
    // Given
    SendEmailRequest request = SendEmailRequest.builder()
        .messageType("account_registration")
        .firstName(TEST_FIRST_NAME)
        .lastName(TEST_LAST_NAME)
        .language(TEST_LANGUAGE)
        .build();

    // When
    universalLoginService.sendEmailToLeisureAccount(TEST_EMAIL, request);

    // Then
    verify(genericEmailService, timeout(2000)).sendAccountRegistrationEmail(any(AccountRegistration.class));
  }

  @Test
  void shouldSendChangePasswordConfirmationEmail() {
    // Given
    SendEmailRequest request = SendEmailRequest.builder()
        .messageType("change_password_confirmation")
        .firstName(TEST_FIRST_NAME)
        .lastName(TEST_LAST_NAME)
        .language(TEST_LANGUAGE)
        .build();

    // When
    universalLoginService.sendEmailToLeisureAccount(TEST_EMAIL, request);

    // Then
    verify(genericEmailService, timeout(2000)).sendChangePasswordConfirmationEmail(any(ChangePasswordConfirmation.class));
  }

  @Test
  void shouldSendBlockedAccountEmail_WithAllParameters() {
    // Given
    when(auth0LeisureService.getPasswordResetUrl(TEST_EMAIL))
        .thenReturn(PasswordResetResponse.builder().passwordResetUrl(TEST_URL).build());

    SendEmailRequest request = SendEmailRequest.builder()
        .messageType("blocked_account")
        .firstName(TEST_FIRST_NAME)
        .lastName(TEST_LAST_NAME)
        .language(TEST_LANGUAGE)
        .build();

    // When
    universalLoginService.sendEmailToLeisureAccount(TEST_EMAIL, request);

    // Then
    verify(genericEmailService, timeout(2000)).sendAccountBlockedEmail(any(AccountBlocked.class));
  }

  @Test
  void shouldSendBlockedAccountEmail_WhenPasswordResetUrlIsNull() {
    // Given
    when(auth0LeisureService.getPasswordResetUrl(TEST_EMAIL))
        .thenReturn(null);

    SendEmailRequest request = SendEmailRequest.builder()
        .messageType("blocked_account")
        .firstName(TEST_FIRST_NAME)
        .lastName(TEST_LAST_NAME)
        .language(TEST_LANGUAGE)
        .build();

    // When
    universalLoginService.sendEmailToLeisureAccount(TEST_EMAIL, request);

    // Then
    verify(genericEmailService, timeout(2000)).sendAccountBlockedEmail(any(AccountBlocked.class));
  }

  @Test
  void shouldSendResetEmailByCode_WithAllParameters() {
    // Given
    SendEmailRequest request = SendEmailRequest.builder()
        .messageType("reset_email_by_code")
        .firstName(TEST_FIRST_NAME)
        .lastName(TEST_LAST_NAME)
        .language(TEST_LANGUAGE)
        .otp(TEST_OTP)
        .build();

    // When
    universalLoginService.sendEmailToLeisureAccount(TEST_EMAIL, request);

    // Then
    verify(genericEmailService, timeout(2000)).sendOtpPasswordResetEmail(any(OtpPasswordReset.class));
  }

  @Test
  void shouldThrowException_WhenInvalidMessageType() {
    // Given
    SendEmailRequest request = SendEmailRequest.builder()
        .messageType("invalid_type")
        .language(TEST_LANGUAGE)
        .build();

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        universalLoginService.sendEmailToLeisureAccount(TEST_EMAIL, request)
    );

    assertEquals("Unknown message type: invalid_type", exception.getMessage());
    verifyNoInteractions(genericEmailService);
  }

  @Test
  void shouldHandleException_WhenGenericEmailServiceFails_AccountRegistration() {
    // Given
    doThrow(new RuntimeException("Email service failure"))
        .when(genericEmailService).sendAccountRegistrationEmail(any(AccountRegistration.class));

    SendEmailRequest request = SendEmailRequest.builder()
        .messageType("account_registration")
        .firstName(TEST_FIRST_NAME)
        .lastName(TEST_LAST_NAME)
        .language(TEST_LANGUAGE)
        .build();

    // When - should not throw exception because it's handled in async block
    universalLoginService.sendEmailToLeisureAccount(TEST_EMAIL, request);

    // Then - verify the method was called (exception is logged, not thrown)
    verify(genericEmailService, timeout(2000)).sendAccountRegistrationEmail(any(AccountRegistration.class));
  }

  @Test
  void shouldHandleException_WhenGenericEmailServiceFails_BlockedAccount() {
    // Given
    when(auth0LeisureService.getPasswordResetUrl(TEST_EMAIL))
        .thenReturn(PasswordResetResponse.builder().passwordResetUrl(TEST_URL).build());

    doThrow(new RuntimeException("Email service failure"))
        .when(genericEmailService).sendAccountBlockedEmail(any(AccountBlocked.class));

    SendEmailRequest request = SendEmailRequest.builder()
        .messageType("blocked_account")
        .firstName(TEST_FIRST_NAME)
        .lastName(TEST_LAST_NAME)
        .language(TEST_LANGUAGE)
        .build();

    // When - should not throw exception because it's handled in async block
    universalLoginService.sendEmailToLeisureAccount(TEST_EMAIL, request);

    // Then - verify the method was called (exception is logged, not thrown)
    verify(genericEmailService, timeout(2000)).sendAccountBlockedEmail(any(AccountBlocked.class));
  }

  @Test
  void shouldHandleException_WhenGenericEmailServiceFails_ResetByCode() {
    // Given
    doThrow(new RuntimeException("Email service failure"))
        .when(genericEmailService).sendOtpPasswordResetEmail(any(OtpPasswordReset.class));

    SendEmailRequest request = SendEmailRequest.builder()
        .messageType("reset_email_by_code")
        .firstName(TEST_FIRST_NAME)
        .lastName(TEST_LAST_NAME)
        .language(TEST_LANGUAGE)
        .otp(TEST_OTP)
        .build();

    // When - should not throw exception because it's handled in async block
    universalLoginService.sendEmailToLeisureAccount(TEST_EMAIL, request);

    // Then - verify the method was called (exception is logged, not thrown)
    verify(genericEmailService, timeout(2000)).sendOtpPasswordResetEmail(any(OtpPasswordReset.class));
  }

  @Test
  void shouldHandleException_WhenGenericEmailServiceFails_ChangePasswordConfirmation() {
    // Given
    doThrow(new RuntimeException("Email service failure"))
        .when(genericEmailService).sendChangePasswordConfirmationEmail(any(ChangePasswordConfirmation.class));

    SendEmailRequest request = SendEmailRequest.builder()
        .messageType("change_password_confirmation")
        .firstName(TEST_FIRST_NAME)
        .lastName(TEST_LAST_NAME)
        .language(TEST_LANGUAGE)
        .build();

    // When - should not throw exception because it's handled in async block
    universalLoginService.sendEmailToLeisureAccount(TEST_EMAIL, request);

    // Then - verify the method was called (exception is logged, not thrown)
    verify(genericEmailService, timeout(2000)).sendChangePasswordConfirmationEmail(any(ChangePasswordConfirmation.class));
  }

  @Test
  void test_getPasswordResetUrl() {
    // Given
    when(auth0LeisureService.getPasswordResetUrl(any())).thenReturn(PasswordResetResponse.builder().passwordResetUrl("url").build());

    // When
    PasswordResetResponse resp = universalLoginService.getPasswordResetUrl(TEST_EMAIL);

    // Then
    assertNotNull(resp);
    assertEquals("url", resp.getPasswordResetUrl());
  }
}


