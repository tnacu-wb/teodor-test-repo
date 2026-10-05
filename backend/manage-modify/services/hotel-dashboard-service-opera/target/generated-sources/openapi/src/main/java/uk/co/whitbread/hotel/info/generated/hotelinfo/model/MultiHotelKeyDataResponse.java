package uk.co.whitbread.hotel.info.generated.hotelinfo.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;
import uk.co.whitbread.hotel.info.generated.hotelinfo.model.HotelKeyData;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MultiHotelKeyDataResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:34.398121+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MultiHotelKeyDataResponse {

  @Valid
  private List<@Valid HotelKeyData> keyData = new ArrayList<>();

  public MultiHotelKeyDataResponse keyData(List<@Valid HotelKeyData> keyData) {
    this.keyData = keyData;
    return this;
  }

  public MultiHotelKeyDataResponse addKeyDataItem(HotelKeyData keyDataItem) {
    if (this.keyData == null) {
      this.keyData = new ArrayList<>();
    }
    this.keyData.add(keyDataItem);
    return this;
  }

  /**
   * Get keyData
   * @return keyData
   */
  @Valid 
  @Schema(name = "keyData", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("keyData")
  public List<@Valid HotelKeyData> getKeyData() {
    return keyData;
  }

  public void setKeyData(List<@Valid HotelKeyData> keyData) {
    this.keyData = keyData;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MultiHotelKeyDataResponse multiHotelKeyDataResponse = (MultiHotelKeyDataResponse) o;
    return Objects.equals(this.keyData, multiHotelKeyDataResponse.keyData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(keyData);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MultiHotelKeyDataResponse {\n");
    sb.append("    keyData: ").append(toIndentedString(keyData)).append("\n");
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

