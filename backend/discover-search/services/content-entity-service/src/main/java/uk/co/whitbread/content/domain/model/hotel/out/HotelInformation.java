package uk.co.whitbread.content.domain.model.hotel.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class HotelInformation {

  private List<GalleryImage> galleryImages;
  private List<HotelFacility> hotelFacilities;
  private List<String> transportInformation;
  private RoomConfiguration roomConfiguration;
  private String directions;
  private String brand;
  private String name;
  private String hotelId;
  private String hotelDescription;
  private Announcement announcement;
  private String satNavDirections;
  private String headline;
  private String hotelOpeningDate;
  private Restaurant restaurant;
  private String parkingDescription;
  private Address address;
  private Links links;
  private BookingFlow bookingFlow;
  private Coordinates coordinates;
  private String whatThreeWords;
  private ContactDetails contactDetails;
  private MessagingFlag messagingFlag;
  private List<ThumbnailImage> thumbnailImages;
  private ImportantInfo importantInfo;
  private AccessibilityInfo accessibilityInfo;
  private String pageTitle;
  private String title;
  private String pageDescription;
  private List<TopSectionImage> topSectionImages;
  private List<AcceptedCreditCard> paymentCodeTypes;
  private String county;
  private Faq faq;
  private Facts facts;
  private TripAdvisorReviews tripAdvisorReviews;
  private List<Breadcrumb> breadcrumb;
  private AncillaryCloseout ancillaryCloseout;
  private FacilityCloseout facilityCloseout;
  private Seo seo;
  private Double distanceFromReference;
  private List<RoomClassConfiguration> roomClassConfiguration;
  private CityTax cityTax;
  private String countryCodeISO;
  private Boolean isKioskQRCodeScanEnabled;
  private BookRestaurantCta bookRestaurantCta;
  private HotelFlags hotelFlags;
  private Boolean isDataTransEnabled;
  private List<ExtraCutoff> extrasCutoffs;
}
