package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import static uk.co.whitbread.content.infrastructure.rest.client.utils.HotelInformationUtils.haversineDistance;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.content.domain.model.hotel.out.Awards;
import uk.co.whitbread.content.domain.model.hotel.out.Breadcrumb;
import uk.co.whitbread.content.domain.model.hotel.out.ExtraCutoff;
import uk.co.whitbread.content.domain.model.hotel.out.FactItem;
import uk.co.whitbread.content.domain.model.hotel.out.FaqItem;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInformation;
import uk.co.whitbread.content.domain.model.hotel.out.HotelPaymentInformation;
import uk.co.whitbread.content.domain.model.hotel.out.HotelShortInformation;
import uk.co.whitbread.content.domain.model.hotel.out.Reviews;
import uk.co.whitbread.content.domain.model.hotel.out.RoomClassConfiguration;
import uk.co.whitbread.content.domain.model.hotel.out.SubRatings;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.AemHotelInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.CutoffExtra;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.HotelShortInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.InfoItem;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.TripAdvisorReviews;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.TripAdvisorReviewsDto;


@Mapper(componentModel = "spring", uses = {
    AddressMapper.class,
    HotelFacilityMapper.class,
    HotelGalleryImageMapper.class,
    RestaurantMapper.class,
    RoomConfigurationMapper.class,
    ThumbnailImageMapper.class,
    BookingFlowItemMapper.class }, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface HotelInformationMapper {

  TripAdvisorReviews toModel(TripAdvisorReviewsDto tripAdvisorReviewsDto);

  @Mapping(source = "roomClassConfiguration", target = "roomClassConfiguration",
      qualifiedByName = "roomClassConfigurationMapper")
  @Mapping(source = "code", target = "hotelId")
  @Mapping(source = "facilities", target = "hotelFacilities")
  @Mapping(source = "topSectionImages", target = "galleryImages")
  @Mapping(source = "hotelDirections", target = "directions")
  @Mapping(source = "satNav.postCode", target = "satNavDirections")
  @Mapping(source = "localInfo.infoItems", target = "transportInformation",
      qualifiedByName = "transportInformationCustomMapper")
  @Mapping(target = "isKioskQRCodeScanEnabled", source = "isKioskQRCodeScanEnabled")
  @Mapping(target = "countryCodeISO", source = "countryCodeISO")
  @Mapping(source = "hotelRoomConfiguration", target = "roomConfiguration")
  @Mapping(source = "map", target = "coordinates")
  @Mapping(source = "threeWords.threeWords", target = "whatThreeWords")
  @Mapping(source = "messagingFlag.flagText", target = "messagingFlag.text")
  @Mapping(source = "messagingFlag.flagDescription", target = "messagingFlag.description")
  @Mapping(source = "messagingFlag.flagColor", target = "messagingFlag.color")
  @Mapping(source = "images", target = "thumbnailImages")
  @Mapping(source = "acceptedCreditCards", target = "paymentCodeTypes")
  @Mapping(source = "location.county", target = "county")
  @Mapping(target = "faq.faqItems",
      expression = "java(toFaqItemsDomainModel(faq.getFaqItems()))")
  @Mapping(target = "facts.factItems",
      expression = "java(toFactItemsDomainModel(facts.getFactItems()))")
  @Mapping(target = "breadcrumb.breadcrumb",
      expression = "java(toBreadcrumbDomainModel(breadcrumb.breadcrumb()))")
  @Mapping(target = "tripAdvisorReviews.awards",
      expression = "java(toAwardsDomainModel(tripAdvisorReviews.getAwards()))")
  @Mapping(target = "tripAdvisorReviews.subRatings",
      expression = "java(toSubRatingsDomainModel(tripAdvisorReviews.getSubRatings()))")
  @Mapping(target = "tripAdvisorReviews.reviews",
      expression = "java(toReviewsDomainModel(tripAdvisorReviews.getReviews()))")
  @Mapping(target = "distanceFromReference", ignore = true)
  @Mapping(source = "cutoffMinutes.extras", target = "extrasCutoffs")
  HotelInformation toDomainModel(AemHotelInformationDto aemHotelInformationDto);

  default HotelInformation toDomainModel(AemHotelInformationDto aemHotelInfo,
          Double latitudeRef, Double longitudeRef, boolean useMiles) {

    HotelInformation hotelInformation = toDomainModel(aemHotelInfo);
    if (aemHotelInfo.getMap() != null) {
      Double hotelLatitude = aemHotelInfo.getMap().getLatitude();
      Double hotelLongitude = aemHotelInfo.getMap().getLongitude();

      if (hotelLatitude != null && hotelLongitude != null
              && latitudeRef != null && longitudeRef != null) {

        Double distanceFromReference = haversineDistance(
                latitudeRef, longitudeRef, hotelLatitude, hotelLongitude, useMiles);
        hotelInformation.setDistanceFromReference(distanceFromReference);
      }
    }
    return hotelInformation;
  }

  HotelShortInformation toShortHotelDomainModel(HotelShortInformationDto hotelShortInformationDto);

  @Mapping(source = "acceptedAnswer", target = "answer")
  FaqItem toFaqItemDomainModel(
      uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.FaqItem faqItem);

  List<FaqItem> toFaqItemsDomainModel(
      List<uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.FaqItem> faqItems);

  Awards toAwardDomainModel(
      uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.Awards award);

  List<Awards> toAwardsDomainModel(
      List<uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.Awards> awards);

  SubRatings toSubRatingDomainModel(
      uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.SubRatings subRating);

  List<SubRatings> toSubRatingsDomainModel(
      List<uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.SubRatings> subRatings);

  Reviews toReviewDomainModel(
      uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.Reviews review);

  List<Reviews> toReviewsDomainModel(
      List<uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.Reviews> reviews);

  @Mapping(source = "description", target = "description")
  FactItem toFactItemDomainModel(
      uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.FactItem factItem);

  List<FactItem> toFactItemsDomainModel(
      List<uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.FactItem>
          factItems);

  Breadcrumb toBreadcrumbDomainModel(
      uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.Breadcrumb breadcrumb);

  HotelPaymentInformation toHotelPaymentInformationModel(
      AemHotelInformationDto aemHotelInformationDto);

  @Named("transportInformationCustomMapper")
  default List<String> toTransportInformationDto(List<InfoItem> infoItemsList) {
    return infoItemsList.stream().map(InfoItem::getText).toList();
  }

  @Named("roomClassConfigurationMapper")
  default List<RoomClassConfiguration> toRoomClassConfigurationModel(
      Map<String,
          uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.RoomClassConfiguration> roomClassMap) {

    if (roomClassMap == null || roomClassMap.isEmpty()) {
      return Collections.emptyList();
    }

    return roomClassMap.values().stream()
        .filter(Objects::nonNull)
        .map(this::toRoomClassConfigurationModel)
        .toList();
  }

  default RoomClassConfiguration toRoomClassConfigurationModel(
      uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.RoomClassConfiguration source) {

    if (source == null) {
      return null;
    }

    RoomClassConfiguration target = new RoomClassConfiguration();
    target.setCode(source.getCode());
    target.setTitle(source.getTitle());
    return target;
  }

  @Mapping(source = "ciol", target = "cutOffMinutesCIOL")
  @Mapping(source = "booking", target = "cutOffMinutesBooking")
  @Mapping(source = "cutoffWindowMessage", target = "message")
  ExtraCutoff toExtraCutoffModel(CutoffExtra cutoffExtra);

  List<ExtraCutoff> toExtraCutoffModel(List<CutoffExtra> cutoffExtras);
}
