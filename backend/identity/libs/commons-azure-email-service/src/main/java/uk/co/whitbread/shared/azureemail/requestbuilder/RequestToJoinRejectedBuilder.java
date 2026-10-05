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
import uk.co.whitbread.shared.azureemail.model.RequestToJoinRejected;

@UtilityClass
public class RequestToJoinRejectedBuilder {

  static final String FIRST_NAME_ATTRIBUTE = "FirstName";
  static final String LAST_NAME_ATTRIBUTE = "LastName";
  static final String COMPANY_NAME_ATTRIBUTE = "CompanyName";
  static final String ADMIN_FIRST_NAME_ATTRIBUTE = "AdminFirstName";
  static final String ADMIN_LAST_NAME_ATTRIBUTE = "AdminLastName";
  static final String EMAIL_ADDRESS_ATTRIBUTE = "EmailAddress";

  public static CreateRequest buildRequestToJoinRejectedRequest(
      RequestToJoinRejected requestToJoinRejected) {
    CreateRequest createRequest = new CreateRequest();
    createRequest.getObjects().add(buildTriggeredSend(requestToJoinRejected));
    return createRequest;
  }

  private static TriggeredSend buildTriggeredSend(RequestToJoinRejected requestToJoinRejected) {
    TriggeredSend triggeredSend = new TriggeredSend();
    triggeredSend.setTriggeredSendDefinition(
        buildTriggeredSendDefinition(requestToJoinRejected.getLanguage()));
    triggeredSend.getSubscribers().add(buildSubscriber(requestToJoinRejected));
    return triggeredSend;
  }

  private static TriggeredSendDefinition buildTriggeredSendDefinition(String language) {
    TriggeredSendDefinition triggeredSendDefinition = new TriggeredSendDefinition();
    triggeredSendDefinition.setCustomerKey(getTemplateId(language));
    return triggeredSendDefinition;
  }

  private static Subscriber buildSubscriber(RequestToJoinRejected requestToJoinRejected) {
    Subscriber subscriber = new Subscriber();
    subscriber.setOwner(buildOwner());
    subscriber.setEmailAddress(requestToJoinRejected.getEmailAddress());
    subscriber.getAttributes().addAll(buildAttributes(requestToJoinRejected));
    subscriber.setSubscriberKey(requestToJoinRejected.getEmailAddress());
    return subscriber;
  }

  private static List<Attribute> buildAttributes(RequestToJoinRejected requestToJoinRejected) {
    Attribute companyNameAttribute = new Attribute();
    companyNameAttribute.setName(COMPANY_NAME_ATTRIBUTE);
    companyNameAttribute.setValue(requestToJoinRejected.getCompanyName());

    Attribute firstNameAttribute = new Attribute();
    firstNameAttribute.setName(FIRST_NAME_ATTRIBUTE);
    firstNameAttribute.setValue(requestToJoinRejected.getFirstName());

    Attribute lastNameAttribute = new Attribute();
    lastNameAttribute.setName(LAST_NAME_ATTRIBUTE);
    lastNameAttribute.setValue(requestToJoinRejected.getLastName());

    Attribute adminFirstNameAttribute = new Attribute();
    adminFirstNameAttribute.setName(ADMIN_FIRST_NAME_ATTRIBUTE);
    adminFirstNameAttribute.setValue(requestToJoinRejected.getAdminFirstName());

    Attribute adminLastNameAttribute = new Attribute();
    adminLastNameAttribute.setName(ADMIN_LAST_NAME_ATTRIBUTE);
    adminLastNameAttribute.setValue(requestToJoinRejected.getAdminLastName());

    Attribute emailAddressAttribute = new Attribute();
    emailAddressAttribute.setName(EMAIL_ADDRESS_ATTRIBUTE);
    emailAddressAttribute.setValue(requestToJoinRejected.getEmailAddress());

    return List
        .of(firstNameAttribute, lastNameAttribute, companyNameAttribute, adminFirstNameAttribute,
            adminLastNameAttribute, emailAddressAttribute);
  }

  private static String getTemplateId(String language) {
    if (LANGUAGE_DE.equalsIgnoreCase(language)) {
      return EmailTemplate.REQUEST_TO_JOIN_REJECTED_DE.getTemplateId();
    }
    return EmailTemplate.REQUEST_TO_JOIN_REJECTED.getTemplateId();
  }
}
