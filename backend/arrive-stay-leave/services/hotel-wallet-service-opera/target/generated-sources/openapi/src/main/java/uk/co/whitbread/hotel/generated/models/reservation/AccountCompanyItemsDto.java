package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AccountCompanyItemsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AccountCompanyItemsDto {

  private @Nullable String charges;

  private @Nullable String companyId;

  private @Nullable String companyNumber;

  public AccountCompanyItemsDto charges(String charges) {
    this.charges = charges;
    return this;
  }

  /**
   * Get charges
   * @return charges
   */
  
  @Schema(name = "charges", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("charges")
  public String getCharges() {
    return charges;
  }

  public void setCharges(String charges) {
    this.charges = charges;
  }

  public AccountCompanyItemsDto companyId(String companyId) {
    this.companyId = companyId;
    return this;
  }

  /**
   * Get companyId
   * @return companyId
   */
  
  @Schema(name = "companyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyId")
  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public AccountCompanyItemsDto companyNumber(String companyNumber) {
    this.companyNumber = companyNumber;
    return this;
  }

  /**
   * Get companyNumber
   * @return companyNumber
   */
  
  @Schema(name = "companyNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyNumber")
  public String getCompanyNumber() {
    return companyNumber;
  }

  public void setCompanyNumber(String companyNumber) {
    this.companyNumber = companyNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountCompanyItemsDto accountCompanyItemsDto = (AccountCompanyItemsDto) o;
    return Objects.equals(this.charges, accountCompanyItemsDto.charges) &&
        Objects.equals(this.companyId, accountCompanyItemsDto.companyId) &&
        Objects.equals(this.companyNumber, accountCompanyItemsDto.companyNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(charges, companyId, companyNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountCompanyItemsDto {\n");
    sb.append("    charges: ").append(toIndentedString(charges)).append("\n");
    sb.append("    companyId: ").append(toIndentedString(companyId)).append("\n");
    sb.append("    companyNumber: ").append(toIndentedString(companyNumber)).append("\n");
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

