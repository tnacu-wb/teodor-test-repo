package uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryDataDto {

  private String dictionary;
  private Map<String, String> data;

}
