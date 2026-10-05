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
 * BookingCardInformationRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingCardInformationRequestDto {

  private String arrivalDate;

  private String bookerLastName;

  private String bookingReference;

  private String country = "gb";

  private String language = "en";

  public BookingCardInformationRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BookingCardInformationRequestDto(String arrivalDate, String bookerLastName, String bookingReference) {
    this.arrivalDate = arrivalDate;
    this.bookerLastName = bookerLastName;
    this.bookingReference = bookingReference;
  }

  public BookingCardInformationRequestDto arrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  @NotNull 
  @Schema(name = "arrivalDate", example = "2023-01-23", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("arrivalDate")
  public String getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public BookingCardInformationRequestDto bookerLastName(String bookerLastName) {
    this.bookerLastName = bookerLastName;
    return this;
  }

  /**
   * Get bookerLastName
   * @return bookerLastName
   */
  @NotNull 
  @Schema(name = "bookerLastName", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bookerLastName")
  public String getBookerLastName() {
    return bookerLastName;
  }

  public void setBookerLastName(String bookerLastName) {
    this.bookerLastName = bookerLastName;
  }

  public BookingCardInformationRequestDto bookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
    return this;
  }

  /**
   * Get bookingReference
   * @return bookingReference
   */
  @NotNull 
  @Schema(name = "bookingReference", example = "AWMR378632", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bookingReference")
  public String getBookingReference() {
    return bookingReference;
  }

  public void setBookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
  }

  public BookingCardInformationRequestDto country(String country) {
    this.country = country;
    return this;
  }

  /**
   * Get country
   * @return country
   */
  
  @Schema(name = "country", example = "gb", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("country")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public BookingCardInformationRequestDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  
  @Schema(name = "language", example = "en", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    BookingCardInformationRequestDto bookingCardInformationRequestDto = (BookingCardInformationRequestDto) o;
    return Objects.equals(this.arrivalDate, bookingCardInformationRequestDto.arrivalDate) &&
        Objects.equals(this.bookerLastName, bookingCardInformationRequestDto.bookerLastName) &&
        Objects.equals(this.bookingReference, bookingCardInformationRequestDto.bookingReference) &&
        Objects.equals(this.country, bookingCardInformationRequestDto.country) &&
        Objects.equals(this.language, bookingCardInformationRequestDto.language);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, bookerLastName, bookingReference, country, language);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingCardInformationRequestDto {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    bookerLastName: ").append(toIndentedString(bookerLastName)).append("\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
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

