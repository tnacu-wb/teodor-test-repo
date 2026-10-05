package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * PackagesScheduledSelectionDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PackagesScheduledSelectionDto {

  private @Nullable String id;

  private @Nullable Double price;

  private @Nullable Integer noSelections;

  @Valid
  private List<String> scheduledDates = new ArrayList<>();

  public PackagesScheduledSelectionDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public PackagesScheduledSelectionDto price(Double price) {
    this.price = price;
    return this;
  }

  /**
   * Get price
   * @return price
   */
  
  @Schema(name = "price", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public PackagesScheduledSelectionDto noSelections(Integer noSelections) {
    this.noSelections = noSelections;
    return this;
  }

  /**
   * Get noSelections
   * @return noSelections
   */
  
  @Schema(name = "noSelections", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("noSelections")
  public Integer getNoSelections() {
    return noSelections;
  }

  public void setNoSelections(Integer noSelections) {
    this.noSelections = noSelections;
  }

  public PackagesScheduledSelectionDto scheduledDates(List<String> scheduledDates) {
    this.scheduledDates = scheduledDates;
    return this;
  }

  public PackagesScheduledSelectionDto addScheduledDatesItem(String scheduledDatesItem) {
    if (this.scheduledDates == null) {
      this.scheduledDates = new ArrayList<>();
    }
    this.scheduledDates.add(scheduledDatesItem);
    return this;
  }

  /**
   * Get scheduledDates
   * @return scheduledDates
   */
  
  @Schema(name = "scheduledDates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("scheduledDates")
  public List<String> getScheduledDates() {
    return scheduledDates;
  }

  public void setScheduledDates(List<String> scheduledDates) {
    this.scheduledDates = scheduledDates;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackagesScheduledSelectionDto packagesScheduledSelectionDto = (PackagesScheduledSelectionDto) o;
    return Objects.equals(this.id, packagesScheduledSelectionDto.id) &&
        Objects.equals(this.price, packagesScheduledSelectionDto.price) &&
        Objects.equals(this.noSelections, packagesScheduledSelectionDto.noSelections) &&
        Objects.equals(this.scheduledDates, packagesScheduledSelectionDto.scheduledDates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, price, noSelections, scheduledDates);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PackagesScheduledSelectionDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
    sb.append("    noSelections: ").append(toIndentedString(noSelections)).append("\n");
    sb.append("    scheduledDates: ").append(toIndentedString(scheduledDates)).append("\n");
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

