package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Global {

  private String addRoom;
  private String adults;
  @JsonProperty("double")
  private String doubleLabel;
  private String family;
  private String accessible;
  private String roomLabel;
  private String nights;
  private String tomorrow;
  private List<Promotions> promotions;
  private List<Offer> offers;
  private String single;
  private String done;
  private String room;
  private String twin;
  private String adult;
  private String child;
  private String night;
  private String rooms;
  private String today;
  private Integer maxRooms;
  private Integer maxArrivalDate;
  private List<AcceptedRoomTypes> acceptedRoomTypes;
  private String adultsLabel;
  private String childrenLabel;
  private String children;
  private String roomsLabel;
  private Brand brand;
  private String thirdParties;
  private String accessibleOrBarrierFree;
}
