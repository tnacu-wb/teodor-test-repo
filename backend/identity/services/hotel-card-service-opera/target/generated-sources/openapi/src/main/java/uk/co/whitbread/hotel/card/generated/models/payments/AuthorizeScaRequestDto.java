package uk.co.whitbread.hotel.card.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AuthorizeScaRequestDto
 */

@JsonTypeName("AuthorizeScaRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:52.919417+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AuthorizeScaRequestDto {

  private @Nullable String bookingReference;

  private String country = "gb";

  private String environment;

  private @Nullable String language;

  private String requestId;

  public AuthorizeScaRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AuthorizeScaRequestDto(String environment, String requestId) {
    this.environment = environment;
    this.requestId = requestId;
  }

  public AuthorizeScaRequestDto bookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
    return this;
  }

  /**
   * Booking reference
   * @return bookingReference
   */
  
  @Schema(name = "bookingReference", description = "Booking reference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingReference")
  public String getBookingReference() {
    return bookingReference;
  }

  public void setBookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
  }

  public AuthorizeScaRequestDto country(String country) {
    this.country = country;
    return this;
  }

  /**
   * Country as per User site
   * @return country
   */
  
  @Schema(name = "country", description = "Country as per User site", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("country")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public AuthorizeScaRequestDto environment(String environment) {
    this.environment = environment;
    return this;
  }

  /**
   * Environment hostname
   * @return environment
   */
  @NotNull 
  @Schema(name = "environment", description = "Environment hostname", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("environment")
  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public AuthorizeScaRequestDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Language specified by the user
   * @return language
   */
  
  @Schema(name = "language", description = "Language specified by the user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public AuthorizeScaRequestDto requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Unique reference for transaction provided by consumer.
   * @return requestId
   */
  @NotNull 
  @Schema(name = "requestId", example = "a0a9f782-98ee-468c-9839-30c487c832a3", description = "Unique reference for transaction provided by consumer.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("requestId")
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuthorizeScaRequestDto authorizeScaRequest = (AuthorizeScaRequestDto) o;
    return Objects.equals(this.bookingReference, authorizeScaRequest.bookingReference) &&
        Objects.equals(this.country, authorizeScaRequest.country) &&
        Objects.equals(this.environment, authorizeScaRequest.environment) &&
        Objects.equals(this.language, authorizeScaRequest.language) &&
        Objects.equals(this.requestId, authorizeScaRequest.requestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingReference, country, environment, language, requestId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthorizeScaRequestDto {\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
    sb.append("    environment: ").append(toIndentedString(environment)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
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

