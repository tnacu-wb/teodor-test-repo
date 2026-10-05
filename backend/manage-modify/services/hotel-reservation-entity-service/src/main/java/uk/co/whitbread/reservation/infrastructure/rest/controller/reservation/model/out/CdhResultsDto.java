package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdhResultsDto {

  private String bookingReference;
  private String status;
  private String hotelId;
  private String hotelName;
  private String sourceSystem;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private BigDecimal totalCost;
  private String currencyCode;
  private CdhBookerDto booker;
  private List<RoomsDto> rooms;
  private List<CdhGuestsDto> guests;
}
