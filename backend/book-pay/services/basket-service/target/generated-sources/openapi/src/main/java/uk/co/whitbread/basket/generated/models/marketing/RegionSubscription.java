package uk.co.whitbread.basket.generated.models.marketing;

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
 * RegionSubscription
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RegionSubscription {

  private String regionId;

  private Boolean subscribed;

  public RegionSubscription() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RegionSubscription(String regionId, Boolean subscribed) {
    this.regionId = regionId;
    this.subscribed = subscribed;
  }

  public RegionSubscription regionId(String regionId) {
    this.regionId = regionId;
    return this;
  }

  /**
   * Get regionId
   * @return regionId
   */
  @NotNull 
  @Schema(name = "regionId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("regionId")
  public String getRegionId() {
    return regionId;
  }

  public void setRegionId(String regionId) {
    this.regionId = regionId;
  }

  public RegionSubscription subscribed(Boolean subscribed) {
    this.subscribed = subscribed;
    return this;
  }

  /**
   * Get subscribed
   * @return subscribed
   */
  @NotNull 
  @Schema(name = "subscribed", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("subscribed")
  public Boolean getSubscribed() {
    return subscribed;
  }

  public void setSubscribed(Boolean subscribed) {
    this.subscribed = subscribed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegionSubscription regionSubscription = (RegionSubscription) o;
    return Objects.equals(this.regionId, regionSubscription.regionId) &&
        Objects.equals(this.subscribed, regionSubscription.subscribed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(regionId, subscribed);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegionSubscription {\n");
    sb.append("    regionId: ").append(toIndentedString(regionId)).append("\n");
    sb.append("    subscribed: ").append(toIndentedString(subscribed)).append("\n");
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

