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
import uk.co.whitbread.shared.azureemail.model.CompanyActivation;
import uk.co.whitbread.shared.azureemail.model.EmailTemplate;

@UtilityClass
public class BBCompanyActivationBuilder {

  public static CreateRequest buildBBCompanyActivationRequest(CompanyActivation companyActivation) {
    CreateRequest createRequest = new CreateRequest();
    createRequest.getObjects().add(buildTriggeredSend(companyActivation));
    return createRequest;
  }

  private static TriggeredSend buildTriggeredSend(CompanyActivation companyActivation) {
    TriggeredSend triggeredSend = new TriggeredSend();
    triggeredSend.setTriggeredSendDefinition(
        buildTriggeredSendDefinition(companyActivation.getLanguage()));
    triggeredSend.getSubscribers().add(buildSubscriber(companyActivation));
    return triggeredSend;
  }

  private static TriggeredSendDefinition buildTriggeredSendDefinition(String language) {
    TriggeredSendDefinition triggeredSendDefinition = new TriggeredSendDefinition();
    triggeredSendDefinition.setCustomerKey(getTemplateId(language));
    return triggeredSendDefinition;
  }

  private static Subscriber buildSubscriber(CompanyActivation companyActivation) {
    Subscriber subscriber = new Subscriber();
    subscriber.setOwner(buildOwner());
    subscriber.setEmailAddress(companyActivation.getEmail());
    subscriber.getAttributes().addAll(buildAttributes(companyActivation));
    subscriber.setSubscriberKey(companyActivation.getEmail());
    return subscriber;
  }

  private static List<Attribute> buildAttributes(CompanyActivation companyActivation) {
    Attribute firstNameAttribute = new Attribute();
    firstNameAttribute.setName("FirstName");
    firstNameAttribute.setValue(companyActivation.getFirstName());

    Attribute lastNameAttribute = new Attribute();
    lastNameAttribute.setName("LastName");
    lastNameAttribute.setValue(companyActivation.getLastName());

    Attribute companyNameAttribute = new Attribute();
    companyNameAttribute.setName("CompanyName");
    companyNameAttribute.setValue(companyActivation.getCompanyName());

    Attribute activationLinkAttribute = new Attribute();
    activationLinkAttribute.setName("CompanyLink");
    activationLinkAttribute.setValue(companyActivation.getActivationLink());

    return List.of(firstNameAttribute, lastNameAttribute, companyNameAttribute,
        activationLinkAttribute);
  }

  private static String getTemplateId(String language) {
    if (LANGUAGE_DE.equalsIgnoreCase(language)) {
      return EmailTemplate.BB_COMPANY_ACTIVATION_DE.getTemplateId();
    }
    return EmailTemplate.BB_COMPANY_ACTIVATION.getTemplateId();
  }
}
