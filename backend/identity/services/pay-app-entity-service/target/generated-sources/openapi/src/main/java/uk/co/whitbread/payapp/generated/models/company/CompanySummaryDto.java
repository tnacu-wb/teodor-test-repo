package uk.co.whitbread.payapp.generated.models.company;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.payapp.generated.models.company.AddressDto;
import uk.co.whitbread.payapp.generated.models.company.MainContactDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Update company request
 */

@Schema(name = "CompanySummary", description = "Update company request")
@JsonTypeName("CompanySummary")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CompanySummaryDto {

  private String alternateCompanyName;

  private AddressDto companyAddress;

  private String companyName;

  private MainContactDto mainContact;

  public CompanySummaryDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CompanySummaryDto(String alternateCompanyName, AddressDto companyAddress, String companyName, MainContactDto mainContact) {
    this.alternateCompanyName = alternateCompanyName;
    this.companyAddress = companyAddress;
    this.companyName = companyName;
    this.mainContact = mainContact;
  }

  public CompanySummaryDto alternateCompanyName(String alternateCompanyName) {
    this.alternateCompanyName = alternateCompanyName;
    return this;
  }

  /**
   * Get alternateCompanyName
   * @return alternateCompanyName
   */
  @NotNull 
  @Schema(name = "alternateCompanyName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("alternateCompanyName")
  public String getAlternateCompanyName() {
    return alternateCompanyName;
  }

  public void setAlternateCompanyName(String alternateCompanyName) {
    this.alternateCompanyName = alternateCompanyName;
  }

  public CompanySummaryDto companyAddress(AddressDto companyAddress) {
    this.companyAddress = companyAddress;
    return this;
  }

  /**
   * Get companyAddress
   * @return companyAddress
   */
  @NotNull @Valid 
  @Schema(name = "companyAddress", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("companyAddress")
  public AddressDto getCompanyAddress() {
    return companyAddress;
  }

  public void setCompanyAddress(AddressDto companyAddress) {
    this.companyAddress = companyAddress;
  }

  public CompanySummaryDto companyName(String companyName) {
    this.companyName = companyName;
    return this;
  }

  /**
   * Get companyName
   * @return companyName
   */
  @NotNull 
  @Schema(name = "companyName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("companyName")
  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public CompanySummaryDto mainContact(MainContactDto mainContact) {
    this.mainContact = mainContact;
    return this;
  }

  /**
   * Get mainContact
   * @return mainContact
   */
  @NotNull @Valid 
  @Schema(name = "mainContact", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("mainContact")
  public MainContactDto getMainContact() {
    return mainContact;
  }

  public void setMainContact(MainContactDto mainContact) {
    this.mainContact = mainContact;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompanySummaryDto companySummary = (CompanySummaryDto) o;
    return Objects.equals(this.alternateCompanyName, companySummary.alternateCompanyName) &&
        Objects.equals(this.companyAddress, companySummary.companyAddress) &&
        Objects.equals(this.companyName, companySummary.companyName) &&
        Objects.equals(this.mainContact, companySummary.mainContact);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alternateCompanyName, companyAddress, companyName, mainContact);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompanySummaryDto {\n");
    sb.append("    alternateCompanyName: ").append(toIndentedString(alternateCompanyName)).append("\n");
    sb.append("    companyAddress: ").append(toIndentedString(companyAddress)).append("\n");
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    mainContact: ").append(toIndentedString(mainContact)).append("\n");
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

