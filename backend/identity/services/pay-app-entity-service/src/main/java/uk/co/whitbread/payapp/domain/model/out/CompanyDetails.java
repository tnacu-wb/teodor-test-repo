package uk.co.whitbread.payapp.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDetails {

  private Address address;
  private String companyName;
  private String creditAgencyReference;

}
