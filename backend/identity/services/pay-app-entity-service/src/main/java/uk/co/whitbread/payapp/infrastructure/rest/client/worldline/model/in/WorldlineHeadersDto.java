package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorldlineHeadersDto {

  private Integer companyNumber;
  private TrustedPartnerCredentialsDto trustedPartnerCredentialsDto;
  private String cultureCode;
  private String ipAddress;

}
