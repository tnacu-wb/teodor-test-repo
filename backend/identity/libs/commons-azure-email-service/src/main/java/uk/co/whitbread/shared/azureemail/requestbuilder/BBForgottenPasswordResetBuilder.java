package uk.co.whitbread.shared.azureemail.requestbuilder;

import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.BB_FORGOT_PASSWORD;
import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.BB_FORGOT_PASSWORD_DE;
import static uk.co.whitbread.shared.azureemail.properties.AzureEmailProperties.LANGUAGE_DE;
import static uk.co.whitbread.shared.azureemail.requestbuilder.utils.RequestUtils.buildOwner;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Attribute;
import uk.co.whitbread.shared.azureemail.genericEmail.api.CreateRequest;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Subscriber;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSend;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSendDefinition;
import uk.co.whitbread.shared.azureemail.model.PasswordReset;

@Component
@RequiredArgsConstructor
public class BBForgottenPasswordResetBuilder {

  static final String FIRST_NAME_ATTRIBUTE = "FirstName";
  static final String LAST_NAME_ATTRIBUTE = "LastName";
  static final String RESET_LINK_ATTRIBUTE = "ResetLink";

  public CreateRequest buildBBForgotPasswordRequest(PasswordReset passwordReset) {
    CreateRequest createRequest = new CreateRequest();
    createRequest.getObjects().add(buildTriggeredSend(passwordReset));
    return createRequest;
  }

  private TriggeredSend buildTriggeredSend(PasswordReset passwordReset) {
    TriggeredSend triggeredSend = new TriggeredSend();
    triggeredSend.setTriggeredSendDefinition(buildTriggeredSendDefinition(passwordReset.getLanguage()));
    triggeredSend.getSubscribers().add(buildSubscriber(passwordReset));
    return triggeredSend;
  }

  private TriggeredSendDefinition buildTriggeredSendDefinition(String language) {
    TriggeredSendDefinition triggeredSendDefinition = new TriggeredSendDefinition();
    triggeredSendDefinition.setCustomerKey(getTemplateId(language));
    return triggeredSendDefinition;
  }

  private String getTemplateId(String language) {
    return LANGUAGE_DE.equalsIgnoreCase(language) ? BB_FORGOT_PASSWORD_DE.getTemplateId()
        : BB_FORGOT_PASSWORD.getTemplateId();
  }

  private Subscriber buildSubscriber(PasswordReset passwordReset) {
    Subscriber subscriber = new Subscriber();
    subscriber.setOwner(buildOwner());
    subscriber.setEmailAddress(passwordReset.getEmail());
    subscriber.getAttributes().addAll(buildAttributes(passwordReset));
    subscriber.setSubscriberKey(passwordReset.getEmail());
    return subscriber;
  }

  private List<Attribute> buildAttributes(PasswordReset passwordReset) {
    Attribute firstNameAttribute = new Attribute();
    firstNameAttribute.setName(FIRST_NAME_ATTRIBUTE);
    firstNameAttribute.setValue(passwordReset.getFirstName());

    Attribute lastNameAttribute = new Attribute();
    lastNameAttribute.setName(LAST_NAME_ATTRIBUTE);
    lastNameAttribute.setValue(passwordReset.getLastName());

    Attribute resetLinkAttribute = new Attribute();
    resetLinkAttribute.setName(RESET_LINK_ATTRIBUTE);
    resetLinkAttribute.setValue(passwordReset.getResetPasswordUrl());

    return List.of(firstNameAttribute, lastNameAttribute, resetLinkAttribute);
  }
}
