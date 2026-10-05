package uk.co.whitbread.shared.azureemail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.shared.azureemail.model.AccountBlocked;
import uk.co.whitbread.shared.azureemail.model.AccountRegistration;
import uk.co.whitbread.shared.azureemail.model.AccountUpdate;
import uk.co.whitbread.shared.azureemail.model.ChangePasswordConfirmation;
import uk.co.whitbread.shared.azureemail.model.OtpPasswordReset;
import uk.co.whitbread.shared.azureemail.model.CompanyActivation;
import uk.co.whitbread.shared.azureemail.model.EmployeeAccessLevelChange;
import uk.co.whitbread.shared.azureemail.model.EmployeeAccountActivation;
import uk.co.whitbread.shared.azureemail.model.EmployeeInvite;
import uk.co.whitbread.shared.azureemail.model.EmployeeRegistrationNotification;
import uk.co.whitbread.shared.azureemail.model.OutOfPolicySetup;
import uk.co.whitbread.shared.azureemail.model.PasswordReset;
import uk.co.whitbread.shared.azureemail.model.PiRegister;
import uk.co.whitbread.shared.azureemail.model.RequestToJoinAccepted;
import uk.co.whitbread.shared.azureemail.model.RequestToJoinRejected;

@Service
@Slf4j
@RequiredArgsConstructor
public class AzureEmailService {

  private final PTIEmailService ptiEmailService;
  private final GenericEmailService genericEmailService;

  public void sendResetPasswordEmail(PasswordReset passwordReset) {
    ptiEmailService.sendResetPasswordEmail(passwordReset);
  }

  public void sendBBResetPasswordEmail(PasswordReset passwordReset) {
    genericEmailService.sendBBResetPasswordEmail(passwordReset);
  }

  public void sendAccountUpdateEmail(AccountUpdate accountUpdate) {
    ptiEmailService.sendAccountUpdateEmail(accountUpdate);
  }

  public void sendPiRegisterEmail(PiRegister piRegister) {
    ptiEmailService.sendPiRegisterEmail(piRegister);
  }

  public void sendBBCompanyActivationEmail(CompanyActivation companyActivation) {
    genericEmailService.sendBBCompanyActivationEmail(companyActivation);
  }

  public void sendEmployeeAccountActivationEmail(EmployeeAccountActivation employeeActivation) {
    genericEmailService.sendEmployeeAccountActivationEmail(employeeActivation);
  }

  public void sendBBInviteEmployeeEmail(EmployeeInvite employeeInvite) {
    genericEmailService.sendBBInviteEmployeeEmail(employeeInvite);
  }

  public void sendEmployeeRegistrationNotificationEmail(
      EmployeeRegistrationNotification employeeRegistrationNotification) {
    genericEmailService.sendEmployeeRegistrationNotificationEmail(employeeRegistrationNotification);
  }

  public void sendBBChangeEmployeeAccessLevel(EmployeeAccessLevelChange employeeAccessLevel) {
    genericEmailService.sendBBChangeEmployeeAccessLevel(employeeAccessLevel);
  }

  public void sendOutOfPolicySetupEmail(OutOfPolicySetup outOfPolicySetup) {
    genericEmailService.sendOutOfPolicySetupEmails(outOfPolicySetup);
  }

  public void sendBBRequestToJoinAcceptedEmail(
      RequestToJoinAccepted requestToJoinAccepted) {
    genericEmailService.sendBBRequestToJoinAcceptedEmail(requestToJoinAccepted);
  }

  public void sendRequestToJoinRejectedEmail(RequestToJoinRejected requestToJoinRejected) {
    genericEmailService.sendRequestToJoinRejectedEmail(requestToJoinRejected);
  }

  public void sendAccountRegistrationEmail(AccountRegistration accountRegistration) {
    genericEmailService.sendAccountRegistrationEmail(accountRegistration);
  }

  public void sendOtpPasswordResetEmail(OtpPasswordReset piPasswordReset) {
    genericEmailService.sendOtpPasswordResetEmail(piPasswordReset);
  }

  public void sendChangePasswordConfirmationEmail(
      ChangePasswordConfirmation changePasswordConfirmation) {
    genericEmailService.sendChangePasswordConfirmationEmail(changePasswordConfirmation);
  }

  public void sendAccountBlockedEmail(AccountBlocked accountBlocked) {
    genericEmailService.sendAccountBlockedEmail(accountBlocked);
  }
}
