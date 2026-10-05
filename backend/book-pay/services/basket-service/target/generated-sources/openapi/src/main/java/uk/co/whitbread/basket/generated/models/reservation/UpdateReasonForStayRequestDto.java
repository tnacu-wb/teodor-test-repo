package uk.co.whitbread.basket.generated.models.reservation;

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
 * UpdateReasonForStayRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateReasonForStayRequestDto {

  private String basketReference;

  private String hotelId;

  private String reasonForStay;

  public UpdateReasonForStayRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateReasonForStayRequestDto(String basketReference, String hotelId, String reasonForStay) {
    this.basketReference = basketReference;
    this.hotelId = hotelId;
    this.reasonForStay = reasonForStay;
  }

  public UpdateReasonForStayRequestDto basketReference(String basketReference) {
    this.basketReference = basketReference;
    return this;
  }

  /**
   * Get basketReference
   * @return basketReference
   */
  @NotNull 
  @Schema(name = "basketReference", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("basketReference")
  public String getBasketReference() {
    return basketReference;
  }

  public void setBasketReference(String basketReference) {
    this.basketReference = basketReference;
  }

  public UpdateReasonForStayRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull 
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public UpdateReasonForStayRequestDto reasonForStay(String reasonForStay) {
    this.reasonForStay = reasonForStay;
    return this;
  }

  /**
   * Get reasonForStay
   * @return reasonForStay
   */
  @NotNull 
  @Schema(name = "reasonForStay", example = "LEI", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reasonForStay")
  public String getReasonForStay() {
    return reasonForStay;
  }

  public void setReasonForStay(String reasonForStay) {
    this.reasonForStay = reasonForStay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateReasonForStayRequestDto updateReasonForStayRequestDto = (UpdateReasonForStayRequestDto) o;
    return Objects.equals(this.basketReference, updateReasonForStayRequestDto.basketReference) &&
        Objects.equals(this.hotelId, updateReasonForStayRequestDto.hotelId) &&
        Objects.equals(this.reasonForStay, updateReasonForStayRequestDto.reasonForStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(basketReference, hotelId, reasonForStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReasonForStayRequestDto {\n");
    sb.append("    basketReference: ").append(toIndentedString(basketReference)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    reasonForStay: ").append(toIndentedString(reasonForStay)).append("\n");
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

