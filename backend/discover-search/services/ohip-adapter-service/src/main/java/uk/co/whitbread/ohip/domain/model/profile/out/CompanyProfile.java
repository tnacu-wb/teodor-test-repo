package uk.co.whitbread.ohip.domain.model.profile.out;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
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
  String restrictedReason;
  Address address;
}
