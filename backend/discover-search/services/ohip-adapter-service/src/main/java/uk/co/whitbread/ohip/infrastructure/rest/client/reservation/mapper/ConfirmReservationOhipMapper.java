package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.Collections;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardNumberTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardProcessingType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuaranteeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmReservationRequest;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {PaymentMethodOhipMapper.class})
public abstract class ConfirmReservationOhipMapper {

  public static final String A2C_PAYMENT_TYPE = "AC";
  private ReservationOhipProperties reservationOhipProperties;
  private PaymentMethodOhipMapper paymentMethodOhipMapper;

  @Autowired
  public final void toConfirmReservationOhipPropertiesForModel(
      ReservationOhipProperties reservationOhipProperties,
      PaymentMethodOhipMapper paymentMethodOhipMapper) {
    this.reservationOhipProperties = reservationOhipProperties;
    this.paymentMethodOhipMapper = paymentMethodOhipMapper;
  }

  @Mapping(expression = "java(injectReservationDetails(confirmReservationRequest))", target = "reservationIdList")
  @Mapping(expression = "java(injectPaymentMethods(confirmReservationRequest))", target = "reservationPaymentMethods")
  @Mapping(expression = "java(injectGuaranteeCode(confirmReservationRequest))", target = "roomStay.guarantee")
  abstract HotelReservationInstructionType fromDto(
      ConfirmReservationRequest confirmReservationRequest);

  protected List<UniqueIDType> injectReservationDetails(
      ConfirmReservationRequest confirmReservationRequest) {
    final var uniqueIdType = new UniqueIDType();

    uniqueIdType.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    uniqueIdType.setId(confirmReservationRequest.getReservationId());

    return List.of(uniqueIdType);
  }

  protected List<ReservationPaymentMethodType> injectPaymentMethods(
      ConfirmReservationRequest confirmReservationRequest) {

    switch (confirmReservationRequest.getPaymentOption()) {
      case ACCOUNT_COMPANY -> {
        var reservationPaymentMethodType = new ReservationPaymentMethodType();
        var paymentCard = new ResPaymentCardType();
        paymentCard.setCardOrToken(CardNumberTypeType.TOKEN);
        paymentCard.setProcessing(CardProcessingType.MANUAL);
        reservationPaymentMethodType.setPaymentCard(paymentCard);
        reservationPaymentMethodType.setPaymentMethod(A2C_PAYMENT_TYPE);
        reservationPaymentMethodType.setFolioView(2);

        return List.of(reservationPaymentMethodType);
      }
      case RESERVE_WITHOUT_CARD -> {
        return Collections.emptyList();
      }
      default -> {
        // DNRQ-41937 Replace this logic when mapping will be updated by AEM
        final var paymentType = confirmReservationRequest.getPaymentType();
        final var reservationPaymentMethodType = new ReservationPaymentMethodType();
        final var paymentCard =
            paymentMethodOhipMapper.toModel(confirmReservationRequest.getPaymentCard());
        paymentCard.setCardOrToken(CardNumberTypeType.TOKEN);
        paymentCard.setProcessing(CardProcessingType.MANUAL);
        reservationPaymentMethodType.setPaymentCard(paymentCard);
        reservationPaymentMethodType.setPaymentMethod(paymentType.toUpperCase());
        reservationPaymentMethodType.setFolioView(reservationOhipProperties.getFolio());

        return List.of(reservationPaymentMethodType);
      }
    }
  }

  protected ResGuaranteeType injectGuaranteeCode(
      ConfirmReservationRequest confirmReservationRequest) {
    final var resGuaranteeType = new ResGuaranteeType();
    resGuaranteeType.setGuaranteeCode(
        establishGuaranteeCodeFrom(confirmReservationRequest));
    resGuaranteeType.setOnHold(Boolean.FALSE);
    return resGuaranteeType;
  }

  private String establishGuaranteeCodeFrom(ConfirmReservationRequest confirmReservationRequest) {
    switch (confirmReservationRequest.getPaymentOption()) {
      case RESERVE_WITHOUT_CARD -> {
        return reservationOhipProperties.getNonGuaranteeCode();
      }
      case ACCOUNT_COMPANY -> {
        return reservationOhipProperties.getCompanyGuaranteeCode();
      }
      case PAY_NOW -> {
        return reservationOhipProperties.getDepositReceivedGuaranteeCode();
      }
      default -> {
        return reservationOhipProperties.getConfirmationGuaranteeCode();
      }
    }
  }

}
