package uk.co.whitbread.shared.azureemail.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static uk.co.whitbread.shared.azureemail.properties.AzureEmailProperties.LANGUAGE_EN;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.shared.azureemail.genericEmail.api.CreateRequest;
import uk.co.whitbread.shared.azureemail.model.CompanyActivation;
import uk.co.whitbread.shared.azureemail.model.AccountBlocked;
import uk.co.whitbread.shared.azureemail.model.AccountRegistration;
import uk.co.whitbread.shared.azureemail.model.ChangePasswordConfirmation;
import uk.co.whitbread.shared.azureemail.model.OtpPasswordReset;
import uk.co.whitbread.shared.azureemail.model.RequestToJoinAccepted;
import uk.co.whitbread.shared.azureemail.model.EmployeeAccessLevelChange;
import uk.co.whitbread.shared.azureemail.model.EmployeeAccountActivation;
import uk.co.whitbread.shared.azureemail.model.EmployeeInvite;
import uk.co.whitbread.shared.azureemail.model.EmployeeRegistrationNotification;
import uk.co.whitbread.shared.azureemail.model.OutOfPolicySetup;
import uk.co.whitbread.shared.azureemail.model.PasswordReset;
import uk.co.whitbread.shared.azureemail.model.RequestToJoinRejected;
import uk.co.whitbread.shared.azureemail.properties.GenericEmailProperties;
import uk.co.whitbread.shared.azureemail.requestbuilder.BBForgottenPasswordResetBuilder;
import uk.co.whitbread.shared.azureemail.security.GenericEmailHeadersCallback;

@ExtendWith(MockitoExtension.class)
class GenericEmailServiceTest {

  private static final String HOST = "https://azure-host.com";
  private static final String EMAIL = "email@test.com";
  private static final String RESET_PASSWORD_URL = "https://return-url.com";
  private static final String FIRST_NAME = "First";
  private static final String LAST_NAME = "Last";
  private static final String COMPANY_NAME = "Company";
  private static final String ACCESS_LEVEL = "SELF";
  private static final String ACTIVATION_LINK = "https://premierinn.com?key=tyj84d3uKq";
  private static final String SOAP_ACTION_KEY = "Create";
  private static final String TM_ACCEPT_REQUEST_TO_JOIN_LINK ="https://www.beta.premierinn.digital/gb/en/business-booker/home.html";
  private static final CreateRequest REQUEST = new CreateRequest();

  @Mock
  private WebServiceTemplate webServiceTemplateMock;
  @Mock
  private GenericEmailProperties azureGenericEmailProperties;
  @Mock
  private GenericEmailHeadersCallback genericEmailHeadersCallback;
  @Mock
  private BBForgottenPasswordResetBuilder bbForgottenPasswordResetBuilder;
  @InjectMocks
  private GenericEmailService genericEmailService;

  @BeforeEach
  public void setUp() {
    doReturn(HOST).when(azureGenericEmailProperties).getHost();
    doReturn((WebServiceMessageCallback) webServiceMessage -> {
    }).when(genericEmailHeadersCallback).addHeaders(SOAP_ACTION_KEY);
  }

