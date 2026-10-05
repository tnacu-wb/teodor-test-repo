package uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.kiosk.domain.model.checkin.in.GuestAddress;
import uk.co.whitbread.kiosk.domain.model.checkin.in.GuestDetails;
import uk.co.whitbread.kiosk.domain.model.checkin.in.KioskAddress;
import uk.co.whitbread.kiosk.domain.model.checkin.in.PassportDetails;
import uk.co.whitbread.kiosk.domain.model.checkin.in.ProfileRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.StayingGuestDetails;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public abstract class OhipProfileRequestMapper {

  @Mapping(target = "guestDetails",
      expression = "java(injectGuestDetails(stayingGuestDetailsList))")
  public abstract ProfileRequest toKioskUpdateProfileRequestModel(String dummy,
      List<StayingGuestDetails> stayingGuestDetailsList);

  protected List<GuestDetails> injectGuestDetails(
      List<StayingGuestDetails> stayingGuestDetailsList) {

    List<GuestDetails> guestDetailsList = new ArrayList<>();
    for (StayingGuestDetails stayingGuestDetails : stayingGuestDetailsList) {
      var guestDetails = new GuestDetails();
      guestDetails.setGivenName(stayingGuestDetails.getFirstName());
      guestDetails.setSurname(stayingGuestDetails.getLastName());
      guestDetails.setNameTitle(stayingGuestDetails.getTitle());
      guestDetails.setNameType("Primary");
      guestDetails.setNationality(stayingGuestDetails.getNationality());
      guestDetails.setEmailAddress(stayingGuestDetails.getAddress() == null ? null
          : stayingGuestDetails.getAddress().getEmailAddress());
      guestDetails.setPhoneNumber(stayingGuestDetails.getAddress() == null ? null
          : stayingGuestDetails.getAddress().getMobileNumber());

      guestDetails.setKioskAddress(injectAddresses(stayingGuestDetails.getAddress()));

      guestDetails.setPassportDetails(injectPassportDetails(stayingGuestDetails));

      guestDetailsList.add(guestDetails);
    }
    return guestDetailsList;
  }

  private PassportDetails injectPassportDetails(StayingGuestDetails stayingGuestDetails) {
    var passportDetails = new PassportDetails();
    if (StringUtils.isNotBlank(stayingGuestDetails.getPassport())) {
      passportDetails.setPassportNumber(stayingGuestDetails.getPassport());
    }
    if (StringUtils.isNotBlank(stayingGuestDetails.getPlaceOfIssue())) {
      passportDetails.setPlaceOfIssue(stayingGuestDetails.getPlaceOfIssue());
    }
    if (StringUtils.isNotBlank(stayingGuestDetails.getNextDestination())) {
      passportDetails.setNextDestination(stayingGuestDetails.getNextDestination());
    }

    return passportDetails;
  }

  private KioskAddress injectAddresses(GuestAddress guestAddress) {

    if (guestAddress == null) {
      return null;
    }

    var kioskAddress = new KioskAddress();

    if (guestAddress.getAddressLine1() != null || guestAddress.getAddressLine2() != null
        || guestAddress.getAddressLine3() != null || guestAddress.getAddressLine4() != null) {

      kioskAddress.setAddressLine1(guestAddress.getAddressLine1());
      kioskAddress.setAddressLine2(guestAddress.getAddressLine2());
      kioskAddress.setAddressLine3(guestAddress.getAddressLine3());
      kioskAddress.setAddressLine4(guestAddress.getAddressLine4());

    }

    if (guestAddress.getPostalCode() != null) {
      kioskAddress.setPostalCode(guestAddress.getPostalCode());
    }

    if (StringUtils.isNotBlank(guestAddress.getCityName())) {
      kioskAddress.setCityName(guestAddress.getCityName());
    }

    if (StringUtils.isNotBlank(guestAddress.getCountryCode())) {
      kioskAddress.setCountry(guestAddress.getCountryCode());
    }
    return kioskAddress;
  }

}
