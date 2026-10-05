package uk.co.whitbread.content.domain.model.countries.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
public class CountriesRequest implements SelfValidation<CountriesRequest> {

  @NotEmpty
  private String country;

  @NotEmpty
  private String language;

  @NotEmpty
  private String site;

  public CountriesRequest(String country, String language, String site) {
    this.country = country;
    this.language = language;
    this.site = site;
    this.validateSelf();
  }
}
