package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelFacilityDto {

  private String code;
  private String name;
  private String description;
  private Integer weight;
  private String icon;
  private Boolean isVisible;

}