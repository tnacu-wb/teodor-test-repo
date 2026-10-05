package uk.co.whitbread.shared.azureemail.requestbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.co.whitbread.shared.azureemail.requestbuilder.OutOfPolicySetupBuilder.buildOutOfPolicySetupRequest;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.azureemail.genericEmail.api.APIObject;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Attribute;
import uk.co.whitbread.shared.azureemail.genericEmail.api.CreateRequest;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Subscriber;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSend;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSendDefinition;
import uk.co.whitbread.shared.azureemail.model.EmailTemplate;
import uk.co.whitbread.shared.azureemail.model.OutOfPolicySetup;

@ExtendWith(MockitoExtension.class)
class OutOfPolicySetupBuilderTest {

  private static final String EMAIL = "email@test.com";
  private static final String FIRST_NAME = "First";
  private static final String LAST_NAME = "Last";

  @Test
  void buildSendOutOfPolicySetupRequest() {
    OutOfPolicySetup outOfPolicySetup = OutOfPolicySetup.builder()
        .email(EMAIL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .build();
    CreateRequest request = buildOutOfPolicySetupRequest(outOfPolicySetup);

    List<APIObject> apiObjects = request.getObjects();
    assertEquals(1, apiObjects.size());

    TriggeredSend triggeredSend = (TriggeredSend) apiObjects.get(0);
    assertNotNull(triggeredSend.getTriggeredSendDefinition());

    TriggeredSendDefinition triggeredSendDefinition = triggeredSend.getTriggeredSendDefinition();
    assertEquals(EmailTemplate.OUT_OF_POLICY_SETUP.getTemplateId(),
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
    assertEquals(3, attributes.size());

    Attribute firstNameAttribute = attributes.get(0);
    assertEquals("FirstName", firstNameAttribute.getName());
    assertEquals(FIRST_NAME, firstNameAttribute.getValue());

    Attribute lastNameAttribute = attributes.get(1);
    assertEquals("LastName", lastNameAttribute.getName());
    assertEquals(LAST_NAME, lastNameAttribute.getValue());

    Attribute activationLinkAttribute = attributes.get(2);
    assertEquals("ActivateLink", activationLinkAttribute.getName());
    assertEquals(StringUtils.EMPTY, activationLinkAttribute.getValue());
  }
}