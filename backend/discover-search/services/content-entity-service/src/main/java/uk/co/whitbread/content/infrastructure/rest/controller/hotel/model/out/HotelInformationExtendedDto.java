package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class HotelInformationExtendedDto {

  private List<GalleryImageDto> galleryImages;
  private List<HotelFacilityDto> hotelFacilities;
  private List<String> transportInformation;
  private RoomConfigurationDto roomConfiguration;
  private String brand;
  private String name;
  private String title;
  private String hotelId;
  private String headline;
  private String hotelDescription;
  private AnnouncementDto announcement;
  private String hotelOpeningDate;
  private RestaurantDto restaurant;
  private String directions;
  private String satNavDirections;
  private AddressDto address;
  private String parkingDescription;
  private LinksDto links;
  private BookingFlowDto bookingFlow;
  private CoordinatesDto coordinates;
  private String whatThreeWords;
  private ContactDetailsDto contactDetails;
  private MessagingFlagDto messagingFlag;
  private List<ThumbnailImageDto> thumbnailImages;
  private ImportantInfoDto importantInfo;
  private AccessibilityInfoDto accessibilityInfo;
  private List<TopSectionImageDto> topSectionImages;
  private List<AcceptedCreditCardDto> paymentCodeTypes;
  private String currencyCode;
  private String languageCode;
  private String timeZone;
  private String checkOutTime;
  private String checkInTime;
  private String county;
  private AncillaryCloseoutDto ancillaryCloseout;
  private TripAdvisorReviewsDto tripAdvisorReviews;
  private List<RoomClassConfigurationDto> roomClassConfiguration;
  private HotelCityTaxDto cityTax;
  private Boolean isKioskQRCodeScanEnabled;
  private String countryCodeISO;
  private BookRestaurantCtaDto bookRestaurantCta;
  private SeoDto seo;
  private HotelFlagsDto hotelFlags;
  private Boolean isDataTransEnabled;
  private List<ExtraCutoffDto> extrasCutoffs;
}