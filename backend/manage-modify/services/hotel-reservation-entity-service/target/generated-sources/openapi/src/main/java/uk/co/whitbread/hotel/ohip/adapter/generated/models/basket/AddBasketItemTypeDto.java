package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

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
 * AddBasketItemTypeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AddBasketItemTypeDto {

  @Valid
  private List<String> confirmationData = new ArrayList<>();

  private @Nullable String type;

  public AddBasketItemTypeDto confirmationData(List<String> confirmationData) {
    this.confirmationData = confirmationData;
    return this;
  }

  public AddBasketItemTypeDto addConfirmationDataItem(String confirmationDataItem) {
    if (this.confirmationData == null) {
      this.confirmationData = new ArrayList<>();
    }
    this.confirmationData.add(confirmationDataItem);
    return this;
  }

  /**
   * Get confirmationData
   * @return confirmationData
   */
  
  @Schema(name = "confirmationData", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("confirmationData")
  public List<String> getConfirmationData() {
    return confirmationData;
  }

  public void setConfirmationData(List<String> confirmationData) {
    this.confirmationData = confirmationData;
  }

  public AddBasketItemTypeDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  
  @Schema(name = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AddBasketItemTypeDto addBasketItemTypeDto = (AddBasketItemTypeDto) o;
    return Objects.equals(this.confirmationData, addBasketItemTypeDto.confirmationData) &&
        Objects.equals(this.type, addBasketItemTypeDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(confirmationData, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddBasketItemTypeDto {\n");
    sb.append("    confirmationData: ").append(toIndentedString(confirmationData)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

