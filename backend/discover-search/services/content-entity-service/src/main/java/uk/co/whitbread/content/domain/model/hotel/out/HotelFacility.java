package uk.co.whitbread.content.domain.model.hotel.out;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelFacility {

  private String code;
  private String name;
  private String description;
  private Integer weight;
  private String icon;
  private Boolean isVisible;
}