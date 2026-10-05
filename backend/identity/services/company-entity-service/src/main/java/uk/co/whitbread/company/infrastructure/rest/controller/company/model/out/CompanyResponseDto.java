package uk.co.whitbread.company.infrastructure.rest.controller.company.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponseDto {

  private String name;
  private String telephoneNumber;
  private String profileType;
  private String corpId;
  private String companyId;
  private String language;
  private String arNumber;
  private boolean active;
  private boolean negotiatedRateEnabled;
  private boolean restricted;
  private String restrictedReason;
  private AddressDto address;

}
