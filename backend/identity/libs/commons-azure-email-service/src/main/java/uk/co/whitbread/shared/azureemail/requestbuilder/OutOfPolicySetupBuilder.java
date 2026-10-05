package uk.co.whitbread.shared.azureemail.requestbuilder;

import static uk.co.whitbread.shared.azureemail.requestbuilder.utils.RequestUtils.buildOwner;

import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Attribute;
import uk.co.whitbread.shared.azureemail.genericEmail.api.CreateRequest;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Subscriber;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSend;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSendDefinition;
import uk.co.whitbread.shared.azureemail.model.EmailTemplate;
import uk.co.whitbread.shared.azureemail.model.OutOfPolicySetup;

@UtilityClass
public class OutOfPolicySetupBuilder {

  public static CreateRequest buildOutOfPolicySetupRequest(OutOfPolicySetup outOfPolicySetup) {
    CreateRequest createRequest = new CreateRequest();
    createRequest.getObjects().add(buildTriggeredSend(outOfPolicySetup));
    return createRequest;
  }

  private static TriggeredSend buildTriggeredSend(OutOfPolicySetup outOfPolicySetup) {
    TriggeredSend triggeredSend = new TriggeredSend();
    triggeredSend.setTriggeredSendDefinition(buildTriggeredSendDefinition());
    triggeredSend.getSubscribers().add(buildSubscriber(outOfPolicySetup));
    return triggeredSend;
  }

  private static TriggeredSendDefinition buildTriggeredSendDefinition() {
    TriggeredSendDefinition triggeredSendDefinition = new TriggeredSendDefinition();
    triggeredSendDefinition.setCustomerKey(EmailTemplate.OUT_OF_POLICY_SETUP.getTemplateId());
    return triggeredSendDefinition;
  }

  private static Subscriber buildSubscriber(OutOfPolicySetup outOfPolicySetup) {
    Subscriber subscriber = new Subscriber();
    subscriber.setOwner(buildOwner());
    subscriber.setEmailAddress(outOfPolicySetup.getEmail());
    subscriber.getAttributes().addAll(buildAttributes(outOfPolicySetup));
    subscriber.setSubscriberKey(outOfPolicySetup.getEmail());
    return subscriber;
  }

  private static List<Attribute> buildAttributes(OutOfPolicySetup outOfPolicySetup) {
    Attribute firstNameAttribute = new Attribute();
    firstNameAttribute.setName("FirstName");
    firstNameAttribute.setValue(outOfPolicySetup.getFirstName());

    Attribute lastNameAttribute = new Attribute();
    lastNameAttribute.setName("LastName");
    lastNameAttribute.setValue(outOfPolicySetup.getLastName());

    Attribute activationLinkAttribute = new Attribute();
    activationLinkAttribute.setName("ActivateLink");
    activationLinkAttribute.setValue(StringUtils.EMPTY);

    return List.of(firstNameAttribute, lastNameAttribute, activationLinkAttribute);
  }
}
