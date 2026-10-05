package uk.co.whitbread.content.infrastructure.rest.client.content.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalizationRequestDto {

  private String country;
  private String language;
}
