package uk.co.whitbread.basket.domain.model.payments.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessSite {

  private String identifier;
  private String name;
  private String type;
  private String location;
}
