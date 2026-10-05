package uk.co.whitbread.shared.azureemail.requestbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.REQUEST_TO_JOIN_REJECTED;
import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.REQUEST_TO_JOIN_REJECTED_DE;
import static uk.co.whitbread.shared.azureemail.properties.AzureEmailProperties.LANGUAGE_DE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBEmployeeAccessLevelChangeBuilder.FIRST_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.RequestToJoinRejectedBuilder.ADMIN_FIRST_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.RequestToJoinRejectedBuilder.ADMIN_LAST_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.RequestToJoinRejectedBuilder.COMPANY_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.RequestToJoinRejectedBuilder.EMAIL_ADDRESS_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.RequestToJoinRejectedBuilder.LAST_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.RequestToJoinRejectedBuilder.buildRequestToJoinRejectedRequest;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.azureemail.genericEmail.api.APIObject;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Attribute;
import uk.co.whitbread.shared.azureemail.genericEmail.api.CreateRequest;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Subscriber;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSend;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSendDefinition;
import uk.co.whitbread.shared.azureemail.model.RequestToJoinRejected;

@ExtendWith(MockitoExtension.class)
class RequestToJoinRejectedBuilderTest {

  private static final String EMAIL = "email@test.com";
  private static final String FIRST_NAME = "First";
  private static final String LAST_NAME = "Last";
  private static final String COMPANY_NAME = "Company";
  private static final String ADMIN_FIRST_NAME = "adminFirst";
  private static final String ADMIN_LAST_NAME = "adminLast";

  @Test
  void buildSendRequestToJoinRejected_en() {
    testBuildRequestToJoinRejected("whatever");
  }

  @Test
  void buildSendRequestToJoinRejected_de() {
    testBuildRequestToJoinRejected(LANGUAGE_DE);
  }

  private void testBuildRequestToJoinRejected(String language) {
    RequestToJoinRejected requestToJoinRejected = RequestToJoinRejected.builder()
        .emailAddress(EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .companyName(COMPANY_NAME)
        .language(language)
        .adminFirstName(ADMIN_FIRST_NAME)
        .adminLastName(ADMIN_LAST_NAME)
        .build();
    CreateRequest request = buildRequestToJoinRejectedRequest(requestToJoinRejected);

    List<APIObject> apiObjects = request.getObjects();
    assertEquals(1, apiObjects.size());

    TriggeredSend triggeredSend = (TriggeredSend) apiObjects.get(0);
    assertNotNull(triggeredSend.getTriggeredSendDefinition());

    TriggeredSendDefinition triggeredSendDefinition = triggeredSend.getTriggeredSendDefinition();
    assertEquals(getExpectedTemplateId(language), triggeredSendDefinition.getCustomerKey());

    List<Subscriber> subscribers = triggeredSend.getSubscribers();
    assertEquals(1, subscribers.size());

    Subscriber subscriber = subscribers.get(0);
    assertSubscribers(subscriber);
  }

  private void assertSubscribers(Subscriber subscriber) {
    assertNotNull(subscriber.getOwner());
    assertEquals(EMAIL, subscriber.getEmailAddress());
    assertEquals(EMAIL, subscriber.getSubscriberKey());
    assertAttributes(subscriber);
  }

  private void assertAttributes(Subscriber subscriber) {
    List<Attribute> attributes = subscriber.getAttributes();
    assertEquals(6, attributes.size());

    Attribute firstNameAttribute = attributes.get(0);
    assertEquals(FIRST_NAME_ATTRIBUTE, firstNameAttribute.getName());
    assertEquals(FIRST_NAME, firstNameAttribute.getValue());

    Attribute lastNameAttribute = attributes.get(1);
    assertEquals(LAST_NAME_ATTRIBUTE, lastNameAttribute.getName());
    assertEquals(LAST_NAME, lastNameAttribute.getValue());

    Attribute companyNameAttribute = attributes.get(2);
    assertEquals(COMPANY_NAME_ATTRIBUTE, companyNameAttribute.getName());
    assertEquals(COMPANY_NAME, companyNameAttribute.getValue());

    Attribute adminFirstNameAttribute = attributes.get(3);
    assertEquals(ADMIN_FIRST_NAME_ATTRIBUTE, adminFirstNameAttribute.getName());
    assertEquals(ADMIN_FIRST_NAME, adminFirstNameAttribute.getValue());

    Attribute adminLastNameAttribute = attributes.get(4);
    assertEquals(ADMIN_LAST_NAME_ATTRIBUTE, adminLastNameAttribute.getName());
    assertEquals(ADMIN_LAST_NAME, adminLastNameAttribute.getValue());

    Attribute emailAttribute = attributes.get(5);
    assertEquals(EMAIL_ADDRESS_ATTRIBUTE, emailAttribute.getName());
    assertEquals(EMAIL, emailAttribute.getValue());
  }

  private String getExpectedTemplateId(String language) {
    return LANGUAGE_DE.equalsIgnoreCase(language)
        ? REQUEST_TO_JOIN_REJECTED_DE.getTemplateId()
        : REQUEST_TO_JOIN_REJECTED.getTemplateId();
  }
}