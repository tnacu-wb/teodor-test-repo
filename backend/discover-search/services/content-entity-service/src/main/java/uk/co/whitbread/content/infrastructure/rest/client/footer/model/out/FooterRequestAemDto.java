package uk.co.whitbread.content.infrastructure.rest.client.footer.model.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FooterRequestAemDto {

  @NotEmpty
  private String country;

  @NotEmpty
  private String language;

  @NotEmpty
  private String site;

}
