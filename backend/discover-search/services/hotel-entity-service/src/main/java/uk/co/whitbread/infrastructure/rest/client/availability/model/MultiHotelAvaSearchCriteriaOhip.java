package uk.co.whitbread.infrastructure.rest.client.availability.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiHotelAvaSearchCriteriaOhip {

  List<String> hotelIds;
  String arrivalDate;
  String departureDate;
  List<Integer> numberOfRooms;
  List<String> roomTypes;
  List<Integer> adults;
  List<Integer> children;
  List<Boolean> cotsRequired;
  String channel;
  String companyId;
}
