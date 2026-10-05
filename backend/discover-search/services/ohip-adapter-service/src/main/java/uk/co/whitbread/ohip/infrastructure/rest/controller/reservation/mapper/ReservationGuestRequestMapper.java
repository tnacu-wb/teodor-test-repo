package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.ohip.domain.model.reservation.in.BillingAddressRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuestRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.BillingAddressCaptRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationGuestRequestDto;

@Mapper(componentModel = "spring")
public interface ReservationGuestRequestMapper {

  ReservationGuestRequest toModel(ReservationGuestRequestDto reservationGuestRequestDto);

  @Mapping(target = "profileUpdateIndicators.updateCompanyProfile", source = "updateCompanyProfile")
  @Mapping(target = "profileUpdateIndicators.updateContactProfile", source = "updateContactProfile")
  @Mapping(target = "profileUpdateIndicators.updateGuestProfile", source = "updateGuestProfile")
  BillingAddressRequest toModel(BillingAddressCaptRequestDto billingAddressRequestDto);

}
