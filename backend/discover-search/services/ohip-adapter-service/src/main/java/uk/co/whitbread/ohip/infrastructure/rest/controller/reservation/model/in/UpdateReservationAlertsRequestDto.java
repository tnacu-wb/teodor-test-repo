package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

public record UpdateReservationAlertsRequestDto(
    @NotNull
    @NotEmpty
    Set<String> reservationIds,
    @NotEmpty
    String hotelId,
    @NotNull
    @Valid
    List<AlertDto> alerts
) {

}
