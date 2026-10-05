package uk.co.whitbread.hotel.generated.models.reservation;

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
 * UpdateReservationOverrideReasonsRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateReservationOverrideReasonsRequestDto {

  private String basketReference;

  private String callerName;

  private String hotelId;

  private @Nullable String managerName;

  private String reasonCode;

  private String reasonName;

  public UpdateReservationOverrideReasonsRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateReservationOverrideReasonsRequestDto(String basketReference, String callerName, String hotelId, String reasonCode, String reasonName) {
    this.basketReference = basketReference;
    this.callerName = callerName;
    this.hotelId = hotelId;
    this.reasonCode = reasonCode;
    this.reasonName = reasonName;
  }

  public UpdateReservationOverrideReasonsRequestDto basketReference(String basketReference) {
    this.basketReference = basketReference;
    return this;
  }

  /**
   * Get basketReference
   * @return basketReference
   */
  @NotNull 
  @Schema(name = "basketReference", example = "GBM6919649", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("basketReference")
  public String getBasketReference() {
    return basketReference;
  }

  public void setBasketReference(String basketReference) {
    this.basketReference = basketReference;
  }

  public UpdateReservationOverrideReasonsRequestDto callerName(String callerName) {
    this.callerName = callerName;
    return this;
  }

  /**
   * Get callerName
   * @return callerName
   */
  @NotNull 
  @Schema(name = "callerName", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("callerName")
  public String getCallerName() {
    return callerName;
  }

  public void setCallerName(String callerName) {
    this.callerName = callerName;
  }

  public UpdateReservationOverrideReasonsRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull 
  @Schema(name = "hotelId", example = "LONEUS", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public UpdateReservationOverrideReasonsRequestDto managerName(String managerName) {
    this.managerName = managerName;
    return this;
  }

  /**
   * Get managerName
   * @return managerName
   */
  
  @Schema(name = "managerName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("managerName")
  public String getManagerName() {
    return managerName;
  }

  public void setManagerName(String managerName) {
    this.managerName = managerName;
  }

  public UpdateReservationOverrideReasonsRequestDto reasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
    return this;
  }

  /**
   * Get reasonCode
   * @return reasonCode
   */
  @NotNull 
  @Schema(name = "reasonCode", example = "ILL", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reasonCode")
  public String getReasonCode() {
    return reasonCode;
  }

  public void setReasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
  }

  public UpdateReservationOverrideReasonsRequestDto reasonName(String reasonName) {
    this.reasonName = reasonName;
    return this;
  }

  /**
   * Get reasonName
   * @return reasonName
   */
  @NotNull 
  @Schema(name = "reasonName", example = "Medical Appointments", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reasonName")
  public String getReasonName() {
    return reasonName;
  }

  public void setReasonName(String reasonName) {
    this.reasonName = reasonName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateReservationOverrideReasonsRequestDto updateReservationOverrideReasonsRequestDto = (UpdateReservationOverrideReasonsRequestDto) o;
    return Objects.equals(this.basketReference, updateReservationOverrideReasonsRequestDto.basketReference) &&
        Objects.equals(this.callerName, updateReservationOverrideReasonsRequestDto.callerName) &&
        Objects.equals(this.hotelId, updateReservationOverrideReasonsRequestDto.hotelId) &&
        Objects.equals(this.managerName, updateReservationOverrideReasonsRequestDto.managerName) &&
        Objects.equals(this.reasonCode, updateReservationOverrideReasonsRequestDto.reasonCode) &&
        Objects.equals(this.reasonName, updateReservationOverrideReasonsRequestDto.reasonName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(basketReference, callerName, hotelId, managerName, reasonCode, reasonName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReservationOverrideReasonsRequestDto {\n");
    sb.append("    basketReference: ").append(toIndentedString(basketReference)).append("\n");
    sb.append("    callerName: ").append(toIndentedString(callerName)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    managerName: ").append(toIndentedString(managerName)).append("\n");
    sb.append("    reasonCode: ").append(toIndentedString(reasonCode)).append("\n");
    sb.append("    reasonName: ").append(toIndentedString(reasonName)).append("\n");
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

