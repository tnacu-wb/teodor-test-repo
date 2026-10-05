package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerAddress;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuestRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public interface ReservationCompanyRequestOhipMapper {

  default Profile toDto(String companyName) {
    if (StringUtils.isNotBlank(companyName)) {
      return toModel(companyName);
    }
    return null;
  }

  default Profile toDto(ReservationGuestRequest guestReservationRequest) {
    return Optional.ofNullable(guestReservationRequest.getBooker())
        .map(BookerDetails::getAddress)
        .map(BookerAddress::getCompanyName)
        .filter(StringUtils::isNotBlank)
        .map(ignored -> toModel(guestReservationRequest.getBooker()))
        .orElse(null);
  }

  @Mapping(target = "profileDetails.profileType", constant = "COMPANY")
  @Mapping(target = "profileDetails.company.companyName", source = "companyName")
  Profile toModel(String companyName);

  @Mapping(target = "profileDetails.profileType", constant = "COMPANY")
  @Mapping(target = "profileDetails.company.companyName", source = "bookerDetails.address.companyName")
  @Mapping(target = "profileDetails.addresses.addressInfo",
      expression = "java(Arrays.asList(toModel(bookerAddress)))")
  Profile toModel(BookerDetails bookerDetails);

  @Mapping(target = "address.type", source = "bookerAddress.addressType")
  @Mapping(target = "address.cityName", source = "bookerAddress.cityName")
  @Mapping(target = "address.postalCode", source = "bookerAddress.postalCode")
  @Mapping(target = "address.country.value", source = "bookerAddress.countryCode")
  @Mapping(target = "address.addressLine",
      expression = "java(Arrays.asList("
          + "bookerAddress.getAddressLine1(),"
          + "bookerAddress.getAddressLine2(),"
          + "bookerAddress.getAddressLine3(),"
          + "bookerAddress.getAddressLine4()))")
  AddressInfoType toModel(BookerAddress bookerAddress);
}
