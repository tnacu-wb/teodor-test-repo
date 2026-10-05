package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * HotelCityTaxDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelCityTaxDto {

  private @Nullable BigDecimal amount;

  private @Nullable String bookingDateFrom;

  private @Nullable String calculation;

  private @Nullable String cityTaxWebUrl;

  private @Nullable String effectiveFrom;

  private @Nullable Boolean isCityTaxBusinessHotel;

  private @Nullable Boolean isCityTaxHotel;

  private @Nullable Integer maxNights;

  private @Nullable BigDecimal percentage;

  private @Nullable String posting;

  private @Nullable BigDecimal vat;

  public HotelCityTaxDto amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Get amount
   * @return amount
   */
  @Valid 
  @Schema(name = "amount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amount")
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public HotelCityTaxDto bookingDateFrom(String bookingDateFrom) {
    this.bookingDateFrom = bookingDateFrom;
    return this;
  }

  /**
   * Get bookingDateFrom
   * @return bookingDateFrom
   */
  
  @Schema(name = "bookingDateFrom", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingDateFrom")
  public String getBookingDateFrom() {
    return bookingDateFrom;
  }

  public void setBookingDateFrom(String bookingDateFrom) {
    this.bookingDateFrom = bookingDateFrom;
  }

  public HotelCityTaxDto calculation(String calculation) {
    this.calculation = calculation;
    return this;
  }

  /**
   * Get calculation
   * @return calculation
   */
  
  @Schema(name = "calculation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("calculation")
  public String getCalculation() {
    return calculation;
  }

  public void setCalculation(String calculation) {
    this.calculation = calculation;
  }

  public HotelCityTaxDto cityTaxWebUrl(String cityTaxWebUrl) {
    this.cityTaxWebUrl = cityTaxWebUrl;
    return this;
  }

  /**
   * Get cityTaxWebUrl
   * @return cityTaxWebUrl
   */
  
  @Schema(name = "cityTaxWebUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cityTaxWebUrl")
  public String getCityTaxWebUrl() {
    return cityTaxWebUrl;
  }

  public void setCityTaxWebUrl(String cityTaxWebUrl) {
    this.cityTaxWebUrl = cityTaxWebUrl;
  }

  public HotelCityTaxDto effectiveFrom(String effectiveFrom) {
    this.effectiveFrom = effectiveFrom;
    return this;
  }

  /**
   * Get effectiveFrom
   * @return effectiveFrom
   */
  
  @Schema(name = "effectiveFrom", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("effectiveFrom")
  public String getEffectiveFrom() {
    return effectiveFrom;
  }

  public void setEffectiveFrom(String effectiveFrom) {
    this.effectiveFrom = effectiveFrom;
  }

  public HotelCityTaxDto isCityTaxBusinessHotel(Boolean isCityTaxBusinessHotel) {
    this.isCityTaxBusinessHotel = isCityTaxBusinessHotel;
    return this;
  }

  /**
   * Get isCityTaxBusinessHotel
   * @return isCityTaxBusinessHotel
   */
  
  @Schema(name = "isCityTaxBusinessHotel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isCityTaxBusinessHotel")
  public Boolean getIsCityTaxBusinessHotel() {
    return isCityTaxBusinessHotel;
  }

  public void setIsCityTaxBusinessHotel(Boolean isCityTaxBusinessHotel) {
    this.isCityTaxBusinessHotel = isCityTaxBusinessHotel;
  }

  public HotelCityTaxDto isCityTaxHotel(Boolean isCityTaxHotel) {
    this.isCityTaxHotel = isCityTaxHotel;
    return this;
  }

  /**
   * Get isCityTaxHotel
   * @return isCityTaxHotel
   */
  
  @Schema(name = "isCityTaxHotel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isCityTaxHotel")
  public Boolean getIsCityTaxHotel() {
    return isCityTaxHotel;
  }

  public void setIsCityTaxHotel(Boolean isCityTaxHotel) {
    this.isCityTaxHotel = isCityTaxHotel;
  }

  public HotelCityTaxDto maxNights(Integer maxNights) {
    this.maxNights = maxNights;
    return this;
  }

  /**
   * Get maxNights
   * @return maxNights
   */
  
  @Schema(name = "maxNights", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxNights")
  public Integer getMaxNights() {
    return maxNights;
  }

  public void setMaxNights(Integer maxNights) {
    this.maxNights = maxNights;
  }

  public HotelCityTaxDto percentage(BigDecimal percentage) {
    this.percentage = percentage;
    return this;
  }

  /**
   * Get percentage
   * @return percentage
   */
  @Valid 
  @Schema(name = "percentage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("percentage")
  public BigDecimal getPercentage() {
    return percentage;
  }

  public void setPercentage(BigDecimal percentage) {
    this.percentage = percentage;
  }

  public HotelCityTaxDto posting(String posting) {
    this.posting = posting;
    return this;
  }

  /**
   * Get posting
   * @return posting
   */
  
  @Schema(name = "posting", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("posting")
  public String getPosting() {
    return posting;
  }

  public void setPosting(String posting) {
    this.posting = posting;
  }

  public HotelCityTaxDto vat(BigDecimal vat) {
    this.vat = vat;
    return this;
  }

  /**
   * Get vat
   * @return vat
   */
  @Valid 
  @Schema(name = "vat", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("vat")
  public BigDecimal getVat() {
    return vat;
  }

  public void setVat(BigDecimal vat) {
    this.vat = vat;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelCityTaxDto hotelCityTaxDto = (HotelCityTaxDto) o;
    return Objects.equals(this.amount, hotelCityTaxDto.amount) &&
        Objects.equals(this.bookingDateFrom, hotelCityTaxDto.bookingDateFrom) &&
        Objects.equals(this.calculation, hotelCityTaxDto.calculation) &&
        Objects.equals(this.cityTaxWebUrl, hotelCityTaxDto.cityTaxWebUrl) &&
        Objects.equals(this.effectiveFrom, hotelCityTaxDto.effectiveFrom) &&
        Objects.equals(this.isCityTaxBusinessHotel, hotelCityTaxDto.isCityTaxBusinessHotel) &&
        Objects.equals(this.isCityTaxHotel, hotelCityTaxDto.isCityTaxHotel) &&
        Objects.equals(this.maxNights, hotelCityTaxDto.maxNights) &&
        Objects.equals(this.percentage, hotelCityTaxDto.percentage) &&
        Objects.equals(this.posting, hotelCityTaxDto.posting) &&
        Objects.equals(this.vat, hotelCityTaxDto.vat);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, bookingDateFrom, calculation, cityTaxWebUrl, effectiveFrom, isCityTaxBusinessHotel, isCityTaxHotel, maxNights, percentage, posting, vat);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelCityTaxDto {\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    bookingDateFrom: ").append(toIndentedString(bookingDateFrom)).append("\n");
    sb.append("    calculation: ").append(toIndentedString(calculation)).append("\n");
    sb.append("    cityTaxWebUrl: ").append(toIndentedString(cityTaxWebUrl)).append("\n");
    sb.append("    effectiveFrom: ").append(toIndentedString(effectiveFrom)).append("\n");
    sb.append("    isCityTaxBusinessHotel: ").append(toIndentedString(isCityTaxBusinessHotel)).append("\n");
    sb.append("    isCityTaxHotel: ").append(toIndentedString(isCityTaxHotel)).append("\n");
    sb.append("    maxNights: ").append(toIndentedString(maxNights)).append("\n");
    sb.append("    percentage: ").append(toIndentedString(percentage)).append("\n");
    sb.append("    posting: ").append(toIndentedString(posting)).append("\n");
    sb.append("    vat: ").append(toIndentedString(vat)).append("\n");
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

