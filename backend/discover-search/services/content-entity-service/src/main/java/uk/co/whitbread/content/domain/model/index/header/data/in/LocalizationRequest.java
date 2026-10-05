package uk.co.whitbread.content.domain.model.index.header.data.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
public class LocalizationRequest implements SelfValidation<LocalizationRequest> {

  @NotEmpty
  private String country;
  @NotEmpty
  private String language;

  public LocalizationRequest(String country, String language) {
    this.country = country;
    this.language = language;
    this.validateSelf();
  }
}
