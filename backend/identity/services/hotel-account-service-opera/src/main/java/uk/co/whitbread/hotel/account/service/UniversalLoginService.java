package uk.co.whitbread.hotel.account.service;

import static uk.co.whitbread.hotel.account.utils.Utils.sanitizeInputString;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.account.model.EmailMessageType;
import uk.co.whitbread.hotel.account.model.PasswordResetResponse;
import uk.co.whitbread.hotel.account.model.SendEmailRequest;
import uk.co.whitbread.hotel.account.properties.Auth0Properties;
import uk.co.whitbread.hotel.account.service.auth0.Auth0Service;
import uk.co.whitbread.shared.azureemail.model.AccountBlocked;
import uk.co.whitbread.shared.azureemail.model.AccountRegistration;
import uk.co.whitbread.shared.azureemail.model.ChangePasswordConfirmation;
import uk.co.whitbread.shared.azureemail.model.OtpPasswordReset;
import uk.co.whitbread.shared.azureemail.service.GenericEmailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UniversalLoginService {

  private final GenericEmailService genericEmailService;

  @Qualifier("auth0LeisureService")
  private final Auth0Service auth0LeisureService;

  private final Auth0Properties auth0Properties;

  @Value("${universal-login.login-domain-url:}")
  private String loginDomainUrl;

  public void sendEmailToLeisureAccount(String email, SendEmailRequest request) {
    log.info("Sending email to leisure account: {} with message type: {} and language: {}",
        sanitizeInputString(email), sanitizeInputString(request.getMessageType()),
        sanitizeInputString(request.getLanguage()));

    EmailMessageType messageType = EmailMessageType.fromValue(request.getMessageType());

    switch (messageType) {
      case ACCOUNT_REGISTRATION:
        sendAccountRegistrationEmail(email, request);
        break;
      case CHANGE_PASSWORD_CONFIRMATION:
        sendChangePasswordConfirmationEmail(email, request);
        break;
      case RESET_EMAIL_BY_CODE:
        sendPasswordResetEmail(email, request);
        break;
      case BLOCKED_ACCOUNT:
        sendBlockedAccountEmail(email, request);
        break;
    }
  }

  private void sendAccountRegistrationEmail(String email, SendEmailRequest request) {
    AccountRegistration accountRegistration = AccountRegistration.builder()
        .emailAddress(email)
        .customerName(buildCustomerName(request))
        .language(request.getLanguage())
        .loginUrl(generateLoginUrl(request.getLanguage()))
        .build();
    runAsync("account registration", () -> genericEmailService.sendAccountRegistrationEmail(accountRegistration));
  }

  private void sendChangePasswordConfirmationEmail(String email, SendEmailRequest request) {
    ChangePasswordConfirmation changePasswordConfirmation = ChangePasswordConfirmation.builder()
        .emailAddress(email)
        .customerName(buildCustomerName(request))
        .language(request.getLanguage())
        .loginUrl(generateLoginUrl(request.getLanguage()))
        .build();
    runAsync("change password confirmation", () ->
        genericEmailService.sendChangePasswordConfirmationEmail(changePasswordConfirmation));
  }

  private void sendBlockedAccountEmail(String email, SendEmailRequest request) {
    runAsync("blocked account", () -> {
      PasswordResetResponse passwordResetResponse = getPasswordResetUrl(email);
      String passwordResetUrl = Objects.nonNull(passwordResetResponse) ? passwordResetResponse.getPasswordResetUrl()
          : StringUtils.EMPTY;
      AccountBlocked accountBlocked = AccountBlocked.builder()
          .emailAddress(email)
          .customerName(buildCustomerName(request))
          .passwordResetUrl(passwordResetUrl)
          .build();
      genericEmailService.sendAccountBlockedEmail(accountBlocked);
    });
  }

  private void sendPasswordResetEmail(String email, SendEmailRequest request) {
    OtpPasswordReset otpPasswordReset = OtpPasswordReset.builder()
        .emailAddress(email)
        .customerName(buildCustomerName(request))
        .language(request.getLanguage())
        .otp(request.getOtp())
        .build();
    runAsync("password reset", () -> genericEmailService.sendOtpPasswordResetEmail(otpPasswordReset));
  }

  public PasswordResetResponse getPasswordResetUrl(String email) {
    return auth0LeisureService.getPasswordResetUrl(email);
  }

  private String generateLoginUrl(String locale) {
    String connection = auth0Properties.getB2cConnection();
    return loginDomainUrl + "?connection=" + connection + "&ui_locales=" + locale;
  }

  private String buildCustomerName(SendEmailRequest request) {
    return request.getFirstName() + " " + request.getLastName();
  }

  private void runAsync(String emailType, Runnable task) {
    CompletableFuture.runAsync(() -> {
      try {
        task.run();
      } catch (Exception e) {
        log.error("An error occurred while sending {} email", sanitizeInputString(emailType), e);
      }
    });
  }
}



