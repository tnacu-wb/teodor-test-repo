package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationsType;
import uk.co.whitbread.ohip.domain.model.reservation.out.ConfirmReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ConfirmationCustomer;

@Mapper(componentModel = "spring")
public interface ConfirmationResponseOhipMapper {

  @Mapping(source = "reservationDetails", target = "reservationGuest", qualifiedByName = "toReservationGuestModel")
  ConfirmReservationResponse toConfirmReservationResponseModel(
      HotelReservationType reservationDetails);

  @Named("toConfirmReservationResponseModel")
  default ConfirmReservationResponse toConfirmReservationResponseModel(
      HotelReservationsType hotelReservations) {
    ConfirmReservationResponse response = null;
    Optional<ConfirmReservationResponse> confirmReservationResponse = hotelReservations
        .getReservation().stream().map(this::toConfirmReservationResponseModel)
        .findFirst();
    if (!confirmReservationResponse.isEmpty()) {
      response = confirmReservationResponse.get();
    }
    return response;
  }

  @Named("toReservationGuestModel")
  default ConfirmationCustomer toReservationGuestModel(HotelReservationType reservationDetails) {
    return ConfirmationCustomer.builder()
        .givenName(reservationDetails.getReservationGuests().get(0).getProfileInfo().getProfile()
            .getCustomer().getPersonName().get(0).getGivenName())
        .surName(reservationDetails.getReservationGuests().get(0).getProfileInfo().getProfile()
            .getCustomer().getPersonName().get(0).getSurname())
        .build();
  }
}
