package uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDataRequestDto {

  private String country;
  private String language;
  private List<DictionaryEnumDto> dictionaries;

}
