package uk.co.whitbread.content.infrastructure.rest.client.content.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultipleLabelsRequestDto {

  private String country;
  private String language;
  private List<CategoryEnumDto> categories;

}
