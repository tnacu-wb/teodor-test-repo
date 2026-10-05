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
 * UpdateReasonForStayRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateReasonForStayRequestDto {

  private @Nullable String arrivalDate;

  private String basketReference;

  private @Nullable String country;

  private String hotelId;

  private @Nullable String language;

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

  public UpdateReasonForStayRequestDto arrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  
  @Schema(name = "arrivalDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDate")
  public String getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
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

  public UpdateReasonForStayRequestDto country(String country) {
    this.country = country;
    return this;
  }

  /**
   * Get country
   * @return country
   */
  
  @Schema(name = "country", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("country")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
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

  public UpdateReasonForStayRequestDto language(String language) {
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
    return Objects.equals(this.arrivalDate, updateReasonForStayRequestDto.arrivalDate) &&
        Objects.equals(this.basketReference, updateReasonForStayRequestDto.basketReference) &&
        Objects.equals(this.country, updateReasonForStayRequestDto.country) &&
        Objects.equals(this.hotelId, updateReasonForStayRequestDto.hotelId) &&
        Objects.equals(this.language, updateReasonForStayRequestDto.language) &&
        Objects.equals(this.reasonForStay, updateReasonForStayRequestDto.reasonForStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, basketReference, country, hotelId, language, reasonForStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateReasonForStayRequestDto {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    basketReference: ").append(toIndentedString(basketReference)).append("\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
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

