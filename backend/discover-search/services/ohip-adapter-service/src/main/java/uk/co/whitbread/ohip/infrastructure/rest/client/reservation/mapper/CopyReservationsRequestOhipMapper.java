package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChildAgeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CreateReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ExternalReferenceType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.GuestCountsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.GuaranteeCodeTypeEnumDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.PromotionUtils;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class CopyReservationsRequestOhipMapper {

  private ReservationOhipProperties reservationOhipProperties;

  @Autowired
  public final void toReservationOhipPropertiesForModel(
      ReservationOhipProperties reservationOhipProperties) {
    this.reservationOhipProperties = reservationOhipProperties;
  }

  @Mapping(target = "reservations", source = "reservation.reservations")
  @Mapping(target = "fetchInstructions", ignore = true)
  @Mapping(target = "reservationsInstructionsType", ignore = true)
  @Mapping(target = "channelInformation", ignore = true)
  @Mapping(target = "links", ignore = true)
  @Mapping(target = "warnings", ignore = true)
  public abstract CreateReservation toCreateReservationDto(Reservation reservation,
      String externalReferenceId);

  @AfterMapping
  public void toCreateReservationWithMissingFieldsDto(String externalReferenceId,
      @MappingTarget CreateReservation createReservation) {
    createReservation
        .getReservations()
        .getReservation()
        .stream()
        .findFirst()
        .ifPresent(hotelReservationType -> {
          hotelReservationType.setReservationIdList(null);
          injectExternalReferences(hotelReservationType, externalReferenceId);
          injectGuarantee(hotelReservationType);
          injectGuestChildAges(hotelReservationType);
          injectFixedRate(hotelReservationType);
          injectOverrideInventoryCheck(hotelReservationType);
          removeEciLcoPackages(hotelReservationType);
          PromotionUtils.truncatePromotion(hotelReservationType);
        });
  }

  private void injectOverrideInventoryCheck(HotelReservationType hotelReservationType) {
    hotelReservationType.setOverrideInventoryCheck(true);
  }

  private void injectGuarantee(HotelReservationType hotelReservationType) {
    Optional.ofNullable(hotelReservationType.getRoomStay())
        .ifPresent(roomStayType -> {
          roomStayType.getGuarantee().setOnHold(true);
          roomStayType.getGuarantee().setGuaranteeCode(GuaranteeCodeTypeEnumDto.ON_HOLD.value());
        });
  }

  private void injectGuestChildAges(HotelReservationType hotelReservationType) {
    Optional.ofNullable(hotelReservationType.getRoomStay())
        .ifPresent(roomStayType -> {
          Optional.ofNullable(roomStayType.getGuestCounts())
              .ifPresent(guestCountsType -> roomStayType.getGuestCounts()
                  .setChildAges(buildChildAgeTypes(guestCountsType)));

          roomStayType.getRoomRates()
              .forEach(roomRateType -> Optional.ofNullable(roomRateType.getGuestCounts())
                  .ifPresent(guestCountsType -> roomRateType.getGuestCounts()
                      .setChildAges(buildChildAgeTypes(guestCountsType))));
        });
  }

  private void injectFixedRate(HotelReservationType hotelReservationType) {
    Optional.ofNullable(hotelReservationType.getRoomStay())
        .ifPresent(roomStayType -> roomStayType.getRoomRates()
            .forEach(roomRateType -> roomRateType.setFixedRate(true)));
  }

  private void injectExternalReferences(HotelReservationType hotelReservationType,
      String externalReferenceId) {
    var externalReferenceType = new ExternalReferenceType();
    externalReferenceType.setId(externalReferenceId);
    externalReferenceType.setIdContext(reservationOhipProperties.getContextId());
    hotelReservationType.setExternalReferences(List.of(externalReferenceType));
  }

  /**
   * Currently the Early Check In/Late Check Out packages can be added only at booking creation time.
   *
   * <p>These packages can't be amended because of item inventory issues.
   *
   * <p>The issue is caused by decreasing the ECI/LCO inventory items multiple times instead of once.
   * 1 item inventory is decreased at copyBooking, 1 item inventory is decreased at confirmAmend
   * automatically by Opera during package addition.
   *
   * <p>In order to fix this issue, the ECI/LCO packages are removed from the temporary reservations
   * at copyBooking.
   *
   * @see <a href="https://whitbreadis.atlassian.net/browse/DNRQ-70306">DNRQ-70306</a>
   */
  private void removeEciLcoPackages(HotelReservationType hotelReservationType) {
    Optional.ofNullable(hotelReservationType.getReservationPackages())
        .ifPresent(list -> list.removeIf(
            reservationPackageType -> "HSCKIN".equals(reservationPackageType.getPackageCode())
                || "HSCOU2".equals(reservationPackageType.getPackageCode())));
  }

  private static List<ChildAgeType> buildChildAgeTypes(GuestCountsType guestCountsType) {
    return IntStream.rangeClosed(1, guestCountsType.getChildren())
        .mapToObj(i -> new ChildAgeType().age(3))
        .toList();
  }

}

