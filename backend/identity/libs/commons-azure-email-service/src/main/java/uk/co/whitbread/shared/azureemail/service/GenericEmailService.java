package uk.co.whitbread.shared.azureemail.service;

import static uk.co.whitbread.shared.azureemail.requestbuilder.AccountBlockedBuilder.buildAccountBlockedRequest;
import static uk.co.whitbread.shared.azureemail.requestbuilder.AccountRegistrationBuilder.buildAccountRegistrationRequest;
import static uk.co.whitbread.shared.azureemail.requestbuilder.ActivateYourAccountBuilder.buildActivateYourAccountRequest;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBCompanyActivationBuilder.buildBBCompanyActivationRequest;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBEmployeeAccessLevelChangeBuilder.buildBBChangeEmployeeAccessLevelRequest;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBInviteEmployeeBuilder.buildBBInviteEmployeeRequest;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBRequestToJoinAcceptedBuilder.buildBBEmployeeAcceptToJoinNotification;
import static uk.co.whitbread.shared.azureemail.requestbuilder.ChangePasswordConfirmationBuilder.buildChangePasswordConfirmationRequest;
import static uk.co.whitbread.shared.azureemail.requestbuilder.EmployeeRegistrationNotificationRequestBuilder.buildEmployeeRegistrationNotificationRequest;
import static uk.co.whitbread.shared.azureemail.requestbuilder.OutOfPolicySetupBuilder.buildOutOfPolicySetupRequest;
import static uk.co.whitbread.shared.azureemail.requestbuilder.OtpPasswordResetBuilder.buildOtpPasswordResetRequest;
import static uk.co.whitbread.shared.azureemail.requestbuilder.RequestToJoinRejectedBuilder.buildRequestToJoinRejectedRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.shared.azureemail.exception.AzureEmailServiceException;
import uk.co.whitbread.shared.azureemail.genericEmail.api.CreateRequest;
import uk.co.whitbread.shared.azureemail.model.AccountBlocked;
import uk.co.whitbread.shared.azureemail.model.AccountRegistration;
import uk.co.whitbread.shared.azureemail.model.ChangePasswordConfirmation;
import uk.co.whitbread.shared.azureemail.model.CompanyActivation;
import uk.co.whitbread.shared.azureemail.model.EmployeeAccessLevelChange;
import uk.co.whitbread.shared.azureemail.model.EmployeeAccountActivation;
import uk.co.whitbread.shared.azureemail.model.EmployeeInvite;
import uk.co.whitbread.shared.azureemail.model.EmployeeRegistrationNotification;
import uk.co.whitbread.shared.azureemail.model.OutOfPolicySetup;
import uk.co.whitbread.shared.azureemail.model.OtpPasswordReset;
import uk.co.whitbread.shared.azureemail.model.PasswordReset;
import uk.co.whitbread.shared.azureemail.model.RequestToJoinAccepted;
import uk.co.whitbread.shared.azureemail.model.RequestToJoinRejected;
import uk.co.whitbread.shared.azureemail.properties.GenericEmailProperties;
import uk.co.whitbread.shared.azureemail.requestbuilder.BBForgottenPasswordResetBuilder;
import uk.co.whitbread.shared.azureemail.security.GenericEmailHeadersCallback;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenericEmailService {

  private static final String SOAP_ACTION_VALUE = "Create";
  private final WebServiceTemplate webServiceTemplate;
  private final GenericEmailProperties genericEmailProperties;
  private final GenericEmailHeadersCallback genericEmailHeadersCallback;
  private final BBForgottenPasswordResetBuilder bbForgottenPasswordResetBuilder;

  public void sendBBResetPasswordEmail(PasswordReset passwordReset) {
    CreateRequest request = bbForgottenPasswordResetBuilder.buildBBForgotPasswordRequest(
        passwordReset);
    marshalSendAndReceive(request, SOAP_ACTION_VALUE);
  }

  public void sendBBCompanyActivationEmail(CompanyActivation companyActivation) {
    CreateRequest request = buildBBCompanyActivationRequest(companyActivation);
    marshalSendAndReceive(request, SOAP_ACTION_VALUE);
  }

  public void sendEmployeeAccountActivationEmail(EmployeeAccountActivation employeeActivation) {
    final CreateRequest request = buildActivateYourAccountRequest(employeeActivation);
    marshalSendAndReceive(request, SOAP_ACTION_VALUE);
  }

  public void sendBBInviteEmployeeEmail(EmployeeInvite employeeInvite) {
    CreateRequest request = buildBBInviteEmployeeRequest(employeeInvite);
    marshalSendAndReceive(request, SOAP_ACTION_VALUE);
  }

  public void sendEmployeeRegistrationNotificationEmail(
      EmployeeRegistrationNotification employeeRegistrationNotification) {

    final CreateRequest request = buildEmployeeRegistrationNotificationRequest(
        employeeRegistrationNotification);
    marshalSendAndReceive(request, SOAP_ACTION_VALUE);
  }

  public void sendBBChangeEmployeeAccessLevel(EmployeeAccessLevelChange employeeAccessLevel) {
    CreateRequest request = buildBBChangeEmployeeAccessLevelRequest(employeeAccessLevel);
    marshalSendAndReceive(request, SOAP_ACTION_VALUE);
  }

  public void sendOutOfPolicySetupEmails(OutOfPolicySetup outOfPolicySetup) {
    CreateRequest request = buildOutOfPolicySetupRequest(outOfPolicySetup);
    marshalSendAndReceive(request, SOAP_ACTION_VALUE);
  }

  public void sendBBRequestToJoinAcceptedEmail(RequestToJoinAccepted requestToJoinAccepted){
    CreateRequest request = buildBBEmployeeAcceptToJoinNotification(requestToJoinAccepted);
    marshalSendAndReceive(request, SOAP_ACTION_VALUE);
  }

  public void sendRequestToJoinRejectedEmail(RequestToJoinRejected requestToJoinRejected) {
    CreateRequest request = buildRequestToJoinRejectedRequest(requestToJoinRejected);
    marshalSendAndReceive(request, SOAP_ACTION_VALUE);
  }

  public void sendAccountRegistrationEmail(AccountRegistration accountRegistration) {
    CreateRequest request = buildAccountRegistrationRequest(accountRegistration);
    marshalSendAndReceive(request, SOAP_ACTION_VALUE);
  }

  public void sendOtpPasswordResetEmail(OtpPasswordReset piPasswordReset) {
    CreateRequest request = buildOtpPasswordResetRequest(piPasswordReset);
    marshalSendAndReceive(request, SOAP_ACTION_VALUE);
  }

  public void sendChangePasswordConfirmationEmail(
      ChangePasswordConfirmation changePasswordConfirmation) {
    CreateRequest request = buildChangePasswordConfirmationRequest(changePasswordConfirmation);
    marshalSendAndReceive(request, SOAP_ACTION_VALUE);
  }

  public void sendAccountBlockedEmail(AccountBlocked accountBlocked) {
    CreateRequest request = buildAccountBlockedRequest(accountBlocked);
    marshalSendAndReceive(request, SOAP_ACTION_VALUE);
  }

  private void marshalSendAndReceive(Object request, String soapAction) {
    final String host = genericEmailProperties.getHost();
    try {
      webServiceTemplate.marshalSendAndReceive(host, request,
          genericEmailHeadersCallback.addHeaders(soapAction));
    } catch (RuntimeException e) {
      log.error("Error while making email request {} to {}", request, host);
      throw new AzureEmailServiceException(e);
    }
  }
}
