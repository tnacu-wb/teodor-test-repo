package uk.co.whitbread.rules.agent.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import uk.co.whitbread.rules.agent.generated.models.OccupancySupplementResponseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MultiOccupancySupplementResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:44.384518+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MultiOccupancySupplementResponseDto {

  @Valid
  private Map<String, BigDecimal> dictionary = new HashMap<>();

  @Valid
  private List<@Valid OccupancySupplementResponseDto> _list = new ArrayList<>();

  public MultiOccupancySupplementResponseDto dictionary(Map<String, BigDecimal> dictionary) {
    this.dictionary = dictionary;
    return this;
  }

  public MultiOccupancySupplementResponseDto putDictionaryItem(String key, BigDecimal dictionaryItem) {
    if (this.dictionary == null) {
      this.dictionary = new HashMap<>();
    }
    this.dictionary.put(key, dictionaryItem);
    return this;
  }

  /**
   * Get dictionary
   * @return dictionary
   */
  @Valid 
  @Schema(name = "dictionary", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dictionary")
  public Map<String, BigDecimal> getDictionary() {
    return dictionary;
  }

  public void setDictionary(Map<String, BigDecimal> dictionary) {
    this.dictionary = dictionary;
  }

  public MultiOccupancySupplementResponseDto _list(List<@Valid OccupancySupplementResponseDto> _list) {
    this._list = _list;
    return this;
  }

  public MultiOccupancySupplementResponseDto addListItem(OccupancySupplementResponseDto _listItem) {
    if (this._list == null) {
      this._list = new ArrayList<>();
    }
    this._list.add(_listItem);
    return this;
  }

  /**
   * Get _list
   * @return _list
   */
  @Valid 
  @Schema(name = "list", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("list")
  public List<@Valid OccupancySupplementResponseDto> getList() {
    return _list;
  }

  public void setList(List<@Valid OccupancySupplementResponseDto> _list) {
    this._list = _list;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MultiOccupancySupplementResponseDto multiOccupancySupplementResponseDto = (MultiOccupancySupplementResponseDto) o;
    return Objects.equals(this.dictionary, multiOccupancySupplementResponseDto.dictionary) &&
        Objects.equals(this._list, multiOccupancySupplementResponseDto._list);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dictionary, _list);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MultiOccupancySupplementResponseDto {\n");
    sb.append("    dictionary: ").append(toIndentedString(dictionary)).append("\n");
    sb.append("    _list: ").append(toIndentedString(_list)).append("\n");
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

