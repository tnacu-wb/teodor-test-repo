package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.DestinationsDto;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelCodesDto;
import uk.co.whitbread.content.entity.service.generated.models.content.InfoMessagesDto;
import uk.co.whitbread.content.entity.service.generated.models.content.SeoHreflangsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PriceFinderViewsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PriceFinderViewsDto {

  private @Nullable String bannerColour;

  private @Nullable String bannerHeadline;

  private @Nullable String bannerImage;

  private @Nullable String bannerSubtext;

  private @Nullable String checkinDate;

  private @Nullable String dateRangeEnd;

  private @Nullable String dateRangeStart;

  @Valid
  private List<@Valid DestinationsDto> destinations = new ArrayList<>();

  private @Nullable String filterRoomSelected;

  private @Nullable String highlightedPricePrimaryColour;

  private @Nullable Integer highlightedPriceRangeMax;

  private @Nullable Integer highlightedPriceRangeMin;

  private @Nullable Integer highlightedPriceRangeStep;

  private @Nullable String highlightedPriceSecondaryColour;

  @Valid
  private List<@Valid HotelCodesDto> hotelCodes = new ArrayList<>();

  @Valid
  private List<@Valid InfoMessagesDto> infoMessages = new ArrayList<>();

  private @Nullable String locationId;

  private @Nullable String locationName;

  private @Nullable Double locationRadius;

  private @Nullable String path;

  private @Nullable String seoCardImageUrl;

  @Valid
  private List<@Valid SeoHreflangsDto> seoHreflangs = new ArrayList<>();

  private @Nullable String seoMetaDescription;

  private @Nullable String seoMetaTitle;

  private @Nullable String seoRobots;

  private @Nullable String termsLabel;

  public PriceFinderViewsDto bannerColour(String bannerColour) {
    this.bannerColour = bannerColour;
    return this;
  }

  /**
   * Get bannerColour
   * @return bannerColour
   */
  
  @Schema(name = "bannerColour", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bannerColour")
  public String getBannerColour() {
    return bannerColour;
  }

  public void setBannerColour(String bannerColour) {
    this.bannerColour = bannerColour;
  }

  public PriceFinderViewsDto bannerHeadline(String bannerHeadline) {
    this.bannerHeadline = bannerHeadline;
    return this;
  }

  /**
   * Get bannerHeadline
   * @return bannerHeadline
   */
  
  @Schema(name = "bannerHeadline", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bannerHeadline")
  public String getBannerHeadline() {
    return bannerHeadline;
  }

  public void setBannerHeadline(String bannerHeadline) {
    this.bannerHeadline = bannerHeadline;
  }

  public PriceFinderViewsDto bannerImage(String bannerImage) {
    this.bannerImage = bannerImage;
    return this;
  }

  /**
   * Get bannerImage
   * @return bannerImage
   */
  
  @Schema(name = "bannerImage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bannerImage")
  public String getBannerImage() {
    return bannerImage;
  }

  public void setBannerImage(String bannerImage) {
    this.bannerImage = bannerImage;
  }

  public PriceFinderViewsDto bannerSubtext(String bannerSubtext) {
    this.bannerSubtext = bannerSubtext;
    return this;
  }

  /**
   * Get bannerSubtext
   * @return bannerSubtext
   */
  
  @Schema(name = "bannerSubtext", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bannerSubtext")
  public String getBannerSubtext() {
    return bannerSubtext;
  }

  public void setBannerSubtext(String bannerSubtext) {
    this.bannerSubtext = bannerSubtext;
  }

  public PriceFinderViewsDto checkinDate(String checkinDate) {
    this.checkinDate = checkinDate;
    return this;
  }

  /**
   * Get checkinDate
   * @return checkinDate
   */
  
  @Schema(name = "checkinDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkinDate")
  public String getCheckinDate() {
    return checkinDate;
  }

  public void setCheckinDate(String checkinDate) {
    this.checkinDate = checkinDate;
  }

  public PriceFinderViewsDto dateRangeEnd(String dateRangeEnd) {
    this.dateRangeEnd = dateRangeEnd;
    return this;
  }

  /**
   * Get dateRangeEnd
   * @return dateRangeEnd
   */
  
  @Schema(name = "dateRangeEnd", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dateRangeEnd")
  public String getDateRangeEnd() {
    return dateRangeEnd;
  }

  public void setDateRangeEnd(String dateRangeEnd) {
    this.dateRangeEnd = dateRangeEnd;
  }

  public PriceFinderViewsDto dateRangeStart(String dateRangeStart) {
    this.dateRangeStart = dateRangeStart;
    return this;
  }

  /**
   * Get dateRangeStart
   * @return dateRangeStart
   */
  
  @Schema(name = "dateRangeStart", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dateRangeStart")
  public String getDateRangeStart() {
    return dateRangeStart;
  }

  public void setDateRangeStart(String dateRangeStart) {
    this.dateRangeStart = dateRangeStart;
  }

  public PriceFinderViewsDto destinations(List<@Valid DestinationsDto> destinations) {
    this.destinations = destinations;
    return this;
  }

  public PriceFinderViewsDto addDestinationsItem(DestinationsDto destinationsItem) {
    if (this.destinations == null) {
      this.destinations = new ArrayList<>();
    }
    this.destinations.add(destinationsItem);
    return this;
  }

  /**
   * Get destinations
   * @return destinations
   */
  @Valid 
  @Schema(name = "destinations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("destinations")
  public List<@Valid DestinationsDto> getDestinations() {
    return destinations;
  }

  public void setDestinations(List<@Valid DestinationsDto> destinations) {
    this.destinations = destinations;
  }

  public PriceFinderViewsDto filterRoomSelected(String filterRoomSelected) {
    this.filterRoomSelected = filterRoomSelected;
    return this;
  }

  /**
   * Get filterRoomSelected
   * @return filterRoomSelected
   */
  
  @Schema(name = "filterRoomSelected", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("filterRoomSelected")
  public String getFilterRoomSelected() {
    return filterRoomSelected;
  }

  public void setFilterRoomSelected(String filterRoomSelected) {
    this.filterRoomSelected = filterRoomSelected;
  }

  public PriceFinderViewsDto highlightedPricePrimaryColour(String highlightedPricePrimaryColour) {
    this.highlightedPricePrimaryColour = highlightedPricePrimaryColour;
    return this;
  }

  /**
   * Get highlightedPricePrimaryColour
   * @return highlightedPricePrimaryColour
   */
  
  @Schema(name = "highlightedPricePrimaryColour", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("highlightedPricePrimaryColour")
  public String getHighlightedPricePrimaryColour() {
    return highlightedPricePrimaryColour;
  }

  public void setHighlightedPricePrimaryColour(String highlightedPricePrimaryColour) {
    this.highlightedPricePrimaryColour = highlightedPricePrimaryColour;
  }

  public PriceFinderViewsDto highlightedPriceRangeMax(Integer highlightedPriceRangeMax) {
    this.highlightedPriceRangeMax = highlightedPriceRangeMax;
    return this;
  }

  /**
   * Get highlightedPriceRangeMax
   * @return highlightedPriceRangeMax
   */
  
  @Schema(name = "highlightedPriceRangeMax", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("highlightedPriceRangeMax")
  public Integer getHighlightedPriceRangeMax() {
    return highlightedPriceRangeMax;
  }

  public void setHighlightedPriceRangeMax(Integer highlightedPriceRangeMax) {
    this.highlightedPriceRangeMax = highlightedPriceRangeMax;
  }

  public PriceFinderViewsDto highlightedPriceRangeMin(Integer highlightedPriceRangeMin) {
    this.highlightedPriceRangeMin = highlightedPriceRangeMin;
    return this;
  }

  /**
   * Get highlightedPriceRangeMin
   * @return highlightedPriceRangeMin
   */
  
  @Schema(name = "highlightedPriceRangeMin", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("highlightedPriceRangeMin")
  public Integer getHighlightedPriceRangeMin() {
    return highlightedPriceRangeMin;
  }

  public void setHighlightedPriceRangeMin(Integer highlightedPriceRangeMin) {
    this.highlightedPriceRangeMin = highlightedPriceRangeMin;
  }

  public PriceFinderViewsDto highlightedPriceRangeStep(Integer highlightedPriceRangeStep) {
    this.highlightedPriceRangeStep = highlightedPriceRangeStep;
    return this;
  }

  /**
   * Get highlightedPriceRangeStep
   * @return highlightedPriceRangeStep
   */
  
  @Schema(name = "highlightedPriceRangeStep", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("highlightedPriceRangeStep")
  public Integer getHighlightedPriceRangeStep() {
    return highlightedPriceRangeStep;
  }

  public void setHighlightedPriceRangeStep(Integer highlightedPriceRangeStep) {
    this.highlightedPriceRangeStep = highlightedPriceRangeStep;
  }

  public PriceFinderViewsDto highlightedPriceSecondaryColour(String highlightedPriceSecondaryColour) {
    this.highlightedPriceSecondaryColour = highlightedPriceSecondaryColour;
    return this;
  }

  /**
   * Get highlightedPriceSecondaryColour
   * @return highlightedPriceSecondaryColour
   */
  
  @Schema(name = "highlightedPriceSecondaryColour", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("highlightedPriceSecondaryColour")
  public String getHighlightedPriceSecondaryColour() {
    return highlightedPriceSecondaryColour;
  }

  public void setHighlightedPriceSecondaryColour(String highlightedPriceSecondaryColour) {
    this.highlightedPriceSecondaryColour = highlightedPriceSecondaryColour;
  }

  public PriceFinderViewsDto hotelCodes(List<@Valid HotelCodesDto> hotelCodes) {
    this.hotelCodes = hotelCodes;
    return this;
  }

  public PriceFinderViewsDto addHotelCodesItem(HotelCodesDto hotelCodesItem) {
    if (this.hotelCodes == null) {
      this.hotelCodes = new ArrayList<>();
    }
    this.hotelCodes.add(hotelCodesItem);
    return this;
  }

  /**
   * Get hotelCodes
   * @return hotelCodes
   */
  @Valid 
  @Schema(name = "hotelCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelCodes")
  public List<@Valid HotelCodesDto> getHotelCodes() {
    return hotelCodes;
  }

  public void setHotelCodes(List<@Valid HotelCodesDto> hotelCodes) {
    this.hotelCodes = hotelCodes;
  }

  public PriceFinderViewsDto infoMessages(List<@Valid InfoMessagesDto> infoMessages) {
    this.infoMessages = infoMessages;
    return this;
  }

  public PriceFinderViewsDto addInfoMessagesItem(InfoMessagesDto infoMessagesItem) {
    if (this.infoMessages == null) {
      this.infoMessages = new ArrayList<>();
    }
    this.infoMessages.add(infoMessagesItem);
    return this;
  }

  /**
   * Get infoMessages
   * @return infoMessages
   */
  @Valid 
  @Schema(name = "infoMessages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("infoMessages")
  public List<@Valid InfoMessagesDto> getInfoMessages() {
    return infoMessages;
  }

  public void setInfoMessages(List<@Valid InfoMessagesDto> infoMessages) {
    this.infoMessages = infoMessages;
  }

  public PriceFinderViewsDto locationId(String locationId) {
    this.locationId = locationId;
    return this;
  }

  /**
   * Get locationId
   * @return locationId
   */
  
  @Schema(name = "locationId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("locationId")
  public String getLocationId() {
    return locationId;
  }

  public void setLocationId(String locationId) {
    this.locationId = locationId;
  }

  public PriceFinderViewsDto locationName(String locationName) {
    this.locationName = locationName;
    return this;
  }

  /**
   * Get locationName
   * @return locationName
   */
  
  @Schema(name = "locationName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("locationName")
  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public PriceFinderViewsDto locationRadius(Double locationRadius) {
    this.locationRadius = locationRadius;
    return this;
  }

  /**
   * Get locationRadius
   * @return locationRadius
   */
  
  @Schema(name = "locationRadius", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("locationRadius")
  public Double getLocationRadius() {
    return locationRadius;
  }

  public void setLocationRadius(Double locationRadius) {
    this.locationRadius = locationRadius;
  }

  public PriceFinderViewsDto path(String path) {
    this.path = path;
    return this;
  }

  /**
   * Get path
   * @return path
   */
  
  @Schema(name = "path", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("path")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public PriceFinderViewsDto seoCardImageUrl(String seoCardImageUrl) {
    this.seoCardImageUrl = seoCardImageUrl;
    return this;
  }

  /**
   * Get seoCardImageUrl
   * @return seoCardImageUrl
   */
  
  @Schema(name = "seoCardImageUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("seoCardImageUrl")
  public String getSeoCardImageUrl() {
    return seoCardImageUrl;
  }

  public void setSeoCardImageUrl(String seoCardImageUrl) {
    this.seoCardImageUrl = seoCardImageUrl;
  }

  public PriceFinderViewsDto seoHreflangs(List<@Valid SeoHreflangsDto> seoHreflangs) {
    this.seoHreflangs = seoHreflangs;
    return this;
  }

  public PriceFinderViewsDto addSeoHreflangsItem(SeoHreflangsDto seoHreflangsItem) {
    if (this.seoHreflangs == null) {
      this.seoHreflangs = new ArrayList<>();
    }
    this.seoHreflangs.add(seoHreflangsItem);
    return this;
  }

  /**
   * Get seoHreflangs
   * @return seoHreflangs
   */
  @Valid 
  @Schema(name = "seoHreflangs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("seoHreflangs")
  public List<@Valid SeoHreflangsDto> getSeoHreflangs() {
    return seoHreflangs;
  }

  public void setSeoHreflangs(List<@Valid SeoHreflangsDto> seoHreflangs) {
    this.seoHreflangs = seoHreflangs;
  }

  public PriceFinderViewsDto seoMetaDescription(String seoMetaDescription) {
    this.seoMetaDescription = seoMetaDescription;
    return this;
  }

  /**
   * Get seoMetaDescription
   * @return seoMetaDescription
   */
  
  @Schema(name = "seoMetaDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("seoMetaDescription")
  public String getSeoMetaDescription() {
    return seoMetaDescription;
  }

  public void setSeoMetaDescription(String seoMetaDescription) {
    this.seoMetaDescription = seoMetaDescription;
  }

  public PriceFinderViewsDto seoMetaTitle(String seoMetaTitle) {
    this.seoMetaTitle = seoMetaTitle;
    return this;
  }

  /**
   * Get seoMetaTitle
   * @return seoMetaTitle
   */
  
  @Schema(name = "seoMetaTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("seoMetaTitle")
  public String getSeoMetaTitle() {
    return seoMetaTitle;
  }

  public void setSeoMetaTitle(String seoMetaTitle) {
    this.seoMetaTitle = seoMetaTitle;
  }

  public PriceFinderViewsDto seoRobots(String seoRobots) {
    this.seoRobots = seoRobots;
    return this;
  }

  /**
   * Get seoRobots
   * @return seoRobots
   */
  
  @Schema(name = "seoRobots", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("seoRobots")
  public String getSeoRobots() {
    return seoRobots;
  }

  public void setSeoRobots(String seoRobots) {
    this.seoRobots = seoRobots;
  }

  public PriceFinderViewsDto termsLabel(String termsLabel) {
    this.termsLabel = termsLabel;
    return this;
  }

  /**
   * Get termsLabel
   * @return termsLabel
   */
  
  @Schema(name = "termsLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("termsLabel")
  public String getTermsLabel() {
    return termsLabel;
  }

  public void setTermsLabel(String termsLabel) {
    this.termsLabel = termsLabel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PriceFinderViewsDto priceFinderViewsDto = (PriceFinderViewsDto) o;
    return Objects.equals(this.bannerColour, priceFinderViewsDto.bannerColour) &&
        Objects.equals(this.bannerHeadline, priceFinderViewsDto.bannerHeadline) &&
        Objects.equals(this.bannerImage, priceFinderViewsDto.bannerImage) &&
        Objects.equals(this.bannerSubtext, priceFinderViewsDto.bannerSubtext) &&
        Objects.equals(this.checkinDate, priceFinderViewsDto.checkinDate) &&
        Objects.equals(this.dateRangeEnd, priceFinderViewsDto.dateRangeEnd) &&
        Objects.equals(this.dateRangeStart, priceFinderViewsDto.dateRangeStart) &&
        Objects.equals(this.destinations, priceFinderViewsDto.destinations) &&
        Objects.equals(this.filterRoomSelected, priceFinderViewsDto.filterRoomSelected) &&
        Objects.equals(this.highlightedPricePrimaryColour, priceFinderViewsDto.highlightedPricePrimaryColour) &&
        Objects.equals(this.highlightedPriceRangeMax, priceFinderViewsDto.highlightedPriceRangeMax) &&
        Objects.equals(this.highlightedPriceRangeMin, priceFinderViewsDto.highlightedPriceRangeMin) &&
        Objects.equals(this.highlightedPriceRangeStep, priceFinderViewsDto.highlightedPriceRangeStep) &&
        Objects.equals(this.highlightedPriceSecondaryColour, priceFinderViewsDto.highlightedPriceSecondaryColour) &&
        Objects.equals(this.hotelCodes, priceFinderViewsDto.hotelCodes) &&
        Objects.equals(this.infoMessages, priceFinderViewsDto.infoMessages) &&
        Objects.equals(this.locationId, priceFinderViewsDto.locationId) &&
        Objects.equals(this.locationName, priceFinderViewsDto.locationName) &&
        Objects.equals(this.locationRadius, priceFinderViewsDto.locationRadius) &&
        Objects.equals(this.path, priceFinderViewsDto.path) &&
        Objects.equals(this.seoCardImageUrl, priceFinderViewsDto.seoCardImageUrl) &&
        Objects.equals(this.seoHreflangs, priceFinderViewsDto.seoHreflangs) &&
        Objects.equals(this.seoMetaDescription, priceFinderViewsDto.seoMetaDescription) &&
        Objects.equals(this.seoMetaTitle, priceFinderViewsDto.seoMetaTitle) &&
        Objects.equals(this.seoRobots, priceFinderViewsDto.seoRobots) &&
        Objects.equals(this.termsLabel, priceFinderViewsDto.termsLabel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bannerColour, bannerHeadline, bannerImage, bannerSubtext, checkinDate, dateRangeEnd, dateRangeStart, destinations, filterRoomSelected, highlightedPricePrimaryColour, highlightedPriceRangeMax, highlightedPriceRangeMin, highlightedPriceRangeStep, highlightedPriceSecondaryColour, hotelCodes, infoMessages, locationId, locationName, locationRadius, path, seoCardImageUrl, seoHreflangs, seoMetaDescription, seoMetaTitle, seoRobots, termsLabel);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PriceFinderViewsDto {\n");
    sb.append("    bannerColour: ").append(toIndentedString(bannerColour)).append("\n");
    sb.append("    bannerHeadline: ").append(toIndentedString(bannerHeadline)).append("\n");
    sb.append("    bannerImage: ").append(toIndentedString(bannerImage)).append("\n");
    sb.append("    bannerSubtext: ").append(toIndentedString(bannerSubtext)).append("\n");
    sb.append("    checkinDate: ").append(toIndentedString(checkinDate)).append("\n");
    sb.append("    dateRangeEnd: ").append(toIndentedString(dateRangeEnd)).append("\n");
    sb.append("    dateRangeStart: ").append(toIndentedString(dateRangeStart)).append("\n");
    sb.append("    destinations: ").append(toIndentedString(destinations)).append("\n");
    sb.append("    filterRoomSelected: ").append(toIndentedString(filterRoomSelected)).append("\n");
    sb.append("    highlightedPricePrimaryColour: ").append(toIndentedString(highlightedPricePrimaryColour)).append("\n");
    sb.append("    highlightedPriceRangeMax: ").append(toIndentedString(highlightedPriceRangeMax)).append("\n");
    sb.append("    highlightedPriceRangeMin: ").append(toIndentedString(highlightedPriceRangeMin)).append("\n");
    sb.append("    highlightedPriceRangeStep: ").append(toIndentedString(highlightedPriceRangeStep)).append("\n");
    sb.append("    highlightedPriceSecondaryColour: ").append(toIndentedString(highlightedPriceSecondaryColour)).append("\n");
    sb.append("    hotelCodes: ").append(toIndentedString(hotelCodes)).append("\n");
    sb.append("    infoMessages: ").append(toIndentedString(infoMessages)).append("\n");
    sb.append("    locationId: ").append(toIndentedString(locationId)).append("\n");
    sb.append("    locationName: ").append(toIndentedString(locationName)).append("\n");
    sb.append("    locationRadius: ").append(toIndentedString(locationRadius)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    seoCardImageUrl: ").append(toIndentedString(seoCardImageUrl)).append("\n");
    sb.append("    seoHreflangs: ").append(toIndentedString(seoHreflangs)).append("\n");
    sb.append("    seoMetaDescription: ").append(toIndentedString(seoMetaDescription)).append("\n");
    sb.append("    seoMetaTitle: ").append(toIndentedString(seoMetaTitle)).append("\n");
    sb.append("    seoRobots: ").append(toIndentedString(seoRobots)).append("\n");
    sb.append("    termsLabel: ").append(toIndentedString(termsLabel)).append("\n");
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

