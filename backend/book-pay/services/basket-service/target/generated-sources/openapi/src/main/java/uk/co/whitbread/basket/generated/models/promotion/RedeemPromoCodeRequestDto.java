package uk.co.whitbread.basket.generated.models.promotion;

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
 * RedeemPromoCodeRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:16.998949+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RedeemPromoCodeRequestDto {

  private String promoCode;

  private String bookingReference;

  public RedeemPromoCodeRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RedeemPromoCodeRequestDto(String promoCode, String bookingReference) {
    this.promoCode = promoCode;
    this.bookingReference = bookingReference;
  }

  public RedeemPromoCodeRequestDto promoCode(String promoCode) {
    this.promoCode = promoCode;
    return this;
  }

  /**
   * Get promoCode
   * @return promoCode
   */
  @NotNull 
  @Schema(name = "promoCode", example = "27CR7HCV49", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("promoCode")
  public String getPromoCode() {
    return promoCode;
  }

  public void setPromoCode(String promoCode) {
    this.promoCode = promoCode;
  }

  public RedeemPromoCodeRequestDto bookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
    return this;
  }

  /**
   * Get bookingReference
   * @return bookingReference
   */
  @NotNull 
  @Schema(name = "bookingReference", example = "AQN3269618", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bookingReference")
  public String getBookingReference() {
    return bookingReference;
  }

  public void setBookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RedeemPromoCodeRequestDto redeemPromoCodeRequestDto = (RedeemPromoCodeRequestDto) o;
    return Objects.equals(this.promoCode, redeemPromoCodeRequestDto.promoCode) &&
        Objects.equals(this.bookingReference, redeemPromoCodeRequestDto.bookingReference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promoCode, bookingReference);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RedeemPromoCodeRequestDto {\n");
    sb.append("    promoCode: ").append(toIndentedString(promoCode)).append("\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
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

