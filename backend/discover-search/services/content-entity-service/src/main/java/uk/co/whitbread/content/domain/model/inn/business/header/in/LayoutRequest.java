package uk.co.whitbread.content.domain.model.inn.business.header.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LayoutRequest implements SelfValidation<HeaderRequest> {

  private String dictionary;
  private String language;

}
