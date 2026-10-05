package uk.co.whitbread.hotel.reservation.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.reservation.generated.models.BookerDetailsCnpDto;
import uk.co.whitbread.hotel.reservation.generated.models.BusinessAccountCnpDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateCnpReservationRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:09:52.163805+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateCnpReservationRequestDto {

  private @Nullable BookerDetailsCnpDto booker;

  private BusinessAccountCnpDto businessAccount;

  private @Nullable String language;

  public UpdateCnpReservationRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateCnpReservationRequestDto(BusinessAccountCnpDto businessAccount) {
    this.businessAccount = businessAccount;
  }

  public UpdateCnpReservationRequestDto booker(BookerDetailsCnpDto booker) {
    this.booker = booker;
    return this;
  }

  /**
   * Get booker
   * @return booker
   */
  @Valid 
  @Schema(name = "booker", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("booker")
  public BookerDetailsCnpDto getBooker() {
    return booker;
  }

  public void setBooker(BookerDetailsCnpDto booker) {
    this.booker = booker;
  }

  public UpdateCnpReservationRequestDto businessAccount(BusinessAccountCnpDto businessAccount) {
    this.businessAccount = businessAccount;
    return this;
  }

  /**
   * Get businessAccount
   * @return businessAccount
   */
  @NotNull @Valid 
  @Schema(name = "businessAccount", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("businessAccount")
  public BusinessAccountCnpDto getBusinessAccount() {
    return businessAccount;
  }

  public void setBusinessAccount(BusinessAccountCnpDto businessAccount) {
    this.businessAccount = businessAccount;
  }

  public UpdateCnpReservationRequestDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  
  @Schema(name = "language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateCnpReservationRequestDto updateCnpReservationRequestDto = (UpdateCnpReservationRequestDto) o;
    return Objects.equals(this.booker, updateCnpReservationRequestDto.booker) &&
        Objects.equals(this.businessAccount, updateCnpReservationRequestDto.businessAccount) &&
        Objects.equals(this.language, updateCnpReservationRequestDto.language);
  }

  @Override
  public int hashCode() {
    return Objects.hash(booker, businessAccount, language);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateCnpReservationRequestDto {\n");
    sb.append("    booker: ").append(toIndentedString(booker)).append("\n");
    sb.append("    businessAccount: ").append(toIndentedString(businessAccount)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
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

