package uk.co.whitbread.reservation.domain.model.out.aem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookPage {

  private String heroImageSrc;
  private String subtitleName;
  private String name;
  private String heroBackgroundImageSrc;

}
