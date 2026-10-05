package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.PackagesScheduledSelectionDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomsScheduledSelectionsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomsScheduledSelectionsDto {

  @Valid
  private List<@Valid PackagesScheduledSelectionDto> packagesSelection = new ArrayList<>();

  public RoomsScheduledSelectionsDto packagesSelection(List<@Valid PackagesScheduledSelectionDto> packagesSelection) {
    this.packagesSelection = packagesSelection;
    return this;
  }

  public RoomsScheduledSelectionsDto addPackagesSelectionItem(PackagesScheduledSelectionDto packagesSelectionItem) {
    if (this.packagesSelection == null) {
      this.packagesSelection = new ArrayList<>();
    }
    this.packagesSelection.add(packagesSelectionItem);
    return this;
  }

  /**
   * Get packagesSelection
   * @return packagesSelection
   */
  @Valid 
  @Schema(name = "packagesSelection", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packagesSelection")
  public List<@Valid PackagesScheduledSelectionDto> getPackagesSelection() {
    return packagesSelection;
  }

  public void setPackagesSelection(List<@Valid PackagesScheduledSelectionDto> packagesSelection) {
    this.packagesSelection = packagesSelection;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomsScheduledSelectionsDto roomsScheduledSelectionsDto = (RoomsScheduledSelectionsDto) o;
    return Objects.equals(this.packagesSelection, roomsScheduledSelectionsDto.packagesSelection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(packagesSelection);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomsScheduledSelectionsDto {\n");
    sb.append("    packagesSelection: ").append(toIndentedString(packagesSelection)).append("\n");
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

