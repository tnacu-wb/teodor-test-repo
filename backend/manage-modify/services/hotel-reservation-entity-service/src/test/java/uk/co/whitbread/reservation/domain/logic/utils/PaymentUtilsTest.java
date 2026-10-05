package uk.co.whitbread.reservation.domain.logic.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto.PaymentOptionEnum;
import uk.co.whitbread.reservation.domain.exceptions.GenericReservationException;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.AcceptedCreditCard;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.HotelPaymentInformation;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationIdDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;

public class PaymentUtilsTest {
    private static final String A2C_GUARANTEE_OPERA_CODE = "CO";

    @Test
    void getDefaultPaymentMethod_ok() {

        // Arrange
        var hotelPaymentInformation = mockHotelPaymentInfoResponse();

        // Act
        var defaultPaymentMethod = PaymentUtils.getDefaultPaymentMethod("VA", hotelPaymentInformation, "PI");

        // Assert
        assertThat(defaultPaymentMethod, is("DVA"));
    }

    @Test
    void getDefaultPaymentMethod_null() {

        // Arrange
        var hotelPaymentInformation = mockHotelPaymentInfoResponse();

        // Act
        var defaultPaymentMethod = PaymentUtils.getDefaultPaymentMethod("VA", hotelPaymentInformation, "");

        // Assert
        assertNull(defaultPaymentMethod);
    }

    @Test
    void getPaymentOption_basketPaymentOption() {

        // Arrange
        var basket = BasketResponse.builder().paymentOption(PaymentOption.PAY_NOW).build();

        // Act
        var paymentOption = PaymentUtils.getPaymentOption(basket, null);

        // Assert
        assertThat(paymentOption, is(PaymentOption.PAY_NOW));
    }

    @Test
    void getPaymentOption_POA_nullChannel_zeroAmountPaid_ok() {

        // Arrange
        var basket = BasketResponse.builder().paymentOption(PaymentOption.PAY_ON_ARRIVAL).build();

        var reservations = ReservationByBasketRefResponse.builder().amountPaid(BigDecimal.ZERO).build();

        // Act
        var paymentOption = PaymentUtils.getPaymentOption(basket, reservations);

        // Assert
        assertThat(paymentOption, is(PaymentOption.PAY_ON_ARRIVAL));
    }

    @Test
    void getPaymentOption_nullPaymentOption_DISTRChannel_zeroAmountPaid_ok() {

        // Arrange
        var basket = BasketResponse.builder().channel("DISTR").build();

        var reservations = ReservationByBasketRefResponse.builder().amountPaid(BigDecimal.ZERO).build();

        // Act
        var paymentOption = PaymentUtils.getPaymentOption(basket, reservations);

        // Assert
        assertThat(paymentOption, is(PaymentOption.PAY_ON_ARRIVAL));
    }

    @Test
    void getPaymentOption_nullPaymentOption_DISTRChannel_oneAmountPaid_ok() {

        // Arrange
        var basket = BasketResponse.builder().channel("DISTR").build();

        var reservations = ReservationByBasketRefResponse.builder().amountPaid(BigDecimal.ONE).build();

        // Act
        var paymentOption = PaymentUtils.getPaymentOption(basket, reservations);

        // Assert
        assertThat(paymentOption, is(PaymentOption.PAY_ON_ARRIVAL));
    }

    @Test
    void getPaymentOption_POA() {

        // Arrange
        var reservations = ReservationByBasketRefResponse.builder().amountPaid(BigDecimal.ZERO).build();
        // Act
        var paymentOption = PaymentUtils.getPaymentOption(new BasketResponse(), reservations);

        // Assert
        assertThat(paymentOption, is(PaymentOption.PAY_ON_ARRIVAL));
    }

    @Test
    void getPaymentOption_enum_ok() {

        // Arrange
        var operaRsv = ReservationsDetailsEnhancedResponse.builder().amountPaid(BigDecimal.ONE).build();

        // Act
        var paymentOption = PaymentUtils.getPaymentOption(operaRsv);

        // Assert
        assertThat(paymentOption, is(CreateBasketRequestDto.PaymentOptionEnum.PAY_NOW));
    }

    @Test
    void getPaymentOptionPOA_enum_ok() {

        // Arrange
        var operaRsv = ReservationsDetailsEnhancedResponse.builder().amountPaid(BigDecimal.ZERO).build();

        // Act
        var paymentOption = PaymentUtils.getPaymentOption(operaRsv);

        // Assert
        assertThat(paymentOption, is(PaymentOptionEnum.PAY_ON_ARRIVAL));
    }


    @Test
    void getPaymentOptionResId_enum_ok() {
        // Arrange
        var operaRsv = ManageReservationUtils.mockReservationIdResponsePOA();
        // Act
        var paymentOption = PaymentUtils.getPaymentOptionResId(operaRsv);

        // Assert
        assertThat(paymentOption, is(PaymentOptionEnum.PAY_ON_ARRIVAL));
    }

    @Test
    void getPaymentOptionResIdPN_enum_ok() {
        // Arrange
        var operaRsv = ManageReservationUtils.mockReservationIdResponseForPN();
        // Act
        var paymentOption = PaymentUtils.getPaymentOptionResId(operaRsv);

        // Assert
        assertThat(paymentOption, is(PaymentOptionEnum.PAY_NOW));
    }

    @Test
    void getPaymentOption_shouldThrowException() {

        // Arrange
        var reservations = ReservationByBasketRefResponse.builder().amountPaid(BigDecimal.ONE).build();

        // Act and assert
        Assertions.assertThrows(GenericReservationException.class,
                () -> PaymentUtils.getPaymentOption(new BasketResponse(), reservations));
    }

    @Test
    void getRsvPaymentOption_A2C_enum_ok() {
        // Arrange
        var operaRsv = ManageReservationUtils.mockReservationByBasketRefResponse_OperaConfirmation(A2C_GUARANTEE_OPERA_CODE);
        // Act
        var paymentOption = PaymentUtils.getRsvPaymentOption(operaRsv);

        // Assert
        assertThat(paymentOption, is(PaymentOptionEnum.ACCOUNT_COMPANY));
    }

    @Test
    void getRsvPaymentOption_PN_enum_ok() {
        // Arrange
        var operaRsv = ManageReservationUtils.mockReservationByBasketRefResponse_PrePaid_OperaConfirmation();
        // Act
        var paymentOption = PaymentUtils.getRsvPaymentOption(operaRsv);

        // Assert
        assertThat(paymentOption, is(PaymentOptionEnum.PAY_NOW));
    }

    private HotelPaymentInformation mockHotelPaymentInfoResponse() {
        return HotelPaymentInformation.builder()
            .acceptedCreditCards(List.of(
                AcceptedCreditCard.builder().code("DL").codeOpera("VA").build(),
                AcceptedCreditCard.builder().code("EL").codeOpera("VA").build()
            )).paymentMethodsOpera(Map.of("PI_VA", "DVA", "PI_MC", "DMC", "KIOSK_VA", "KVA")).build();
    }

}
