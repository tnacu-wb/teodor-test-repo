package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class AppCompanyDetailsRequest extends DomainValidator<AppCompanyDetailsRequest> {

  @NotBlank
  private String companyRegistrationNumber;

  private Scheme scheme;

  public AppCompanyDetailsRequest(String companyRegistrationNumber, Scheme scheme) {
    this.companyRegistrationNumber = companyRegistrationNumber;
    this.scheme = scheme;
    this.validateSelf();
  }


}
