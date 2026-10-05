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
import uk.co.whitbread.shared.azureemail.model.ChangePasswordConfirmation;
import uk.co.whitbread.shared.azureemail.model.EmailTemplate;

@UtilityClass
public class ChangePasswordConfirmationBuilder {

  public static CreateRequest buildChangePasswordConfirmationRequest(
      ChangePasswordConfirmation changePasswordConfirmation) {

    final CreateRequest createRequest = new CreateRequest();
    createRequest.getObjects().add(buildTriggeredSend(changePasswordConfirmation));
    return createRequest;
  }

  private static TriggeredSend buildTriggeredSend(
      ChangePasswordConfirmation changePasswordConfirmation) {
    final TriggeredSend triggeredSend = new TriggeredSend();
    triggeredSend.setTriggeredSendDefinition(
        buildTriggeredSendDefinition(changePasswordConfirmation.getLanguage()));
    triggeredSend.getSubscribers().add(buildSubscriber(changePasswordConfirmation));
    return triggeredSend;
  }

  private static TriggeredSendDefinition buildTriggeredSendDefinition(String language) {
    final TriggeredSendDefinition triggeredSendDefinition = new TriggeredSendDefinition();
    triggeredSendDefinition.setCustomerKey(getTemplateId(language));
    return triggeredSendDefinition;
  }

  private static Subscriber buildSubscriber(
      ChangePasswordConfirmation changePasswordConfirmation) {
    final Subscriber subscriber = new Subscriber();
    subscriber.setOwner(buildOwner());
    final String email = changePasswordConfirmation.getEmailAddress();
    subscriber.setEmailAddress(email);
    subscriber.getAttributes().addAll(buildAttributes(changePasswordConfirmation));
    subscriber.setSubscriberKey(email);
    return subscriber;
  }

  private static List<Attribute> buildAttributes(
      ChangePasswordConfirmation changePasswordConfirmation) {
    final Attribute customerNameAttribute = new Attribute();
    customerNameAttribute.setName("customerName");
    customerNameAttribute.setValue(changePasswordConfirmation.getCustomerName());

    final Attribute loginUrlAttribute = new Attribute();
    loginUrlAttribute.setName("loginUrl");
    loginUrlAttribute.setValue(changePasswordConfirmation.getLoginUrl());

    return List.of(customerNameAttribute, loginUrlAttribute);
  }

  private static String getTemplateId(String language) {
    if (LANGUAGE_DE.equalsIgnoreCase(language)) {
      return EmailTemplate.PI_CHANGE_PASSWORD_CONFIRMATION_DE.getTemplateId();
    }
    return EmailTemplate.PI_CHANGE_PASSWORD_CONFIRMATION.getTemplateId();
  }
}
