package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationByBasketRefResponseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReservationsRequestDto {

  private BookingChannelDto bookingChannel;

  private String companyId;

  private List<UpdateReservationRequestDto> updateReservationsRequest;

  private ReservationByBasketRefResponseDto tempReservations;

  private Map<String, String> linkAmendReservations;

  private List<ReservationDto> newRatesReservation;

  private String distributionIATANumber;

}
