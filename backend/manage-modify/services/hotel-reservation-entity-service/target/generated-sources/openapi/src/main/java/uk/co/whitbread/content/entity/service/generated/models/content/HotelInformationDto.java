package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.AccessibilityInfoDto;
import uk.co.whitbread.content.entity.service.generated.models.content.AddressDto;
import uk.co.whitbread.content.entity.service.generated.models.content.AnnouncementDto;
import uk.co.whitbread.content.entity.service.generated.models.content.BookRestaurantCtaDto;
import uk.co.whitbread.content.entity.service.generated.models.content.BookingFlowDto;
import uk.co.whitbread.content.entity.service.generated.models.content.Breadcrumb;
import uk.co.whitbread.content.entity.service.generated.models.content.ContactDetailsDto;
import uk.co.whitbread.content.entity.service.generated.models.content.CoordinatesDto;
import uk.co.whitbread.content.entity.service.generated.models.content.Facts;
import uk.co.whitbread.content.entity.service.generated.models.content.Faq;
import uk.co.whitbread.content.entity.service.generated.models.content.GalleryImageDto;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelCityTaxDto;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelFacilityDto;
import uk.co.whitbread.content.entity.service.generated.models.content.ImportantInfoDto;
import uk.co.whitbread.content.entity.service.generated.models.content.LinksDto;
import uk.co.whitbread.content.entity.service.generated.models.content.MessagingFlagDto;
import uk.co.whitbread.content.entity.service.generated.models.content.RestaurantDto;
import uk.co.whitbread.content.entity.service.generated.models.content.RoomClassConfigurationDto;
import uk.co.whitbread.content.entity.service.generated.models.content.RoomConfigurationDto;
import uk.co.whitbread.content.entity.service.generated.models.content.SeoDto;
import uk.co.whitbread.content.entity.service.generated.models.content.ThumbnailImageDto;
import uk.co.whitbread.content.entity.service.generated.models.content.TopSectionImageDto;
import uk.co.whitbread.content.entity.service.generated.models.content.TripAdvisorReviews;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * HotelInformationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelInformationDto {

  private @Nullable AccessibilityInfoDto accessibilityInfo;

  private @Nullable AddressDto address;

  private @Nullable AnnouncementDto announcement;

  private @Nullable BookRestaurantCtaDto bookRestaurantCta;

  private @Nullable BookingFlowDto bookingFlow;

  private @Nullable String brand;

  @Valid
  private List<@Valid Breadcrumb> breadcrumb = new ArrayList<>();

  private @Nullable HotelCityTaxDto cityTax;

  private @Nullable ContactDetailsDto contactDetails;

  private @Nullable CoordinatesDto coordinates;

  private @Nullable String directions;

  private @Nullable Double distanceFromReference;

  private @Nullable Facts facts;

  private @Nullable Faq faq;

  @Valid
  private List<@Valid GalleryImageDto> galleryImages = new ArrayList<>();

  private @Nullable String headline;

  private @Nullable String hotelDescription;

  @Valid
  private List<@Valid HotelFacilityDto> hotelFacilities = new ArrayList<>();

  private @Nullable String hotelId;

  private @Nullable String hotelOpeningDate;

  private @Nullable ImportantInfoDto importantInfo;

  private @Nullable Boolean isKioskQRCodeScanEnabled;

  private @Nullable LinksDto links;

  private @Nullable MessagingFlagDto messagingFlag;

  private @Nullable String name;

  private @Nullable String parkingDescription;

  private @Nullable RestaurantDto restaurant;

  @Valid
  private List<@Valid RoomClassConfigurationDto> roomClassConfiguration = new ArrayList<>();

  private @Nullable RoomConfigurationDto roomConfiguration;

  private @Nullable String satNavDirections;

  private @Nullable SeoDto seo;

  @Valid
  private List<@Valid ThumbnailImageDto> thumbnailImages = new ArrayList<>();

  private @Nullable String title;

  @Valid
  private List<@Valid TopSectionImageDto> topSectionImages = new ArrayList<>();

  @Valid
  private List<String> transportInformation = new ArrayList<>();

  private @Nullable TripAdvisorReviews tripAdvisorReviews;

  private @Nullable String whatThreeWords;

  public HotelInformationDto accessibilityInfo(AccessibilityInfoDto accessibilityInfo) {
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

  public HotelInformationDto address(AddressDto address) {
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

  public HotelInformationDto announcement(AnnouncementDto announcement) {
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

  public HotelInformationDto bookRestaurantCta(BookRestaurantCtaDto bookRestaurantCta) {
    this.bookRestaurantCta = bookRestaurantCta;
    return this;
  }

  /**
   * Get bookRestaurantCta
   * @return bookRestaurantCta
   */
  @Valid 
  @Schema(name = "bookRestaurantCta", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookRestaurantCta")
  public BookRestaurantCtaDto getBookRestaurantCta() {
    return bookRestaurantCta;
  }

  public void setBookRestaurantCta(BookRestaurantCtaDto bookRestaurantCta) {
    this.bookRestaurantCta = bookRestaurantCta;
  }

  public HotelInformationDto bookingFlow(BookingFlowDto bookingFlow) {
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

  public HotelInformationDto brand(String brand) {
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

  public HotelInformationDto breadcrumb(List<@Valid Breadcrumb> breadcrumb) {
    this.breadcrumb = breadcrumb;
    return this;
  }

  public HotelInformationDto addBreadcrumbItem(Breadcrumb breadcrumbItem) {
    if (this.breadcrumb == null) {
      this.breadcrumb = new ArrayList<>();
    }
    this.breadcrumb.add(breadcrumbItem);
    return this;
  }

  /**
   * Get breadcrumb
   * @return breadcrumb
   */
  @Valid 
  @Schema(name = "breadcrumb", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("breadcrumb")
  public List<@Valid Breadcrumb> getBreadcrumb() {
    return breadcrumb;
  }

  public void setBreadcrumb(List<@Valid Breadcrumb> breadcrumb) {
    this.breadcrumb = breadcrumb;
  }

  public HotelInformationDto cityTax(HotelCityTaxDto cityTax) {
    this.cityTax = cityTax;
    return this;
  }

  /**
   * Get cityTax
   * @return cityTax
   */
  @Valid 
  @Schema(name = "cityTax", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cityTax")
  public HotelCityTaxDto getCityTax() {
    return cityTax;
  }

  public void setCityTax(HotelCityTaxDto cityTax) {
    this.cityTax = cityTax;
  }

  public HotelInformationDto contactDetails(ContactDetailsDto contactDetails) {
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

  public HotelInformationDto coordinates(CoordinatesDto coordinates) {
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

  public HotelInformationDto directions(String directions) {
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

  public HotelInformationDto distanceFromReference(Double distanceFromReference) {
    this.distanceFromReference = distanceFromReference;
    return this;
  }

  /**
   * Get distanceFromReference
   * @return distanceFromReference
   */
  
  @Schema(name = "distanceFromReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("distanceFromReference")
  public Double getDistanceFromReference() {
    return distanceFromReference;
  }

  public void setDistanceFromReference(Double distanceFromReference) {
    this.distanceFromReference = distanceFromReference;
  }

  public HotelInformationDto facts(Facts facts) {
    this.facts = facts;
    return this;
  }

  /**
   * Get facts
   * @return facts
   */
  @Valid 
  @Schema(name = "facts", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("facts")
  public Facts getFacts() {
    return facts;
  }

  public void setFacts(Facts facts) {
    this.facts = facts;
  }

  public HotelInformationDto faq(Faq faq) {
    this.faq = faq;
    return this;
  }

  /**
   * Get faq
   * @return faq
   */
  @Valid 
  @Schema(name = "faq", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("faq")
  public Faq getFaq() {
    return faq;
  }

  public void setFaq(Faq faq) {
    this.faq = faq;
  }

  public HotelInformationDto galleryImages(List<@Valid GalleryImageDto> galleryImages) {
    this.galleryImages = galleryImages;
    return this;
  }

  public HotelInformationDto addGalleryImagesItem(GalleryImageDto galleryImagesItem) {
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

  public HotelInformationDto headline(String headline) {
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

  public HotelInformationDto hotelDescription(String hotelDescription) {
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

  public HotelInformationDto hotelFacilities(List<@Valid HotelFacilityDto> hotelFacilities) {
    this.hotelFacilities = hotelFacilities;
    return this;
  }

  public HotelInformationDto addHotelFacilitiesItem(HotelFacilityDto hotelFacilitiesItem) {
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

  public HotelInformationDto hotelId(String hotelId) {
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

  public HotelInformationDto hotelOpeningDate(String hotelOpeningDate) {
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

  public HotelInformationDto importantInfo(ImportantInfoDto importantInfo) {
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

  public HotelInformationDto isKioskQRCodeScanEnabled(Boolean isKioskQRCodeScanEnabled) {
    this.isKioskQRCodeScanEnabled = isKioskQRCodeScanEnabled;
    return this;
  }

  /**
   * Get isKioskQRCodeScanEnabled
   * @return isKioskQRCodeScanEnabled
   */
  
  @Schema(name = "isKioskQRCodeScanEnabled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isKioskQRCodeScanEnabled")
  public Boolean getIsKioskQRCodeScanEnabled() {
    return isKioskQRCodeScanEnabled;
  }

  public void setIsKioskQRCodeScanEnabled(Boolean isKioskQRCodeScanEnabled) {
    this.isKioskQRCodeScanEnabled = isKioskQRCodeScanEnabled;
  }

  public HotelInformationDto links(LinksDto links) {
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

  public HotelInformationDto messagingFlag(MessagingFlagDto messagingFlag) {
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

  public HotelInformationDto name(String name) {
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

  public HotelInformationDto parkingDescription(String parkingDescription) {
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

  public HotelInformationDto restaurant(RestaurantDto restaurant) {
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

  public HotelInformationDto roomClassConfiguration(List<@Valid RoomClassConfigurationDto> roomClassConfiguration) {
    this.roomClassConfiguration = roomClassConfiguration;
    return this;
  }

  public HotelInformationDto addRoomClassConfigurationItem(RoomClassConfigurationDto roomClassConfigurationItem) {
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
  @Valid 
  @Schema(name = "roomClassConfiguration", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomClassConfiguration")
  public List<@Valid RoomClassConfigurationDto> getRoomClassConfiguration() {
    return roomClassConfiguration;
  }

  public void setRoomClassConfiguration(List<@Valid RoomClassConfigurationDto> roomClassConfiguration) {
    this.roomClassConfiguration = roomClassConfiguration;
  }

  public HotelInformationDto roomConfiguration(RoomConfigurationDto roomConfiguration) {
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

  public HotelInformationDto satNavDirections(String satNavDirections) {
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

  public HotelInformationDto seo(SeoDto seo) {
    this.seo = seo;
    return this;
  }

  /**
   * Get seo
   * @return seo
   */
  @Valid 
  @Schema(name = "seo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("seo")
  public SeoDto getSeo() {
    return seo;
  }

  public void setSeo(SeoDto seo) {
    this.seo = seo;
  }

  public HotelInformationDto thumbnailImages(List<@Valid ThumbnailImageDto> thumbnailImages) {
    this.thumbnailImages = thumbnailImages;
    return this;
  }

  public HotelInformationDto addThumbnailImagesItem(ThumbnailImageDto thumbnailImagesItem) {
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

  public HotelInformationDto title(String title) {
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

  public HotelInformationDto topSectionImages(List<@Valid TopSectionImageDto> topSectionImages) {
    this.topSectionImages = topSectionImages;
    return this;
  }

  public HotelInformationDto addTopSectionImagesItem(TopSectionImageDto topSectionImagesItem) {
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

  public HotelInformationDto transportInformation(List<String> transportInformation) {
    this.transportInformation = transportInformation;
    return this;
  }

  public HotelInformationDto addTransportInformationItem(String transportInformationItem) {
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

  public HotelInformationDto tripAdvisorReviews(TripAdvisorReviews tripAdvisorReviews) {
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
  public TripAdvisorReviews getTripAdvisorReviews() {
    return tripAdvisorReviews;
  }

  public void setTripAdvisorReviews(TripAdvisorReviews tripAdvisorReviews) {
    this.tripAdvisorReviews = tripAdvisorReviews;
  }

  public HotelInformationDto whatThreeWords(String whatThreeWords) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelInformationDto hotelInformationDto = (HotelInformationDto) o;
    return Objects.equals(this.accessibilityInfo, hotelInformationDto.accessibilityInfo) &&
        Objects.equals(this.address, hotelInformationDto.address) &&
        Objects.equals(this.announcement, hotelInformationDto.announcement) &&
        Objects.equals(this.bookRestaurantCta, hotelInformationDto.bookRestaurantCta) &&
        Objects.equals(this.bookingFlow, hotelInformationDto.bookingFlow) &&
        Objects.equals(this.brand, hotelInformationDto.brand) &&
        Objects.equals(this.breadcrumb, hotelInformationDto.breadcrumb) &&
        Objects.equals(this.cityTax, hotelInformationDto.cityTax) &&
        Objects.equals(this.contactDetails, hotelInformationDto.contactDetails) &&
        Objects.equals(this.coordinates, hotelInformationDto.coordinates) &&
        Objects.equals(this.directions, hotelInformationDto.directions) &&
        Objects.equals(this.distanceFromReference, hotelInformationDto.distanceFromReference) &&
        Objects.equals(this.facts, hotelInformationDto.facts) &&
        Objects.equals(this.faq, hotelInformationDto.faq) &&
        Objects.equals(this.galleryImages, hotelInformationDto.galleryImages) &&
        Objects.equals(this.headline, hotelInformationDto.headline) &&
        Objects.equals(this.hotelDescription, hotelInformationDto.hotelDescription) &&
        Objects.equals(this.hotelFacilities, hotelInformationDto.hotelFacilities) &&
        Objects.equals(this.hotelId, hotelInformationDto.hotelId) &&
        Objects.equals(this.hotelOpeningDate, hotelInformationDto.hotelOpeningDate) &&
        Objects.equals(this.importantInfo, hotelInformationDto.importantInfo) &&
        Objects.equals(this.isKioskQRCodeScanEnabled, hotelInformationDto.isKioskQRCodeScanEnabled) &&
        Objects.equals(this.links, hotelInformationDto.links) &&
        Objects.equals(this.messagingFlag, hotelInformationDto.messagingFlag) &&
        Objects.equals(this.name, hotelInformationDto.name) &&
        Objects.equals(this.parkingDescription, hotelInformationDto.parkingDescription) &&
        Objects.equals(this.restaurant, hotelInformationDto.restaurant) &&
        Objects.equals(this.roomClassConfiguration, hotelInformationDto.roomClassConfiguration) &&
        Objects.equals(this.roomConfiguration, hotelInformationDto.roomConfiguration) &&
        Objects.equals(this.satNavDirections, hotelInformationDto.satNavDirections) &&
        Objects.equals(this.seo, hotelInformationDto.seo) &&
        Objects.equals(this.thumbnailImages, hotelInformationDto.thumbnailImages) &&
        Objects.equals(this.title, hotelInformationDto.title) &&
        Objects.equals(this.topSectionImages, hotelInformationDto.topSectionImages) &&
        Objects.equals(this.transportInformation, hotelInformationDto.transportInformation) &&
        Objects.equals(this.tripAdvisorReviews, hotelInformationDto.tripAdvisorReviews) &&
        Objects.equals(this.whatThreeWords, hotelInformationDto.whatThreeWords);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessibilityInfo, address, announcement, bookRestaurantCta, bookingFlow, brand, breadcrumb, cityTax, contactDetails, coordinates, directions, distanceFromReference, facts, faq, galleryImages, headline, hotelDescription, hotelFacilities, hotelId, hotelOpeningDate, importantInfo, isKioskQRCodeScanEnabled, links, messagingFlag, name, parkingDescription, restaurant, roomClassConfiguration, roomConfiguration, satNavDirections, seo, thumbnailImages, title, topSectionImages, transportInformation, tripAdvisorReviews, whatThreeWords);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelInformationDto {\n");
    sb.append("    accessibilityInfo: ").append(toIndentedString(accessibilityInfo)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    announcement: ").append(toIndentedString(announcement)).append("\n");
    sb.append("    bookRestaurantCta: ").append(toIndentedString(bookRestaurantCta)).append("\n");
    sb.append("    bookingFlow: ").append(toIndentedString(bookingFlow)).append("\n");
    sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
    sb.append("    breadcrumb: ").append(toIndentedString(breadcrumb)).append("\n");
    sb.append("    cityTax: ").append(toIndentedString(cityTax)).append("\n");
    sb.append("    contactDetails: ").append(toIndentedString(contactDetails)).append("\n");
    sb.append("    coordinates: ").append(toIndentedString(coordinates)).append("\n");
    sb.append("    directions: ").append(toIndentedString(directions)).append("\n");
    sb.append("    distanceFromReference: ").append(toIndentedString(distanceFromReference)).append("\n");
    sb.append("    facts: ").append(toIndentedString(facts)).append("\n");
    sb.append("    faq: ").append(toIndentedString(faq)).append("\n");
    sb.append("    galleryImages: ").append(toIndentedString(galleryImages)).append("\n");
    sb.append("    headline: ").append(toIndentedString(headline)).append("\n");
    sb.append("    hotelDescription: ").append(toIndentedString(hotelDescription)).append("\n");
    sb.append("    hotelFacilities: ").append(toIndentedString(hotelFacilities)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    hotelOpeningDate: ").append(toIndentedString(hotelOpeningDate)).append("\n");
    sb.append("    importantInfo: ").append(toIndentedString(importantInfo)).append("\n");
    sb.append("    isKioskQRCodeScanEnabled: ").append(toIndentedString(isKioskQRCodeScanEnabled)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
    sb.append("    messagingFlag: ").append(toIndentedString(messagingFlag)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    parkingDescription: ").append(toIndentedString(parkingDescription)).append("\n");
    sb.append("    restaurant: ").append(toIndentedString(restaurant)).append("\n");
    sb.append("    roomClassConfiguration: ").append(toIndentedString(roomClassConfiguration)).append("\n");
    sb.append("    roomConfiguration: ").append(toIndentedString(roomConfiguration)).append("\n");
    sb.append("    satNavDirections: ").append(toIndentedString(satNavDirections)).append("\n");
    sb.append("    seo: ").append(toIndentedString(seo)).append("\n");
    sb.append("    thumbnailImages: ").append(toIndentedString(thumbnailImages)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    topSectionImages: ").append(toIndentedString(topSectionImages)).append("\n");
    sb.append("    transportInformation: ").append(toIndentedString(transportInformation)).append("\n");
    sb.append("    tripAdvisorReviews: ").append(toIndentedString(tripAdvisorReviews)).append("\n");
    sb.append("    whatThreeWords: ").append(toIndentedString(whatThreeWords)).append("\n");
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

