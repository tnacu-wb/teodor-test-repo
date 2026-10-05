package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WLHostedPageAppInitResponseDto {

  private String responseCode;
  private WLHostedPageAppInitDto data;
  private List<WLErrorDto> errors;
}