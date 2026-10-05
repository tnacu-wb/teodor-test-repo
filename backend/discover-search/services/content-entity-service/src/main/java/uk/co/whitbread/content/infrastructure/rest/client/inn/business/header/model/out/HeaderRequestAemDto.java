package uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeaderRequestAemDto {

  @NotEmpty
  private String country;

  @NotEmpty
  private String language;
}
