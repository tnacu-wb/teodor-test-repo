package uk.co.whitbread.content.entity.service.generated.models.content;

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
 * ContactDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ContactDetailsDto {

  private @Nullable String email;

  private @Nullable String hotelNationalPhone;

  private @Nullable String phone;

  public ContactDetailsDto email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
   */
  
  @Schema(name = "email", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public ContactDetailsDto hotelNationalPhone(String hotelNationalPhone) {
    this.hotelNationalPhone = hotelNationalPhone;
    return this;
  }

  /**
   * Get hotelNationalPhone
   * @return hotelNationalPhone
   */
  
  @Schema(name = "hotelNationalPhone", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelNationalPhone")
  public String getHotelNationalPhone() {
    return hotelNationalPhone;
  }

  public void setHotelNationalPhone(String hotelNationalPhone) {
    this.hotelNationalPhone = hotelNationalPhone;
  }

  public ContactDetailsDto phone(String phone) {
    this.phone = phone;
    return this;
  }

  /**
   * Get phone
   * @return phone
   */
  
  @Schema(name = "phone", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("phone")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContactDetailsDto contactDetailsDto = (ContactDetailsDto) o;
    return Objects.equals(this.email, contactDetailsDto.email) &&
        Objects.equals(this.hotelNationalPhone, contactDetailsDto.hotelNationalPhone) &&
        Objects.equals(this.phone, contactDetailsDto.phone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, hotelNationalPhone, phone);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContactDetailsDto {\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    hotelNationalPhone: ").append(toIndentedString(hotelNationalPhone)).append("\n");
    sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
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

