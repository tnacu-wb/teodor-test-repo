package uk.co.whitbread.rules.entity.service.generated.models.agent;

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
 * MaxRoomOccupancyDataDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:32.125466+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MaxRoomOccupancyDataDto {

  @Valid
  private List<String> acceptedRoomTypes = new ArrayList<>();

  private @Nullable Integer adultsNumber;

  private @Nullable Integer childrenNumber;

  public MaxRoomOccupancyDataDto acceptedRoomTypes(List<String> acceptedRoomTypes) {
    this.acceptedRoomTypes = acceptedRoomTypes;
    return this;
  }

  public MaxRoomOccupancyDataDto addAcceptedRoomTypesItem(String acceptedRoomTypesItem) {
    if (this.acceptedRoomTypes == null) {
      this.acceptedRoomTypes = new ArrayList<>();
    }
    this.acceptedRoomTypes.add(acceptedRoomTypesItem);
    return this;
  }

  /**
   * Get acceptedRoomTypes
   * @return acceptedRoomTypes
   */
  
  @Schema(name = "acceptedRoomTypes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("acceptedRoomTypes")
  public List<String> getAcceptedRoomTypes() {
    return acceptedRoomTypes;
  }

  public void setAcceptedRoomTypes(List<String> acceptedRoomTypes) {
    this.acceptedRoomTypes = acceptedRoomTypes;
  }

  public MaxRoomOccupancyDataDto adultsNumber(Integer adultsNumber) {
    this.adultsNumber = adultsNumber;
    return this;
  }

  /**
   * Get adultsNumber
   * @return adultsNumber
   */
  
  @Schema(name = "adultsNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adultsNumber")
  public Integer getAdultsNumber() {
    return adultsNumber;
  }

  public void setAdultsNumber(Integer adultsNumber) {
    this.adultsNumber = adultsNumber;
  }

  public MaxRoomOccupancyDataDto childrenNumber(Integer childrenNumber) {
    this.childrenNumber = childrenNumber;
    return this;
  }

  /**
   * Get childrenNumber
   * @return childrenNumber
   */
  
  @Schema(name = "childrenNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("childrenNumber")
  public Integer getChildrenNumber() {
    return childrenNumber;
  }

  public void setChildrenNumber(Integer childrenNumber) {
    this.childrenNumber = childrenNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MaxRoomOccupancyDataDto maxRoomOccupancyDataDto = (MaxRoomOccupancyDataDto) o;
    return Objects.equals(this.acceptedRoomTypes, maxRoomOccupancyDataDto.acceptedRoomTypes) &&
        Objects.equals(this.adultsNumber, maxRoomOccupancyDataDto.adultsNumber) &&
        Objects.equals(this.childrenNumber, maxRoomOccupancyDataDto.childrenNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(acceptedRoomTypes, adultsNumber, childrenNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MaxRoomOccupancyDataDto {\n");
    sb.append("    acceptedRoomTypes: ").append(toIndentedString(acceptedRoomTypes)).append("\n");
    sb.append("    adultsNumber: ").append(toIndentedString(adultsNumber)).append("\n");
    sb.append("    childrenNumber: ").append(toIndentedString(childrenNumber)).append("\n");
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

