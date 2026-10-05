package uk.co.whitbread.hotel.account.service;

import static java.lang.Boolean.TRUE;
import static uk.co.whitbread.hotel.account.utils.Utils.sanitizeInputString;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.account.model.ForgottenPasswordRequest;
import uk.co.whitbread.hotel.account.model.ForgottenPasswordResponse;
import uk.co.whitbread.hotel.account.properties.Auth0Properties;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.model.Auth0User;
import uk.co.whitbread.shared.auth.model.PasswordResetRequest;
import uk.co.whitbread.shared.auth.service.ManagementService;
import uk.co.whitbread.shared.azureemail.model.PasswordReset;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;

@Slf4j
@RequiredArgsConstructor
@Service
public class HotelAuthServiceV2 {

  private final Auth0Properties auth0Properties;
  private final ManagementService leisureManagementService;
  private final ManagementService businessManagementService;
  private final AzureEmailService azureEmailService;

  public ForgottenPasswordResponse forgotPassword(ForgottenPasswordRequest request,
      boolean business) {

    Optional<String> userId = getAuth0UserId(request.getUsername(), business);
    if (userId.isEmpty()) {
      log.info("Received a forgotten password request who wasn't found in Auth0");
      //Pretending it was ok, to not give information about whether an email is in the system or not.
      return new ForgottenPasswordResponse(TRUE);
    }
    if (business) {
      sendAsyncBBResetPasswordEmail(userId.get(), request);
      return new ForgottenPasswordResponse(TRUE);
    }
    sendAsyncPIResetPasswordEmail(userId.get(), request);
    return new ForgottenPasswordResponse(TRUE);
  }

  void sendAsyncPIResetPasswordEmail(String userId, ForgottenPasswordRequest request) {
    CompletableFuture.runAsync(() -> {
      try {
        PasswordReset passwordReset = getPasswordReset(userId, request, false);
        azureEmailService.sendResetPasswordEmail(passwordReset);
        log.info("Forgotten password email sent success");
      } catch (Exception e) {
        log.error("An error occurred while trying to send a reset password email", e);
      }
    });
  }

  void sendAsyncBBResetPasswordEmail(String userId, ForgottenPasswordRequest request) {
    CompletableFuture.runAsync(() -> {
      try {
        PasswordReset passwordReset = getPasswordReset(userId, request, true);
        azureEmailService.sendBBResetPasswordEmail(passwordReset);
        log.info("BB forgotten password email sent success");
      } catch (Exception e) {
        log.error("An error occurred while trying to send a BB reset password email", e);
      }
    });
  }

  private PasswordReset getPasswordReset(String userId, ForgottenPasswordRequest request,
      boolean business) {

    PasswordResetRequest passwordResetRequest = PasswordResetRequest.builder()
        .userId(userId)
        .resultUrl(request.getUrl())
        .markEmailAsVerified(TRUE)
        .ttlSeconds(auth0Properties.getResetPasswordTokenTtlSec())
        .build();
    String resetPasswordUrl = getManagementService(business).requestPasswordChange(passwordResetRequest);
    return PasswordReset.builder()
        .email(request.getUsername())
        .resetPasswordUrl(resetPasswordUrl)
        .build();
  }

  private Optional<String> getAuth0UserId(String email, boolean business) {
    try {
      final ManagementService managementService = getManagementService(business);
      Optional<Auth0User> optionalUser = managementService.getUser(email);
      if (optionalUser.isEmpty()) {
        log.debug("User {} doesn't exist in Auth0 database {}", sanitizeInputString(email),
            managementService.getManagementProperties().getConnection());
        return Optional.empty();
      }
      String userId = optionalUser.get().getId();
      return Optional.of(userId);
    } catch (AuthServiceException e) {
      log.error("Error while retrieving user from Auth0", e);
      return Optional.empty();
    }
  }

  private ManagementService getManagementService(boolean business) {
    return business ? businessManagementService : leisureManagementService;
  }
}
