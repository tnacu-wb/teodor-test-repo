package uk.co.whitbread.shared.azureemail.requestbuilder;

import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.BB_ACTIVATE_ACCOUNT;
import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.BB_ACTIVATE_ACCOUNT_DE;

import java.util.List;
import lombok.experimental.UtilityClass;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Attribute;
import uk.co.whitbread.shared.azureemail.genericEmail.api.CreateRequest;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Subscriber;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSend;
import uk.co.whitbread.shared.azureemail.genericEmail.api.TriggeredSendDefinition;
import uk.co.whitbread.shared.azureemail.model.EmployeeAccountActivation;
import uk.co.whitbread.shared.azureemail.properties.AzureEmailProperties;
import uk.co.whitbread.shared.azureemail.requestbuilder.utils.RequestUtils;

@UtilityClass
public class ActivateYourAccountBuilder {

  public static CreateRequest buildActivateYourAccountRequest(
      EmployeeAccountActivation employeeAccountActivation) {

    final CreateRequest createRequest = new CreateRequest();
    createRequest.getObjects().add(buildTriggeredSend(employeeAccountActivation));
    return createRequest;
  }

  private static TriggeredSend buildTriggeredSend(EmployeeAccountActivation employeeAccountActivation) {
    final TriggeredSend triggeredSend = new TriggeredSend();
    triggeredSend.setTriggeredSendDefinition(
        buildTriggeredSendDefinition(employeeAccountActivation.getLanguage()));
    triggeredSend.getSubscribers().add(buildSubscriber(employeeAccountActivation));
    return triggeredSend;
  }

  private static TriggeredSendDefinition buildTriggeredSendDefinition(String language) {
    final TriggeredSendDefinition triggeredSendDefinition = new TriggeredSendDefinition();
    triggeredSendDefinition.setCustomerKey(getTemplateId(language));
    return triggeredSendDefinition;
  }

  private static Subscriber buildSubscriber(EmployeeAccountActivation employeeAccountActivation) {
    final Subscriber subscriber = new Subscriber();
    subscriber.setOwner(RequestUtils.buildOwner());
    final String employeeEmail = employeeAccountActivation.getEmployeeEmail();
    subscriber.setEmailAddress(employeeEmail);
    subscriber.getAttributes().addAll(buildAttributes(employeeAccountActivation));
    subscriber.setSubscriberKey(employeeEmail);
    return subscriber;
  }

  private static List<Attribute> buildAttributes(EmployeeAccountActivation employeeAccountActivation) {
    final Attribute firstNameAttribute = new Attribute();
    firstNameAttribute.setName("FirstName");
    firstNameAttribute.setValue(employeeAccountActivation.getFirstName());

    final Attribute lastNameAttribute = new Attribute();
    lastNameAttribute.setName("LastName");
    lastNameAttribute.setValue(employeeAccountActivation.getLastName());

    final Attribute companyNameAttribute = new Attribute();
    companyNameAttribute.setName("CompanyName");
    companyNameAttribute.setValue(employeeAccountActivation.getCompanyName());

    final Attribute activateLinkAttribute = new Attribute();
    activateLinkAttribute.setName("ActivateLink");
    activateLinkAttribute.setValue(employeeAccountActivation.getActivationLink());

    final Attribute authorityLevelAttribute = new Attribute();
    authorityLevelAttribute.setName("AuthorityLevel");
    authorityLevelAttribute.setValue(employeeAccountActivation.getAccessLevel());

    return List.of(firstNameAttribute, lastNameAttribute, companyNameAttribute,
        authorityLevelAttribute, activateLinkAttribute);
  }

  private static String getTemplateId(String language) {
    if (AzureEmailProperties.LANGUAGE_DE.equalsIgnoreCase(language)) {
      return BB_ACTIVATE_ACCOUNT_DE.getTemplateId();
    }
    return BB_ACTIVATE_ACCOUNT.getTemplateId();
  }
}

