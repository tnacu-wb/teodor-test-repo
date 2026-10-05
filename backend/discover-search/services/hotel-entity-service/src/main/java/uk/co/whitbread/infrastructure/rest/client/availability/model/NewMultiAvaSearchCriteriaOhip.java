package uk.co.whitbread.infrastructure.rest.client.availability.model;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BookingChannelDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomV2Dto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewMultiAvaSearchCriteriaOhip {
  String accountId;
  String arrivalDate;
  BookingChannelDto bookingChannel;
  String departureDate;
  List<String> hotelIds;
  Boolean includePublicRates;
  Integer limit;
  BigDecimal minRate;
  Integer offset;
  List<RoomV2Dto> rooms;
  String sortBy;
}
