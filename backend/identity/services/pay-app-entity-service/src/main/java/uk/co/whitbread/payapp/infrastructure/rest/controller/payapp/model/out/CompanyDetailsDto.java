package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.AddressDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDetailsDto {

  private AddressDto address;
  private String companyName;
  private String creditAgencyReference;

}
