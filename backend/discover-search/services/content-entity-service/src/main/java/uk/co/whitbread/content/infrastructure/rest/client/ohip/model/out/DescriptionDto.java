package uk.co.whitbread.content.infrastructure.rest.client.ohip.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class DescriptionDto {

  private String defaultText;
}
