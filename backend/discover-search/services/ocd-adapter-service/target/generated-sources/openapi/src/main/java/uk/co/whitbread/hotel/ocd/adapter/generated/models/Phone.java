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
 * Phone
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Phone {

  private @Nullable String phoneTechType;

  private @Nullable String phoneLocationType;

  private @Nullable String phoneNumber;

  public Phone phoneTechType(String phoneTechType) {
    this.phoneTechType = phoneTechType;
    return this;
  }

  /**
   * The ype of technology associated with the telephone number.
   * @return phoneTechType
   */
  
  @Schema(name = "phoneTechType", description = "The ype of technology associated with the telephone number.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("phoneTechType")
  public String getPhoneTechType() {
    return phoneTechType;
  }

  public void setPhoneTechType(String phoneTechType) {
    this.phoneTechType = phoneTechType;
  }

  public Phone phoneLocationType(String phoneLocationType) {
    this.phoneLocationType = phoneLocationType;
    return this;
  }

  /**
   * Describes the location of the phone. 
   * @return phoneLocationType
   */
  
  @Schema(name = "phoneLocationType", description = "Describes the location of the phone. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("phoneLocationType")
  public String getPhoneLocationType() {
    return phoneLocationType;
  }

  public void setPhoneLocationType(String phoneLocationType) {
    this.phoneLocationType = phoneLocationType;
  }

  public Phone phoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * The phone number assigned to a specific location.
   * @return phoneNumber
   */
  
  @Schema(name = "phoneNumber", description = "The phone number assigned to a specific location.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("phoneNumber")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Phone phone = (Phone) o;
    return Objects.equals(this.phoneTechType, phone.phoneTechType) &&
        Objects.equals(this.phoneLocationType, phone.phoneLocationType) &&
        Objects.equals(this.phoneNumber, phone.phoneNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(phoneTechType, phoneLocationType, phoneNumber);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Phone {\n");
    sb.append("    phoneTechType: ").append(toIndentedString(phoneTechType)).append("\n");
    sb.append("    phoneLocationType: ").append(toIndentedString(phoneLocationType)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
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

