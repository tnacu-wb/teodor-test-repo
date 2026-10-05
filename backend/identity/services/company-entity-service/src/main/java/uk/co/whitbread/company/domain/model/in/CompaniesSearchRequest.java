package uk.co.whitbread.company.domain.model.in;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.company.domain.model.validation.DomainValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class CompaniesSearchRequest extends DomainValidator<CompaniesSearchRequest> {
  @NotEmpty
  String companyName;
  @Min(1)
  int pageSize;
  @Min(1)
  int pageNumber;
  boolean negotiatedRateCompanies;
  String accessContext;
  String accessedBy;

  public CompaniesSearchRequest(String companyName, int pageSize, int pageNumber,
      boolean negotiatedRateCompanies, String accessContext, String accessedBy) {
    this.companyName = companyName;
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
    this.negotiatedRateCompanies = negotiatedRateCompanies;
    this.accessContext =  accessContext;
    this.accessedBy = accessedBy;
    this.validateSelf();
  }
}
