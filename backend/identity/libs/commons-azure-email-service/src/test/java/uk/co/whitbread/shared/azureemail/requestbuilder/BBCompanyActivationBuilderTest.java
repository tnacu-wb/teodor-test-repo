package uk.co.whitbread.shared.azureemail.requestbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.co.whitbread.shared.azureemail.properties.AzureEmailProperties.LANGUAGE_DE;
import static uk.co.whitbread.shared.azureemail.properties.AzureEmailProperties.LANGUAGE_EN;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBCompanyActivationBuilder.buildBBCompanyActivationRequest;

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
import uk.co.whitbread.shared.azureemail.model.CompanyActivation;
import uk.co.whitbread.shared.azureemail.model.EmailTemplate;

@ExtendWith(MockitoExtension.class)
class BBCompanyActivationBuilderTest {

  private static final String EMAIL = "email@test.com";
  private static final String FIRST_NAME = "First";
  private static final String LAST_NAME = "Last";
  private static final String COMPANY_NAME = "Company";
  private static final String ACTIVATION_LINK = "https://premierinn.com?key=tyj84d3uKq";

  @Test
  void buildSendBBCompanyActivationRequest_en() {
    CompanyActivation companyActivation = CompanyActivation.builder()
        .email(EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .companyName(COMPANY_NAME)
        .activationLink(ACTIVATION_LINK)
        .language(LANGUAGE_EN)
        .build();
    CreateRequest request = buildBBCompanyActivationRequest(companyActivation);

    List<APIObject> apiObjects = request.getObjects();
    assertEquals(1, apiObjects.size());

    TriggeredSend triggeredSend = (TriggeredSend) apiObjects.get(0);
    assertNotNull(triggeredSend.getTriggeredSendDefinition());

    TriggeredSendDefinition triggeredSendDefinition = triggeredSend.getTriggeredSendDefinition();
    assertEquals(EmailTemplate.BB_COMPANY_ACTIVATION.getTemplateId(),
        triggeredSendDefinition.getCustomerKey());

    List<Subscriber> subscribers = triggeredSend.getSubscribers();
    assertEquals(1, subscribers.size());

    Subscriber subscriber = subscribers.get(0);
    assertSubscribers(subscriber);
  }

  @Test
  void buildSendBBCompanyActivationRequest_de() {
    CompanyActivation companyActivation = CompanyActivation.builder()
        .email(EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .companyName(COMPANY_NAME)
        .activationLink(ACTIVATION_LINK)
        .language(LANGUAGE_DE)
        .build();
    CreateRequest request = buildBBCompanyActivationRequest(companyActivation);

    List<APIObject> apiObjects = request.getObjects();
    assertEquals(1, apiObjects.size());

    TriggeredSend triggeredSend = (TriggeredSend) apiObjects.get(0);
    assertNotNull(triggeredSend.getTriggeredSendDefinition());

    TriggeredSendDefinition triggeredSendDefinition = triggeredSend.getTriggeredSendDefinition();
    assertEquals(EmailTemplate.BB_COMPANY_ACTIVATION_DE.getTemplateId(),
        triggeredSendDefinition.getCustomerKey());

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
    assertEquals(4, attributes.size());

    Attribute firstNameAttribute = attributes.get(0);
    assertEquals("FirstName", firstNameAttribute.getName());
    assertEquals(FIRST_NAME, firstNameAttribute.getValue());

    Attribute lastNameAttribute = attributes.get(1);
    assertEquals("LastName", lastNameAttribute.getName());
    assertEquals(LAST_NAME, lastNameAttribute.getValue());

    Attribute companyNameAttribute = attributes.get(2);
    assertEquals("CompanyName", companyNameAttribute.getName());
    assertEquals(COMPANY_NAME, companyNameAttribute.getValue());

    Attribute activationLinkAttribute = attributes.get(3);
    assertEquals("CompanyLink", activationLinkAttribute.getName());
    assertEquals(ACTIVATION_LINK, activationLinkAttribute.getValue());
  }
}