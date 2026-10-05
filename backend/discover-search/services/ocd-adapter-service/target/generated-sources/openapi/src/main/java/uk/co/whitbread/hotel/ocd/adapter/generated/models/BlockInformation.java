package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Block related information
 */

@Schema(name = "BlockInformation", description = "Block related information")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BlockInformation {

  private @Nullable String blockCode;

  private @Nullable String blockId;

  private @Nullable String blockName;

  public BlockInformation blockCode(String blockCode) {
    this.blockCode = blockCode;
    return this;
  }

  /**
   * A code to retrieve price and availability from OPERA Cloud business block
   * @return blockCode
   */
  @Size(min = 1, max = 20) 
  @Schema(name = "blockCode", description = "A code to retrieve price and availability from OPERA Cloud business block", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("blockCode")
  public String getBlockCode() {
    return blockCode;
  }

  public void setBlockCode(String blockCode) {
    this.blockCode = blockCode;
  }

  public BlockInformation blockId(String blockId) {
    this.blockId = blockId;
    return this;
  }

  /**
   * Unique OPERA Block ID which is a primary identification of a Block in OPERA
   * @return blockId
   */
  @Size(min = 1, max = 40) 
  @Schema(name = "blockId", description = "Unique OPERA Block ID which is a primary identification of a Block in OPERA", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("blockId")
  public String getBlockId() {
    return blockId;
  }

  public void setBlockId(String blockId) {
    this.blockId = blockId;
  }

  public BlockInformation blockName(String blockName) {
    this.blockName = blockName;
    return this;
  }

  /**
   * Name of block
   * @return blockName
   */
  @Size(min = 1, max = 2000) 
  @Schema(name = "blockName", description = "Name of block", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("blockName")
  public String getBlockName() {
    return blockName;
  }

  public void setBlockName(String blockName) {
    this.blockName = blockName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlockInformation blockInformation = (BlockInformation) o;
    return Objects.equals(this.blockCode, blockInformation.blockCode) &&
        Objects.equals(this.blockId, blockInformation.blockId) &&
        Objects.equals(this.blockName, blockInformation.blockName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(blockCode, blockId, blockName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BlockInformation {\n");
    sb.append("    blockCode: ").append(toIndentedString(blockCode)).append("\n");
    sb.append("    blockId: ").append(toIndentedString(blockId)).append("\n");
    sb.append("    blockName: ").append(toIndentedString(blockName)).append("\n");
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

