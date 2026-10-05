package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * ReservationPaymentMethodDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationPaymentMethodDto {

  private @Nullable String description;

  private @Nullable Integer folioView;

  private @Nullable String paymentMethod;

  public ReservationPaymentMethodDto description(String description) {
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

  public ReservationPaymentMethodDto folioView(Integer folioView) {
    this.folioView = folioView;
    return this;
  }

  /**
   * Get folioView
   * @return folioView
   */
  
  @Schema(name = "folioView", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("folioView")
  public Integer getFolioView() {
    return folioView;
  }

  public void setFolioView(Integer folioView) {
    this.folioView = folioView;
  }

  public ReservationPaymentMethodDto paymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
    return this;
  }

  /**
   * Get paymentMethod
   * @return paymentMethod
   */
  
  @Schema(name = "paymentMethod", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentMethod")
  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationPaymentMethodDto reservationPaymentMethodDto = (ReservationPaymentMethodDto) o;
    return Objects.equals(this.description, reservationPaymentMethodDto.description) &&
        Objects.equals(this.folioView, reservationPaymentMethodDto.folioView) &&
        Objects.equals(this.paymentMethod, reservationPaymentMethodDto.paymentMethod);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, folioView, paymentMethod);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationPaymentMethodDto {\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    folioView: ").append(toIndentedString(folioView)).append("\n");
    sb.append("    paymentMethod: ").append(toIndentedString(paymentMethod)).append("\n");
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

