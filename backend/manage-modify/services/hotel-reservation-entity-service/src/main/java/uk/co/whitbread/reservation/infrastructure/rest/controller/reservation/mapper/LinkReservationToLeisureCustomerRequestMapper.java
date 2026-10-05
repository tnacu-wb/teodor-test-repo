package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.LinkReservationToLeisureCustomerRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.LinkReservationToLeisureCustomerRequestDto;

@Mapper(componentModel = "spring")
public interface LinkReservationToLeisureCustomerRequestMapper {

  LinkReservationToLeisureCustomerRequest toModel(
      LinkReservationToLeisureCustomerRequestDto updateReservationOverrideReasonsRequestDto);
}