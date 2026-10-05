package uk.co.whitbread.cdh.domain.model.account.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySearchCriteria {

  @JsonProperty("CompanyName")
  private String companyName;

  @JsonProperty("AddressLine1")
  private String addressLine1;

  @JsonProperty("AddressLine2")
  private String addressLine2;

  @JsonProperty("AddressLine3")
  private String addressLine3;

  @JsonProperty("AddressLine4")
  private String addressLine4;

  @JsonProperty("AddressLine5")
  private String addressLine5;

  @JsonProperty("CountryCode")
  private String countryCode;

  @JsonProperty("PostCode")
  private String postCode;

  @JsonProperty("GlobalCompanyId")
  private Integer globalCompanyId;

  @JsonProperty("PageSize")
  private Integer pageSize;

  @JsonProperty("CompanyType")
  private String companyType;

  @JsonProperty("PageNumber")
  private Integer pageNumber;

  @JsonProperty("SortBy")
  private String sortBy;

  @JsonProperty("SortDirection")
  private String sortDirection;

  @JsonProperty("CellCode")
  private String cellCode;

  private String accessContext;
  private String accessedBy;
}
