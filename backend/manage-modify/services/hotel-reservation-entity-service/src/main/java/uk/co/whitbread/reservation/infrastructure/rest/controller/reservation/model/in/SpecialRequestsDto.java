package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.Valid;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpecialRequestsDto {

  @Valid
  private List<String> reservationIds;
  private String hotelId;
  private List<String> specialRequests;
  private List<String> bookingNotes;
}
