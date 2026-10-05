package uk.co.whitbread.basket.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.payments.out.BasketPaymentStatus;
import uk.co.whitbread.basket.domain.model.payments.out.RefundResponse;
import uk.co.whitbread.basket.domain.model.refund.in.Amount;
import uk.co.whitbread.basket.domain.model.refund.in.Booking;
import uk.co.whitbread.basket.domain.model.refund.in.BusinessSite;
import uk.co.whitbread.basket.domain.model.refund.in.Card;
import uk.co.whitbread.basket.domain.model.refund.in.PaymentType;
import uk.co.whitbread.basket.domain.model.refund.in.Refund;
import uk.co.whitbread.basket.domain.model.refund.in.RefundReason;
import uk.co.whitbread.basket.domain.model.refund.in.RefundRequest;
import uk.co.whitbread.basket.domain.model.refund.in.RefundType;
import uk.co.whitbread.basket.domain.model.reservation.out.CurrencyAmountType;
import uk.co.whitbread.basket.domain.model.reservation.out.Deposits;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositsResponse;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RefundOutPort;

@ExtendWith(MockitoExtension.class)
class RefundInPortImplTest {

    public static final String BASKET_REFERENCE = "BKRb6a8b47b-16fe-4dd3-9f1a-407544824f07";
    public static final String BOOKING_REFERENCE = "BKR12345";
    @Mock
    private BasketOutPort basketOutPort;

    @Mock
    private RefundOutPort refundOutPort;

    @Mock
    private HotelReservationOutPort reservationOutPort;

    @InjectMocks
    private RefundInPortImpl refundInPort;

    @Test
    void testPartialRefundPayment() {
        var refundRequest = mockRefundRequest(RefundReason.AMEND);
        Basket basket = mockBasket();

        refundInPort.refundPayment(basket.getBasketId(), refundRequest);

        verify(refundOutPort, times(1))
            .processTokenRefund(any(RefundRequest.class));
    }

