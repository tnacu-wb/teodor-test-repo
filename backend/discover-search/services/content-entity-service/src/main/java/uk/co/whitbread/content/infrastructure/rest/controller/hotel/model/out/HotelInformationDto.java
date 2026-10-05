package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.domain.model.hotel.out.Breadcrumb;
import uk.co.whitbread.content.domain.model.hotel.out.Facts;
import uk.co.whitbread.content.domain.model.hotel.out.Faq;
import uk.co.whitbread.content.domain.model.hotel.out.TripAdvisorReviews;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class HotelInformationDto {

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
  private Faq faq;
  private Facts facts;
  private List<Breadcrumb> breadcrumb;
  private TripAdvisorReviews tripAdvisorReviews;
  private SeoDto seo;
  private Double distanceFromReference;
  private List<RoomClassConfigurationDto> roomClassConfiguration;
  private HotelCityTaxDto cityTax;
  private Boolean isKioskQRCodeScanEnabled;
  private String countryCodeISO;
  private BookRestaurantCtaDto bookRestaurantCta;
  private HotelFlagsDto hotelFlags;
}