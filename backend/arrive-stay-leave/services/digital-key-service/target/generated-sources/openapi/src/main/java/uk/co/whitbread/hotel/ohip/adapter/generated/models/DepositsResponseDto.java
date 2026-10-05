package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DepositsResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DepositsResponseDto {

  @Valid
  private @Nullable List<@Valid DepositsDto> deposits;

  public DepositsResponseDto deposits(List<@Valid DepositsDto> deposits) {
    this.deposits = deposits;
    return this;
  }

  public DepositsResponseDto addDepositsItem(DepositsDto depositsItem) {
    if (this.deposits == null) {
      this.deposits = new ArrayList<>();
    }
    this.deposits.add(depositsItem);
    return this;
  }

  /**
   * Get deposits
   * @return deposits
   */
  @Valid 
  @Schema(name = "deposits", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deposits")
  public List<@Valid DepositsDto> getDeposits() {
    return deposits;
  }

  public void setDeposits(List<@Valid DepositsDto> deposits) {
    this.deposits = deposits;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DepositsResponseDto depositsResponseDto = (DepositsResponseDto) o;
    return Objects.equals(this.deposits, depositsResponseDto.deposits);
  }

  @Override
  public int hashCode() {
    return Objects.hash(deposits);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DepositsResponseDto {\n");
    sb.append("    deposits: ").append(toIndentedString(deposits)).append("\n");
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

