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
import uk.co.whitbread.shared.azureemail.model.EmployeeAccessLevelChange;

@UtilityClass
public class BBEmployeeAccessLevelChangeBuilder {

  static final String FIRST_NAME_ATTRIBUTE = "FirstName";
  static final String AUTHORITY_LEVEL_ATTRIBUTE = "AuthorityLevel";
  static final String LOGIN_URL_ATTRIBUTE = "LoginUrl";

  public static CreateRequest buildBBChangeEmployeeAccessLevelRequest(
      EmployeeAccessLevelChange employeeAccessLevel) {
    CreateRequest createRequest = new CreateRequest();
    createRequest.getObjects().add(buildTriggeredSend(employeeAccessLevel));
    return createRequest;
  }

  private static TriggeredSend buildTriggeredSend(EmployeeAccessLevelChange employeeAccessLevel) {
    TriggeredSend triggeredSend = new TriggeredSend();
    triggeredSend.setTriggeredSendDefinition(
        buildTriggeredSendDefinition(employeeAccessLevel.getLanguage()));
    triggeredSend.getSubscribers().add(buildSubscriber(employeeAccessLevel));
    return triggeredSend;
  }

  private static TriggeredSendDefinition buildTriggeredSendDefinition(String language) {
    TriggeredSendDefinition triggeredSendDefinition = new TriggeredSendDefinition();
    triggeredSendDefinition.setCustomerKey(getTemplateId(language));
    return triggeredSendDefinition;
  }

  private static Subscriber buildSubscriber(EmployeeAccessLevelChange employeeAccessLevel) {
    Subscriber subscriber = new Subscriber();
    subscriber.setOwner(buildOwner());
    subscriber.setEmailAddress(employeeAccessLevel.getEmail());
    subscriber.getAttributes().addAll(buildAttributes(employeeAccessLevel));
    subscriber.setSubscriberKey(employeeAccessLevel.getEmail());
    return subscriber;
  }

  private static List<Attribute> buildAttributes(EmployeeAccessLevelChange employeeAccessLevel) {
    Attribute firstNameAttribute = new Attribute();
    firstNameAttribute.setName(FIRST_NAME_ATTRIBUTE);
    firstNameAttribute.setValue(employeeAccessLevel.getFirstName());

    Attribute authorityLevelAttribute = new Attribute();
    authorityLevelAttribute.setName(AUTHORITY_LEVEL_ATTRIBUTE);
    authorityLevelAttribute.setValue(employeeAccessLevel.getAccessLevel());

    Attribute loginUrlAttribute = new Attribute();
    loginUrlAttribute.setName(LOGIN_URL_ATTRIBUTE);
    loginUrlAttribute.setValue(employeeAccessLevel.getLoginUrl());

    return List.of(firstNameAttribute, authorityLevelAttribute, loginUrlAttribute);
  }

  private static String getTemplateId(String language) {
    if (LANGUAGE_DE.equalsIgnoreCase(language)) {
      return EmailTemplate.BB_EMPLOYEE_ACCESS_LEVEL_CHANGE_DE.getTemplateId();
    }
    return EmailTemplate.BB_EMPLOYEE_ACCESS_LEVEL_CHANGE.getTemplateId();
  }
}
