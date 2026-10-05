package uk.co.whitbread.ohip.domain.model.availability.in;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode
public class MultiHotelAvailabilityRequestV2 {

  private BookingChannel bookingChannel;
  private List<String> hotelIds;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private String accountId;
  private Boolean includePublicRates;
  private List<Room> rooms;
  private Integer offset;
  private Integer limit;
  private String sortBy;
  private BigDecimal minRate;
}
