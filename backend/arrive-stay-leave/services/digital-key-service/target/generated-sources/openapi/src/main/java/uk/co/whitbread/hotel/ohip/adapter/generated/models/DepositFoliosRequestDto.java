package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFolioRequestDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DepositFoliosRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DepositFoliosRequestDto {

  @Valid
  private List<@Valid DepositFolioRequestDto> depositFolios;

  public DepositFoliosRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DepositFoliosRequestDto(List<@Valid DepositFolioRequestDto> depositFolios) {
    this.depositFolios = depositFolios;
  }

  public DepositFoliosRequestDto depositFolios(List<@Valid DepositFolioRequestDto> depositFolios) {
    this.depositFolios = depositFolios;
    return this;
  }

  public DepositFoliosRequestDto addDepositFoliosItem(DepositFolioRequestDto depositFoliosItem) {
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
  @NotNull @Valid @Size(min = 1, max = 2147483647) 
  @Schema(name = "depositFolios", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("depositFolios")
  public List<@Valid DepositFolioRequestDto> getDepositFolios() {
    return depositFolios;
  }

  public void setDepositFolios(List<@Valid DepositFolioRequestDto> depositFolios) {
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
    DepositFoliosRequestDto depositFoliosRequestDto = (DepositFoliosRequestDto) o;
    return Objects.equals(this.depositFolios, depositFoliosRequestDto.depositFolios);
  }

  @Override
  public int hashCode() {
    return Objects.hash(depositFolios);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DepositFoliosRequestDto {\n");
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

