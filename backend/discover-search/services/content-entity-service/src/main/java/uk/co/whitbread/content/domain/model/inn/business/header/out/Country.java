package uk.co.whitbread.content.domain.model.inn.business.header.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Country {

  private String code;
  private String language;
  private String flagUrl;
  private String url;

}
