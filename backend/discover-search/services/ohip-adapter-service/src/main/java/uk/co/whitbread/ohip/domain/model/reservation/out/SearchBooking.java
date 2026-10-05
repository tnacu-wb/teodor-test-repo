package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SearchBooking {

  private String bookingReference;
  private String hotelId;
  private String hotelName;
  private String status;
  private SearchBookingBooker booker;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private List<SearchBookingReservation> reservations;
  private BigDecimal totalCost;
  private String currencyCode;

}
