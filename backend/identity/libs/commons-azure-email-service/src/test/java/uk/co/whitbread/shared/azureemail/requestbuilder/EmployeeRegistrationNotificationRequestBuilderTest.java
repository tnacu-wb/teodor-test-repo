package uk.co.whitbread.shared.azureemail.requestbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.BB_EMPLOYEE_REGISTRATION_NOTIFICATION;
import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.BB_EMPLOYEE_REGISTRATION_NOTIFICATION_DE;
import static uk.co.whitbread.shared.azureemail.properties.AzureEmailProperties.LANGUAGE_DE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.EmployeeRegistrationNotificationRequestBuilder.COMPANY_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.EmployeeRegistrationNotificationRequestBuilder.EMAIL_ADDRESS_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.EmployeeRegistrationNotificationRequestBuilder.FIRST_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.EmployeeRegistrationNotificationRequestBuilder.LAST_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.EmployeeRegistrationNotificationRequestBuilder.REQUESTER_FIRST_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.EmployeeRegistrationNotificationRequestBuilder.REQUESTER_LAST_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.EmployeeRegistrationNotificationRequestBuilder.buildEmployeeRegistrationNotificationRequest;

import java.util.List;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.shared.azureemail.genericEmail.api.APIObject;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Attribute;
import uk.co.whitbread.shared.azureemail.genericEmail.api.CreateRequest;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Subscriber;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSend;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSendDefinition;
import uk.co.whitbread.shared.azureemail.model.EmployeeRegistrationNotification;

class EmployeeRegistrationNotificationRequestBuilderTest {

  private static final String EMAIL = "email@test.com";
  private static final String FIRST_NAME = "First";
  private static final String LAST_NAME = "Last";
  private static final String REQUESTER_FIRST_NAME = "ReqFirst";
  private static final String REQUESTER_LAST_NAME = "ReqLast";
  private static final String COMPANY_NAME = "Company";

  @Test
  void buildEmployeeRegistrationNotificationRequest_en() {
    testBuildEmployeeRegistrationNotificationRequest("whatever");
  }

  @Test
  void buildEmployeeRegistrationNotificationRequest_de() {
    testBuildEmployeeRegistrationNotificationRequest(LANGUAGE_DE);
  }

  private void testBuildEmployeeRegistrationNotificationRequest(String language) {
    final EmployeeRegistrationNotification employeeRegistrationNotification =
        EmployeeRegistrationNotification.builder()
            .emailAddress(EMAIL)
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .requesterFirstName(REQUESTER_FIRST_NAME)
            .requesterLastName(REQUESTER_LAST_NAME)
            .companyName(COMPANY_NAME)
            .language(language)
            .build();

    final CreateRequest request = buildEmployeeRegistrationNotificationRequest(employeeRegistrationNotification);

    final List<APIObject> apiObjects = request.getObjects();
    assertEquals(1, apiObjects.size());

    final TriggeredSend triggeredSend = (TriggeredSend) apiObjects.get(0);
    assertNotNull(triggeredSend.getTriggeredSendDefinition());

    final TriggeredSendDefinition triggeredSendDefinition = triggeredSend.getTriggeredSendDefinition();
    assertEquals(getExpectedTemplateId(language), triggeredSendDefinition.getCustomerKey());

    final List<Subscriber> subscribers = triggeredSend.getSubscribers();
    assertEquals(1, subscribers.size());

    final Subscriber subscriber = subscribers.get(0);
    assertSubscribers(subscriber);
  }

  private void assertSubscribers(Subscriber subscriber) {
    assertNotNull(subscriber.getOwner());
    assertEquals(EMAIL, subscriber.getEmailAddress());
    assertEquals(EMAIL, subscriber.getSubscriberKey());
    assertAttributes(subscriber);
  }

  private void assertAttributes(Subscriber subscriber) {
    final List<Attribute> attributes = subscriber.getAttributes();
    assertEquals(6, attributes.size());

    Attribute firstNameAttribute = attributes.get(0);
    assertEquals(FIRST_NAME_ATTRIBUTE, firstNameAttribute.getName());
    assertEquals(FIRST_NAME, firstNameAttribute.getValue());

    Attribute lastNameAttribute = attributes.get(1);
    assertEquals(LAST_NAME_ATTRIBUTE, lastNameAttribute.getName());
    assertEquals(LAST_NAME, lastNameAttribute.getValue());

    Attribute requesterFirstNameAttribute = attributes.get(2);
    assertEquals(REQUESTER_FIRST_NAME_ATTRIBUTE, requesterFirstNameAttribute.getName());
    assertEquals(REQUESTER_FIRST_NAME, requesterFirstNameAttribute.getValue());

    Attribute requesterLastNameAttribute = attributes.get(3);
    assertEquals(REQUESTER_LAST_NAME_ATTRIBUTE, requesterLastNameAttribute.getName());
    assertEquals(REQUESTER_LAST_NAME, requesterLastNameAttribute.getValue());

    Attribute companyNameAttribute = attributes.get(4);
    assertEquals(COMPANY_NAME_ATTRIBUTE, companyNameAttribute.getName());
    assertEquals(COMPANY_NAME, companyNameAttribute.getValue());

    Attribute emailAddressAttribute = attributes.get(5);
    assertEquals(EMAIL_ADDRESS_ATTRIBUTE, emailAddressAttribute.getName());
    assertEquals(EMAIL, emailAddressAttribute.getValue());
  }

  private String getExpectedTemplateId(String language) {
    return LANGUAGE_DE.equalsIgnoreCase(language)
        ? BB_EMPLOYEE_REGISTRATION_NOTIFICATION_DE.getTemplateId()
        : BB_EMPLOYEE_REGISTRATION_NOTIFICATION.getTemplateId();
  }
}