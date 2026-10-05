package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static java.util.Collections.singletonList;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardNumberTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardProcessingType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.CreditCardInfo;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.PaymentUtils;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class MovePaymentDetailsOhipMapper {

  private ReservationOhipProperties reservationOhipProperties;
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Autowired
  public final void toReservationOhipPropertiesForModel(
      ReservationOhipProperties reservationOhipProperties,
      UnleashWrapper<FeatureFlag> unleashWrapper) {
    this.reservationOhipProperties = reservationOhipProperties;
    this.unleashWrapper = unleashWrapper;
  }

  @Mapping(expression = "java(injectReservations(reservationPaymentMethod, creditCardInfo))", target = "reservations")
  public abstract ChangeReservation toChangeReservationDto(ReservationPaymentMethodType reservationPaymentMethod,
      CreditCardInfo creditCardInfo);

  protected List<HotelReservationInstructionType> injectReservations(
      ReservationPaymentMethodType reservationPaymentMethod,
      CreditCardInfo creditCardInfo) {
    var hotelReservation = new HotelReservationInstructionType();
    var reservationPaymentMethods = createPaymentMethods(reservationPaymentMethod, creditCardInfo);
    hotelReservation.setReservationPaymentMethods(reservationPaymentMethods);
    return singletonList(hotelReservation);
  }

  private List<ReservationPaymentMethodType> createPaymentMethods(
      ReservationPaymentMethodType reservationPaymentMethod,
      CreditCardInfo creditCardInfo) {
    return List.of(createPaymentMethodWindow1(),
        createPaymentMethodWindow2(reservationPaymentMethod, creditCardInfo));
  }

  private ReservationPaymentMethodType createPaymentMethodWindow1() {
    var folio1 = new ReservationPaymentMethodType();
    var paymentMethod = unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getSetDefaultPaymentMethodDs())
        ? reservationOhipProperties.getDefaultPaymentMethodDs()
        : reservationOhipProperties.getDefaultPaymentMethod();
    folio1.setPaymentMethod(paymentMethod);
    folio1.setFolioView(reservationOhipProperties.getFolio());
    return folio1;
  }

  private ReservationPaymentMethodType createPaymentMethodWindow2(
      ReservationPaymentMethodType reservationPaymentMethod,
      CreditCardInfo creditCardInfo) {

    var card = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    card.setCardNumber(creditCardInfo.getCreditCard().getCardNumber());
    card.setCardNumberMasked(reservationPaymentMethod.getPaymentCard().getCardNumberMasked());
    PaymentUtils.setCardType(creditCardInfo, card);
    card.setExpirationDate(creditCardInfo.getCreditCard().getExpirationDate());
    card.setCardHolderName(reservationPaymentMethod.getPaymentCard().getCardHolderName());
    card.setCardNumberLast4Digits(creditCardInfo.getCreditCard().getCardNumberLast4Digits());
    card.setCardOrToken(CardNumberTypeType.TOKEN);
    card.setProcessing(CardProcessingType.MANUAL);
    var folio2 = new ReservationPaymentMethodType();
    folio2.setPaymentCard(card);
    folio2.setPaymentMethod(reservationPaymentMethod.getPaymentMethod());
    folio2.setFolioView(2);
    return folio2;
  }

}
