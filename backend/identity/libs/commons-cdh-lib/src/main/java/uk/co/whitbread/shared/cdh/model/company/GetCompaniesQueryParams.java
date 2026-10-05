package uk.co.whitbread.shared.cdh.model.company;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetCompaniesQueryParams {

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
  private String globalCompanyId;
  @JsonProperty("PageToken")
  private String pageToken;
  @JsonProperty("PageSize")
  private int pageSize;
}
