package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WLErrorDto {

  private String code;
  private String target;
  private String message;
}
