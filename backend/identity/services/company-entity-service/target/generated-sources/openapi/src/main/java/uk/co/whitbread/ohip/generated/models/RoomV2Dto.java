package uk.co.whitbread.ohip.generated.models;

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
 * RoomV2Dto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomV2Dto {

  private @Nullable Integer adults;

  private @Nullable Integer children;

  private @Nullable Integer numberOfRooms;

  private String tag;

  public RoomV2Dto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RoomV2Dto(String tag) {
    this.tag = tag;
  }

  public RoomV2Dto adults(Integer adults) {
    this.adults = adults;
    return this;
  }

  /**
   * Get adults
   * @return adults
   */
  
  @Schema(name = "adults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adults")
  public Integer getAdults() {
    return adults;
  }

  public void setAdults(Integer adults) {
    this.adults = adults;
  }

  public RoomV2Dto children(Integer children) {
    this.children = children;
    return this;
  }

  /**
   * Get children
   * @return children
   */
  
  @Schema(name = "children", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("children")
  public Integer getChildren() {
    return children;
  }

  public void setChildren(Integer children) {
    this.children = children;
  }

  public RoomV2Dto numberOfRooms(Integer numberOfRooms) {
    this.numberOfRooms = numberOfRooms;
    return this;
  }

  /**
   * Get numberOfRooms
   * @return numberOfRooms
   */
  
  @Schema(name = "numberOfRooms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numberOfRooms")
  public Integer getNumberOfRooms() {
    return numberOfRooms;
  }

  public void setNumberOfRooms(Integer numberOfRooms) {
    this.numberOfRooms = numberOfRooms;
  }

  public RoomV2Dto tag(String tag) {
    this.tag = tag;
    return this;
  }

  /**
   * Get tag
   * @return tag
   */
  @NotNull 
  @Schema(name = "tag", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("tag")
  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomV2Dto roomV2Dto = (RoomV2Dto) o;
    return Objects.equals(this.adults, roomV2Dto.adults) &&
        Objects.equals(this.children, roomV2Dto.children) &&
        Objects.equals(this.numberOfRooms, roomV2Dto.numberOfRooms) &&
        Objects.equals(this.tag, roomV2Dto.tag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adults, children, numberOfRooms, tag);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomV2Dto {\n");
    sb.append("    adults: ").append(toIndentedString(adults)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
    sb.append("    numberOfRooms: ").append(toIndentedString(numberOfRooms)).append("\n");
    sb.append("    tag: ").append(toIndentedString(tag)).append("\n");
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

