package uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DlpInformationRequestDto {

  private String country;
  private String language;
  private String dlpPath;
}
