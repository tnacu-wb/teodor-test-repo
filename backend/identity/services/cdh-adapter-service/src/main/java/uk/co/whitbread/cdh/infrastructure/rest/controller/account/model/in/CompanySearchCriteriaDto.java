package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySearchCriteriaDto {
  private String companyName;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String addressLine5;
  private String countryCode;
  private String postCode;
  private Integer globalCompanyId;
  private Integer pageSize;
  private String companyType;
  private Integer pageNumber;
  private String sortBy;
  private String sortDirection;
  private String cellCode;
  @NotEmpty
  private String accessContext;
  @NotEmpty
  private String accessedBy;
}
