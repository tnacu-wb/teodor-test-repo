package uk.co.whitbread.content.domain.model.dlp.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
public class DlpInformationRequest implements SelfValidation<DlpInformationRequest> {

  @NotEmpty
  private String country;

  @NotEmpty
  private String language;

  @NotEmpty
  private String dlpPath;
}
