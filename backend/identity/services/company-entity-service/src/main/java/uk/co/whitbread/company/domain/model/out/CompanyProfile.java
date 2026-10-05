package uk.co.whitbread.company.domain.model.out;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class CompanyProfile {
  String name;
  String telephoneNumber;
  String profileType;
  String corpId;
  String companyId;
  String language;
  String arNumber;
  boolean active;
  boolean restricted;
  boolean negotiatedRateEnabled;
  String restrictedReason;
  Address address;
}
