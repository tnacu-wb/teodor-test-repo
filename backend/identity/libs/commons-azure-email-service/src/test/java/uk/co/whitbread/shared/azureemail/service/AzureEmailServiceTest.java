package uk.co.whitbread.shared.azureemail.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.azureemail.model.AccountBlocked;
import uk.co.whitbread.shared.azureemail.model.AccountRegistration;
import uk.co.whitbread.shared.azureemail.model.AccountUpdate;
import uk.co.whitbread.shared.azureemail.model.ChangePasswordConfirmation;
import uk.co.whitbread.shared.azureemail.model.OtpPasswordReset;
import uk.co.whitbread.shared.azureemail.model.CompanyActivation;
import uk.co.whitbread.shared.azureemail.model.RequestToJoinAccepted;
import uk.co.whitbread.shared.azureemail.model.EmployeeAccessLevelChange;
import uk.co.whitbread.shared.azureemail.model.EmployeeAccountActivation;
import uk.co.whitbread.shared.azureemail.model.EmployeeInvite;
import uk.co.whitbread.shared.azureemail.model.EmployeeRegistrationNotification;
import uk.co.whitbread.shared.azureemail.model.OutOfPolicySetup;
import uk.co.whitbread.shared.azureemail.model.PasswordReset;
import uk.co.whitbread.shared.azureemail.model.PiRegister;
import uk.co.whitbread.shared.azureemail.model.RequestToJoinRejected;

@ExtendWith(MockitoExtension.class)
class AzureEmailServiceTest {

  private static final String EMAIL = "email@test.com";
  private static final String RESET_PASSWORD_URL = "https://return-url.com";
  private PasswordReset passwordReset;

  @Mock
  private PTIEmailService ptiEmailService;
  @Mock
  private GenericEmailService genericEmailService;
  @InjectMocks
  private AzureEmailService azureEmailService;

  @BeforeEach
  void setUp() {
    passwordReset = PasswordReset.builder()
        .email(EMAIL)
        .resetPasswordUrl(RESET_PASSWORD_URL)
        .build();
  }

  @Test
  void sendResetPasswordEmail_Success() {
    azureEmailService.sendResetPasswordEmail(passwordReset);
    verify(ptiEmailService).sendResetPasswordEmail(passwordReset);
  }

  @Test
  void sendBBResetPasswordEmail_Success() {
    azureEmailService.sendBBResetPasswordEmail(passwordReset);
    verify(genericEmailService).sendBBResetPasswordEmail(passwordReset);
  }

  @Test
  void sendAccountUpdateEmail_Success() {
    AccountUpdate accountUpdate = AccountUpdate.builder().email(EMAIL).build();
    azureEmailService.sendAccountUpdateEmail(accountUpdate);
    verify(ptiEmailService).sendAccountUpdateEmail(accountUpdate);
  }

  @Test
  void sendPiRegisterEmail_Success() {
    PiRegister piRegister = PiRegister.builder().emailAddress(EMAIL).build();
    azureEmailService.sendPiRegisterEmail(piRegister);
    verify(ptiEmailService).sendPiRegisterEmail(piRegister);
  }

  @Test
  void sendBBCompanyActivationEmail_Success() {
    CompanyActivation companyActivation = CompanyActivation.builder().email(EMAIL).build();
    azureEmailService.sendBBCompanyActivationEmail(companyActivation);
    verify(genericEmailService).sendBBCompanyActivationEmail(companyActivation);
  }

  @Test
  void sendAccountActivationEmail_Success() {
    final EmployeeAccountActivation accountActivation = EmployeeAccountActivation.builder()
        .employeeEmail(EMAIL).build();
    azureEmailService.sendEmployeeAccountActivationEmail(accountActivation);
    verify(genericEmailService).sendEmployeeAccountActivationEmail(accountActivation);
  }

  @Test
  void sendBBInviteEmployeeEmail_Success() {
    EmployeeInvite employeeInvite = EmployeeInvite.builder().email(EMAIL).build();
    azureEmailService.sendBBInviteEmployeeEmail(employeeInvite);
    verify(genericEmailService).sendBBInviteEmployeeEmail(employeeInvite);
  }

  @Test
  void sendEmployeeRegistrationNotificationEmail_Success() {
    final EmployeeRegistrationNotification employeeRegistrationNotification = EmployeeRegistrationNotification.builder()
        .emailAddress(EMAIL).build();
    azureEmailService.sendEmployeeRegistrationNotificationEmail(employeeRegistrationNotification);
    verify(genericEmailService).sendEmployeeRegistrationNotificationEmail(
        employeeRegistrationNotification);
  }

  @Test
  void sendBBChangeEmployeeAccessLevel_Success() {
    EmployeeAccessLevelChange employeeAccessLevel = EmployeeAccessLevelChange.builder().build();
    azureEmailService.sendBBChangeEmployeeAccessLevel(employeeAccessLevel);
    verify(genericEmailService).sendBBChangeEmployeeAccessLevel(employeeAccessLevel);
  }

  @Test
  void sendOutOfPolicySetupEmail_Success() {
    OutOfPolicySetup outOfPolicySetup = OutOfPolicySetup.builder().email(EMAIL).build();
    azureEmailService.sendOutOfPolicySetupEmail(outOfPolicySetup);
    verify(genericEmailService).sendOutOfPolicySetupEmails(outOfPolicySetup);
  }

  @Test
  void sendBBRequestToJoinAcceptedEmail_Success(){
    RequestToJoinAccepted requestToJoinAccepted = RequestToJoinAccepted.builder()
        .email(EMAIL)
        .build();
    azureEmailService.sendBBRequestToJoinAcceptedEmail(requestToJoinAccepted);
    verify(genericEmailService).sendBBRequestToJoinAcceptedEmail(requestToJoinAccepted);
  }

  @Test
  void sendRequestToJoinRejectedEMail_Success() {
    RequestToJoinRejected requestToJoinRejected = RequestToJoinRejected.builder().build();
    azureEmailService.sendRequestToJoinRejectedEmail(requestToJoinRejected);
    verify(genericEmailService).sendRequestToJoinRejectedEmail(requestToJoinRejected);
  }

  @Test
  void sendAccountRegistrationEmail_Success() {
    AccountRegistration accountRegistration = AccountRegistration.builder()
        .emailAddress(EMAIL).build();
    azureEmailService.sendAccountRegistrationEmail(accountRegistration);
    verify(genericEmailService).sendAccountRegistrationEmail(accountRegistration);
  }

  @Test
  void sendOtpPasswordResetEmail_Success() {
    OtpPasswordReset piPasswordReset = OtpPasswordReset.builder().emailAddress(EMAIL).build();
    azureEmailService.sendOtpPasswordResetEmail(piPasswordReset);
    verify(genericEmailService).sendOtpPasswordResetEmail(piPasswordReset);
  }

  @Test
  void sendChangePasswordConfirmationEmail_Success() {
    ChangePasswordConfirmation changePasswordConfirmation = ChangePasswordConfirmation.builder()
        .emailAddress(EMAIL).build();
    azureEmailService.sendChangePasswordConfirmationEmail(changePasswordConfirmation);
    verify(genericEmailService).sendChangePasswordConfirmationEmail(changePasswordConfirmation);
  }

  @Test
  void sendAccountBlockedEmail_Success() {
    AccountBlocked accountBlocked = AccountBlocked.builder().emailAddress(EMAIL).build();
    azureEmailService.sendAccountBlockedEmail(accountBlocked);
    verify(genericEmailService).sendAccountBlockedEmail(accountBlocked);
  }
}
