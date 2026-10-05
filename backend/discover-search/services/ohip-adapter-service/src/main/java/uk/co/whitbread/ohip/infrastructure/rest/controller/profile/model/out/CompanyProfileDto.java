package uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyProfileDto {
  private String name;
  private String telephoneNumber;
  private String profileType;
  private String corpId;
  private String companyId;
  private String language;
  private String arNumber;
  private boolean active;
  private boolean restricted;
  private String restrictedReason;
  private AddressDto address;
}
