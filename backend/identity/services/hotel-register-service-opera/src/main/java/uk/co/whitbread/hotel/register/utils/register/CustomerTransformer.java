package uk.co.whitbread.hotel.register.utils.register;

import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import uk.co.whitbread.hotel.register.model.ContactDetail;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.shared.azureemail.model.PiRegister;
import uk.co.whitbread.shared.azureemail.model.PiRegister.PiRegisterBuilder;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerTransformer {

  @Mapping(target = "address1", source = "contactDetail.address.line1", defaultValue = "")
  @Mapping(target = "address2", source = "contactDetail.address.line2")
  @Mapping(target = "address3", source = "contactDetail.address.line3")
  @Mapping(target = "emailAddress", source = "contactDetail.email")
  @Mapping(target = "guestForename", source = "contactDetail.firstName")
  @Mapping(target = "guestSurname", source = "contactDetail.lastName")
  @Mapping(target = "guestTitle", source = "contactDetail.title", defaultValue = "")
  @Mapping(target = "postCode", source = "contactDetail.address.postCode")
  @Mapping(target = "userName", source = "contactDetail.email")
  PiRegister toPiRegister0(Customer customer);

  default PiRegister toPiRegister(Customer customer, String languageCode, String country) {
    final PiRegister piRegister = toPiRegister0(customer);
    final PiRegisterBuilder piRegisterBuilder = piRegister.toBuilder();
    final ContactDetail contactDetail = customer.getContactDetail();

    final String informalName = getInformalName(contactDetail);
    if (informalName.length() > 0) {
      piRegisterBuilder.infName(informalName);
    }

    Optional.ofNullable(contactDetail.getTelephone()).ifPresentOrElse(piRegisterBuilder::telephone, () ->
        Optional.ofNullable(contactDetail.getMobile()).ifPresent(piRegisterBuilder::telephone));

    piRegisterBuilder.languageCode(languageCode);
    piRegisterBuilder.country(country != null ? country : "");
    return piRegisterBuilder.build();
  }

  private String getInformalName(ContactDetail contactDetail) {
    final StringBuilder informalNameBuilder = new StringBuilder();
    Optional.ofNullable(contactDetail.getTitle()).ifPresent(informalNameBuilder::append);
    Optional.ofNullable(contactDetail.getLastName()).ifPresent(lastName -> {
      if (informalNameBuilder.length() > 0) {
        informalNameBuilder.append(" ");
      }
      informalNameBuilder.append(lastName);
    });
    return informalNameBuilder.toString();
  }
}
