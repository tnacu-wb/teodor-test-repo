package uk.co.whitbread.shared.azureemail.requestbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.REQUEST_TO_JOIN_ACCEPTED;
import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.REQUEST_TO_JOIN_ACCEPTED_DE;
import static uk.co.whitbread.shared.azureemail.properties.AzureEmailProperties.LANGUAGE_DE;

import static uk.co.whitbread.shared.azureemail.requestbuilder.BBRequestToJoinAcceptedBuilder.ADMIN_FIRST_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBRequestToJoinAcceptedBuilder.ADMIN_LAST_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBRequestToJoinAcceptedBuilder.BUSINESS_BOOKER_ACTIVATED_USER_VERIFICATION_URL_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBRequestToJoinAcceptedBuilder.COMPANY_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBRequestToJoinAcceptedBuilder.EMAIL_ADDRESS_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBRequestToJoinAcceptedBuilder.FIRST_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBRequestToJoinAcceptedBuilder.LAST_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBRequestToJoinAcceptedBuilder.buildBBEmployeeAcceptToJoinNotification;

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
import uk.co.whitbread.shared.azureemail.model.RequestToJoinAccepted;

@ExtendWith(MockitoExtension.class)
public class RequestToJoinAcceptedBuilderTest {

  private static final String EMAIL = "email@test.com";
  private static final String FIRST_NAME = "First";
  private static final String LAST_NAME = "Last";
  private static final String COMPANY_NAME = "Company";
  private static final String ADMIN_FIRST_NAME = "adminFirst";
  private static final String ADMIN_LAST_NAME = "adminLast";
  static final String BUSINESS_BOOKER_ACTIVATED_USER_VERIFICATION_URL = "Business_Booker_Activated_User_Verification_Url";

  @Test
  void buildSendRequestToJoinAccepted_en() {
    testBuildRequestToJoinAccepted("whatever");
  }

  @Test
  void buildSendRequestToJoinAccepted_de() {
    testBuildRequestToJoinAccepted(LANGUAGE_DE);
  }

  private void testBuildRequestToJoinAccepted(String language) {
    RequestToJoinAccepted requestToJoinAccepted = RequestToJoinAccepted.builder()
        .email(EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .companyName(COMPANY_NAME)
        .language(language)
        .adminFirstName(ADMIN_FIRST_NAME)
        .adminLastName(ADMIN_LAST_NAME)
        .bbActivatedVerificationUrl(BUSINESS_BOOKER_ACTIVATED_USER_VERIFICATION_URL)
        .build();
    CreateRequest request = buildBBEmployeeAcceptToJoinNotification(requestToJoinAccepted);

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
    assertEquals(7, attributes.size());

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

    Attribute bbVerificationLinkAttribute = attributes.get(6);
    assertEquals(BUSINESS_BOOKER_ACTIVATED_USER_VERIFICATION_URL_ATTRIBUTE,
        bbVerificationLinkAttribute.getName());
    assertEquals(BUSINESS_BOOKER_ACTIVATED_USER_VERIFICATION_URL,
        bbVerificationLinkAttribute.getValue());
  }

  private String getExpectedTemplateId(String language) {
    return LANGUAGE_DE.equalsIgnoreCase(language)
        ? REQUEST_TO_JOIN_ACCEPTED_DE.getTemplateId()
        : REQUEST_TO_JOIN_ACCEPTED.getTemplateId();
  }
}
