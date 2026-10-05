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
public class WLAppCompanyDetailsUpdateResponseDto {

  private String responseCode;
  private String data;
  private List<WLErrorDto> errors;

}
