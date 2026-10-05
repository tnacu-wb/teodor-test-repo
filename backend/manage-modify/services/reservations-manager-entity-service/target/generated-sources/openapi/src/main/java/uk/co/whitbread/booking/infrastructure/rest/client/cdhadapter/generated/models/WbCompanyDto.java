package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * WbCompanyDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class WbCompanyDto {

  @Valid
  private List<String> legalOwnerText = new ArrayList<>();

  private @Nullable String name;

  private @Nullable String regNumber;

  private @Nullable String vatRegNumber;

  public WbCompanyDto legalOwnerText(List<String> legalOwnerText) {
    this.legalOwnerText = legalOwnerText;
    return this;
  }

  public WbCompanyDto addLegalOwnerTextItem(String legalOwnerTextItem) {
    if (this.legalOwnerText == null) {
      this.legalOwnerText = new ArrayList<>();
    }
    this.legalOwnerText.add(legalOwnerTextItem);
    return this;
  }

  /**
   * Get legalOwnerText
   * @return legalOwnerText
   */
  
  @Schema(name = "legalOwnerText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("legalOwnerText")
  public List<String> getLegalOwnerText() {
    return legalOwnerText;
  }

  public void setLegalOwnerText(List<String> legalOwnerText) {
    this.legalOwnerText = legalOwnerText;
  }

  public WbCompanyDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public WbCompanyDto regNumber(String regNumber) {
    this.regNumber = regNumber;
    return this;
  }

  /**
   * Get regNumber
   * @return regNumber
   */
  
  @Schema(name = "regNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("regNumber")
  public String getRegNumber() {
    return regNumber;
  }

  public void setRegNumber(String regNumber) {
    this.regNumber = regNumber;
  }

  public WbCompanyDto vatRegNumber(String vatRegNumber) {
    this.vatRegNumber = vatRegNumber;
    return this;
  }

  /**
   * Get vatRegNumber
   * @return vatRegNumber
   */
  
  @Schema(name = "vatRegNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("vatRegNumber")
  public String getVatRegNumber() {
    return vatRegNumber;
  }

  public void setVatRegNumber(String vatRegNumber) {
    this.vatRegNumber = vatRegNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WbCompanyDto wbCompanyDto = (WbCompanyDto) o;
    return Objects.equals(this.legalOwnerText, wbCompanyDto.legalOwnerText) &&
        Objects.equals(this.name, wbCompanyDto.name) &&
        Objects.equals(this.regNumber, wbCompanyDto.regNumber) &&
        Objects.equals(this.vatRegNumber, wbCompanyDto.vatRegNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(legalOwnerText, name, regNumber, vatRegNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WbCompanyDto {\n");
    sb.append("    legalOwnerText: ").append(toIndentedString(legalOwnerText)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    regNumber: ").append(toIndentedString(regNumber)).append("\n");
    sb.append("    vatRegNumber: ").append(toIndentedString(vatRegNumber)).append("\n");
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

