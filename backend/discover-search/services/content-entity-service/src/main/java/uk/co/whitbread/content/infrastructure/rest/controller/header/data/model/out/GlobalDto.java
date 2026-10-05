package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

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
public class GlobalDto {

  private String addRoom;
  private String done;
  private String room;
  private String roomLabel;
  private String single;
  @JsonProperty("double")
  private String doubleLabel;
  private String twin;
  private String accessible;
  private String family;
  private String adult;
  private String adults;
  private String child;
  private String children;
  private String night;
  private String rooms;
  private String adultsLabel;
  private String childrenLabel;
  private String today;
  private String tomorrow;
  private BrandDto brand;
  private List<PromotionDto> promotions;
  private List<OfferDto> offers;
  private String thirdParties;
  private String accessibleOrBarrierFree;
}
