package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.Collections;
import java.util.Objects;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardNumberTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardProcessingType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuaranteeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationById;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPaymentCardType;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {
    UniqueIdTypeEnumDto.class, Collections.class, CardTypeType.class, CardProcessingType.class,
    CardNumberTypeType.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class PayOnArrivalReservationRequestOhipMapper {

  private ReservationOhipProperties reservationOhipProperties;

  @Autowired
  public void toPayOnArrivalReservationModel(
      final ReservationOhipProperties reservationOhipProperties) {
    this.reservationOhipProperties = reservationOhipProperties;
  }

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "reservations",
      expression = "java(Collections.singletonList(injectHotelReservationInstructionType(reservation)))")
  public abstract ChangeReservation toChangeReservationForModel(ReservationById reservation);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "reservationIdList",
      expression = "java(Collections.singletonList(injectReservationId(reservation)))")
  @Mapping(target = "reservationPaymentMethods",
      expression = "java(Collections.singletonList(injectReservationPaymentMethod(reservation)))")
  @Mapping(target = "roomStay",
      expression = "java(injectRoomStay())")
  abstract HotelReservationInstructionType injectHotelReservationInstructionType(
      ReservationById reservation);

  @Mapping(target = "id", source = "reservationId")
  @Mapping(target = "type", expression = "java(UniqueIdTypeEnumDto.RESERVATION_TYPE.value())")
  abstract UniqueIDType injectReservationId(
      ReservationById reservation);

  @Mapping(target = "paymentMethod",
      expression = "java(reservation.getPaymentCard().getPaymentMethod().toUpperCase())")
  @Mapping(target = "paymentCard",
      expression = "java(injectPaymentCard(reservation.getPaymentCard()))")
  abstract ReservationPaymentMethodType injectReservationPaymentMethod(
      ReservationById reservation);

  @Mapping(target = "cardType",
      expression = "java(injectCardType(reservationPaymentCardType))")
  @Mapping(target = "processing",
      expression = "java(CardProcessingType.fromValue(reservationPaymentCardType.getProcessing()))")
  @Mapping(target = "cardOrToken",
      expression = "java(CardNumberTypeType.fromValue(reservationPaymentCardType.getCardOrToken()))")
  abstract ResPaymentCardType injectPaymentCard(
      ReservationPaymentCardType reservationPaymentCardType);

  protected RoomStayType injectRoomStay() {
    ResGuaranteeType resGuaranteeType = new ResGuaranteeType();
    resGuaranteeType.setGuaranteeCode(reservationOhipProperties.getConfirmationGuaranteeCode());

    var roomStayType = new RoomStayType();
    roomStayType.setGuarantee(resGuaranteeType);
    return roomStayType;
  }

  protected CardTypeType injectCardType(ReservationPaymentCardType reservationPaymentCardType) {
    var userDefinedCardType = reservationPaymentCardType.getUserDefinedCardType();
    if (Objects.nonNull(userDefinedCardType) && !userDefinedCardType.isEmpty()) {
      return null;
    }

    return CardTypeType.fromValue(reservationPaymentCardType.getCardType());
  }
}
