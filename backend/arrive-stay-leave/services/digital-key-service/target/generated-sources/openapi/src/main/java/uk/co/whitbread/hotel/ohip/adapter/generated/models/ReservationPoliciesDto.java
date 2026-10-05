package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositPoliciesDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationPoliciesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationPoliciesDto {

  @Valid
  private @Nullable List<@Valid DepositPoliciesDto> depositPolicies;

  public ReservationPoliciesDto depositPolicies(List<@Valid DepositPoliciesDto> depositPolicies) {
    this.depositPolicies = depositPolicies;
    return this;
  }

  public ReservationPoliciesDto addDepositPoliciesItem(DepositPoliciesDto depositPoliciesItem) {
    if (this.depositPolicies == null) {
      this.depositPolicies = new ArrayList<>();
    }
    this.depositPolicies.add(depositPoliciesItem);
    return this;
  }

  /**
   * Get depositPolicies
   * @return depositPolicies
   */
  @Valid 
  @Schema(name = "depositPolicies", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("depositPolicies")
  public List<@Valid DepositPoliciesDto> getDepositPolicies() {
    return depositPolicies;
  }

  public void setDepositPolicies(List<@Valid DepositPoliciesDto> depositPolicies) {
    this.depositPolicies = depositPolicies;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationPoliciesDto reservationPoliciesDto = (ReservationPoliciesDto) o;
    return Objects.equals(this.depositPolicies, reservationPoliciesDto.depositPolicies);
  }

  @Override
  public int hashCode() {
    return Objects.hash(depositPolicies);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationPoliciesDto {\n");
    sb.append("    depositPolicies: ").append(toIndentedString(depositPolicies)).append("\n");
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

