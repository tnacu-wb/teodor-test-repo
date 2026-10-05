package uk.co.whitbread.hotel.account.service.generated.models;

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
 * MarketingPreference
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:35.247020+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MarketingPreference {

  @Valid
  private List<String> regionPreferences = new ArrayList<>();

  private @Nullable Boolean wantRestaurantNews;

  public MarketingPreference regionPreferences(List<String> regionPreferences) {
    this.regionPreferences = regionPreferences;
    return this;
  }

  public MarketingPreference addRegionPreferencesItem(String regionPreferencesItem) {
    if (this.regionPreferences == null) {
      this.regionPreferences = new ArrayList<>();
    }
    this.regionPreferences.add(regionPreferencesItem);
    return this;
  }

  /**
   * Get regionPreferences
   * @return regionPreferences
   */
  
  @Schema(name = "regionPreferences", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("regionPreferences")
  public List<String> getRegionPreferences() {
    return regionPreferences;
  }

  public void setRegionPreferences(List<String> regionPreferences) {
    this.regionPreferences = regionPreferences;
  }

  public MarketingPreference wantRestaurantNews(Boolean wantRestaurantNews) {
    this.wantRestaurantNews = wantRestaurantNews;
    return this;
  }

  /**
   * Get wantRestaurantNews
   * @return wantRestaurantNews
   */
  
  @Schema(name = "wantRestaurantNews", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("wantRestaurantNews")
  public Boolean getWantRestaurantNews() {
    return wantRestaurantNews;
  }

  public void setWantRestaurantNews(Boolean wantRestaurantNews) {
    this.wantRestaurantNews = wantRestaurantNews;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MarketingPreference marketingPreference = (MarketingPreference) o;
    return Objects.equals(this.regionPreferences, marketingPreference.regionPreferences) &&
        Objects.equals(this.wantRestaurantNews, marketingPreference.wantRestaurantNews);
  }

  @Override
  public int hashCode() {
    return Objects.hash(regionPreferences, wantRestaurantNews);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MarketingPreference {\n");
    sb.append("    regionPreferences: ").append(toIndentedString(regionPreferences)).append("\n");
    sb.append("    wantRestaurantNews: ").append(toIndentedString(wantRestaurantNews)).append("\n");
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

