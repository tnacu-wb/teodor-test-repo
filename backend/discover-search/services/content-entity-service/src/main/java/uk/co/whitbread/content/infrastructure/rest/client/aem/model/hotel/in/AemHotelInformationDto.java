package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AemHotelInformationDto {

  private List<String> leftList;
  private List<String> rightList;
  private ContactDetails contactDetails;
  private Location location;
  private List<AcceptedCreditCard> acceptedCreditCards;
  private List<PaymentMethodsConfiguration> paymentMethodsConfiguration;
  private java.util.Map<String, String> paymentMethodsOpera;
  private List<PaymentProvider> paymentProviders;
  private Address address;
  private Map map;
  private List<Image> images;
  private List<Facility> facilities;
  private GuestRating guestRating;
  private TripAdvisor tripAdvisor;
  private MessagingFlag messagingFlag;
  private Links links;
  private HotelRoomConfiguration hotelRoomConfiguration;
  private CityTax cityTax;
  private List<Donation> donations;
  private BookingFlow bookingFlow;
  private Restaurant restaurant;
  private RestaurantTime restaurantTime;
  private ThreeWords threeWords;
  private Announcement announcement;
  private List<@NotNull Breadcrumb> breadcrumb;
  private LocalInfo localInfo;
  private Faq faq;
  private Facts facts;
  private TripAdvisorReviews tripAdvisorReviews;
  private List<TopSectionImage> topSectionImages;
  private SatNav satNav;
  private String hotelDescription;
  private String hotelOpeningDate;
  private String hotelSpfDescription;
  private String hotelDirections;
  private String parkingDescription;
  private String hotelGooglePlus;
  private String parkName;
  private String img;
  private String distanceOverride;
  private String usingNewBookingFlow;
  private String code;
  private String name;
  private String title;
  private String headline;
  private String brand;
  private String countryCodeISO;
  private boolean authenticationRequired;
  private Boolean isKioskQRCodeScanEnabled;
  private ImportantInfo importantInfo;
  private AccessibilityInfoDto accessibilityInfo;
  private String pageTitle;
  private String pageDescription;
  private AncillaryCloseout ancillaryCloseout;
  private java.util.Map<String, String> serviceCodeAndUpsellCodeMapping;
  private FacilityCloseout facilityCloseout;
  private Seo seo;
  private java.util.Map<String, RoomClassConfiguration> roomClassConfiguration;
  private BookRestaurantCta bookRestaurantCta;
  private HotelFlags hotelFlags;
  private Boolean isDataTransEnabled;
  private CutoffMinutes cutoffMinutes;
}
