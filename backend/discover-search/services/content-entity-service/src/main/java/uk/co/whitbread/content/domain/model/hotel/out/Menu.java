package uk.co.whitbread.content.domain.model.hotel.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

  private String name;
  private String description;
  private String imageSrc;
  private String menuSrc;
  private String menuLabel;
  private String disclaimer;
  private String stayStartDate;
  private String stayEndDate;
}
