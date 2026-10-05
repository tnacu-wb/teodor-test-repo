package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

  private List<Menu> menus;
  private String brand;
  private String name;
  private String description;
  private String image;
  private String bookingCardImage;
  private String bookingCardTitle;
  private String bookingCardDescription;
  private String bookingCardBackgroundImage;
}