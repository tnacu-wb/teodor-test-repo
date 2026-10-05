package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchBookingDto {

  private String bookingReference;
  private String hotelId;
  private String hotelName;
  private String status;
  private SearchBookingBookerDto booker;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private List<SearchBookingReservationDto> reservations;
  private BigDecimal totalCost;
  private String currencyCode;

}
