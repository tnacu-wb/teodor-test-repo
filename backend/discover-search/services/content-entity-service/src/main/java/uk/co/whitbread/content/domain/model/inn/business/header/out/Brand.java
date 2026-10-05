package uk.co.whitbread.content.domain.model.inn.business.header.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Brand {

  private String piLogo;
  private String pidLogo;
  private String hubLogo;
  private String zipLogo;

}
