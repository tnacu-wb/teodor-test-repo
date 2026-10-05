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
import uk.co.whitbread.shared.azureemail.model.AccountRegistration;
import uk.co.whitbread.shared.azureemail.model.EmailTemplate;

@UtilityClass
public class AccountRegistrationBuilder {

  public static CreateRequest buildAccountRegistrationRequest(
      AccountRegistration accountRegistration) {

    final CreateRequest createRequest = new CreateRequest();
    createRequest.getObjects().add(buildTriggeredSend(accountRegistration));
    return createRequest;
  }

  private static TriggeredSend buildTriggeredSend(AccountRegistration accountRegistration) {
    final TriggeredSend triggeredSend = new TriggeredSend();
    triggeredSend.setTriggeredSendDefinition(
        buildTriggeredSendDefinition(accountRegistration.getLanguage()));
    triggeredSend.getSubscribers().add(buildSubscriber(accountRegistration));
    return triggeredSend;
  }

  private static TriggeredSendDefinition buildTriggeredSendDefinition(String language) {
    final TriggeredSendDefinition triggeredSendDefinition = new TriggeredSendDefinition();
    triggeredSendDefinition.setCustomerKey(getTemplateId(language));
    return triggeredSendDefinition;
  }

  private static Subscriber buildSubscriber(AccountRegistration accountRegistration) {
    final Subscriber subscriber = new Subscriber();
    subscriber.setOwner(buildOwner());
    final String email = accountRegistration.getEmailAddress();
    subscriber.setEmailAddress(email);
    subscriber.getAttributes().addAll(buildAttributes(accountRegistration));
    subscriber.setSubscriberKey(email);
    return subscriber;
  }

  private static List<Attribute> buildAttributes(AccountRegistration accountRegistration) {
    final Attribute customerNameAttribute = new Attribute();
    customerNameAttribute.setName("customerName");
    customerNameAttribute.setValue(accountRegistration.getCustomerName());

    final Attribute loginUrlAttribute = new Attribute();
    loginUrlAttribute.setName("loginUrl");
    loginUrlAttribute.setValue(accountRegistration.getLoginUrl());

    return List.of(customerNameAttribute, loginUrlAttribute);
  }

  private static String getTemplateId(String language) {
    if (LANGUAGE_DE.equalsIgnoreCase(language)) {
      return EmailTemplate.PI_ACCOUNT_REGISTRATION_DE.getTemplateId();
    }
    return EmailTemplate.PI_ACCOUNT_REGISTRATION.getTemplateId();
  }
}
