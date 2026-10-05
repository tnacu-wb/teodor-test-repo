package uk.co.whitbread.shared.azureemail.requestbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.co.whitbread.shared.azureemail.properties.AzureEmailProperties.LANGUAGE_DE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBForgottenPasswordResetBuilder.FIRST_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBForgottenPasswordResetBuilder.LAST_NAME_ATTRIBUTE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.BBForgottenPasswordResetBuilder.RESET_LINK_ATTRIBUTE;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
import uk.co.whitbread.shared.azureemail.model.PasswordReset;

@ExtendWith(MockitoExtension.class)
class BBForgottenPasswordResetBuilderTest {

  private static final String EMAIL = "email@test.com";
  private static final String RESET_PASSWORD_URL = "https://return-url.com";
  private static final String FIRST_NAME = "First";
  private static final String LAST_NAME = "Last";
  private BBForgottenPasswordResetBuilder bbForgottenPasswordResetBuilder;

  @BeforeEach
  public void setUp() {
    bbForgottenPasswordResetBuilder = new BBForgottenPasswordResetBuilder();
  }

  @Test
  void buildSendForgotPasswordRequest_en() {
    buildSendForgotPasswordRequest("whatever");
  }

  @Test
  void buildSendForgotPasswordRequest_de() {
    buildSendForgotPasswordRequest(LANGUAGE_DE);
  }

  private void buildSendForgotPasswordRequest(String language) {
    PasswordReset passwordReset = PasswordReset.builder()
        .email(EMAIL)
        .resetPasswordUrl(RESET_PASSWORD_URL)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .language(language)
        .build();
    CreateRequest request = bbForgottenPasswordResetBuilder.buildBBForgotPasswordRequest(
        passwordReset);

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

    Attribute lastNameAttribute = attributes.get(1);
    assertEquals(LAST_NAME_ATTRIBUTE, lastNameAttribute.getName());
    assertEquals(LAST_NAME, lastNameAttribute.getValue());

    Attribute resetLinkAttribute = attributes.get(2);
    assertEquals(RESET_LINK_ATTRIBUTE, resetLinkAttribute.getName());
    assertEquals(RESET_PASSWORD_URL, resetLinkAttribute.getValue());
  }

  private String getExpectedTemplateId(String language) {
    return LANGUAGE_DE.equalsIgnoreCase(language)
        ? EmailTemplate.BB_FORGOT_PASSWORD_DE.getTemplateId()
        : EmailTemplate.BB_FORGOT_PASSWORD.getTemplateId();
  }
}