package uk.co.whitbread.shared.azureemail.requestbuilder;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.shared.azureemail.api.ContentPremierGuestEdited;
import uk.co.whitbread.shared.azureemail.model.AccountUpdate;
import uk.co.whitbread.shared.azureemail.properties.PTIEmailProperties;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
abstract class AccountUpdateTransformer {

  @Autowired
  protected PTIEmailProperties ptiEmailProperties;

  @Mapping(source = "accountUpdate.title", target = "guestTitle")
  @Mapping(source = "accountUpdate.firstName", target = "guestForename")
  @Mapping(source = "accountUpdate.lastName", target = "guestSurname")
  @Mapping(source = "accountUpdate.addressLine1", target = "address1")
  @Mapping(source = "accountUpdate.addressLine2", target = "address2")
  @Mapping(source = "accountUpdate.addressLine3", target = "address3")
  @Mapping(source = "accountUpdate.countryCode", target = "country")
  @Mapping(source = "accountUpdate.postCode", target = "postCode")
  @Mapping(source = "accountUpdate.email", target = "userName")
  @Mapping(source = "accountUpdate.email", target = "emailAddress")
  @Mapping(target = "languageCode", expression = "java(ptiEmailProperties.getDefaultLanguageCode())")
  abstract ContentPremierGuestEdited toPremierGuestEditedContent0(AccountUpdate accountUpdate);

  ContentPremierGuestEdited toPremierGuestEditedContent(AccountUpdate accountUpdate) {
    final ContentPremierGuestEdited content = toPremierGuestEditedContent0(accountUpdate);
    final String informalName = getInformalName(accountUpdate);
    if (informalName.length() > 0) {
      content.setInfName(informalName);
    }
    Optional.ofNullable(accountUpdate.getTelephone()).ifPresentOrElse(content::setTelephone, () ->
        Optional.ofNullable(accountUpdate.getMobile()).ifPresent(content::setTelephone));
    return content;
  }

  private String getInformalName(AccountUpdate accountUpdate) {
    final StringBuilder informalNameBuilder = new StringBuilder();
    Optional.ofNullable(accountUpdate.getTitle()).ifPresent(informalNameBuilder::append);
    Optional.ofNullable(accountUpdate.getLastName()).ifPresent(lastName -> {
      if (informalNameBuilder.length() > 0) {
        informalNameBuilder.append(" ");
      }
      informalNameBuilder.append(lastName);
    });
    return informalNameBuilder.toString();
  }
}
