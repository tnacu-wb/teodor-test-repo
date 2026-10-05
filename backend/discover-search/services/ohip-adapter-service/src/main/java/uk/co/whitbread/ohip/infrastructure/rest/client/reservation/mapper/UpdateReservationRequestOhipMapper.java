package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_14;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChildAgeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.GuestCountsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomRateType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuests;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationType;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomOccupancy;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomRate;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomStay;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class UpdateReservationRequestOhipMapper extends SpecialRequestOhipMapper {

  @Mapping(expression = "java(injectReservationDetails(updateReservationRequest))", target = "reservations")
  public abstract ChangeReservation toDto(UpdateReservationRequest updateReservationRequest);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "type", source = "type")
  public abstract UniqueIDType toReservationIdTypeDto(ReservationType reservationType);


  @Mapping(target = "guestCounts", source = "roomOccupancy")
  @Mapping(target = "roomRates", source = "roomRates")
  public abstract RoomStayType toReservationIdTypeDto(RoomStay roomStay);

  public abstract List<ResGuestType> toResGuestTypeDto(List<ReservationGuests> reservationGuests);

  @Mapping(target = "adults", source = "adultCount")
  @Mapping(target = "children", source = "childCount")
  public abstract GuestCountsType toGuestCountsTypeDto(RoomOccupancy roomOccupancy);

  @Mapping(target = "start", source = "startDate")
  @Mapping(target = "end", source = "endDate")
  @Mapping(target = "guestCounts", source = "roomOccupancy")
  @Mapping(target = "rates", source = "rates")
  public abstract RoomRateType toRoomRateTypeDto(RoomRate roomOccupancy);

  public abstract List<UniqueIDType> toReservationsIdTypeDto(List<ReservationType> reservationType);

  protected List<HotelReservationInstructionType> injectReservationDetails(
      UpdateReservationRequest updateReservationRequest) {

    HotelReservationInstructionType reservationDetails = new HotelReservationInstructionType();
    reservationDetails.setHotelId(updateReservationRequest.getHotelId());
    reservationDetails.setReservationIdList(toReservationsIdTypeDto(
        List.of(updateReservationRequest.getReservationType())));
    reservationDetails.setRoomStay(toReservationIdTypeDto(updateReservationRequest.getRoomStay()));

    final var userDefinedFields = new UserDefinedFieldsType();

    if (updateReservationRequest.getSendEmailConfirmation() != null
        || updateReservationRequest.getSendEmailInvoice() != null) {
      StringBuilder value = new StringBuilder();

      if (updateReservationRequest.getSendEmailConfirmation() != null) {
        mapValueToUdfc14(updateReservationRequest.getSendEmailConfirmation(), 0, value);
      }

      if (updateReservationRequest.getSendEmailInvoice() != null) {
        mapValueToUdfc14(updateReservationRequest.getSendEmailInvoice(), 1, value);
      }

      final var characterUDF14 = new CharacterUDFType();
      characterUDF14.setName(UDFC_14);
      characterUDF14.setValue(value.toString());

      userDefinedFields.addCharacterUDFsItem(characterUDF14);
    }

    reservationDetails.setUserDefinedFields(userDefinedFields);

    reservationDetails.setReservationGuests(toResGuestTypeDto(updateReservationRequest.getReservationGuests()));

    injectGuestChildAges(reservationDetails);
    updatePreferenceCollection(updateReservationRequest.getSpecialRequests(), reservationDetails);

    return List.of(reservationDetails);
  }

  private static void mapValueToUdfc14(Boolean udfValue, int position, StringBuilder value) {
    if (Boolean.TRUE.equals(udfValue)) {
      value.insert(position, "Y");
    } else {
      value.insert(position, "N");
    }
  }

  private void injectGuestChildAges(HotelReservationInstructionType hotelReservationType) {
    Optional.ofNullable(hotelReservationType.getRoomStay())
        .ifPresent(roomStayType -> {

          Optional.ofNullable(roomStayType.getGuestCounts())
              .ifPresent(guestCountsType -> roomStayType.getGuestCounts()
                  .setChildAges(buildChildAgeTypes(guestCountsType)));

          Optional.ofNullable(roomStayType.getRoomRates())
              .ifPresent(roomRates -> {
                roomRates.forEach(roomRateType -> Optional.ofNullable(roomRateType.getGuestCounts())
                        .ifPresent(guestCountsType -> roomRateType.getGuestCounts()
                            .setChildAges(buildChildAgeTypes(guestCountsType))));
              });
        });
  }

  private static List<ChildAgeType> buildChildAgeTypes(GuestCountsType guestCountsType) {
    return IntStream.rangeClosed(1, guestCountsType.getChildren())
        .mapToObj(i -> new ChildAgeType().age(3))
        .toList();
  }
}
