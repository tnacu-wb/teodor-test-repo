package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {

  private List<MenuDto> menus;
  private String logoSrc;
  private String name;
  private String description;
  private String bookingCardImage;
  private String bookingCardTitle;
  private String bookingCardDescription;
  private String bookingCardBackgroundImage;
}
