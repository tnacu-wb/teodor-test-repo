package uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardNumberTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardProcessingType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.model.in.EckohChangeRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.PaymentMethodOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {PaymentMethodOhipMapper.class})
public abstract class EckohWebhookOhipMapper {

  private ReservationOhipProperties reservationOhipProperties;
  private PaymentMethodOhipMapper paymentMethodOhipMapper;

  @Autowired
  public final void toEckohOhipPropertiesForModel(
      ReservationOhipProperties reservationOhipProperties,
      PaymentMethodOhipMapper paymentMethodOhipMapper) {
    this.reservationOhipProperties = reservationOhipProperties;
    this.paymentMethodOhipMapper = paymentMethodOhipMapper;
  }

  @Mapping(expression = "java(injectReservationDetails(eckohChangeRequest))", target = "reservationIdList")
  @Mapping(expression = "java(injectPaymentMethods(eckohChangeRequest))", target = "reservationPaymentMethods")
  abstract HotelReservationInstructionType fromDto(
      EckohChangeRequestDto eckohChangeRequest);

  protected List<UniqueIDType> injectReservationDetails(
      EckohChangeRequestDto eckohChangeRequest) {
    final var uniqueIdType = new UniqueIDType();

    uniqueIdType.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    uniqueIdType.setId(eckohChangeRequest.getReservationId());

    return List.of(uniqueIdType);
  }

  protected List<ReservationPaymentMethodType> injectPaymentMethods(
      EckohChangeRequestDto eckohChangeRequest) {
    var reservationPaymentMethodType = new ReservationPaymentMethodType();
    var paymentCard =
        paymentMethodOhipMapper.toEckohModel(eckohChangeRequest.getPaymentCard());
    paymentCard.setCardOrToken(CardNumberTypeType.TOKEN);
    paymentCard.setProcessing(CardProcessingType.MANUAL);
    reservationPaymentMethodType.setPaymentCard(paymentCard);
    reservationPaymentMethodType.setPaymentMethod(
        eckohChangeRequest.getPaymentCard().getPaymentMethod());
    reservationPaymentMethodType.setFolioView(reservationOhipProperties.getFolio());

    return List.of(reservationPaymentMethodType);

  }

}
