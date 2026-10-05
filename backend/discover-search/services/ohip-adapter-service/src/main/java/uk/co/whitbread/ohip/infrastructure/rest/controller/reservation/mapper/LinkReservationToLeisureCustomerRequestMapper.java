package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.LinkReservationToLeisureCustomerRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.LinkReservationToLeisureCustomerRequestDto;

@Mapper(componentModel = "spring")
public interface LinkReservationToLeisureCustomerRequestMapper {

  LinkReservationToLeisureCustomerRequest toModel(
      LinkReservationToLeisureCustomerRequestDto linkReservationToLeisureCustomerRequestDto);
}