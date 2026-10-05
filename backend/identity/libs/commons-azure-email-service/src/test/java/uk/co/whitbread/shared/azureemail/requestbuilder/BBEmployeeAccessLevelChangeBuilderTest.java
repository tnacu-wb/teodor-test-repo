package uk.co.whitbread.shared.azureemail.requestbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.BB_EMPLOYEE_ACCESS_LEVEL_CHANGE;
import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.BB_EMPLOYEE_ACCESS_LEVEL_CHANGE_DE;
import static uk.co.whitbread.shared.azureemail.properties.AzureEmailProperties.LANGUAGE_DE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBEmployeeAccessLevelChangeBuilder.AUTHORITY_LEVEL_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBEmployeeAccessLevelChangeBuilder.FIRST_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBEmployeeAccessLevelChangeBuilder.LOGIN_URL_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBEmployeeAccessLevelChangeBuilder.buildBBChangeEmployeeAccessLevelRequest;

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
import uk.co.whitbread.shared.azureemail.model.EmployeeAccessLevelChange;

@ExtendWith(MockitoExtension.class)
class BBEmployeeAccessLevelChangeBuilderTest {

  private static final String EMAIL = "email@test.com";
  private static final String FIRST_NAME = "First";
  private static final String AUTHORITY_LEVEL = "SELF";
  private static final String LOGIN_URL = "https://premierinn.com?activate=employee&key=tyj84d3uKq";

  @Test
  void buildSendBBChangeEmployeeAccessLevelRequest_en() {
    testBuildBBChangeEmployeeAccessLevelRequest("whatever");
  }

  @Test
  void buildSendBBChangeEmployeeAccessLevelRequest_de() {
    testBuildBBChangeEmployeeAccessLevelRequest(LANGUAGE_DE);
  }

  private void testBuildBBChangeEmployeeAccessLevelRequest(String language) {
    EmployeeAccessLevelChange employeeAccessLevel = EmployeeAccessLevelChange.builder()
        .email(EMAIL)
        .firstName(FIRST_NAME)
        .accessLevel(AUTHORITY_LEVEL)
        .loginUrl(LOGIN_URL)
        .language(language)
        .build();
    CreateRequest request = buildBBChangeEmployeeAccessLevelRequest(employeeAccessLevel);

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
    assertEquals(3, attributes.size());

    Attribute firstNameAttribute = attributes.get(0);
    assertEquals(FIRST_NAME_ATTRIBUTE, firstNameAttribute.getName());
    assertEquals(FIRST_NAME, firstNameAttribute.getValue());

    Attribute authorityLevelAttribute = attributes.get(1);
    assertEquals(AUTHORITY_LEVEL_ATTRIBUTE, authorityLevelAttribute.getName());
    assertEquals(AUTHORITY_LEVEL, authorityLevelAttribute.getValue());

    Attribute activationLinkAttribute = attributes.get(2);
    assertEquals(LOGIN_URL_ATTRIBUTE, activationLinkAttribute.getName());
    assertEquals(LOGIN_URL, activationLinkAttribute.getValue());
  }

  private String getExpectedTemplateId(String language) {
    return LANGUAGE_DE.equalsIgnoreCase(language)
        ? BB_EMPLOYEE_ACCESS_LEVEL_CHANGE_DE.getTemplateId()
        : BB_EMPLOYEE_ACCESS_LEVEL_CHANGE.getTemplateId();
  }
}