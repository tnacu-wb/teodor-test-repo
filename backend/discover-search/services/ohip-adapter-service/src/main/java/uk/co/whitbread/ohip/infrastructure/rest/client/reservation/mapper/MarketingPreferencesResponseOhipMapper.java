package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import org.mapstruct.Mapper;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.ohip.domain.model.reservation.out.Customer;
import uk.co.whitbread.ohip.domain.model.reservation.out.MarketingPreferencesResponse;

@Mapper(componentModel = "spring")
public interface MarketingPreferencesResponseOhipMapper {

  default MarketingPreferencesResponse toModel(Profile bookerProfile) {

    if (bookerProfile == null || bookerProfile.getProfileDetails() == null) {
      return null;
    }

    final var optInEmail = bookerProfile.getProfileDetails().getPrivacyInfo() != null
        ? bookerProfile.getProfileDetails().getPrivacyInfo().getOptInEmail() : null;
    final var contactValue = bookerProfile.getProfileDetails().getEmails() != null
        && !CollectionUtils.isEmpty(bookerProfile.getProfileDetails().getEmails().getEmailInfo())
        ? bookerProfile.getProfileDetails().getEmails().getEmailInfo().get(0).getEmail().getEmailAddress() : null;
    final var title = bookerProfile.getProfileDetails().getCustomer() != null
        ? bookerProfile.getProfileDetails().getCustomer().getPersonName().get(0).getNameTitle() : null;
    final var firstName = bookerProfile.getProfileDetails().getCustomer() != null
        ? bookerProfile.getProfileDetails().getCustomer().getPersonName().get(0).getGivenName() : null;
    final var lastName = bookerProfile.getProfileDetails().getCustomer() != null
        ? bookerProfile.getProfileDetails().getCustomer().getPersonName().get(0).getSurname() : null;
    final var country = bookerProfile.getProfileDetails().getAddresses() != null
        && bookerProfile.getProfileDetails().getAddresses().getAddressInfo() != null
        ? bookerProfile.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getCountry().getCode() :
        null;
    final var language = bookerProfile.getProfileDetails().getCustomer() != null
        ? bookerProfile.getProfileDetails().getCustomer().getLanguage() : null;

    final var customer = Customer.builder().title(title).firstName(firstName).lastName(lastName).country(country)
        .language(language).build();

    return MarketingPreferencesResponse.builder()
        .optIn(optInEmail).contactValue(contactValue).customer(customer).build();
  }

}