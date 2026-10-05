package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultiHotelAvailabilityRequestV2Dto {

  private List<String> hotelIds;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private String accountId;
  private Boolean includePublicRates;
  private List<RoomDto> rooms;
  private Integer offset;
  private Integer limit;
  private String sortBy;
  private BigDecimal minRate;
}
