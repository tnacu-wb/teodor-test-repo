package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalDto {

  private String today;
  private String tomorrow;
  private String adult;
  private String adults;
  private String child;
  private String children;
  private String room;
  private String rooms;
  private String night;
  private String nights;
  private String single;
  @JsonProperty("double")
  private String doubleValue;
  private String accessible;
  private String twin;
  private String family;
  private String adultsLabel;
  private String childrenLabel;
  @JsonProperty("addRoom")
  private String labelAddRoom;
  @JsonProperty("addRoomIcon")
  private String iconAddRoom;
  private String done;
  private BrandDto brand;

}
