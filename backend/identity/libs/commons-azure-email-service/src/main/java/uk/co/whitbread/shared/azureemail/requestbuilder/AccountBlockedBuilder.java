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
import uk.co.whitbread.shared.azureemail.model.AccountBlocked;
import uk.co.whitbread.shared.azureemail.model.EmailTemplate;

@UtilityClass
public class AccountBlockedBuilder {

  public static CreateRequest buildAccountBlockedRequest(AccountBlocked accountBlocked) {

    final CreateRequest createRequest = new CreateRequest();
    createRequest.getObjects().add(buildTriggeredSend(accountBlocked));
    return createRequest;
  }

  private static TriggeredSend buildTriggeredSend(AccountBlocked accountBlocked) {
    final TriggeredSend triggeredSend = new TriggeredSend();
    triggeredSend.setTriggeredSendDefinition(
        buildTriggeredSendDefinition(accountBlocked.getLanguage()));
    triggeredSend.getSubscribers().add(buildSubscriber(accountBlocked));
    return triggeredSend;
  }

  private static TriggeredSendDefinition buildTriggeredSendDefinition(String language) {
    final TriggeredSendDefinition triggeredSendDefinition = new TriggeredSendDefinition();
    triggeredSendDefinition.setCustomerKey(getTemplateId(language));
    return triggeredSendDefinition;
  }

  private static Subscriber buildSubscriber(AccountBlocked accountBlocked) {
    final Subscriber subscriber = new Subscriber();
    subscriber.setOwner(buildOwner());
    final String email = accountBlocked.getEmailAddress();
    subscriber.setEmailAddress(email);
    subscriber.getAttributes().addAll(buildAttributes(accountBlocked));
    subscriber.setSubscriberKey(email);
    return subscriber;
  }

  private static List<Attribute> buildAttributes(AccountBlocked accountBlocked) {
    final Attribute customerNameAttribute = new Attribute();
    customerNameAttribute.setName("customerName");
    customerNameAttribute.setValue(accountBlocked.getCustomerName());

    final Attribute passwordResetUrlAttribute = new Attribute();
    passwordResetUrlAttribute.setName("passwordResetUrl");
    passwordResetUrlAttribute.setValue(accountBlocked.getPasswordResetUrl());

    return List.of(customerNameAttribute, passwordResetUrlAttribute);
  }

  private static String getTemplateId(String language) {
    if (LANGUAGE_DE.equalsIgnoreCase(language)) {
      return EmailTemplate.PI_ACCOUNT_BLOCKED_DE.getTemplateId();
    }
    return EmailTemplate.PI_ACCOUNT_BLOCKED.getTemplateId();
  }
}
