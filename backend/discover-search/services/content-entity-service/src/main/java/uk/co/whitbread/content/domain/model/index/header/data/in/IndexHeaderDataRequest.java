package uk.co.whitbread.content.domain.model.index.header.data.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
public class IndexHeaderDataRequest implements SelfValidation<IndexHeaderDataRequest> {

  @NotEmpty
  private String country;
  @NotEmpty
  private String language;
  private Boolean businessBooker;

  public IndexHeaderDataRequest(String country, String language, Boolean businessBooker) {
    this.country = country;
    this.language = language;
    this.businessBooker = businessBooker;
    this.validateSelf();
  }

}
