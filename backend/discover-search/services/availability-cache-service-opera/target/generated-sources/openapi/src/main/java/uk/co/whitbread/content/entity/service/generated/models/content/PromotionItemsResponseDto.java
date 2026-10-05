package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PromotionItemsResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PromotionItemsResponseDto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate bookingEndDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate bookingStartDate;

  private @Nullable Boolean enabled;

  private @Nullable String landingPage;

  private @Nullable Integer maxRooms;

  private @Nullable Integer numberOfNights;

  private @Nullable String promoBannerColour;

  private @Nullable String promoBannerIcon;

  private @Nullable String promoBannerSubtitle;

  private @Nullable String promoBannerTitle;

  private @Nullable String promoCode;

  private @Nullable String promoExpiredMessage;

  private @Nullable String promoInvalidMessage;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate stayEndDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate stayStartDate;

  public PromotionItemsResponseDto bookingEndDate(LocalDate bookingEndDate) {
    this.bookingEndDate = bookingEndDate;
    return this;
  }

  /**
   * Get bookingEndDate
   * @return bookingEndDate
   */
  @Valid 
  @Schema(name = "bookingEndDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingEndDate")
  public LocalDate getBookingEndDate() {
    return bookingEndDate;
  }

  public void setBookingEndDate(LocalDate bookingEndDate) {
    this.bookingEndDate = bookingEndDate;
  }

  public PromotionItemsResponseDto bookingStartDate(LocalDate bookingStartDate) {
    this.bookingStartDate = bookingStartDate;
    return this;
  }

  /**
   * Get bookingStartDate
   * @return bookingStartDate
   */
  @Valid 
  @Schema(name = "bookingStartDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingStartDate")
  public LocalDate getBookingStartDate() {
    return bookingStartDate;
  }

  public void setBookingStartDate(LocalDate bookingStartDate) {
    this.bookingStartDate = bookingStartDate;
  }

  public PromotionItemsResponseDto enabled(Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Get enabled
   * @return enabled
   */
  
  @Schema(name = "enabled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("enabled")
  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public PromotionItemsResponseDto landingPage(String landingPage) {
    this.landingPage = landingPage;
    return this;
  }

  /**
   * Get landingPage
   * @return landingPage
   */
  
  @Schema(name = "landingPage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("landingPage")
  public String getLandingPage() {
    return landingPage;
  }

  public void setLandingPage(String landingPage) {
    this.landingPage = landingPage;
  }

  public PromotionItemsResponseDto maxRooms(Integer maxRooms) {
    this.maxRooms = maxRooms;
    return this;
  }

  /**
   * Get maxRooms
   * @return maxRooms
   */
  
  @Schema(name = "maxRooms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxRooms")
  public Integer getMaxRooms() {
    return maxRooms;
  }

  public void setMaxRooms(Integer maxRooms) {
    this.maxRooms = maxRooms;
  }

  public PromotionItemsResponseDto numberOfNights(Integer numberOfNights) {
    this.numberOfNights = numberOfNights;
    return this;
  }

  /**
   * Get numberOfNights
   * @return numberOfNights
   */
  
  @Schema(name = "numberOfNights", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numberOfNights")
  public Integer getNumberOfNights() {
    return numberOfNights;
  }

  public void setNumberOfNights(Integer numberOfNights) {
    this.numberOfNights = numberOfNights;
  }

  public PromotionItemsResponseDto promoBannerColour(String promoBannerColour) {
    this.promoBannerColour = promoBannerColour;
    return this;
  }

  /**
   * Get promoBannerColour
   * @return promoBannerColour
   */
  
  @Schema(name = "promoBannerColour", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoBannerColour")
  public String getPromoBannerColour() {
    return promoBannerColour;
  }

  public void setPromoBannerColour(String promoBannerColour) {
    this.promoBannerColour = promoBannerColour;
  }

  public PromotionItemsResponseDto promoBannerIcon(String promoBannerIcon) {
    this.promoBannerIcon = promoBannerIcon;
    return this;
  }

  /**
   * Get promoBannerIcon
   * @return promoBannerIcon
   */
  
  @Schema(name = "promoBannerIcon", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoBannerIcon")
  public String getPromoBannerIcon() {
    return promoBannerIcon;
  }

  public void setPromoBannerIcon(String promoBannerIcon) {
    this.promoBannerIcon = promoBannerIcon;
  }

  public PromotionItemsResponseDto promoBannerSubtitle(String promoBannerSubtitle) {
    this.promoBannerSubtitle = promoBannerSubtitle;
    return this;
  }

  /**
   * Get promoBannerSubtitle
   * @return promoBannerSubtitle
   */
  
  @Schema(name = "promoBannerSubtitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoBannerSubtitle")
  public String getPromoBannerSubtitle() {
    return promoBannerSubtitle;
  }

  public void setPromoBannerSubtitle(String promoBannerSubtitle) {
    this.promoBannerSubtitle = promoBannerSubtitle;
  }

  public PromotionItemsResponseDto promoBannerTitle(String promoBannerTitle) {
    this.promoBannerTitle = promoBannerTitle;
    return this;
  }

  /**
   * Get promoBannerTitle
   * @return promoBannerTitle
   */
  
  @Schema(name = "promoBannerTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoBannerTitle")
  public String getPromoBannerTitle() {
    return promoBannerTitle;
  }

  public void setPromoBannerTitle(String promoBannerTitle) {
    this.promoBannerTitle = promoBannerTitle;
  }

  public PromotionItemsResponseDto promoCode(String promoCode) {
    this.promoCode = promoCode;
    return this;
  }

  /**
   * Get promoCode
   * @return promoCode
   */
  
  @Schema(name = "promoCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoCode")
  public String getPromoCode() {
    return promoCode;
  }

  public void setPromoCode(String promoCode) {
    this.promoCode = promoCode;
  }

  public PromotionItemsResponseDto promoExpiredMessage(String promoExpiredMessage) {
    this.promoExpiredMessage = promoExpiredMessage;
    return this;
  }

  /**
   * Get promoExpiredMessage
   * @return promoExpiredMessage
   */
  
  @Schema(name = "promoExpiredMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoExpiredMessage")
  public String getPromoExpiredMessage() {
    return promoExpiredMessage;
  }

  public void setPromoExpiredMessage(String promoExpiredMessage) {
    this.promoExpiredMessage = promoExpiredMessage;
  }

  public PromotionItemsResponseDto promoInvalidMessage(String promoInvalidMessage) {
    this.promoInvalidMessage = promoInvalidMessage;
    return this;
  }

  /**
   * Get promoInvalidMessage
   * @return promoInvalidMessage
   */
  
  @Schema(name = "promoInvalidMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoInvalidMessage")
  public String getPromoInvalidMessage() {
    return promoInvalidMessage;
  }

  public void setPromoInvalidMessage(String promoInvalidMessage) {
    this.promoInvalidMessage = promoInvalidMessage;
  }

  public PromotionItemsResponseDto stayEndDate(LocalDate stayEndDate) {
    this.stayEndDate = stayEndDate;
    return this;
  }

  /**
   * Get stayEndDate
   * @return stayEndDate
   */
  @Valid 
  @Schema(name = "stayEndDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stayEndDate")
  public LocalDate getStayEndDate() {
    return stayEndDate;
  }

  public void setStayEndDate(LocalDate stayEndDate) {
    this.stayEndDate = stayEndDate;
  }

  public PromotionItemsResponseDto stayStartDate(LocalDate stayStartDate) {
    this.stayStartDate = stayStartDate;
    return this;
  }

  /**
   * Get stayStartDate
   * @return stayStartDate
   */
  @Valid 
  @Schema(name = "stayStartDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stayStartDate")
  public LocalDate getStayStartDate() {
    return stayStartDate;
  }

  public void setStayStartDate(LocalDate stayStartDate) {
    this.stayStartDate = stayStartDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PromotionItemsResponseDto promotionItemsResponseDto = (PromotionItemsResponseDto) o;
    return Objects.equals(this.bookingEndDate, promotionItemsResponseDto.bookingEndDate) &&
        Objects.equals(this.bookingStartDate, promotionItemsResponseDto.bookingStartDate) &&
        Objects.equals(this.enabled, promotionItemsResponseDto.enabled) &&
        Objects.equals(this.landingPage, promotionItemsResponseDto.landingPage) &&
        Objects.equals(this.maxRooms, promotionItemsResponseDto.maxRooms) &&
        Objects.equals(this.numberOfNights, promotionItemsResponseDto.numberOfNights) &&
        Objects.equals(this.promoBannerColour, promotionItemsResponseDto.promoBannerColour) &&
        Objects.equals(this.promoBannerIcon, promotionItemsResponseDto.promoBannerIcon) &&
        Objects.equals(this.promoBannerSubtitle, promotionItemsResponseDto.promoBannerSubtitle) &&
        Objects.equals(this.promoBannerTitle, promotionItemsResponseDto.promoBannerTitle) &&
        Objects.equals(this.promoCode, promotionItemsResponseDto.promoCode) &&
        Objects.equals(this.promoExpiredMessage, promotionItemsResponseDto.promoExpiredMessage) &&
        Objects.equals(this.promoInvalidMessage, promotionItemsResponseDto.promoInvalidMessage) &&
        Objects.equals(this.stayEndDate, promotionItemsResponseDto.stayEndDate) &&
        Objects.equals(this.stayStartDate, promotionItemsResponseDto.stayStartDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingEndDate, bookingStartDate, enabled, landingPage, maxRooms, numberOfNights, promoBannerColour, promoBannerIcon, promoBannerSubtitle, promoBannerTitle, promoCode, promoExpiredMessage, promoInvalidMessage, stayEndDate, stayStartDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromotionItemsResponseDto {\n");
    sb.append("    bookingEndDate: ").append(toIndentedString(bookingEndDate)).append("\n");
    sb.append("    bookingStartDate: ").append(toIndentedString(bookingStartDate)).append("\n");
    sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
    sb.append("    landingPage: ").append(toIndentedString(landingPage)).append("\n");
    sb.append("    maxRooms: ").append(toIndentedString(maxRooms)).append("\n");
    sb.append("    numberOfNights: ").append(toIndentedString(numberOfNights)).append("\n");
    sb.append("    promoBannerColour: ").append(toIndentedString(promoBannerColour)).append("\n");
    sb.append("    promoBannerIcon: ").append(toIndentedString(promoBannerIcon)).append("\n");
    sb.append("    promoBannerSubtitle: ").append(toIndentedString(promoBannerSubtitle)).append("\n");
    sb.append("    promoBannerTitle: ").append(toIndentedString(promoBannerTitle)).append("\n");
    sb.append("    promoCode: ").append(toIndentedString(promoCode)).append("\n");
    sb.append("    promoExpiredMessage: ").append(toIndentedString(promoExpiredMessage)).append("\n");
    sb.append("    promoInvalidMessage: ").append(toIndentedString(promoInvalidMessage)).append("\n");
    sb.append("    stayEndDate: ").append(toIndentedString(stayEndDate)).append("\n");
    sb.append("    stayStartDate: ").append(toIndentedString(stayStartDate)).append("\n");
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

