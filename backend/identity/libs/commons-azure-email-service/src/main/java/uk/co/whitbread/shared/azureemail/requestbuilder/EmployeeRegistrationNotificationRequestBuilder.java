package uk.co.whitbread.shared.azureemail.requestbuilder;

import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.BB_EMPLOYEE_REGISTRATION_NOTIFICATION;
import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.BB_EMPLOYEE_REGISTRATION_NOTIFICATION_DE;

import java.util.List;
import lombok.experimental.UtilityClass;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Attribute;
import uk.co.whitbread.shared.azureemail.genericEmail.api.CreateRequest;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Subscriber;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSend;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSendDefinition;
import uk.co.whitbread.shared.azureemail.model.EmployeeRegistrationNotification;
import uk.co.whitbread.shared.azureemail.properties.AzureEmailProperties;
import uk.co.whitbread.shared.azureemail.requestbuilder.utils.RequestUtils;

@UtilityClass
public class EmployeeRegistrationNotificationRequestBuilder {

  static final String FIRST_NAME_ATTRIBUTE = "FirstName";
  static final String LAST_NAME_ATTRIBUTE = "LastName";
  static final String REQUESTER_FIRST_NAME_ATTRIBUTE = "RequesterFirstName";
  static final String REQUESTER_LAST_NAME_ATTRIBUTE = "RequesterLastName";
  static final String COMPANY_NAME_ATTRIBUTE = "CompanyName";
  static final String EMAIL_ADDRESS_ATTRIBUTE = "EmailAddress";

  public static CreateRequest buildEmployeeRegistrationNotificationRequest(
      EmployeeRegistrationNotification employeeRegistrationNotification) {

    final CreateRequest createRequest = new CreateRequest();
    createRequest.getObjects().add(buildTriggeredSend(employeeRegistrationNotification));
    return createRequest;
  }

  private static TriggeredSend buildTriggeredSend(
      EmployeeRegistrationNotification employeeRegistrationNotification) {

    final TriggeredSend triggeredSend = new TriggeredSend();
    triggeredSend.setTriggeredSendDefinition(
        buildTriggeredSendDefinition(employeeRegistrationNotification.getLanguage()));
    triggeredSend.getSubscribers().add(buildSubscriber(employeeRegistrationNotification));
    return triggeredSend;
  }

  private static TriggeredSendDefinition buildTriggeredSendDefinition(String language) {
    final TriggeredSendDefinition triggeredSendDefinition = new TriggeredSendDefinition();
    triggeredSendDefinition.setCustomerKey(getTemplateId(language));
    return triggeredSendDefinition;
  }

  private static Subscriber buildSubscriber(
      EmployeeRegistrationNotification employeeRegistrationNotification) {

    final Subscriber subscriber = new Subscriber();
    subscriber.setOwner(RequestUtils.buildOwner());
    final String employeeEmail = employeeRegistrationNotification.getEmailAddress();
    subscriber.setEmailAddress(employeeEmail);
    subscriber.getAttributes().addAll(buildAttributes(employeeRegistrationNotification));
    subscriber.setSubscriberKey(employeeEmail);
    return subscriber;
  }

  private static List<Attribute> buildAttributes(
      EmployeeRegistrationNotification employeeRegistrationNotification) {

    final Attribute firstNameAttribute = new Attribute();
    firstNameAttribute.setName(FIRST_NAME_ATTRIBUTE);
    firstNameAttribute.setValue(employeeRegistrationNotification.getFirstName());

    final Attribute lastNameAttribute = new Attribute();
    lastNameAttribute.setName(LAST_NAME_ATTRIBUTE);
    lastNameAttribute.setValue(employeeRegistrationNotification.getLastName());

    final Attribute requesterFirstNameAttribute = new Attribute();
    requesterFirstNameAttribute.setName(REQUESTER_FIRST_NAME_ATTRIBUTE);
    requesterFirstNameAttribute.setValue(employeeRegistrationNotification.getRequesterFirstName());

    final Attribute requesterLastNameAttribute = new Attribute();
    requesterLastNameAttribute.setName(REQUESTER_LAST_NAME_ATTRIBUTE);
    requesterLastNameAttribute.setValue(employeeRegistrationNotification.getRequesterLastName());

    final Attribute companyNameAttribute = new Attribute();
    companyNameAttribute.setName(COMPANY_NAME_ATTRIBUTE);
    companyNameAttribute.setValue(employeeRegistrationNotification.getCompanyName());

    final Attribute emailAddressAttribute = new Attribute();
    emailAddressAttribute.setName(EMAIL_ADDRESS_ATTRIBUTE);
    emailAddressAttribute.setValue(employeeRegistrationNotification.getEmailAddress());

    return List.of(firstNameAttribute, lastNameAttribute, requesterFirstNameAttribute,
        requesterLastNameAttribute, companyNameAttribute, emailAddressAttribute);
  }

  private static String getTemplateId(String language) {
    if (AzureEmailProperties.LANGUAGE_DE.equalsIgnoreCase(language)) {
      return BB_EMPLOYEE_REGISTRATION_NOTIFICATION_DE.getTemplateId();
    }
    return BB_EMPLOYEE_REGISTRATION_NOTIFICATION.getTemplateId();
  }
}
