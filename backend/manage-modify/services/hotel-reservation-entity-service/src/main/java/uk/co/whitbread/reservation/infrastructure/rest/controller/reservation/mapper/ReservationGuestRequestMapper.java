package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuestRequest;
import uk.co.whitbread.reservation.domain.model.in.StayingGuestAddress;
import uk.co.whitbread.reservation.domain.model.in.StayingGuestAddress.StayingGuestAddressBuilder;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationGuestRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.StayingGuestAddressDto;

@Mapper(componentModel = "spring")
public interface ReservationGuestRequestMapper {

  @Mapping(source = "companyId", target = "companyAccountId")
  ReservationGuestRequest toModel(ReservationGuestRequestDto reservationGuestRequestDto);

  /**
   * This unnecessary override is a hotfix for DNRQ-63419.
   * This should be removed once Address schema is tightened with field validation
   * and corresponding changes are added to FE channels
   *
   * @return StayingGuestAddress Mapped address or null (if address invalid)
   */
  default StayingGuestAddress toStayingGuestAddressModel(
      StayingGuestAddressDto stayingGuestAddressDto) {
    if (stayingGuestAddressDto == null || !stayingGuestAddressDto.isValid()) {
      return null;
    }

    StayingGuestAddressBuilder<?, ?> stayingGuestAddress = StayingGuestAddress.builder();

    stayingGuestAddress.addressType(stayingGuestAddressDto.getAddressType());
    stayingGuestAddress.postalCode(stayingGuestAddressDto.getPostalCode());
    stayingGuestAddress.addressLine1(stayingGuestAddressDto.getAddressLine1());
    stayingGuestAddress.addressLine2(stayingGuestAddressDto.getAddressLine2());
    stayingGuestAddress.addressLine3(stayingGuestAddressDto.getAddressLine3());
    stayingGuestAddress.addressLine4(stayingGuestAddressDto.getAddressLine4());
    stayingGuestAddress.countryCode(stayingGuestAddressDto.getCountryCode());
    stayingGuestAddress.cityName(stayingGuestAddressDto.getCityName());
    stayingGuestAddress.companyName(stayingGuestAddressDto.getCompanyName());
    stayingGuestAddress.addressId(stayingGuestAddressDto.getAddressId());

    return stayingGuestAddress.build();
  }
}
