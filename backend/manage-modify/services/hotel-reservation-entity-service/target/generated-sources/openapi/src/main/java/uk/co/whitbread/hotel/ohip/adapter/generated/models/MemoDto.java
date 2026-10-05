package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MemoIdsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * MemoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MemoDto {

  private @Nullable String createdBy;

  private @Nullable String createdOn;

  private @Nullable String description;

  @Valid
  private List<@Valid MemoIdsDto> ids = new ArrayList<>();

  private @Nullable String memoType;

  private @Nullable String modifiedBy;

  private @Nullable String modifiedOn;

  public MemoDto createdBy(String createdBy) {
    this.createdBy = createdBy;
    return this;
  }

  /**
   * Get createdBy
   * @return createdBy
   */
  
  @Schema(name = "createdBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("createdBy")
  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public MemoDto createdOn(String createdOn) {
    this.createdOn = createdOn;
    return this;
  }

  /**
   * Get createdOn
   * @return createdOn
   */
  
  @Schema(name = "createdOn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("createdOn")
  public String getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public MemoDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public MemoDto ids(List<@Valid MemoIdsDto> ids) {
    this.ids = ids;
    return this;
  }

  public MemoDto addIdsItem(MemoIdsDto idsItem) {
    if (this.ids == null) {
      this.ids = new ArrayList<>();
    }
    this.ids.add(idsItem);
    return this;
  }

  /**
   * Get ids
   * @return ids
   */
  @Valid 
  @Schema(name = "ids", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ids")
  public List<@Valid MemoIdsDto> getIds() {
    return ids;
  }

  public void setIds(List<@Valid MemoIdsDto> ids) {
    this.ids = ids;
  }

  public MemoDto memoType(String memoType) {
    this.memoType = memoType;
    return this;
  }

  /**
   * Get memoType
   * @return memoType
   */
  
  @Schema(name = "memoType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("memoType")
  public String getMemoType() {
    return memoType;
  }

  public void setMemoType(String memoType) {
    this.memoType = memoType;
  }

  public MemoDto modifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
    return this;
  }

  /**
   * Get modifiedBy
   * @return modifiedBy
   */
  
  @Schema(name = "modifiedBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("modifiedBy")
  public String getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public MemoDto modifiedOn(String modifiedOn) {
    this.modifiedOn = modifiedOn;
    return this;
  }

  /**
   * Get modifiedOn
   * @return modifiedOn
   */
  
  @Schema(name = "modifiedOn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("modifiedOn")
  public String getModifiedOn() {
    return modifiedOn;
  }

  public void setModifiedOn(String modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MemoDto memoDto = (MemoDto) o;
    return Objects.equals(this.createdBy, memoDto.createdBy) &&
        Objects.equals(this.createdOn, memoDto.createdOn) &&
        Objects.equals(this.description, memoDto.description) &&
        Objects.equals(this.ids, memoDto.ids) &&
        Objects.equals(this.memoType, memoDto.memoType) &&
        Objects.equals(this.modifiedBy, memoDto.modifiedBy) &&
        Objects.equals(this.modifiedOn, memoDto.modifiedOn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(createdBy, createdOn, description, ids, memoType, modifiedBy, modifiedOn);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MemoDto {\n");
    sb.append("    createdBy: ").append(toIndentedString(createdBy)).append("\n");
    sb.append("    createdOn: ").append(toIndentedString(createdOn)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    ids: ").append(toIndentedString(ids)).append("\n");
    sb.append("    memoType: ").append(toIndentedString(memoType)).append("\n");
    sb.append("    modifiedBy: ").append(toIndentedString(modifiedBy)).append("\n");
    sb.append("    modifiedOn: ").append(toIndentedString(modifiedOn)).append("\n");
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

