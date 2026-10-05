package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.LinkReservationToLeisureCustomerRequestDto;
import uk.co.whitbread.reservation.domain.model.in.LinkReservationToLeisureCustomerRequest;

@Mapper(componentModel = "spring")
public interface LinkReservationToLeisureCustomerRequestOhipMapper {

  LinkReservationToLeisureCustomerRequestDto toDto(
      LinkReservationToLeisureCustomerRequest linkReservationToLeisureCustomerRequest);

}
