package uk.co.whitbread.basket.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomOccupancyDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomOccupancyDto {

  private Integer adultsNumber;

  private Integer childrenNumber;

  private @Nullable Boolean cotRequired;

  public RoomOccupancyDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RoomOccupancyDto(Integer adultsNumber, Integer childrenNumber) {
    this.adultsNumber = adultsNumber;
    this.childrenNumber = childrenNumber;
  }

  public RoomOccupancyDto adultsNumber(Integer adultsNumber) {
    this.adultsNumber = adultsNumber;
    return this;
  }

  /**
   * Get adultsNumber
   * @return adultsNumber
   */
  @NotNull 
  @Schema(name = "adultsNumber", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("adultsNumber")
  public Integer getAdultsNumber() {
    return adultsNumber;
  }

  public void setAdultsNumber(Integer adultsNumber) {
    this.adultsNumber = adultsNumber;
  }

  public RoomOccupancyDto childrenNumber(Integer childrenNumber) {
    this.childrenNumber = childrenNumber;
    return this;
  }

  /**
   * Get childrenNumber
   * @return childrenNumber
   */
  @NotNull 
  @Schema(name = "childrenNumber", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("childrenNumber")
  public Integer getChildrenNumber() {
    return childrenNumber;
  }

  public void setChildrenNumber(Integer childrenNumber) {
    this.childrenNumber = childrenNumber;
  }

  public RoomOccupancyDto cotRequired(Boolean cotRequired) {
    this.cotRequired = cotRequired;
    return this;
  }

  /**
   * Get cotRequired
   * @return cotRequired
   */
  
  @Schema(name = "cotRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cotRequired")
  public Boolean getCotRequired() {
    return cotRequired;
  }

  public void setCotRequired(Boolean cotRequired) {
    this.cotRequired = cotRequired;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomOccupancyDto roomOccupancyDto = (RoomOccupancyDto) o;
    return Objects.equals(this.adultsNumber, roomOccupancyDto.adultsNumber) &&
        Objects.equals(this.childrenNumber, roomOccupancyDto.childrenNumber) &&
        Objects.equals(this.cotRequired, roomOccupancyDto.cotRequired);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adultsNumber, childrenNumber, cotRequired);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomOccupancyDto {\n");
    sb.append("    adultsNumber: ").append(toIndentedString(adultsNumber)).append("\n");
    sb.append("    childrenNumber: ").append(toIndentedString(childrenNumber)).append("\n");
    sb.append("    cotRequired: ").append(toIndentedString(cotRequired)).append("\n");
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

