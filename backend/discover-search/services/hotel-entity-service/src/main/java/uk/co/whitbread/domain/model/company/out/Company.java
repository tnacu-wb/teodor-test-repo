package uk.co.whitbread.domain.model.company.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {

  private Boolean active;
  private Address address;
  private String arNumber;
  private String companyId;
  private String corpId;
  private String language;
  private String name;
  private Boolean negotiatedRateEnabled;
  private String profileType;
  private Boolean restricted;
  private String restrictedReason;
  private String telephoneNumber;
}

