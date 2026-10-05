package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * CheckInTelephoneDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CheckInTelephoneDto {

  private @Nullable String orderSequence;

  private @Nullable String phoneNumber;

  private @Nullable String phoneTechType;

  private @Nullable String phoneUseType;

  private @Nullable Boolean primaryInd;

  public CheckInTelephoneDto orderSequence(String orderSequence) {
    this.orderSequence = orderSequence;
    return this;
  }

  /**
   * Get orderSequence
   * @return orderSequence
   */
  
  @Schema(name = "orderSequence", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("orderSequence")
  public String getOrderSequence() {
    return orderSequence;
  }

  public void setOrderSequence(String orderSequence) {
    this.orderSequence = orderSequence;
  }

  public CheckInTelephoneDto phoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
   */
  
  @Schema(name = "phoneNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("phoneNumber")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public CheckInTelephoneDto phoneTechType(String phoneTechType) {
    this.phoneTechType = phoneTechType;
    return this;
  }

  /**
   * Get phoneTechType
   * @return phoneTechType
   */
  
  @Schema(name = "phoneTechType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("phoneTechType")
  public String getPhoneTechType() {
    return phoneTechType;
  }

  public void setPhoneTechType(String phoneTechType) {
    this.phoneTechType = phoneTechType;
  }

  public CheckInTelephoneDto phoneUseType(String phoneUseType) {
    this.phoneUseType = phoneUseType;
    return this;
  }

  /**
   * Get phoneUseType
   * @return phoneUseType
   */
  
  @Schema(name = "phoneUseType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("phoneUseType")
  public String getPhoneUseType() {
    return phoneUseType;
  }

  public void setPhoneUseType(String phoneUseType) {
    this.phoneUseType = phoneUseType;
  }

  public CheckInTelephoneDto primaryInd(Boolean primaryInd) {
    this.primaryInd = primaryInd;
    return this;
  }

  /**
   * Get primaryInd
   * @return primaryInd
   */
  
  @Schema(name = "primaryInd", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("primaryInd")
  public Boolean getPrimaryInd() {
    return primaryInd;
  }

  public void setPrimaryInd(Boolean primaryInd) {
    this.primaryInd = primaryInd;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckInTelephoneDto checkInTelephoneDto = (CheckInTelephoneDto) o;
    return Objects.equals(this.orderSequence, checkInTelephoneDto.orderSequence) &&
        Objects.equals(this.phoneNumber, checkInTelephoneDto.phoneNumber) &&
        Objects.equals(this.phoneTechType, checkInTelephoneDto.phoneTechType) &&
        Objects.equals(this.phoneUseType, checkInTelephoneDto.phoneUseType) &&
        Objects.equals(this.primaryInd, checkInTelephoneDto.primaryInd);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orderSequence, phoneNumber, phoneTechType, phoneUseType, primaryInd);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckInTelephoneDto {\n");
    sb.append("    orderSequence: ").append(toIndentedString(orderSequence)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    phoneTechType: ").append(toIndentedString(phoneTechType)).append("\n");
    sb.append("    phoneUseType: ").append(toIndentedString(phoneUseType)).append("\n");
    sb.append("    primaryInd: ").append(toIndentedString(primaryInd)).append("\n");
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

