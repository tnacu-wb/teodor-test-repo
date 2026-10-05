package uk.co.whitbread.payapp.generated.models.company;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.payapp.generated.models.company.AddressDto;
import uk.co.whitbread.payapp.generated.models.company.EmployeeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CompanyDetailsDto
 */

@JsonTypeName("CompanyDetails")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CompanyDetailsDto {

  private @Nullable String alternateCompanyName;

  private @Nullable AddressDto companyAddress;

  private @Nullable String companyName;

  private @Nullable EmployeeDto mainEmployee;

  private @Nullable Integer numberOfEmployees;

  public CompanyDetailsDto alternateCompanyName(String alternateCompanyName) {
    this.alternateCompanyName = alternateCompanyName;
    return this;
  }

  /**
   * Get alternateCompanyName
   * @return alternateCompanyName
   */
  
  @Schema(name = "alternateCompanyName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("alternateCompanyName")
  public String getAlternateCompanyName() {
    return alternateCompanyName;
  }

  public void setAlternateCompanyName(String alternateCompanyName) {
    this.alternateCompanyName = alternateCompanyName;
  }

  public CompanyDetailsDto companyAddress(AddressDto companyAddress) {
    this.companyAddress = companyAddress;
    return this;
  }

  /**
   * Get companyAddress
   * @return companyAddress
   */
  @Valid 
  @Schema(name = "companyAddress", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyAddress")
  public AddressDto getCompanyAddress() {
    return companyAddress;
  }

  public void setCompanyAddress(AddressDto companyAddress) {
    this.companyAddress = companyAddress;
  }

  public CompanyDetailsDto companyName(String companyName) {
    this.companyName = companyName;
    return this;
  }

  /**
   * Get companyName
   * @return companyName
   */
  
  @Schema(name = "companyName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyName")
  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public CompanyDetailsDto mainEmployee(EmployeeDto mainEmployee) {
    this.mainEmployee = mainEmployee;
    return this;
  }

  /**
   * Get mainEmployee
   * @return mainEmployee
   */
  @Valid 
  @Schema(name = "mainEmployee", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mainEmployee")
  public EmployeeDto getMainEmployee() {
    return mainEmployee;
  }

  public void setMainEmployee(EmployeeDto mainEmployee) {
    this.mainEmployee = mainEmployee;
  }

  public CompanyDetailsDto numberOfEmployees(Integer numberOfEmployees) {
    this.numberOfEmployees = numberOfEmployees;
    return this;
  }

  /**
   * Get numberOfEmployees
   * @return numberOfEmployees
   */
  
  @Schema(name = "numberOfEmployees", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numberOfEmployees")
  public Integer getNumberOfEmployees() {
    return numberOfEmployees;
  }

  public void setNumberOfEmployees(Integer numberOfEmployees) {
    this.numberOfEmployees = numberOfEmployees;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompanyDetailsDto companyDetails = (CompanyDetailsDto) o;
    return Objects.equals(this.alternateCompanyName, companyDetails.alternateCompanyName) &&
        Objects.equals(this.companyAddress, companyDetails.companyAddress) &&
        Objects.equals(this.companyName, companyDetails.companyName) &&
        Objects.equals(this.mainEmployee, companyDetails.mainEmployee) &&
        Objects.equals(this.numberOfEmployees, companyDetails.numberOfEmployees);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alternateCompanyName, companyAddress, companyName, mainEmployee, numberOfEmployees);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompanyDetailsDto {\n");
    sb.append("    alternateCompanyName: ").append(toIndentedString(alternateCompanyName)).append("\n");
    sb.append("    companyAddress: ").append(toIndentedString(companyAddress)).append("\n");
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    mainEmployee: ").append(toIndentedString(mainEmployee)).append("\n");
    sb.append("    numberOfEmployees: ").append(toIndentedString(numberOfEmployees)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

