package uk.co.whitbread.content.domain.model.hotel.out;


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
  private String logoSrc;
  private String name;
  private String description;
  private String bookingCardImage;
  private String bookingCardTitle;
  private String bookingCardDescription;
  private String bookingCardBackgroundImage;
}
