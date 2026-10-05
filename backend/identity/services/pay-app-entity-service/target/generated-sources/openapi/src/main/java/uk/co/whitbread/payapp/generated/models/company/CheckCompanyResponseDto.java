package uk.co.whitbread.payapp.generated.models.company;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CheckCompanyResponseDto
 */

@JsonTypeName("CheckCompanyResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CheckCompanyResponseDto {

  private @Nullable Boolean existingCompany;

  public CheckCompanyResponseDto existingCompany(Boolean existingCompany) {
    this.existingCompany = existingCompany;
    return this;
  }

  /**
   * Get existingCompany
   * @return existingCompany
   */
  
  @Schema(name = "existingCompany", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("existingCompany")
  public Boolean getExistingCompany() {
    return existingCompany;
  }

  public void setExistingCompany(Boolean existingCompany) {
    this.existingCompany = existingCompany;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckCompanyResponseDto checkCompanyResponse = (CheckCompanyResponseDto) o;
    return Objects.equals(this.existingCompany, checkCompanyResponse.existingCompany);
  }

  @Override
  public int hashCode() {
    return Objects.hash(existingCompany);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckCompanyResponseDto {\n");
    sb.append("    existingCompany: ").append(toIndentedString(existingCompany)).append("\n");
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