    @ParameterizedTest
    @EnumSource(value = RefundType.class, names = {"FULL", "PARTIAL"})
    void testPartialRefundNoCardPresent(RefundType opt) {
        var basketReference = BASKET_REFERENCE;
        var refundRequest = RefundRequest
            .builder()
            .hotelCode("LONEUS")
            .refund(Refund
                .builder()
                .type(PaymentType.CARD)
                .amount(Amount
                    .builder()
                    .currency("GBP")
                    .minorUnits(BigDecimal.TEN)
                    .build())
                .card(null)
                .reason(RefundReason.AMEND)
                .build())
            .booking(Booking
                .builder()
                .channel("WEB")
                .type("PAY_NOW")
                .journey("REFUND")
                .businessSite(BusinessSite
                    .builder()
                    .identifier("LONEUS")
                    .type("HOTEL")
                    .build())
                .build())
            .refundType(opt)
            .build();
        String expectedMessage = "Cannot execute partial refund without card details";
        if (opt.equals(RefundType.FULL)) {
            expectedMessage = String.format("Basket item is missing for basket = %s",
                basketReference);
            var basket = Basket.builder().reference(BOOKING_REFERENCE).basketId(BASKET_REFERENCE)
                .hotelId("MANOLD").paymentID("12345").build();
            when(basketOutPort.getBasketById(basketReference)).thenReturn(basket);
        }
        //Act
        Exception exception = assertThrows(Exception.class,
            () -> refundInPort.refundPayment(basketReference, refundRequest));
        //Assert
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testPartialRefundPaymentForCancel() {
        var refundRequest = mockRefundRequest(RefundReason.CANCEL);
        when(refundOutPort.processTokenRefund(any())).thenReturn(mockRefundResponse());
        when(basketOutPort.getBasketById(any())).thenReturn(mockBasket());
        ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

        refundInPort.refundPayment(BASKET_REFERENCE, refundRequest);

        verify(basketOutPort).updateBasket(captor.capture());
        var basketEntity = captor.getValue();
        assertEquals(BasketPaymentStatus.REFUNDED, basketEntity.getPaymentStatus());
    }

    @Test
    void testFullRefund() {
        var refundRequest = RefundRequest
                .builder()
                .hotelCode("LONEUS")
                .refund(Refund
                        .builder()
                        .type(PaymentType.CARD)
                        .amount(Amount
                                .builder()
                                .currency("GBP")
                                .minorUnits(BigDecimal.TEN)
                                .build())
                        .card(Card
                                .builder()
                                .token("4216333880397891103")
                                .expiryYear("26")
                                .expiryMonth("03")
                                .build())
                        .reason(RefundReason.AMEND)
                        .build())
                .booking(Booking
                        .builder()
                        .channel("WEB")
                        .type("PAY_NOW")
                        .journey("REFUND")
                        .businessSite(BusinessSite
                                .builder()
                                .identifier("LONEUS")
                                .type("HOTEL")
                                .build())
                        .build())
                .refundType(RefundType.FULL)
                .build();

        Basket basket = mockBasket();

        when(basketOutPort.getBasketById(basket.getReference())).thenReturn(basket);
        when(reservationOutPort.getDepositsForReservationId(anyString(), anyString()))
                .thenReturn(mockDepositsResponse());

        refundInPort.refundPayment(basket.getReference(), refundRequest);

        verify(refundOutPort, times(1))
                .processRefund(anyString(), any(RefundRequest.class), anyString());
    }

    @Test
    void testFullRefundMultipleRooms() {
        var refundRequest = RefundRequest
                .builder()
                .hotelCode("LONEUS")
                .refund(Refund
                        .builder()
                        .type(PaymentType.CARD)
                        .amount(Amount
                                .builder()
                                .currency("GBP")
                                .minorUnits(BigDecimal.TEN)
                                .build())
                        .card(Card
                                .builder()
                                .token("4216333880397891103")
                                .expiryYear("26")
                                .expiryMonth("03")
                                .build())
                        .reason(RefundReason.AMEND)
                        .build())
                .booking(Booking
                        .builder()
                        .channel("WEB")
                        .type("PAY_NOW")
                        .journey("REFUND")
                        .businessSite(BusinessSite
                                .builder()
                                .identifier("LONEUS")
                                .type("HOTEL")
                                .build())
                        .build())
                .refundType(RefundType.FULL)
                .build();

        var basketItem = BasketItem.builder().type("STAY").sourceId("123456")
                .reqAction("COMMIT").ack(0).build();
        var basketItem2 = BasketItem.builder().type("STAY").sourceId("454365")
                .reqAction("COMMIT").ack(0).build();
        List<BasketItem> basketItemList = new LinkedList<>();
        basketItemList.add(basketItem);
        basketItemList.add(basketItem2);
        var basket = Basket.builder().reference(BOOKING_REFERENCE).basketId(BASKET_REFERENCE)
                .hotelId("MANOLD").items(basketItemList).paymentID("12345").build();

        List<Deposits> depositsList = new LinkedList<>();
        depositsList.add(Deposits.builder()
                .postedAmount(CurrencyAmountType.
                        builder()
                        .amount(BigDecimal.TEN)
                        .currencyCode("EUR")
                        .build())
                .paymentReference("REF123").build());
        depositsList.add(Deposits.builder()
                .postedAmount(CurrencyAmountType.
                        builder()
                        .amount(BigDecimal.TEN)
                        .currencyCode("EUR")
                        .build())
                .paymentReference("REF123").build());
        depositsList.add(Deposits.builder()
                .postedAmount(CurrencyAmountType.
                        builder()
                        .amount(BigDecimal.valueOf(-10))
                        .currencyCode("EUR")
                        .build())
                .paymentReference("REF123").build());
        var depositsResponse = DepositsResponse.builder()
                .deposits(depositsList).build();


        when(basketOutPort.getBasketById(basket.getBasketId())).thenReturn(basket);
        when(reservationOutPort.getDepositsForReservationId(anyString(), anyString()))
                .thenReturn(depositsResponse);

        refundInPort.refundPayment(basket.getBasketId(), refundRequest);

        verify(refundOutPort, times(1))
                .processRefund(anyString(), any(RefundRequest.class), anyString());
    }

    @Test
    void testFullRefundMissingPaymentReferenceInOpera() {
        var refundRequest = RefundRequest
                .builder()
                .hotelCode("LONEUS")
                .refund(Refund
                        .builder()
                        .type(PaymentType.CARD)
                        .amount(Amount
                                .builder()
                                .currency("GBP")
                                .minorUnits(BigDecimal.TEN)
                                .build())
                        .card(Card
                                .builder()
                                .token("4216333880397891103")
                                .expiryYear("26")
                                .expiryMonth("03")
                                .build())
                        .reason(RefundReason.AMEND)
                        .build())
                .booking(Booking
                        .builder()
                        .channel("WEB")
                        .type("PAY_NOW")
                        .journey("REFUND")
                        .businessSite(BusinessSite
                                .builder()
                                .identifier("LONEUS")
                                .type("HOTEL")
                                .build())
                        .build())
                .refundType(RefundType.FULL)
                .build();

        var basketItem = BasketItem.builder().type("STAY").sourceId("123456")
                .reqAction("COMMIT").ack(0).build();
        List<BasketItem> basketItemList = new LinkedList<>();
        basketItemList.add(basketItem);
        var basket = Basket.builder().reference(BOOKING_REFERENCE).basketId(BASKET_REFERENCE)
                .hotelId("MANOLD").items(basketItemList).paymentID(null).build();

        List<Deposits> depositsList = new LinkedList<>();
        depositsList.add(Deposits.builder()
                .postedAmount(CurrencyAmountType.
                        builder()
                        .amount(BigDecimal.TEN)
                        .currencyCode("EUR")
                        .build())
                .paymentReference(null).build());
        var depositsResponse = DepositsResponse.builder()
                .deposits(depositsList).build();


        when(basketOutPort.getBasketById(basket.getBasketId())).thenReturn(basket);
        when(reservationOutPort.getDepositsForReservationId(anyString(), anyString()))
                .thenReturn(depositsResponse);

        refundInPort.refundPayment(basket.getBasketId(), refundRequest);

        verify(refundOutPort, times(1))
                .processRefund(anyString(), any(RefundRequest.class), any());
    }

    private Basket mockBasket() {

        var basketItem = BasketItem.builder().type("STAY").sourceId("123456")
            .reqAction("COMMIT").ack(0).build();
        List<BasketItem> basketItemList = new LinkedList<>();
        basketItemList.add(basketItem);
        return Basket.builder()
            .basketId(BASKET_REFERENCE)
            .reference(BOOKING_REFERENCE)
            .hotelId("MANOLD")
            .paymentChannel(null)
            .items(basketItemList)
            .paymentID("12345")
            .build();
    }

    private DepositsResponse mockDepositsResponse() {
        List<Deposits> depositsList = new LinkedList<>();
        depositsList.add(Deposits.builder()
                .postedAmount(CurrencyAmountType.
                        builder()
                            .amount(BigDecimal.TEN)
                        .currencyCode("EUR")
                        .build())
                .paymentReference("REF123").build());
        return DepositsResponse.builder()
            .deposits(depositsList).build();
    }

    private static RefundRequest mockRefundRequest(RefundReason reason) {
        return RefundRequest
            .builder()
            .hotelCode("LONEUS")
            .refund(Refund
                .builder()
                .type(PaymentType.CARD)
                .amount(Amount
                    .builder()
                    .currency("GBP")
                    .minorUnits(BigDecimal.TEN)
                    .build())
                .card(Card
                    .builder()
                    .token("4216333880397891103")
                    .expiryYear("26")
                    .expiryMonth("03")
                    .build())
                .reason(reason)
                .build())
            .booking(Booking
                .builder()
                .channel("WEB")
                .type("PAY_NOW")
                .journey("REFUND")
                .businessSite(BusinessSite
                    .builder()
                    .identifier("LONEUS")
                    .type("HOTEL")
                    .build())
                .build())
            .refundType(RefundType.PARTIAL)
            .build();
    }

    private Optional<RefundResponse> mockRefundResponse() {
        return Optional.of(RefundResponse.builder().refunded(true).build());
    }
}
