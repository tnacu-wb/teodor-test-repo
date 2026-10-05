package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFolioDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DepositFoliosResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DepositFoliosResponseDto {

  @Valid
  private @Nullable List<@Valid DepositFolioDto> depositFolios;

  public DepositFoliosResponseDto depositFolios(List<@Valid DepositFolioDto> depositFolios) {
    this.depositFolios = depositFolios;
    return this;
  }

  public DepositFoliosResponseDto addDepositFoliosItem(DepositFolioDto depositFoliosItem) {
    if (this.depositFolios == null) {
      this.depositFolios = new ArrayList<>();
    }
    this.depositFolios.add(depositFoliosItem);
    return this;
  }

  /**
   * Get depositFolios
   * @return depositFolios
   */
  @Valid 
  @Schema(name = "depositFolios", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("depositFolios")
  public List<@Valid DepositFolioDto> getDepositFolios() {
    return depositFolios;
  }

  public void setDepositFolios(List<@Valid DepositFolioDto> depositFolios) {
    this.depositFolios = depositFolios;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DepositFoliosResponseDto depositFoliosResponseDto = (DepositFoliosResponseDto) o;
    return Objects.equals(this.depositFolios, depositFoliosResponseDto.depositFolios);
  }

  @Override
  public int hashCode() {
    return Objects.hash(depositFolios);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DepositFoliosResponseDto {\n");
    sb.append("    depositFolios: ").append(toIndentedString(depositFolios)).append("\n");
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

