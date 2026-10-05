package uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageCodesDto {

  private String packageCode;
  private String packageDescription;
}
