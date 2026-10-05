package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RestrictionSetsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RestrictionsByDateRangeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RestrictionsByDateRangeDto {

  private @Nullable Boolean hasMore;

  private @Nullable String hotelId;

  @Valid
  private @Nullable List<@Valid RestrictionSetsDto> restrictionSets;

  public RestrictionsByDateRangeDto hasMore(Boolean hasMore) {
    this.hasMore = hasMore;
    return this;
  }

  /**
   * Get hasMore
   * @return hasMore
   */
  
  @Schema(name = "hasMore", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hasMore")
  public Boolean getHasMore() {
    return hasMore;
  }

  public void setHasMore(Boolean hasMore) {
    this.hasMore = hasMore;
  }

  public RestrictionsByDateRangeDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public RestrictionsByDateRangeDto restrictionSets(List<@Valid RestrictionSetsDto> restrictionSets) {
    this.restrictionSets = restrictionSets;
    return this;
  }

  public RestrictionsByDateRangeDto addRestrictionSetsItem(RestrictionSetsDto restrictionSetsItem) {
    if (this.restrictionSets == null) {
      this.restrictionSets = new ArrayList<>();
    }
    this.restrictionSets.add(restrictionSetsItem);
    return this;
  }

  /**
   * Get restrictionSets
   * @return restrictionSets
   */
  @Valid 
  @Schema(name = "restrictionSets", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restrictionSets")
  public List<@Valid RestrictionSetsDto> getRestrictionSets() {
    return restrictionSets;
  }

  public void setRestrictionSets(List<@Valid RestrictionSetsDto> restrictionSets) {
    this.restrictionSets = restrictionSets;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RestrictionsByDateRangeDto restrictionsByDateRangeDto = (RestrictionsByDateRangeDto) o;
    return Objects.equals(this.hasMore, restrictionsByDateRangeDto.hasMore) &&
        Objects.equals(this.hotelId, restrictionsByDateRangeDto.hotelId) &&
        Objects.equals(this.restrictionSets, restrictionsByDateRangeDto.restrictionSets);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hasMore, hotelId, restrictionSets);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RestrictionsByDateRangeDto {\n");
    sb.append("    hasMore: ").append(toIndentedString(hasMore)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    restrictionSets: ").append(toIndentedString(restrictionSets)).append("\n");
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

