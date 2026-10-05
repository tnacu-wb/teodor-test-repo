package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MemoDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * MemosResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MemosResponseDto {

  @Valid
  private List<@Valid MemoDto> memos = new ArrayList<>();

  public MemosResponseDto memos(List<@Valid MemoDto> memos) {
    this.memos = memos;
    return this;
  }

  public MemosResponseDto addMemosItem(MemoDto memosItem) {
    if (this.memos == null) {
      this.memos = new ArrayList<>();
    }
    this.memos.add(memosItem);
    return this;
  }

  /**
   * Get memos
   * @return memos
   */
  @Valid 
  @Schema(name = "memos", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("memos")
  public List<@Valid MemoDto> getMemos() {
    return memos;
  }

  public void setMemos(List<@Valid MemoDto> memos) {
    this.memos = memos;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MemosResponseDto memosResponseDto = (MemosResponseDto) o;
    return Objects.equals(this.memos, memosResponseDto.memos);
  }

  @Override
  public int hashCode() {
    return Objects.hash(memos);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MemosResponseDto {\n");
    sb.append("    memos: ").append(toIndentedString(memos)).append("\n");
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