  @Test
  void sendBBResetPasswordEmail_Success() {
    doReturn(REQUEST).when(bbForgottenPasswordResetBuilder)
        .buildBBForgotPasswordRequest(any(PasswordReset.class));
    genericEmailService.sendBBResetPasswordEmail(
        PasswordReset.builder().email(EMAIL).resetPasswordUrl(RESET_PASSWORD_URL).build());
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST), eq(
        REQUEST), any(WebServiceMessageCallback.class));
  }

  @Test
  void sendBBResetPasswordEmail_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), eq(REQUEST),
            any(WebServiceMessageCallback.class));
    PasswordReset passwordReset = PasswordReset.builder().email(EMAIL)
        .resetPasswordUrl(RESET_PASSWORD_URL).build();
    assertThrows(RuntimeException.class,
        () -> genericEmailService.sendBBResetPasswordEmail(passwordReset));
  }

  @Test
  void sendBBCompanyActivationEmail_Success() {
    CompanyActivation companyActivation = CompanyActivation.builder()
        .email(EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .companyName(COMPANY_NAME)
        .activationLink(ACTIVATION_LINK)
        .language(LANGUAGE_EN)
        .build();

    genericEmailService.sendBBCompanyActivationEmail(companyActivation);
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
        any(WebServiceMessageCallback.class));
  }

  @Test
  void sendBBCompanyActivationEmail_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
            any(WebServiceMessageCallback.class));

    CompanyActivation companyActivation = CompanyActivation.builder().build();
    assertThrows(RuntimeException.class,
        () -> genericEmailService.sendBBCompanyActivationEmail(companyActivation));
  }

  @Test
  void sendEmployeeAccountActivationEmail_Success() {
    genericEmailService.sendEmployeeAccountActivationEmail(
        EmployeeAccountActivation.builder().employeeEmail(EMAIL).activationLink(ACTIVATION_LINK)
            .build());
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
        any(WebServiceMessageCallback.class));
  }

  @Test
  void sendEmployeeAccountActivationEmail_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), eq(REQUEST),
            any(WebServiceMessageCallback.class));
    final EmployeeAccountActivation accountActivation = EmployeeAccountActivation.builder()
        .employeeEmail(EMAIL).activationLink(ACTIVATION_LINK).build();
    assertThrows(RuntimeException.class,
        () -> genericEmailService.sendEmployeeAccountActivationEmail(accountActivation));
  }

  @Test
  void sendBBInviteEmployeeEmail_Success() {
    EmployeeInvite employeeInvite = EmployeeInvite.builder()
        .email(EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .accessLevel(ACCESS_LEVEL)
        .activationLink(ACTIVATION_LINK)
        .language(LANGUAGE_EN)
        .build();

    genericEmailService.sendBBInviteEmployeeEmail(employeeInvite);
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
        any(WebServiceMessageCallback.class));
  }

  @Test
  void sendBBInviteEmployeeEmail_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
            any(WebServiceMessageCallback.class));

    EmployeeInvite employeeInvite = EmployeeInvite.builder().build();
    assertThrows(RuntimeException.class,
        () -> genericEmailService.sendBBInviteEmployeeEmail(employeeInvite));
  }

  @Test
  void sendEmployeeRegistrationNotificationEmail_Success() {
    genericEmailService.sendEmployeeRegistrationNotificationEmail(
        EmployeeRegistrationNotification.builder().emailAddress(EMAIL).build());
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
        any(WebServiceMessageCallback.class));
  }

  @Test
  void sendEmployeeRegistrationNotificationEmail_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), eq(REQUEST),
            any(WebServiceMessageCallback.class));
    final EmployeeRegistrationNotification employeeRegistrationNotification = EmployeeRegistrationNotification.builder()
        .emailAddress(EMAIL).build();
    assertThrows(RuntimeException.class,
        () -> genericEmailService.sendEmployeeRegistrationNotificationEmail(employeeRegistrationNotification));
  }

  @Test
  void sendBBChangeEmployeeAccessLevel_Success() {
    genericEmailService
        .sendBBChangeEmployeeAccessLevel(EmployeeAccessLevelChange.builder().build());
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
        any(WebServiceMessageCallback.class));
  }

  @Test
  void sendBBChangeEmployeeAccessLevel_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
            any(WebServiceMessageCallback.class));

    assertThrows(RuntimeException.class, () -> genericEmailService
        .sendBBChangeEmployeeAccessLevel(EmployeeAccessLevelChange.builder().build()));
  }

  @Test
  void sendOutOfPolicySetupEmail_Success() {
    OutOfPolicySetup outOfPolicySetup = OutOfPolicySetup.builder()
        .email(EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .build();

    genericEmailService.sendOutOfPolicySetupEmails(outOfPolicySetup);
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
        any(WebServiceMessageCallback.class));
  }

  @Test
  void sendOutOfPolicySetupEmail_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
            any(WebServiceMessageCallback.class));

    OutOfPolicySetup outOfPolicySetup = OutOfPolicySetup.builder().email(EMAIL).build();
    assertThrows(RuntimeException.class,
        () -> genericEmailService.sendOutOfPolicySetupEmails(outOfPolicySetup));
  }

  @Test
  void sendEmployeeRequestToJoinAccepted_Success() {
    var employeeAcceptToJoinNotification = RequestToJoinAccepted.builder()
        .email(EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .adminLastName(FIRST_NAME)
        .bbActivatedVerificationUrl(TM_ACCEPT_REQUEST_TO_JOIN_LINK)
        .adminLastName(LAST_NAME)
        .build();

    genericEmailService.sendBBRequestToJoinAcceptedEmail(employeeAcceptToJoinNotification);
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
        any(WebServiceMessageCallback.class));
  }

  @Test
  void sendEmployeeRequestToJoinAccepted_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
            any(WebServiceMessageCallback.class));

    var employeeAcceptToJoinNotification = RequestToJoinAccepted.builder().email(EMAIL).build();
    assertThrows(RuntimeException.class,
        () -> genericEmailService.sendBBRequestToJoinAcceptedEmail(employeeAcceptToJoinNotification));
  }

  @Test
  void sendRequestToJoinRejectedEmail_Success() {
    genericEmailService
        .sendRequestToJoinRejectedEmail(RequestToJoinRejected.builder().build());
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
        any(WebServiceMessageCallback.class));
  }

  @Test
  void sendRequestToJoinRejectedEmail_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
            any(WebServiceMessageCallback.class));

    assertThrows(RuntimeException.class, () -> genericEmailService
        .sendRequestToJoinRejectedEmail(RequestToJoinRejected.builder().build()));
  }

  @Test
  void sendAccountRegistrationEmail_Success() {
    AccountRegistration accountRegistration = AccountRegistration.builder()
        .emailAddress(EMAIL)
        .customerName(FIRST_NAME)
        .loginUrl(RESET_PASSWORD_URL)
        .language(LANGUAGE_EN)
        .build();

    genericEmailService.sendAccountRegistrationEmail(accountRegistration);
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
        any(WebServiceMessageCallback.class));
  }

  @Test
  void sendAccountRegistrationEmail_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
            any(WebServiceMessageCallback.class));

    AccountRegistration accountRegistration = AccountRegistration.builder()
        .emailAddress(EMAIL).build();
    assertThrows(RuntimeException.class,
        () -> genericEmailService.sendAccountRegistrationEmail(accountRegistration));
  }

  @Test
  void sendOtpPasswordResetEmail_Success() {
    OtpPasswordReset piPasswordReset = OtpPasswordReset.builder()
        .emailAddress(EMAIL)
        .customerName(FIRST_NAME)
        .otp("12345678")
        .language(LANGUAGE_EN)
        .build();

    genericEmailService.sendOtpPasswordResetEmail(piPasswordReset);
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
        any(WebServiceMessageCallback.class));
  }

  @Test
  void sendOtpPasswordResetEmail_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
            any(WebServiceMessageCallback.class));

    OtpPasswordReset piPasswordReset = OtpPasswordReset.builder().emailAddress(EMAIL).build();
    assertThrows(RuntimeException.class,
        () -> genericEmailService.sendOtpPasswordResetEmail(piPasswordReset));
  }

  @Test
  void sendChangePasswordConfirmationEmail_Success() {
    ChangePasswordConfirmation changePasswordConfirmation = ChangePasswordConfirmation.builder()
        .emailAddress(EMAIL)
        .customerName(FIRST_NAME)
        .loginUrl(RESET_PASSWORD_URL)
        .language(LANGUAGE_EN)
        .build();

    genericEmailService.sendChangePasswordConfirmationEmail(changePasswordConfirmation);
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
        any(WebServiceMessageCallback.class));
  }

  @Test
  void sendChangePasswordConfirmationEmail_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
            any(WebServiceMessageCallback.class));

    ChangePasswordConfirmation changePasswordConfirmation = ChangePasswordConfirmation.builder()
        .emailAddress(EMAIL).build();
    assertThrows(RuntimeException.class,
        () -> genericEmailService.sendChangePasswordConfirmationEmail(changePasswordConfirmation));
  }

  @Test
  void sendAccountBlockedEmail_Success() {
    AccountBlocked accountBlocked = AccountBlocked.builder()
        .emailAddress(EMAIL)
        .customerName(FIRST_NAME)
        .passwordResetUrl(RESET_PASSWORD_URL)
        .language(LANGUAGE_EN)
        .build();

    genericEmailService.sendAccountBlockedEmail(accountBlocked);
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
        any(WebServiceMessageCallback.class));
  }

  @Test
  void sendAccountBlockedEmail_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), any(CreateRequest.class),
            any(WebServiceMessageCallback.class));

    AccountBlocked accountBlocked = AccountBlocked.builder().emailAddress(EMAIL).build();
    assertThrows(RuntimeException.class,
        () -> genericEmailService.sendAccountBlockedEmail(accountBlocked));
  }

}
