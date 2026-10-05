package uk.co.whitbread.shared.azureemail.requestbuilder;

import static uk.co.whitbread.shared.azureemail.properties.AzureEmailProperties.LANGUAGE_DE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.utils.RequestUtils.buildOwner;

import java.util.List;
import lombok.experimental.UtilityClass;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Attribute;
import uk.co.whitbread.shared.azureemail.genericEmail.api.CreateRequest;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Subscriber;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSend;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSendDefinition;
import uk.co.whitbread.shared.azureemail.model.EmailTemplate;
import uk.co.whitbread.shared.azureemail.model.OtpPasswordReset;

@UtilityClass
public class OtpPasswordResetBuilder {

  public static CreateRequest buildOtpPasswordResetRequest(OtpPasswordReset piPasswordReset) {

    final CreateRequest createRequest = new CreateRequest();
    createRequest.getObjects().add(buildTriggeredSend(piPasswordReset));
    return createRequest;
  }

  private static TriggeredSend buildTriggeredSend(OtpPasswordReset piPasswordReset) {
    final TriggeredSend triggeredSend = new TriggeredSend();
    triggeredSend.setTriggeredSendDefinition(
        buildTriggeredSendDefinition(piPasswordReset.getLanguage()));
    triggeredSend.getSubscribers().add(buildSubscriber(piPasswordReset));
    return triggeredSend;
  }

  private static TriggeredSendDefinition buildTriggeredSendDefinition(String language) {
    final TriggeredSendDefinition triggeredSendDefinition = new TriggeredSendDefinition();
    triggeredSendDefinition.setCustomerKey(getTemplateId(language));
    return triggeredSendDefinition;
  }

  private static Subscriber buildSubscriber(OtpPasswordReset piPasswordReset) {
    final Subscriber subscriber = new Subscriber();
    subscriber.setOwner(buildOwner());
    final String email = piPasswordReset.getEmailAddress();
    subscriber.setEmailAddress(email);
    subscriber.getAttributes().addAll(buildAttributes(piPasswordReset));
    subscriber.setSubscriberKey(email);
    return subscriber;
  }

  private static List<Attribute> buildAttributes(OtpPasswordReset piPasswordReset) {
    final Attribute customerNameAttribute = new Attribute();
    customerNameAttribute.setName("customerName");
    customerNameAttribute.setValue(piPasswordReset.getCustomerName());

    final Attribute otpAttribute = new Attribute();
    otpAttribute.setName("otp");
    otpAttribute.setValue(piPasswordReset.getOtp());

    return List.of(customerNameAttribute, otpAttribute);
  }

  private static String getTemplateId(String language) {
    if (LANGUAGE_DE.equalsIgnoreCase(language)) {
      return EmailTemplate.PI_OTP_PASSWORD_RESET_DE.getTemplateId();
    }
    return EmailTemplate.PI_OTP_PASSWORD_RESET.getTemplateId();
  }
}
