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
 * PrepaidDepositsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PrepaidDepositsDto {

  @Valid
  private List<@Valid PrepaidDepositDto> prepaidDepositsDto = new ArrayList<>();

  public PrepaidDepositsDto prepaidDepositsDto(List<@Valid PrepaidDepositDto> prepaidDepositsDto) {
    this.prepaidDepositsDto = prepaidDepositsDto;
    return this;
  }

  public PrepaidDepositsDto addPrepaidDepositsDtoItem(PrepaidDepositDto prepaidDepositsDtoItem) {
    if (this.prepaidDepositsDto == null) {
      this.prepaidDepositsDto = new ArrayList<>();
    }
    this.prepaidDepositsDto.add(prepaidDepositsDtoItem);
    return this;
  }

  /**
   * Get prepaidDepositsDto
   * @return prepaidDepositsDto
   */
  @Valid 
  @Schema(name = "prepaidDepositsDto", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("prepaidDepositsDto")
  public List<@Valid PrepaidDepositDto> getPrepaidDepositsDto() {
    return prepaidDepositsDto;
  }

  public void setPrepaidDepositsDto(List<@Valid PrepaidDepositDto> prepaidDepositsDto) {
    this.prepaidDepositsDto = prepaidDepositsDto;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PrepaidDepositsDto prepaidDepositsDto = (PrepaidDepositsDto) o;
    return Objects.equals(this.prepaidDepositsDto, prepaidDepositsDto.prepaidDepositsDto);
  }

  @Override
  public int hashCode() {
    return Objects.hash(prepaidDepositsDto);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PrepaidDepositsDto {\n");
    sb.append("    prepaidDepositsDto: ").append(toIndentedString(prepaidDepositsDto)).append("\n");
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

