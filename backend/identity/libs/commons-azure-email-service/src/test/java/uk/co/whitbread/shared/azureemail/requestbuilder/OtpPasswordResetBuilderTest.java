package uk.co.whitbread.shared.azureemail.requestbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.co.whitbread.shared.azureemail.properties.AzureEmailProperties.LANGUAGE_DE;
import static uk.co.whitbread.shared.azureemail.properties.AzureEmailProperties.LANGUAGE_EN;
import static uk.co.whitbread.shared.azureemail.requestbuilder.OtpPasswordResetBuilder.buildOtpPasswordResetRequest;

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
import uk.co.whitbread.shared.azureemail.model.EmailTemplate;
import uk.co.whitbread.shared.azureemail.model.OtpPasswordReset;

@ExtendWith(MockitoExtension.class)
class OtpPasswordResetBuilderTest {

  private static final String EMAIL = "email@test.com";
  private static final String CUSTOMER_NAME = "John Doe";
  private static final String OTP = "12345678";

  @Test
  void buildOtpPasswordResetRequest_en() {
    OtpPasswordReset piPasswordReset = OtpPasswordReset.builder()
        .emailAddress(EMAIL)
        .customerName(CUSTOMER_NAME)
        .otp(OTP)
        .language(LANGUAGE_EN)
        .build();
    CreateRequest request = buildOtpPasswordResetRequest(piPasswordReset);

    List<APIObject> apiObjects = request.getObjects();
    assertEquals(1, apiObjects.size());

    TriggeredSend triggeredSend = (TriggeredSend) apiObjects.get(0);
    assertNotNull(triggeredSend.getTriggeredSendDefinition());

    TriggeredSendDefinition triggeredSendDefinition = triggeredSend.getTriggeredSendDefinition();
    assertEquals(EmailTemplate.PI_OTP_PASSWORD_RESET.getTemplateId(),
        triggeredSendDefinition.getCustomerKey());

    List<Subscriber> subscribers = triggeredSend.getSubscribers();
    assertEquals(1, subscribers.size());

    Subscriber subscriber = subscribers.get(0);
    assertSubscribers(subscriber);
  }

  @Test
  void buildOtpPasswordResetRequest_de() {
    OtpPasswordReset piPasswordReset = OtpPasswordReset.builder()
        .emailAddress(EMAIL)
        .customerName(CUSTOMER_NAME)
        .otp(OTP)
        .language(LANGUAGE_DE)
        .build();
    CreateRequest request = buildOtpPasswordResetRequest(piPasswordReset);

    List<APIObject> apiObjects = request.getObjects();
    assertEquals(1, apiObjects.size());

    TriggeredSend triggeredSend = (TriggeredSend) apiObjects.get(0);
    assertNotNull(triggeredSend.getTriggeredSendDefinition());

    TriggeredSendDefinition triggeredSendDefinition = triggeredSend.getTriggeredSendDefinition();
    assertEquals(EmailTemplate.PI_OTP_PASSWORD_RESET_DE.getTemplateId(),
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
    assertEquals(2, attributes.size());

    Attribute customerNameAttribute = attributes.get(0);
    assertEquals("customerName", customerNameAttribute.getName());
    assertEquals(CUSTOMER_NAME, customerNameAttribute.getValue());

    Attribute otpAttribute = attributes.get(1);
    assertEquals("otp", otpAttribute.getName());
    assertEquals(OTP, otpAttribute.getValue());
  }
}
