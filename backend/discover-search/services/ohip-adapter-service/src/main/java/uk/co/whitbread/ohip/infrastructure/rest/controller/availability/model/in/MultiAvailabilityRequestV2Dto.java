package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in;

import jakarta.validation.constraints.NotNull;
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
public class MultiAvailabilityRequestV2Dto {

  @NotNull
  private BookingChannelDto bookingChannel;
  @NotNull
  private List<String> hotelIds;
  @NotNull
  private LocalDate arrivalDate;
  @NotNull
  private LocalDate departureDate;
  private String accountId;
  private Boolean includePublicRates;
  @NotNull
  private List<RoomV2Dto> rooms;
  private Integer offset;
  private Integer limit;
  private String sortBy;
  private BigDecimal minRate;
}
