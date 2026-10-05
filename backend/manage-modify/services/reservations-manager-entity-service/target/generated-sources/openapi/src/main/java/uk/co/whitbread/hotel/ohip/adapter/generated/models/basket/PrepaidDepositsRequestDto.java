package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PrepaidDepositDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PrepaidDepositsRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PrepaidDepositsRequestDto {

  @Valid
  private List<@Valid PrepaidDepositDto> prepaidDeposits = new ArrayList<>();

  public PrepaidDepositsRequestDto prepaidDeposits(List<@Valid PrepaidDepositDto> prepaidDeposits) {
    this.prepaidDeposits = prepaidDeposits;
    return this;
  }

  public PrepaidDepositsRequestDto addPrepaidDepositsItem(PrepaidDepositDto prepaidDepositsItem) {
    if (this.prepaidDeposits == null) {
      this.prepaidDeposits = new ArrayList<>();
    }
    this.prepaidDeposits.add(prepaidDepositsItem);
    return this;
  }

  /**
   * Get prepaidDeposits
   * @return prepaidDeposits
   */
  @Valid 
  @Schema(name = "prepaidDeposits", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("prepaidDeposits")
  public List<@Valid PrepaidDepositDto> getPrepaidDeposits() {
    return prepaidDeposits;
  }

  public void setPrepaidDeposits(List<@Valid PrepaidDepositDto> prepaidDeposits) {
    this.prepaidDeposits = prepaidDeposits;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PrepaidDepositsRequestDto prepaidDepositsRequestDto = (PrepaidDepositsRequestDto) o;
    return Objects.equals(this.prepaidDeposits, prepaidDepositsRequestDto.prepaidDeposits);
  }

  @Override
  public int hashCode() {
    return Objects.hash(prepaidDeposits);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PrepaidDepositsRequestDto {\n");
    sb.append("    prepaidDeposits: ").append(toIndentedString(prepaidDeposits)).append("\n");
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

