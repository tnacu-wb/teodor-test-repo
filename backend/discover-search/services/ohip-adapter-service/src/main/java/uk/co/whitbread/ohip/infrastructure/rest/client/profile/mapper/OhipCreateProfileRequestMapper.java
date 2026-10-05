package uk.co.whitbread.ohip.infrastructure.rest.client.profile.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeEmails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeTelephones;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PersonNameType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PersonNameTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.TelephoneInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.TelephoneType;
import uk.co.whitbread.ohip.domain.model.profile.in.GuestDetails;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public abstract class OhipCreateProfileRequestMapper {

  @Mapping(target = "profileDetails",
      expression = "java(injectProfileDetails(guestDetails, hotelId))")
  public abstract Profile toProfileModel(GuestDetails guestDetails, String hotelId);

  protected ProfileType injectProfileDetails(GuestDetails guestDetails, String hotelId) {

    var profileDetails = new ProfileType();
    profileDetails.setCustomer(injectCustomer(guestDetails));
    profileDetails.setTelephones(injectTelephones(guestDetails));
    profileDetails.setEmails(injectEmails(guestDetails));
    profileDetails.setAddresses(
        OhipUpdateProfileRequestMapper.injectAddresses(null, null, guestDetails));
    profileDetails.setRequestForHotel(hotelId);
    profileDetails.setMarkAsRecentlyAccessed(true);
    profileDetails.setProfileType(ProfileTypeType.GUEST);
    return profileDetails;
  }

  private CompanyProfileTypeEmails injectEmails(GuestDetails guestDetails) {
    var email = guestDetails.getEmailAddress();
    if (email == null) {
      return null;
    }

    var emailType = new EmailType();
    emailType.setEmailAddress(email);
    emailType.setType("EMAIL");
    EmailInfoType emailInfoType = new EmailInfoType();
    emailInfoType.setEmail(emailType);
    CompanyProfileTypeEmails companyProfileTypeEmails = new CompanyProfileTypeEmails();
    companyProfileTypeEmails.setEmailInfo(List.of(emailInfoType));
    return companyProfileTypeEmails;
  }

  private CompanyProfileTypeTelephones injectTelephones(GuestDetails guestDetails) {
    final var telephones = new ArrayList<TelephoneInfoType>();
    final var telephoneType = new TelephoneType();

    if (StringUtils.isNotBlank(guestDetails.getPhoneNumber())) {

      telephoneType.setPhoneTechType("PHONE");
      telephoneType.setPhoneUseType("MOBILE");
      telephoneType.setPrimaryInd(true);
      telephoneType.setPhoneNumber(guestDetails.getPhoneNumber());

      var telephoneInfoType = new TelephoneInfoType();
      telephoneInfoType.setTelephone(telephoneType);
      telephones.add(telephoneInfoType);
    }
    final var profileTypeTelephones = new CompanyProfileTypeTelephones();
    profileTypeTelephones.setTelephoneInfo(telephones);

    return profileTypeTelephones;
  }

  private CustomerType injectCustomer(GuestDetails guestDetails) {

    final var profileNameType = new PersonNameType();

    //personName
    if (StringUtils.isNotBlank(guestDetails.getGivenName())) {
      profileNameType.setGivenName(guestDetails.getGivenName());
    }
    if (StringUtils.isNotBlank(guestDetails.getSurname())) {
      profileNameType.setSurname(guestDetails.getSurname());
    }
    if (StringUtils.isNotBlank(guestDetails.getNameTitle())) {
      profileNameType.setNameTitle(guestDetails.getNameTitle());
    }
    if (StringUtils.isNotBlank(guestDetails.getNameType())) {
      profileNameType.setNameType(PersonNameTypeType.PRIMARY);
    }

    var customerType = new CustomerType();
    customerType.setPersonName(List.of(profileNameType));
    if (StringUtils.isNotBlank(guestDetails.getNationality())) {
      customerType.setNationality(guestDetails.getNationality());
    }

    return customerType;
  }

}
