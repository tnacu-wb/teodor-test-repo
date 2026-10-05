package uk.co.whitbread.reservation.domain.model.out;

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
public class SearchBooking {

  private String bookingReference;
  private String sourcePms;
  private String status;
  private String hotelId;
  private String hotelName;
  private Booker booker;
  private List<StayingGuest> stayingGuests;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private BigDecimal totalCost;
  private String currencyCode;

}
