package uk.co.whitbread.reservation.domain.logic.utils;

import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.A2C_GUARANTEE_OPERA_CODE;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.DEPOSIT_RECEIVED_GUARANTEE_OPERA_CODE;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.NON_GUARANTEED_OPERA_CODE;

import java.math.BigDecimal;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.exceptions.GenericReservationException;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.HotelPaymentInformation;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentUtils {

  //used in case of prepaid reservations where the saved payment method does not contain the channel
  public static String getDefaultPaymentMethod(String rsvPaymentMethod,
                                               HotelPaymentInformation hotelPaymentInformation, String channel) {
    String result = null;
    if (hotelPaymentInformation != null && hotelPaymentInformation.getPaymentMethodsOpera() != null
        && StringUtils.isNotEmpty(channel)) {
      result =
          hotelPaymentInformation.getPaymentMethodsOpera().get(channel.concat("_").concat(rsvPaymentMethod));
    }

    log.debug("defaultPaymentMethod for rsvPAymentMethod={} is {}", rsvPaymentMethod, result);

    return result;

  }

  public static PaymentOption getPaymentOption(BasketResponse basket,
                                               ReservationByBasketRefResponse reservations) {

    if (Objects.isNull(basket.getPaymentOption()) && Objects.nonNull(basket.getChannel())
            && basket.getChannel().equalsIgnoreCase(BookingChannel.DISTR_BOOKING_CHANNEL)) {
      return PaymentOption.PAY_ON_ARRIVAL;
    }

    if (Objects.nonNull(basket.getPaymentOption())) {
      return basket.getPaymentOption();
    }

    //workaround for migrated reservations
    if (reservations.getAmountPaid().compareTo(BigDecimal.ZERO) == 0) {
      return PaymentOption.PAY_ON_ARRIVAL;
    }

    var ex = new GenericReservationException(ErrorCode.DIGITAL_CANCEL_MIGRATED_EXCEPTION,
        "Cannot cancel migrated reservations with refund");
    ExceptionLogger.log(log, ex);
    throw ex;
  }

  public static CreateBasketRequestDto.PaymentOptionEnum getPaymentOption(
      ReservationsDetailsEnhancedResponse operaRes) {
    return !operaRes.getAmountPaid().equals(BigDecimal.ZERO) ? CreateBasketRequestDto.PaymentOptionEnum.PAY_NOW
        : CreateBasketRequestDto.PaymentOptionEnum.PAY_ON_ARRIVAL;
  }

  public static CreateBasketRequestDto.PaymentOptionEnum getPaymentOptionResId(
      ReservationByIdDetailsResponse operaRes) {
    BigDecimal amountPaid = operaRes.getReservationIdDetailsResponse().getReservations()
        .getReservation()
        .get(0).getReservationPolicies().getDepositPolicies().get(0).getAmountPaid().getAmount();
    return !amountPaid.equals(BigDecimal.ZERO) ? CreateBasketRequestDto.PaymentOptionEnum.PAY_NOW
        : CreateBasketRequestDto.PaymentOptionEnum.PAY_ON_ARRIVAL;
  }

  public static CreateBasketRequestDto.PaymentOptionEnum getRsvPaymentOption(ReservationByBasketRefResponse operaRes) {
    BigDecimal amountPaid = operaRes.getAmountPaid();
    var guaranteeCode = operaRes.getReservationByIdList().get(0).getGuaranteeCode();

    if (BigDecimal.ZERO.compareTo(amountPaid) < 0 || DEPOSIT_RECEIVED_GUARANTEE_OPERA_CODE.equals(guaranteeCode)) {
      return CreateBasketRequestDto.PaymentOptionEnum.PAY_NOW;
    }
    CreateBasketRequestDto.PaymentOptionEnum paymentOption = null;
    if (BigDecimal.ZERO.compareTo(amountPaid) == 0) {
      paymentOption = switch (guaranteeCode) {
        case A2C_GUARANTEE_OPERA_CODE -> CreateBasketRequestDto.PaymentOptionEnum.ACCOUNT_COMPANY;
        case NON_GUARANTEED_OPERA_CODE -> CreateBasketRequestDto.PaymentOptionEnum.RESERVE_WITHOUT_CARD;
        default -> CreateBasketRequestDto.PaymentOptionEnum.PAY_ON_ARRIVAL;
      };
    }
    return paymentOption;

  }
}
