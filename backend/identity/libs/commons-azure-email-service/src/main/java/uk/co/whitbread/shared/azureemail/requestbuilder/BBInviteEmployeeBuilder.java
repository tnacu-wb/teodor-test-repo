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
import uk.co.whitbread.shared.azureemail.model.EmployeeInvite;

@UtilityClass
public class BBInviteEmployeeBuilder {

  public static CreateRequest buildBBInviteEmployeeRequest(EmployeeInvite employeeInvite) {
    CreateRequest createRequest = new CreateRequest();
    createRequest.getObjects().add(buildTriggeredSend(employeeInvite));
    return createRequest;
  }

  private static TriggeredSend buildTriggeredSend(EmployeeInvite employeeInvite) {
    TriggeredSend triggeredSend = new TriggeredSend();
    triggeredSend.setTriggeredSendDefinition(
        buildTriggeredSendDefinition(employeeInvite.getLanguage()));
    triggeredSend.getSubscribers().add(buildSubscriber(employeeInvite));
    return triggeredSend;
  }

  private static TriggeredSendDefinition buildTriggeredSendDefinition(String language) {
    TriggeredSendDefinition triggeredSendDefinition = new TriggeredSendDefinition();
    triggeredSendDefinition.setCustomerKey(getTemplateId(language));
    return triggeredSendDefinition;
  }

  private static Subscriber buildSubscriber(EmployeeInvite employeeInvite) {
    Subscriber subscriber = new Subscriber();
    subscriber.setOwner(buildOwner());
    subscriber.setEmailAddress(employeeInvite.getEmail());
    subscriber.getAttributes().addAll(buildAttributes(employeeInvite));
    subscriber.setSubscriberKey(employeeInvite.getEmail());
    return subscriber;
  }

  private static List<Attribute> buildAttributes(EmployeeInvite employeeInvite) {
    Attribute firstNameAttribute = new Attribute();
    firstNameAttribute.setName("FirstName");
    firstNameAttribute.setValue(employeeInvite.getFirstName());

    Attribute lastNameAttribute = new Attribute();
    lastNameAttribute.setName("LastName");
    lastNameAttribute.setValue(employeeInvite.getLastName());

    Attribute authorityLevelAttribute = new Attribute();
    authorityLevelAttribute.setName("AuthorityLevel");
    authorityLevelAttribute.setValue(employeeInvite.getAccessLevel());

    Attribute activationLinkAttribute = new Attribute();
    activationLinkAttribute.setName("ActivateLink");
    activationLinkAttribute.setValue(employeeInvite.getActivationLink());

    return List.of(firstNameAttribute, lastNameAttribute, authorityLevelAttribute,
        activationLinkAttribute);
  }

  private static String getTemplateId(String language) {
    if (LANGUAGE_DE.equalsIgnoreCase(language)) {
      return EmailTemplate.BB_INVITE_EMPLOYEE_DE.getTemplateId();
    }
    return EmailTemplate.BB_INVITE_EMPLOYEE.getTemplateId();
  }
}
