package uk.co.whitbread.reservation.domain.model.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CdhBookerDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CdhGuestsDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdhResults {

  private String bookingReference;
  private String status;
  private String hotelId;
  private String hotelName;
  private String sourceSystem;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private LocalDate cancellationDate;
  private BigDecimal totalCost;
  private String currencyCode;
  private CdhBooker booker;
  private List<Rooms> rooms;
  private List<CdhGuests> guests;
}
