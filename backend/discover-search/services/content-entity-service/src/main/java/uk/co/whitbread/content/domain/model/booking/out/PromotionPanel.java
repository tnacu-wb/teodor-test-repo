package uk.co.whitbread.content.domain.model.booking.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionPanel {

  private String image;
  private String name;
  private String description;
  private String linkLabel;
  private String linkPath;
  private List<String> displayHotels;

}
