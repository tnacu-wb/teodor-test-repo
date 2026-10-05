package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.AcceptedCreditCardDto;
import uk.co.whitbread.content.entity.service.generated.models.content.AccessibilityInfoDto;
import uk.co.whitbread.content.entity.service.generated.models.content.AddressDto;
import uk.co.whitbread.content.entity.service.generated.models.content.AncillaryCloseoutDto;
import uk.co.whitbread.content.entity.service.generated.models.content.AnnouncementDto;
import uk.co.whitbread.content.entity.service.generated.models.content.BookingFlowDto;
import uk.co.whitbread.content.entity.service.generated.models.content.ContactDetailsDto;
import uk.co.whitbread.content.entity.service.generated.models.content.CoordinatesDto;
import uk.co.whitbread.content.entity.service.generated.models.content.GalleryImageDto;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelFacilityDto;
import uk.co.whitbread.content.entity.service.generated.models.content.ImportantInfoDto;
import uk.co.whitbread.content.entity.service.generated.models.content.LinksDto;
import uk.co.whitbread.content.entity.service.generated.models.content.MessagingFlagDto;
import uk.co.whitbread.content.entity.service.generated.models.content.RestaurantDto;
import uk.co.whitbread.content.entity.service.generated.models.content.RoomConfigurationDto;
import uk.co.whitbread.content.entity.service.generated.models.content.ThumbnailImageDto;
import uk.co.whitbread.content.entity.service.generated.models.content.TopSectionImageDto;
import uk.co.whitbread.content.entity.service.generated.models.content.TripAdvisorReviewsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * HotelInformationExtendedDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelInformationExtendedDto {

  private @Nullable AccessibilityInfoDto accessibilityInfo;

  private @Nullable AddressDto address;

  private @Nullable AncillaryCloseoutDto ancillaryCloseout;

  private @Nullable AnnouncementDto announcement;

  private @Nullable BookingFlowDto bookingFlow;

  private @Nullable String brand;

  private @Nullable String checkInTime;

  private @Nullable String checkOutTime;

  private @Nullable ContactDetailsDto contactDetails;

  private @Nullable CoordinatesDto coordinates;

  private @Nullable String county;

  private @Nullable String currencyCode;

  private @Nullable String directions;

  @Valid
  private List<@Valid GalleryImageDto> galleryImages = new ArrayList<>();

  private @Nullable String headline;

  private @Nullable String hotelDescription;

  @Valid
  private List<@Valid HotelFacilityDto> hotelFacilities = new ArrayList<>();

  private @Nullable String hotelId;

  private @Nullable String hotelOpeningDate;

  private @Nullable ImportantInfoDto importantInfo;

  private @Nullable String languageCode;

  private @Nullable LinksDto links;

  private @Nullable MessagingFlagDto messagingFlag;

  private @Nullable String name;

  private @Nullable String parkingDescription;

  @Valid
  private List<@Valid AcceptedCreditCardDto> paymentCodeTypes = new ArrayList<>();

  private @Nullable RestaurantDto restaurant;

  private @Nullable RoomConfigurationDto roomConfiguration;

  private @Nullable String satNavDirections;

  @Valid
  private List<@Valid ThumbnailImageDto> thumbnailImages = new ArrayList<>();

  private @Nullable String timeZone;

  private @Nullable String title;

  @Valid
  private List<@Valid TopSectionImageDto> topSectionImages = new ArrayList<>();

  @Valid
  private List<String> transportInformation = new ArrayList<>();

  private @Nullable TripAdvisorReviewsDto tripAdvisorReviews;

  private @Nullable String whatThreeWords;

  @Valid
  private List<@Valid Object> roomClassConfiguration = new ArrayList<>();

  public HotelInformationExtendedDto accessibilityInfo(AccessibilityInfoDto accessibilityInfo) {
    this.accessibilityInfo = accessibilityInfo;
    return this;
  }

  /**
   * Get accessibilityInfo
   * @return accessibilityInfo
   */
  @Valid 
  @Schema(name = "accessibilityInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessibilityInfo")
  public AccessibilityInfoDto getAccessibilityInfo() {
    return accessibilityInfo;
  }

  public void setAccessibilityInfo(AccessibilityInfoDto accessibilityInfo) {
    this.accessibilityInfo = accessibilityInfo;
  }

  public HotelInformationExtendedDto address(AddressDto address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
   */
  @Valid 
  @Schema(name = "address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("address")
  public AddressDto getAddress() {
    return address;
  }

  public void setAddress(AddressDto address) {
    this.address = address;
  }

  public HotelInformationExtendedDto ancillaryCloseout(AncillaryCloseoutDto ancillaryCloseout) {
    this.ancillaryCloseout = ancillaryCloseout;
    return this;
  }

  /**
   * Get ancillaryCloseout
   * @return ancillaryCloseout
   */
  @Valid 
  @Schema(name = "ancillaryCloseout", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ancillaryCloseout")
  public AncillaryCloseoutDto getAncillaryCloseout() {
    return ancillaryCloseout;
  }

  public void setAncillaryCloseout(AncillaryCloseoutDto ancillaryCloseout) {
    this.ancillaryCloseout = ancillaryCloseout;
  }

  public HotelInformationExtendedDto announcement(AnnouncementDto announcement) {
    this.announcement = announcement;
    return this;
  }

  /**
   * Get announcement
   * @return announcement
   */
  @Valid 
  @Schema(name = "announcement", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("announcement")
  public AnnouncementDto getAnnouncement() {
    return announcement;
  }

  public void setAnnouncement(AnnouncementDto announcement) {
    this.announcement = announcement;
  }

  public HotelInformationExtendedDto bookingFlow(BookingFlowDto bookingFlow) {
    this.bookingFlow = bookingFlow;
    return this;
  }

  /**
   * Get bookingFlow
   * @return bookingFlow
   */
  @Valid 
  @Schema(name = "bookingFlow", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingFlow")
  public BookingFlowDto getBookingFlow() {
    return bookingFlow;
  }

  public void setBookingFlow(BookingFlowDto bookingFlow) {
    this.bookingFlow = bookingFlow;
  }

  public HotelInformationExtendedDto brand(String brand) {
    this.brand = brand;
    return this;
  }

  /**
   * Get brand
   * @return brand
   */
  
  @Schema(name = "brand", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public HotelInformationExtendedDto checkInTime(String checkInTime) {
    this.checkInTime = checkInTime;
    return this;
  }

  /**
   * Get checkInTime
   * @return checkInTime
   */
  
  @Schema(name = "checkInTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkInTime")
  public String getCheckInTime() {
    return checkInTime;
  }

  public void setCheckInTime(String checkInTime) {
    this.checkInTime = checkInTime;
  }

  public HotelInformationExtendedDto checkOutTime(String checkOutTime) {
    this.checkOutTime = checkOutTime;
    return this;
  }

  /**
   * Get checkOutTime
   * @return checkOutTime
   */
  
  @Schema(name = "checkOutTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkOutTime")
  public String getCheckOutTime() {
    return checkOutTime;
  }

  public void setCheckOutTime(String checkOutTime) {
    this.checkOutTime = checkOutTime;
  }

  public HotelInformationExtendedDto contactDetails(ContactDetailsDto contactDetails) {
    this.contactDetails = contactDetails;
    return this;
  }

  /**
   * Get contactDetails
   * @return contactDetails
   */
  @Valid 
  @Schema(name = "contactDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contactDetails")
  public ContactDetailsDto getContactDetails() {
    return contactDetails;
  }

  public void setContactDetails(ContactDetailsDto contactDetails) {
    this.contactDetails = contactDetails;
  }

  public HotelInformationExtendedDto coordinates(CoordinatesDto coordinates) {
    this.coordinates = coordinates;
    return this;
  }

  /**
   * Get coordinates
   * @return coordinates
   */
  @Valid 
  @Schema(name = "coordinates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("coordinates")
  public CoordinatesDto getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(CoordinatesDto coordinates) {
    this.coordinates = coordinates;
  }

  public HotelInformationExtendedDto county(String county) {
    this.county = county;
    return this;
  }

  /**
   * Get county
   * @return county
   */
  
  @Schema(name = "county", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("county")
  public String getCounty() {
    return county;
  }

  public void setCounty(String county) {
    this.county = county;
  }

  public HotelInformationExtendedDto currencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
    return this;
  }

  /**
   * Get currencyCode
   * @return currencyCode
   */
  
  @Schema(name = "currencyCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currencyCode")
  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public HotelInformationExtendedDto directions(String directions) {
    this.directions = directions;
    return this;
  }

  /**
   * Get directions
   * @return directions
   */
  
  @Schema(name = "directions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("directions")
  public String getDirections() {
    return directions;
  }

  public void setDirections(String directions) {
    this.directions = directions;
  }

  public HotelInformationExtendedDto galleryImages(List<@Valid GalleryImageDto> galleryImages) {
    this.galleryImages = galleryImages;
    return this;
  }

  public HotelInformationExtendedDto addGalleryImagesItem(GalleryImageDto galleryImagesItem) {
    if (this.galleryImages == null) {
      this.galleryImages = new ArrayList<>();
    }
    this.galleryImages.add(galleryImagesItem);
    return this;
  }

  /**
   * Get galleryImages
   * @return galleryImages
   */
  @Valid 
  @Schema(name = "galleryImages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("galleryImages")
  public List<@Valid GalleryImageDto> getGalleryImages() {
    return galleryImages;
  }

  public void setGalleryImages(List<@Valid GalleryImageDto> galleryImages) {
    this.galleryImages = galleryImages;
  }

  public HotelInformationExtendedDto headline(String headline) {
    this.headline = headline;
    return this;
  }

  /**
   * Get headline
   * @return headline
   */
  
  @Schema(name = "headline", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("headline")
  public String getHeadline() {
    return headline;
  }

  public void setHeadline(String headline) {
    this.headline = headline;
  }

  public HotelInformationExtendedDto hotelDescription(String hotelDescription) {
    this.hotelDescription = hotelDescription;
    return this;
  }

  /**
   * Get hotelDescription
   * @return hotelDescription
   */
  
  @Schema(name = "hotelDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelDescription")
  public String getHotelDescription() {
    return hotelDescription;
  }

  public void setHotelDescription(String hotelDescription) {
    this.hotelDescription = hotelDescription;
  }

  public HotelInformationExtendedDto hotelFacilities(List<@Valid HotelFacilityDto> hotelFacilities) {
    this.hotelFacilities = hotelFacilities;
    return this;
  }

  public HotelInformationExtendedDto addHotelFacilitiesItem(HotelFacilityDto hotelFacilitiesItem) {
    if (this.hotelFacilities == null) {
      this.hotelFacilities = new ArrayList<>();
    }
    this.hotelFacilities.add(hotelFacilitiesItem);
    return this;
  }

  /**
   * Get hotelFacilities
   * @return hotelFacilities
   */
  @Valid 
  @Schema(name = "hotelFacilities", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelFacilities")
  public List<@Valid HotelFacilityDto> getHotelFacilities() {
    return hotelFacilities;
  }

  public void setHotelFacilities(List<@Valid HotelFacilityDto> hotelFacilities) {
    this.hotelFacilities = hotelFacilities;
  }

  public HotelInformationExtendedDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public HotelInformationExtendedDto hotelOpeningDate(String hotelOpeningDate) {
    this.hotelOpeningDate = hotelOpeningDate;
    return this;
  }

  /**
   * Get hotelOpeningDate
   * @return hotelOpeningDate
   */
  
  @Schema(name = "hotelOpeningDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelOpeningDate")
  public String getHotelOpeningDate() {
    return hotelOpeningDate;
  }

  public void setHotelOpeningDate(String hotelOpeningDate) {
    this.hotelOpeningDate = hotelOpeningDate;
  }

  public HotelInformationExtendedDto importantInfo(ImportantInfoDto importantInfo) {
    this.importantInfo = importantInfo;
    return this;
  }

  /**
   * Get importantInfo
   * @return importantInfo
   */
  @Valid 
  @Schema(name = "importantInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("importantInfo")
  public ImportantInfoDto getImportantInfo() {
    return importantInfo;
  }

  public void setImportantInfo(ImportantInfoDto importantInfo) {
    this.importantInfo = importantInfo;
  }

  public HotelInformationExtendedDto languageCode(String languageCode) {
    this.languageCode = languageCode;
    return this;
  }

  /**
   * Get languageCode
   * @return languageCode
   */
  
  @Schema(name = "languageCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("languageCode")
  public String getLanguageCode() {
    return languageCode;
  }

  public void setLanguageCode(String languageCode) {
    this.languageCode = languageCode;
  }

  public HotelInformationExtendedDto links(LinksDto links) {
    this.links = links;
    return this;
  }

  /**
   * Get links
   * @return links
   */
  @Valid 
  @Schema(name = "links", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("links")
  public LinksDto getLinks() {
    return links;
  }

  public void setLinks(LinksDto links) {
    this.links = links;
  }

  public HotelInformationExtendedDto messagingFlag(MessagingFlagDto messagingFlag) {
    this.messagingFlag = messagingFlag;
    return this;
  }

  /**
   * Get messagingFlag
   * @return messagingFlag
   */
  @Valid 
  @Schema(name = "messagingFlag", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("messagingFlag")
  public MessagingFlagDto getMessagingFlag() {
    return messagingFlag;
  }

  public void setMessagingFlag(MessagingFlagDto messagingFlag) {
    this.messagingFlag = messagingFlag;
  }

  public HotelInformationExtendedDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public HotelInformationExtendedDto parkingDescription(String parkingDescription) {
    this.parkingDescription = parkingDescription;
    return this;
  }

  /**
   * Get parkingDescription
   * @return parkingDescription
   */
  
  @Schema(name = "parkingDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("parkingDescription")
  public String getParkingDescription() {
    return parkingDescription;
  }

  public void setParkingDescription(String parkingDescription) {
    this.parkingDescription = parkingDescription;
  }

  public HotelInformationExtendedDto paymentCodeTypes(List<@Valid AcceptedCreditCardDto> paymentCodeTypes) {
    this.paymentCodeTypes = paymentCodeTypes;
    return this;
  }

  public HotelInformationExtendedDto addPaymentCodeTypesItem(AcceptedCreditCardDto paymentCodeTypesItem) {
    if (this.paymentCodeTypes == null) {
      this.paymentCodeTypes = new ArrayList<>();
    }
    this.paymentCodeTypes.add(paymentCodeTypesItem);
    return this;
  }

  /**
   * Get paymentCodeTypes
   * @return paymentCodeTypes
   */
  @Valid 
  @Schema(name = "paymentCodeTypes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentCodeTypes")
  public List<@Valid AcceptedCreditCardDto> getPaymentCodeTypes() {
    return paymentCodeTypes;
  }

  public void setPaymentCodeTypes(List<@Valid AcceptedCreditCardDto> paymentCodeTypes) {
    this.paymentCodeTypes = paymentCodeTypes;
  }

  public HotelInformationExtendedDto restaurant(RestaurantDto restaurant) {
    this.restaurant = restaurant;
    return this;
  }

  /**
   * Get restaurant
   * @return restaurant
   */
  @Valid 
  @Schema(name = "restaurant", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restaurant")
  public RestaurantDto getRestaurant() {
    return restaurant;
  }

  public void setRestaurant(RestaurantDto restaurant) {
    this.restaurant = restaurant;
  }

  public HotelInformationExtendedDto roomConfiguration(RoomConfigurationDto roomConfiguration) {
    this.roomConfiguration = roomConfiguration;
    return this;
  }

  /**
   * Get roomConfiguration
   * @return roomConfiguration
   */
  @Valid 
  @Schema(name = "roomConfiguration", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomConfiguration")
  public RoomConfigurationDto getRoomConfiguration() {
    return roomConfiguration;
  }

  public void setRoomConfiguration(RoomConfigurationDto roomConfiguration) {
    this.roomConfiguration = roomConfiguration;
  }

  public HotelInformationExtendedDto satNavDirections(String satNavDirections) {
    this.satNavDirections = satNavDirections;
    return this;
  }

  /**
   * Get satNavDirections
   * @return satNavDirections
   */
  
  @Schema(name = "satNavDirections", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("satNavDirections")
  public String getSatNavDirections() {
    return satNavDirections;
  }

  public void setSatNavDirections(String satNavDirections) {
    this.satNavDirections = satNavDirections;
  }

  public HotelInformationExtendedDto thumbnailImages(List<@Valid ThumbnailImageDto> thumbnailImages) {
    this.thumbnailImages = thumbnailImages;
    return this;
  }

  public HotelInformationExtendedDto addThumbnailImagesItem(ThumbnailImageDto thumbnailImagesItem) {
    if (this.thumbnailImages == null) {
      this.thumbnailImages = new ArrayList<>();
    }
    this.thumbnailImages.add(thumbnailImagesItem);
    return this;
  }

  /**
   * Get thumbnailImages
   * @return thumbnailImages
   */
  @Valid 
  @Schema(name = "thumbnailImages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("thumbnailImages")
  public List<@Valid ThumbnailImageDto> getThumbnailImages() {
    return thumbnailImages;
  }

  public void setThumbnailImages(List<@Valid ThumbnailImageDto> thumbnailImages) {
    this.thumbnailImages = thumbnailImages;
  }

  public HotelInformationExtendedDto timeZone(String timeZone) {
    this.timeZone = timeZone;
    return this;
  }

  /**
   * Get timeZone
   * @return timeZone
   */
  
  @Schema(name = "timeZone", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("timeZone")
  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public HotelInformationExtendedDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  
  @Schema(name = "title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public HotelInformationExtendedDto topSectionImages(List<@Valid TopSectionImageDto> topSectionImages) {
    this.topSectionImages = topSectionImages;
    return this;
  }

  public HotelInformationExtendedDto addTopSectionImagesItem(TopSectionImageDto topSectionImagesItem) {
    if (this.topSectionImages == null) {
      this.topSectionImages = new ArrayList<>();
    }
    this.topSectionImages.add(topSectionImagesItem);
    return this;
  }

  /**
   * Get topSectionImages
   * @return topSectionImages
   */
  @Valid 
  @Schema(name = "topSectionImages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("topSectionImages")
  public List<@Valid TopSectionImageDto> getTopSectionImages() {
    return topSectionImages;
  }

  public void setTopSectionImages(List<@Valid TopSectionImageDto> topSectionImages) {
    this.topSectionImages = topSectionImages;
  }

  public HotelInformationExtendedDto transportInformation(List<String> transportInformation) {
    this.transportInformation = transportInformation;
    return this;
  }

  public HotelInformationExtendedDto addTransportInformationItem(String transportInformationItem) {
    if (this.transportInformation == null) {
      this.transportInformation = new ArrayList<>();
    }
    this.transportInformation.add(transportInformationItem);
    return this;
  }

  /**
   * Get transportInformation
   * @return transportInformation
   */
  
  @Schema(name = "transportInformation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("transportInformation")
  public List<String> getTransportInformation() {
    return transportInformation;
  }

  public void setTransportInformation(List<String> transportInformation) {
    this.transportInformation = transportInformation;
  }

  public HotelInformationExtendedDto tripAdvisorReviews(TripAdvisorReviewsDto tripAdvisorReviews) {
    this.tripAdvisorReviews = tripAdvisorReviews;
    return this;
  }

  /**
   * Get tripAdvisorReviews
   * @return tripAdvisorReviews
   */
  @Valid 
  @Schema(name = "tripAdvisorReviews", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tripAdvisorReviews")
  public TripAdvisorReviewsDto getTripAdvisorReviews() {
    return tripAdvisorReviews;
  }

  public void setTripAdvisorReviews(TripAdvisorReviewsDto tripAdvisorReviews) {
    this.tripAdvisorReviews = tripAdvisorReviews;
  }

  public HotelInformationExtendedDto whatThreeWords(String whatThreeWords) {
    this.whatThreeWords = whatThreeWords;
    return this;
  }

  /**
   * Get whatThreeWords
   * @return whatThreeWords
   */
  
  @Schema(name = "whatThreeWords", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("whatThreeWords")
  public String getWhatThreeWords() {
    return whatThreeWords;
  }

  public void setWhatThreeWords(String whatThreeWords) {
    this.whatThreeWords = whatThreeWords;
  }

  public HotelInformationExtendedDto roomClassConfiguration(List<@Valid Object> roomClassConfiguration) {
    this.roomClassConfiguration = roomClassConfiguration;
    return this;
  }

  public HotelInformationExtendedDto addRoomClassConfigurationItem(Object roomClassConfigurationItem) {
    if (this.roomClassConfiguration == null) {
      this.roomClassConfiguration = new ArrayList<>();
    }
    this.roomClassConfiguration.add(roomClassConfigurationItem);
    return this;
  }

  /**
   * Get roomClassConfiguration
   * @return roomClassConfiguration
   */
  
  @Schema(name = "roomClassConfiguration", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomClassConfiguration")
  public List<@Valid Object> getRoomClassConfiguration() {
    return roomClassConfiguration;
  }

  public void setRoomClassConfiguration(List<@Valid Object> roomClassConfiguration) {
    this.roomClassConfiguration = roomClassConfiguration;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelInformationExtendedDto hotelInformationExtendedDto = (HotelInformationExtendedDto) o;
    return Objects.equals(this.accessibilityInfo, hotelInformationExtendedDto.accessibilityInfo) &&
        Objects.equals(this.address, hotelInformationExtendedDto.address) &&
        Objects.equals(this.ancillaryCloseout, hotelInformationExtendedDto.ancillaryCloseout) &&
        Objects.equals(this.announcement, hotelInformationExtendedDto.announcement) &&
        Objects.equals(this.bookingFlow, hotelInformationExtendedDto.bookingFlow) &&
        Objects.equals(this.brand, hotelInformationExtendedDto.brand) &&
        Objects.equals(this.checkInTime, hotelInformationExtendedDto.checkInTime) &&
        Objects.equals(this.checkOutTime, hotelInformationExtendedDto.checkOutTime) &&
        Objects.equals(this.contactDetails, hotelInformationExtendedDto.contactDetails) &&
        Objects.equals(this.coordinates, hotelInformationExtendedDto.coordinates) &&
        Objects.equals(this.county, hotelInformationExtendedDto.county) &&
        Objects.equals(this.currencyCode, hotelInformationExtendedDto.currencyCode) &&
        Objects.equals(this.directions, hotelInformationExtendedDto.directions) &&
        Objects.equals(this.galleryImages, hotelInformationExtendedDto.galleryImages) &&
        Objects.equals(this.headline, hotelInformationExtendedDto.headline) &&
        Objects.equals(this.hotelDescription, hotelInformationExtendedDto.hotelDescription) &&
        Objects.equals(this.hotelFacilities, hotelInformationExtendedDto.hotelFacilities) &&
        Objects.equals(this.hotelId, hotelInformationExtendedDto.hotelId) &&
        Objects.equals(this.hotelOpeningDate, hotelInformationExtendedDto.hotelOpeningDate) &&
        Objects.equals(this.importantInfo, hotelInformationExtendedDto.importantInfo) &&
        Objects.equals(this.languageCode, hotelInformationExtendedDto.languageCode) &&
        Objects.equals(this.links, hotelInformationExtendedDto.links) &&
        Objects.equals(this.messagingFlag, hotelInformationExtendedDto.messagingFlag) &&
        Objects.equals(this.name, hotelInformationExtendedDto.name) &&
        Objects.equals(this.parkingDescription, hotelInformationExtendedDto.parkingDescription) &&
        Objects.equals(this.paymentCodeTypes, hotelInformationExtendedDto.paymentCodeTypes) &&
        Objects.equals(this.restaurant, hotelInformationExtendedDto.restaurant) &&
        Objects.equals(this.roomConfiguration, hotelInformationExtendedDto.roomConfiguration) &&
        Objects.equals(this.satNavDirections, hotelInformationExtendedDto.satNavDirections) &&
        Objects.equals(this.thumbnailImages, hotelInformationExtendedDto.thumbnailImages) &&
        Objects.equals(this.timeZone, hotelInformationExtendedDto.timeZone) &&
        Objects.equals(this.title, hotelInformationExtendedDto.title) &&
        Objects.equals(this.topSectionImages, hotelInformationExtendedDto.topSectionImages) &&
        Objects.equals(this.transportInformation, hotelInformationExtendedDto.transportInformation) &&
        Objects.equals(this.tripAdvisorReviews, hotelInformationExtendedDto.tripAdvisorReviews) &&
        Objects.equals(this.whatThreeWords, hotelInformationExtendedDto.whatThreeWords) &&
        Objects.equals(this.roomClassConfiguration, hotelInformationExtendedDto.roomClassConfiguration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessibilityInfo, address, ancillaryCloseout, announcement, bookingFlow, brand, checkInTime, checkOutTime, contactDetails, coordinates, county, currencyCode, directions, galleryImages, headline, hotelDescription, hotelFacilities, hotelId, hotelOpeningDate, importantInfo, languageCode, links, messagingFlag, name, parkingDescription, paymentCodeTypes, restaurant, roomConfiguration, satNavDirections, thumbnailImages, timeZone, title, topSectionImages, transportInformation, tripAdvisorReviews, whatThreeWords, roomClassConfiguration);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelInformationExtendedDto {\n");
    sb.append("    accessibilityInfo: ").append(toIndentedString(accessibilityInfo)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    ancillaryCloseout: ").append(toIndentedString(ancillaryCloseout)).append("\n");
    sb.append("    announcement: ").append(toIndentedString(announcement)).append("\n");
    sb.append("    bookingFlow: ").append(toIndentedString(bookingFlow)).append("\n");
    sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
    sb.append("    checkInTime: ").append(toIndentedString(checkInTime)).append("\n");
    sb.append("    checkOutTime: ").append(toIndentedString(checkOutTime)).append("\n");
    sb.append("    contactDetails: ").append(toIndentedString(contactDetails)).append("\n");
    sb.append("    coordinates: ").append(toIndentedString(coordinates)).append("\n");
    sb.append("    county: ").append(toIndentedString(county)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    directions: ").append(toIndentedString(directions)).append("\n");
    sb.append("    galleryImages: ").append(toIndentedString(galleryImages)).append("\n");
    sb.append("    headline: ").append(toIndentedString(headline)).append("\n");
    sb.append("    hotelDescription: ").append(toIndentedString(hotelDescription)).append("\n");
    sb.append("    hotelFacilities: ").append(toIndentedString(hotelFacilities)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    hotelOpeningDate: ").append(toIndentedString(hotelOpeningDate)).append("\n");
    sb.append("    importantInfo: ").append(toIndentedString(importantInfo)).append("\n");
    sb.append("    languageCode: ").append(toIndentedString(languageCode)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
    sb.append("    messagingFlag: ").append(toIndentedString(messagingFlag)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    parkingDescription: ").append(toIndentedString(parkingDescription)).append("\n");
    sb.append("    paymentCodeTypes: ").append(toIndentedString(paymentCodeTypes)).append("\n");
    sb.append("    restaurant: ").append(toIndentedString(restaurant)).append("\n");
    sb.append("    roomConfiguration: ").append(toIndentedString(roomConfiguration)).append("\n");
    sb.append("    satNavDirections: ").append(toIndentedString(satNavDirections)).append("\n");
    sb.append("    thumbnailImages: ").append(toIndentedString(thumbnailImages)).append("\n");
    sb.append("    timeZone: ").append(toIndentedString(timeZone)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    topSectionImages: ").append(toIndentedString(topSectionImages)).append("\n");
    sb.append("    transportInformation: ").append(toIndentedString(transportInformation)).append("\n");
    sb.append("    tripAdvisorReviews: ").append(toIndentedString(tripAdvisorReviews)).append("\n");
    sb.append("    whatThreeWords: ").append(toIndentedString(whatThreeWords)).append("\n");
    sb.append("    roomClassConfiguration: ").append(toIndentedString(roomClassConfiguration)).append("\n");
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

