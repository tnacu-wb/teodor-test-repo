package uk.co.whitbread.content.domain.model.inn.business.header.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeaderRequest implements SelfValidation<HeaderRequest> {

  @NotEmpty
  private String country;

  @NotEmpty
  private String language;

}
