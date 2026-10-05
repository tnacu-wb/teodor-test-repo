package uk.co.whitbread.reservation.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.exceptions.AmendReservationException;
import uk.co.whitbread.reservation.domain.model.amend.in.DepositFolioComputationResult;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.AcceptedCreditCard;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.HotelPaymentInformation;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.CurrencyAmount;
import uk.co.whitbread.reservation.domain.model.out.DepositFolio;
import uk.co.whitbread.reservation.domain.model.out.DepositFolioCharge;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.model.out.RefundResponse;
import uk.co.whitbread.reservation.domain.model.out.ResCashieringType;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationPaymentCardType;
import uk.co.whitbread.reservation.domain.model.out.ReservationTaxTypeInfo;
import uk.co.whitbread.reservation.domain.model.out.RoomStayByIdResponse;
import uk.co.whitbread.reservation.domain.model.payment.in.RefundRequest;
import uk.co.whitbread.reservation.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelReservationOhipOutPort;

@ExtendWith(MockitoExtension.class)
class AmendPayNowLogicTest {

  public static final String BASKET_REFERENCE = "TST-16a014c5-d8a4-4418-8d15-2537660f08e9";
  public static final String BOOKING_REFERENCE = "TST1234567";
  private static final String RATE_PLAN_CODE = "FLEX";
  public static final String HOTEL_ID = "TESTHOTEL";
  private static final String ARRIVAL_DATE = "2022-05-05";
  public static final String ACCOMMODATION_CODE = "__ACCMOD__";
  public static final String PI_CHANNEL = "PI";
  @InjectMocks
  private AmendPayNowLogic amendPayNowLogic;

  @Mock
  private HotelReservationOhipOutPort hotelReservationOhipOutPort;

  @Mock
  BasketOutPort basketOutPort;

  @Test
  void depositFolios_roomRemoved() {
    List<String> origResIds = List.of("origResId1", "origResId2", "origResId1", "origResId2");
    BasketResponse temporaryBasket = mockTempBasketRoomRemovedResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());

    var depositFoliosOriginal = mockDepositFolios(origResIds);
    var depositFoliosTemp = mockDepositFolios(tempResIds);

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), PI_CHANNEL,
        mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertFalse(calculatedDfs.getMarkAsPayOnArrival());
    assertEquals(depositFoliosOriginal.getDepositFolios().get(0).getCharges().get(0)
        .getCurrencyAmount().getCurrencyCode(), calculatedDfs.getCurrencyCode());
    assertEquals(2, calculatedDfs.getDepositFoliosAfterAmend().size());
    assertEquals(BigDecimal.valueOf(3660.0), calculatedDfs.getTotalRefundAmt());
  }

  @Test
  void depositFolios_roomAdded_POA_PaymentType() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketRoomAddedResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");
    origResIds.add("origResId2");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFolios(tempResIds);

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertTrue(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_roomAdded_PN_PaymentType() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketRoomAddedResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");
    origResIds.add("origResId2");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFolios(tempResIds);

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_NOW.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertFalse(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_extraChargeAdded_Poa_PaymentType() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");
    origResIds.add("origResId2");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFoliosChargeAdded(tempResIds);

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertTrue(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_extraChargeAdded_Pn_PaymentType_Poa_Amend() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");

    var depositFoliosOriginal = mockDepositFolioBefore(origResIds.get(0));
    var depositFoliosTemp = mockDepositFoliosChargeAddedAfter(tempResIds);

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertTrue(calculatedDfs.getMarkAsPayOnArrival());
    assertEquals(0, calculatedDfs.getDepositFoliosAfterAmend().size());
  }

  @Test
  void depositFolios_extraChargeAdded_Pn_PaymentType_Poa_Amend2() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");

    var depositFoliosOriginal = mockDepositFolioBefore2(origResIds.get(0));
    var depositFoliosTemp = mockDepositFoliosChargeAddedAfter2(tempResIds);

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), "PI", mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertTrue(calculatedDfs.getMarkAsPayOnArrival());
    assertEquals(0, calculatedDfs.getDepositFoliosAfterAmend().size());
  }

  @Test
  void depositFolios_extraChargeAdded_PN_PaymentType() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");
    origResIds.add("origResId2");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFoliosChargeAdded(tempResIds);

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_NOW.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertFalse(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_extraChargeAdded_Only_PN_PaymentType() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFoliosChargeAdded(tempResIds);

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_NOW.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertFalse(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_chargeRemoved() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");
    origResIds.add("origResId2");

    var depositFoliosOriginal = mockDepositFolios(origResIds);
    var depositFoliosTemp = mockDepositFoliosChargeRemoved(tempResIds);

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertFalse(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_sameCharge_lowerValue() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");
    origResIds.add("origResId2");

    var depositFoliosOriginal = mockDepositFolios(origResIds);
    var depositFoliosTemp = mockDepositFolios(tempResIds);
    depositFoliosTemp.getDepositFolios().get(0).getCharges().get(0).getCurrencyAmount()
        .setAmount(BigDecimal.valueOf(135));

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertFalse(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_sameCharge_biggerValue_POA_PaymentType() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");
    origResIds.add("origResId2");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFolios(tempResIds);
    depositFoliosTemp.getDepositFolios().get(0).getCharges().get(0).getCurrencyAmount()
        .setAmount(BigDecimal.valueOf(900));

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertTrue(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_sameCharge_biggerValue_NoPaymentTypeSent() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");
    origResIds.add("origResId2");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFolios(tempResIds);
    depositFoliosTemp.getDepositFolios().get(0).getCharges().get(0).getCurrencyAmount()
        .setAmount(BigDecimal.valueOf(900));

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        null,
        PI_CHANNEL,
        mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertTrue(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_sameCharge_biggerValue_PN_PaymentType() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");
    origResIds.add("origResId2");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFolios(tempResIds);
    depositFoliosTemp.getDepositFolios().get(0).getCharges().get(0).getCurrencyAmount()
        .setAmount(BigDecimal.valueOf(900));

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_NOW.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertFalse(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_sameCharge_useMoneyFromBucket_PN_PaymentType() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFolios(tempResIds);
    depositFoliosTemp.getDepositFolios().get(0).getCharges().get(0).getCurrencyAmount()
        .setAmount(BigDecimal.valueOf(20));
    depositFoliosTemp.getDepositFolios().get(0).getCharges().get(1).getCurrencyAmount()
        .setAmount(BigDecimal.valueOf(1300));

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_NOW.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertFalse(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_sameCharge_useMoneyFromBucket_and_Remaining_with_POA() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFolios(tempResIds);
    depositFoliosTemp.getDepositFolios().get(0).getCharges().get(0).getCurrencyAmount()
        .setAmount(BigDecimal.valueOf(20));
    depositFoliosTemp.getDepositFolios().get(0).getCharges().get(1).getCurrencyAmount()
        .setAmount(BigDecimal.valueOf(1300));

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertTrue(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_sameCharge_biggerValue_PN_PaymentType_NegativeOriginalDFValue() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");
    origResIds.add("origResId2");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFolios(tempResIds);
    depositFoliosTemp.getDepositFolios().get(0).getCharges().get(0).getCurrencyAmount()
        .setAmount(BigDecimal.valueOf(900));

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_NOW.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertFalse(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_removeCharge_addOneLowerValue() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");
    origResIds.add("origResId2");

    var depositFoliosOriginal = mockDepositFolios(origResIds);
    var depositFoliosTemp = mockDepositFoliosChargeRemovedChargeAddedCharge(tempResIds);

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertFalse(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_removeCharge_addOneBiggerValue() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");
    origResIds.add("origResId2");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFoliosChargeRemovedChargeAddedCharge(tempResIds);

    depositFoliosTemp.getDepositFolios().get(0).getCharges().get(2).getCurrencyAmount()
        .setAmount(BigDecimal.valueOf(300));
    depositFoliosTemp.getDepositFolios().get(1).getCharges().get(2).getCurrencyAmount()
        .setAmount(BigDecimal.valueOf(300));

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertTrue(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_removeCharge_addOneBiggerValue_POARemainingPayment() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFoliosChargeRemovedChargeAddedCharge(tempResIds);

    depositFoliosTemp.getDepositFolios().get(0).getCharges().get(2).getCurrencyAmount()
        .setAmount(BigDecimal.valueOf(1300));

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertTrue(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_removeCharge_addOneBiggerValue_NoRefBucket_POAPayment() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFoliosChargeAdded(tempResIds);

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertTrue(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_removeCharge_addOneBiggerValue_PN_PaymentType() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");
    origResIds.add("origResId2");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFoliosChargeRemovedChargeAddedCharge_NegativeRefBucket(
        tempResIds);

    depositFoliosTemp.getDepositFolios().get(0).getCharges().get(2).getCurrencyAmount()
        .setAmount(BigDecimal.valueOf(2300));
    depositFoliosTemp.getDepositFolios().get(1).getCharges().get(2).getCurrencyAmount()
        .setAmount(BigDecimal.valueOf(300));

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_NOW.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertFalse(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_removeCharge_addOneBiggerValue_addOneLowerValue() {
    List<String> origResIds = List.of("origResId1", "origResId2", "origResId1", "origResId2");
    BasketResponse temporaryBasket = mockTempBasketResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());

    var depositFoliosOriginal = mockDepositFolios(origResIds);
    var depositFoliosTemp = mockDepositFoliosChargeRemovedChargeAddedCharge(tempResIds);

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    // only first reservation has a new charge with value bigger than the original one that was removed
    // second reservation has a new charge with a value lower than the original one that was removed, so the money
    // remained are enough to cover both new added charges -> markAsPayOnArrival = false
    depositFoliosTemp.getDepositFolios().get(0).getCharges().get(2).getCurrencyAmount()
        .setAmount(BigDecimal.valueOf(300));

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertFalse(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_roomRemoved_chargeAdded_lowerPrice() {
    List<String> origResIds = List.of("origResId1", "origResId2", "origResId1", "origResId2");
    BasketResponse temporaryBasket = mockTempBasketRoomRemovedResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());

    var depositFoliosOriginal = mockDepositFolios(origResIds);
    var depositFoliosTemp = mockDepositFoliosChargeAdded(tempResIds);

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertFalse(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_chargeRemoved_roomAdded_biggerValue_POA_PaymentType() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketRoomAddedResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");
    origResIds.add("origResId2");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFoliosChargeRemoved(tempResIds);

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_ON_ARRIVAL.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertTrue(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void depositFolios_chargeRemoved_roomAdded_biggerValue_PN_PaymentType() {
    List<String> origResIds = new ArrayList<>();
    BasketResponse temporaryBasket = mockTempBasketRoomAddedResponse();
    List<String> tempResIds = new ArrayList<>(temporaryBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId).toList());
    origResIds.add("origResId1");
    origResIds.add("origResId2");

    var depositFoliosOriginal = mockDepositFolioOriginal(origResIds.get(0));
    var depositFoliosTemp = mockDepositFoliosChargeRemoved(tempResIds);

    // Arrange
    when(basketOutPort.getChargesByReservationIds(any())).thenReturn(depositFoliosOriginal);
    when(hotelReservationOhipOutPort.getGeneratedDepositFolios(HOTEL_ID,
        new HashSet<>(tempResIds))).thenReturn(depositFoliosTemp);

    //Act
    var calculatedDfs = amendPayNowLogic.calculateDfForPayNow(origResIds, HOTEL_ID, temporaryBasket,
        temporaryBasket.getLinkAmendReservations(),
        mockReservationByBasketRefResponse("origResId1"),
        PaymentOption.PAY_NOW.toString(), PI_CHANNEL, mockHotelPaymentInfoResponse());

    //Asserts
    assertNotNull(calculatedDfs);
    assertFalse(calculatedDfs.getMarkAsPayOnArrival());
  }

  @Test
  void saveDepositFolios_Success() {

    DepositFolioComputationResult computationResult = mockDepositFolioComputationResult(
        BigDecimal.valueOf(999));
    String hotelId = "HOTELID";
    List<String> originalReservationIdsToDelete = new ArrayList<>();

    //Act
    amendPayNowLogic.saveDepositFolios(computationResult, hotelId, originalReservationIdsToDelete,
        new ArrayList<>());

    //Assert
    verify(hotelReservationOhipOutPort, times(1)).saveCharges(any());
    verify(basketOutPort, times(1)).saveCharges(any());
  }

  @Test
  void saveDepositFolios_NoCharges() {

    DepositFolioComputationResult computationResult = mockDepositFolioComputationResult(
        BigDecimal.ZERO);
    String hotelId = "HOTELID";
    List<String> originalReservationIdsToDelete = new ArrayList<>();

    //Act
    amendPayNowLogic.saveDepositFolios(computationResult, hotelId, originalReservationIdsToDelete,
        new ArrayList<>());

    //Assert
    verifyNoMoreInteractions(basketOutPort);
    verifyNoMoreInteractions(hotelReservationOhipOutPort);
  }

  @ParameterizedTest()
  @MethodSource("saveDepositFoliosUpdated")
  void saveDepositFoliosUpdated_Success(
      DepositFolioComputationResult computationResult,
      DepositFoliosResponse depositFoliosAmounts,
      DepositFoliosResponse depositFoliosAmountsPos,
      DepositFoliosResponse depositFoliosAmountsNeg) {

    List<String> originalReservationIdsToDelete = new ArrayList<>();

    //Act
    var depositFolios = amendPayNowLogic.saveDepositFolios(computationResult, HOTEL_ID,
        originalReservationIdsToDelete,
        new ArrayList<>());

    //Assert
    assertNotNull(depositFolios);
    assertEquals(depositFoliosAmounts, depositFolios);
    if (!depositFoliosAmounts.getDepositFolios().isEmpty()) {
      verify(basketOutPort).saveCharges(depositFoliosAmounts);
      verify(hotelReservationOhipOutPort).saveCharges(depositFoliosAmountsPos);
      if (depositFoliosAmountsNeg != null) {
        verify(hotelReservationOhipOutPort).saveCharges(depositFoliosAmountsNeg);
      }
    }
  }

  @Test
  void triggerRefund_amountNotRounded_Success() {

    DepositFolioComputationResult computationResult = mockDepositFolioComputationResult(
        BigDecimal.valueOf(999));
    ReservationByBasketRefResponse reservationByBasketRefResponse = mockReservationByBasketRefResponse(null);
    RefundResponse refundResponse = RefundResponse.builder()
        .paymentId("3535356")
        .refunded(true)
        .build();
    ArgumentCaptor<RefundRequest> refundRequestCaptor = ArgumentCaptor.forClass(RefundRequest.class);
    BigDecimal amount = BigDecimal.valueOf(10.0000);
    computationResult.setTotalRefundAmt(amount);
    BigDecimal expectedAmount = BigDecimal.valueOf(1000.0);
    // Arrange
    when(basketOutPort.triggerRefundRequest(anyString(), any())).thenReturn(refundResponse);

    //Act
    DepositFolioComputationResult depositFolioComputationResult = amendPayNowLogic.triggerRefund(
        reservationByBasketRefResponse, computationResult, "reference", "reference");

    // Assert
    verify(basketOutPort).triggerRefundRequest(anyString(), refundRequestCaptor.capture());
    RefundRequest capturedRequest = refundRequestCaptor.getValue();
    assertNotNull(capturedRequest);
    assertEquals(0, expectedAmount.compareTo(capturedRequest.getRefund().getAmount().getMinorUnits()));

    assertNotNull(depositFolioComputationResult);
  }

  @Test
  void triggerRefund_amountRounded_Success() {

    DepositFolioComputationResult computationResult = mockDepositFolioComputationResult(
        BigDecimal.valueOf(999));
    ReservationByBasketRefResponse reservationByBasketRefResponse = mockReservationByBasketRefResponse(null);
    RefundResponse refundResponse = RefundResponse.builder()
        .paymentId("3535356")
        .refunded(true)
        .build();
    ArgumentCaptor<RefundRequest> refundRequestCaptor = ArgumentCaptor.forClass(RefundRequest.class);
    BigDecimal amount = BigDecimal.valueOf(10.0050);
    computationResult.setTotalRefundAmt(amount);
    BigDecimal expectedAmount = BigDecimal.valueOf(1001.0);
    // Arrange
    when(basketOutPort.triggerRefundRequest(anyString(), any())).thenReturn(refundResponse);

    //Act
    DepositFolioComputationResult depositFolioComputationResult = amendPayNowLogic.triggerRefund(
        reservationByBasketRefResponse, computationResult, "reference", "reference");

    // Assert
    verify(basketOutPort, times(1)).triggerRefundRequest(anyString(), refundRequestCaptor.capture());
    RefundRequest capturedRequest = refundRequestCaptor.getValue();
    assertNotNull(capturedRequest);
    assertEquals(0, expectedAmount.compareTo(capturedRequest.getRefund().getAmount().getMinorUnits()));

    assertNotNull(depositFolioComputationResult);
  }

  @Test
  void triggerRefund_throwsException() {

    DepositFolioComputationResult computationResult = mockDepositFolioComputationResult(
        BigDecimal.valueOf(999));
    ReservationByBasketRefResponse reservationByBasketRefResponse = mockReservationByBasketRefResponse(null);

    // Act & Assert
    assertThrows(AmendReservationException.class, () -> amendPayNowLogic.triggerRefund(
        reservationByBasketRefResponse, computationResult, "reference", "reference"));
  }

  private DepositFolioComputationResult mockDepositFolioComputationResult(
      BigDecimal depositFolioCurrencyAmount) {
    Map<String, Map<String, List<BigDecimal>>> depositFoliosAfterAmend = new HashMap<>();
    Map<String, List<BigDecimal>> depositFolioAfterAddingNewRoom = new HashMap<>();
    depositFolioAfterAddingNewRoom.put("reservation01-001", List.of(depositFolioCurrencyAmount));
    depositFoliosAfterAmend.put("reservation01", depositFolioAfterAddingNewRoom);
    return DepositFolioComputationResult.builder()
        .paymentId("Payment001")
        .markAsPayOnArrival(Boolean.TRUE)
        .depositFoliosAfterAmend(depositFoliosAfterAmend)
        .currencyCode("GBP")
        .totalRefundAmt(BigDecimal.valueOf(30))
        .build();
  }

  private DepositFoliosResponse mockDepositFolios(List<String> reservationIds) {

    List<DepositFolio> depositFolios = new ArrayList<>();
    reservationIds.forEach(resId -> {
      var df = mockDepositFolio(resId);
      depositFolios.add(df);
    });
    return DepositFoliosResponse.builder()
        .depositFolios(depositFolios)
        .build();
  }

  private DepositFoliosResponse mockDepositFolioOriginal(String reservationId) {

    List<DepositFolio> depositFolios = new ArrayList<>();
    var df = mockDepositFolio(reservationId);
    depositFolios.add(df);
    return DepositFoliosResponse.builder()
        .depositFolios(depositFolios)
        .build();
  }

  private DepositFoliosResponse mockDepositFoliosChargeAdded(List<String> reservationIds) {

    List<DepositFolio> depositFolios = new ArrayList<>();
    reservationIds.forEach(redId -> {
      var df = mockDepositFolioChargeAdded(redId);
      depositFolios.add(df);
    });
    return DepositFoliosResponse.builder()
        .depositFolios(depositFolios)
        .build();
  }

  private DepositFoliosResponse mockDepositFoliosChargeRemoved(List<String> reservationIds) {

    List<DepositFolio> depositFolios = new ArrayList<>();
    reservationIds.forEach(redId -> {
      var df = mockDepositFolioChargeRemoved(redId);
      depositFolios.add(df);
    });
    return DepositFoliosResponse.builder()
        .depositFolios(depositFolios)
        .build();
  }


  private DepositFoliosResponse mockDepositFoliosChargeRemovedChargeAddedCharge(
      List<String> reservationIds) {

    List<DepositFolio> depositFolios = new ArrayList<>();
    reservationIds.forEach(redId -> {
      var df = mockDepositFolioChargeRemovedChargeAdded(redId);
      depositFolios.add(df);
    });
    return DepositFoliosResponse.builder()
        .depositFolios(depositFolios)
        .build();
  }

  private DepositFoliosResponse mockDepositFoliosChargeRemovedChargeAddedCharge_NegativeRefBucket(
      List<String> reservationIds) {

    List<DepositFolio> depositFolios = new ArrayList<>();
    reservationIds.forEach(redId -> {
      var df = mockDepositFolioChargeRemovedChargeAdded_NegativeRefBucket(redId);
      depositFolios.add(df);
    });
    return DepositFoliosResponse.builder()
        .depositFolios(depositFolios)
        .build();
  }

  private DepositFolio mockDepositFolio(String resId) {
    var dfCharge1 = DepositFolioCharge.builder()
        .transactionCode("9016")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(200), "GBP"))
        .build();
    var dfCharge2 = DepositFolioCharge.builder()
        .transactionCode("9026")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(999), "GBP"))
        .build();
    var dfCharge3 = DepositFolioCharge.builder()
        .transactionCode(ACCOMMODATION_CODE)
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(21), "GBP"))
        .build();
    return DepositFolio.builder()
        .hotelId(HOTEL_ID)
        .reservationId(resId)
        .vatRegion("UK")
        .charges(List.of(dfCharge1, dfCharge2, dfCharge3))
        .build();
  }

  private DepositFolio mockDepositFolioChargeRemoved(String resId) {
    var dfCharge1 = DepositFolioCharge.builder()
        .transactionCode("9016")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(200), "GBP"))
        .build();
    var dfCharge2 = DepositFolioCharge.builder()
        .transactionCode("9026")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(999), "GBP"))
        .build();
    return DepositFolio.builder()
        .hotelId(HOTEL_ID)
        .reservationId(resId)
        .vatRegion("UK")
        .charges(List.of(dfCharge1, dfCharge2))
        .build();
  }

  private DepositFolio mockDepositFolioChargeAdded(String resId) {
    var dfCharge1 = DepositFolioCharge.builder()
        .transactionCode("9016")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(200), "GBP"))
        .build();
    var dfCharge2 = DepositFolioCharge.builder()
        .transactionCode("9026")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(999), "GBP"))
        .build();
    var dfCharge3 = DepositFolioCharge.builder()
        .transactionCode(ACCOMMODATION_CODE)
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(21), "GBP"))
        .build();
    var dfCharge4 = DepositFolioCharge.builder()
        .transactionCode("9036")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(55), "GBP"))
        .build();
    return DepositFolio.builder()
        .hotelId(HOTEL_ID)
        .reservationId(resId)
        .vatRegion("UK")
        .charges(List.of(dfCharge1, dfCharge2, dfCharge3, dfCharge4))
        .build();
  }

  private DepositFolio mockDepositFolioChargeRemovedChargeAdded(String resId) {
    var dfCharge2 = DepositFolioCharge.builder()
        .transactionCode("9026")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(999), "GBP"))
        .build();
    var dfCharge3 = DepositFolioCharge.builder()
        .transactionCode(ACCOMMODATION_CODE)
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(21), "GBP"))
        .build();
    var dfCharge4 = DepositFolioCharge.builder()
        .transactionCode("9036")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(55), "GBP"))
        .build();
    return DepositFolio.builder()
        .hotelId(HOTEL_ID)
        .reservationId(resId)
        .vatRegion("UK")
        .charges(List.of(dfCharge2, dfCharge3, dfCharge4))
        .build();
  }

  private DepositFolio mockDepositFolioChargeRemovedChargeAdded_NegativeRefBucket(String resId) {
    var dfCharge2 = DepositFolioCharge.builder()
        .transactionCode("9026")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(999), "GBP"))
        .build();
    var dfCharge3 = DepositFolioCharge.builder()
        .transactionCode(ACCOMMODATION_CODE)
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(21), "GBP"))
        .build();
    var dfCharge4 = DepositFolioCharge.builder()
        .transactionCode("9036")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(55), "GBP"))
        .build();
    return DepositFolio.builder()
        .hotelId(HOTEL_ID)
        .reservationId(resId)
        .vatRegion("UK")
        .charges(List.of(dfCharge2, dfCharge3, dfCharge4))
        .build();
  }

  private BasketResponse mockTempBasketRoomRemovedResponse() {
    return BasketResponse.builder()
        .hotelId(HOTEL_ID)
        .bookingReference(BOOKING_REFERENCE)
        .reference(BASKET_REFERENCE)
        .createdAt(new Date().toString())
        .status("OPEN")
        .itemTypes(Collections.emptySet())
        .items(mockTempBasketItemsListRoomRemoved())
        .paymentID("118932")
        .paymentOption(PaymentOption.PAY_NOW)
        .linkAmendReservations(Map.of("origResId1", "tempRes1"))
        .originalBasketId("HOTELCODE1001001")
        .build();
  }

  private BasketResponse mockTempBasketRoomAddedResponse() {
    return BasketResponse.builder()
        .hotelId(HOTEL_ID)
        .bookingReference(BOOKING_REFERENCE)
        .reference(BASKET_REFERENCE)
        .createdAt(new Date().toString())
        .status("OPEN")
        .itemTypes(Collections.emptySet())
        .linkAmendReservations(Map.of("origResId1", "tempRes1", "origResId2", "tempRes2",
            "origResId3", "tempRes3", "origResId4", "tempRes4"))
        .items(mockTempBasketItemsListRoomAdded())
        .paymentID("118932")
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .originalBasketId("HOTELCODE1001001")
        .build();
  }

  private BasketResponse mockTempBasketResponse() {
    return BasketResponse.builder()
        .hotelId(HOTEL_ID)
        .bookingReference(BOOKING_REFERENCE)
        .reference(BASKET_REFERENCE)
        .createdAt(new Date().toString())
        .status("OPEN")
        .itemTypes(Collections.emptySet())
        .linkAmendReservations(Map.of("origResId1", "tempRes1", "origResId2", "tempRes2"))
        .items(mockTempBasketItems())
        .paymentID("118932")
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .originalBasketId("HOTELCODE1001001")
        .build();
  }

  private List<BasketItemResponse> mockTempBasketItemsListRoomRemoved() {
    List<BasketItemResponse> itemResponseList = new LinkedList<>();
    itemResponseList.add(
        BasketItemResponse.builder().type("reservation").sourceId("tempRes1").build());
    return itemResponseList;
  }

  private List<BasketItemResponse> mockTempBasketItems() {
    List<BasketItemResponse> itemResponseList = new LinkedList<>();
    itemResponseList.add(
        BasketItemResponse.builder().type("reservation").sourceId("tempRes1").build());
    itemResponseList.add(
        BasketItemResponse.builder().type("reservation").sourceId("tempRes2").build());
    return itemResponseList;
  }

  private List<BasketItemResponse> mockTempBasketItemsListRoomAdded() {
    List<BasketItemResponse> itemResponseList = new LinkedList<>();
    itemResponseList.add(
        BasketItemResponse.builder().type("reservation").sourceId("tempRes1").build());
    itemResponseList.add(
        BasketItemResponse.builder().type("reservation").sourceId("tempRes2").build());
    itemResponseList.add(
        BasketItemResponse.builder().type("reservation").sourceId("tempRes3").build());
    return itemResponseList;
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponse(String rsvId) {
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(
            List.of(mockReservationById(rsvId)))
        .bookingReference("bookingRef")
        .build();
  }

  private ReservationByIdResponse mockReservationById(String rsvId) {
    return ReservationByIdResponse.builder()
        .reservationId(rsvId)
        .roomStay(mockRoomStay())
        .paymentCard(mockReservationPaymentCardType())
        .cashiering(mockResCashieringType())
        .build();
  }

  private ReservationPaymentCardType mockReservationPaymentCardType() {
    return ReservationPaymentCardType.builder().paymentMethod("VA")
        .build();
  }

  private ResCashieringType mockResCashieringType() {
    return ResCashieringType.builder().taxType(ReservationTaxTypeInfo.builder().code("UK").build())
        .build();
  }

  private RoomStayByIdResponse mockRoomStay() {
    return RoomStayByIdResponse.builder()
        .arrivalDate(ARRIVAL_DATE)
        .ratePlanCode(RATE_PLAN_CODE)
        .build();
  }

  private DepositFoliosResponse mockDepositFoliosChargeAddedAfter2(List<String> tempResIds) {
    List<DepositFolio> depositFolios = new ArrayList<>();
    tempResIds.forEach(redId -> {
      var df = mockDepositFolioChargeAddedForReservation2(redId);
      depositFolios.add(df);
    });
    return DepositFoliosResponse.builder()
        .depositFolios(depositFolios)
        .build();
  }

  private DepositFolio mockDepositFolioChargeAddedForReservation2(String resId) {
    var dfCharge1 = DepositFolioCharge.builder()
        .transactionCode("9026")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(999), "GBP"))
        .build();

    var dfCharge2 = DepositFolioCharge.builder()
        .transactionCode("9036")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(110), "GBP"))
        .build();
    return DepositFolio.builder()
        .hotelId(HOTEL_ID)
        .reservationId(resId)
        .vatRegion("UK")
        .charges(List.of(dfCharge1, dfCharge2))
        .build();
  }

  private DepositFoliosResponse mockDepositFolioBefore2(String reservationId) {
    List<DepositFolio> depositFolios = new ArrayList<>();
    var df = mockDepositFolioForReservation2(reservationId);
    depositFolios.add(df);
    return DepositFoliosResponse.builder()
        .depositFolios(depositFolios)
        .build();
  }

  private DepositFolio mockDepositFolioForReservation2(String reservationId) {
    var dfCharge1 = DepositFolioCharge.builder()
        .transactionCode("9026")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(999), "GBP"))
        .build();

    var dfCharge2 = DepositFolioCharge.builder()
        .transactionCode("9036")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(55), "GBP"))
        .build();

    return DepositFolio.builder()
        .hotelId(HOTEL_ID)
        .reservationId(reservationId)
        .vatRegion("UK")
        .charges(List.of(dfCharge1, dfCharge2))
        .build();
  }

  private DepositFoliosResponse mockDepositFoliosChargeAddedAfter(List<String> tempResIds) {
    List<DepositFolio> depositFolios = new ArrayList<>();
    tempResIds.forEach(redId -> {
      var df = mockDepositFolioChargeAddedForReservation(redId);
      depositFolios.add(df);
    });
    return DepositFoliosResponse.builder()
        .depositFolios(depositFolios)
        .build();
  }

  private DepositFolio mockDepositFolioChargeAddedForReservation(String resId) {

    var dfCharge1 = DepositFolioCharge.builder()
        .transactionCode("9026")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(999), "GBP"))
        .build();

    var dfCharge2 = DepositFolioCharge.builder()
        .transactionCode("9036")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(55), "GBP"))
        .build();
    return DepositFolio.builder()
        .hotelId(HOTEL_ID)
        .reservationId(resId)
        .vatRegion("UK")
        .charges(List.of(dfCharge1, dfCharge2))
        .build();
  }

  private DepositFoliosResponse mockDepositFolioBefore(String reservationId) {
    List<DepositFolio> depositFolios = new ArrayList<>();
    var df = mockDepositFolioForReservation(reservationId);
    depositFolios.add(df);
    return DepositFoliosResponse.builder()
        .depositFolios(depositFolios)
        .build();
  }

  private DepositFolio mockDepositFolioForReservation(String reservationId) {

    var dfCharge1 = DepositFolioCharge.builder()
        .transactionCode("9026")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(999), "GBP"))
        .build();
    return DepositFolio.builder()
        .hotelId(HOTEL_ID)
        .reservationId(reservationId)
        .vatRegion("UK")
        .charges(List.of(dfCharge1))
        .build();
  }

  private HotelPaymentInformation mockHotelPaymentInfoResponse() {
    return HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(
            AcceptedCreditCard.builder().code("DL").codeOpera("VA").build(),
            AcceptedCreditCard.builder().code("EL").codeOpera("VA").build()
        )).paymentMethodsOpera(Map.of("PI_VA", "DVA", "PI_MC", "DMC")).build();
  }

  private static Stream<Arguments> saveDepositFoliosUpdated() {
    Map<String, Map<String, List<BigDecimal>>> depositFoliosAfterAmend = new HashMap<>();
    var computationResult1 = mockDepositFolioComputationResult(150, -100, -50);
    var computationResult2 = mockDepositFolioComputationResult(150, 100, -50);

    Map<String, List<BigDecimal>> depositFolioAfterAddingNewRoom = new HashMap<>();
    depositFolioAfterAddingNewRoom.put("2023-01-01-9016", List.of(BigDecimal.valueOf(150)));
    depositFoliosAfterAmend.put("2023-01-01", depositFolioAfterAddingNewRoom);

    var computationResult3 = DepositFolioComputationResult.builder()
        .paymentId("Payment001")
        .markAsPayOnArrival(Boolean.TRUE)
        .depositFoliosAfterAmend(depositFoliosAfterAmend)
        .currencyCode("GBP")
        .totalRefundAmt(BigDecimal.valueOf(30))
        .build();

    var computationResult4 = DepositFolioComputationResult.builder()
        .paymentId("Payment001")
        .markAsPayOnArrival(Boolean.TRUE)
        .depositFoliosAfterAmend(new HashMap<>())
        .currencyCode("GBP")
        .totalRefundAmt(BigDecimal.valueOf(30))
        .build();

    var dfCharge1 = mockDepositFolioCharge(150);
    var dfCharge2 = mockDepositFolioCharge(-100);
    var dfCharge3 = mockDepositFolioCharge(-50);
    var dfCharge4 = mockDepositFolioCharge(100);

    var pos = mockDepositFolio(List.of(dfCharge1));
    var neg = mockDepositFolio(List.of(dfCharge2, dfCharge3));
    var all1 = mockDepositFolio(List.of(dfCharge1, dfCharge2, dfCharge3));
    var all2 = mockDepositFolio(List.of(dfCharge1, dfCharge4, dfCharge3));

    DepositFoliosResponse depositFoliosAmounts1 = mockDepositFoliosResponse(all1);
    DepositFoliosResponse depositFoliosAmounts2 = mockDepositFoliosResponse(all2);
    DepositFoliosResponse depositFoliosAmountsPos = mockDepositFoliosResponse(pos);
    DepositFoliosResponse depositFoliosAmountsNeg = mockDepositFoliosResponse(neg);
    DepositFoliosResponse emptyDeposit = DepositFoliosResponse.builder()
        .depositFolios(Collections.emptyList()).build();

    return Stream.of(
        Arguments.of(computationResult1, depositFoliosAmounts1, depositFoliosAmountsPos,
            depositFoliosAmountsNeg),
        Arguments.of(computationResult2, depositFoliosAmounts2, depositFoliosAmounts2,
            null),
        Arguments.of(computationResult3, depositFoliosAmountsPos, depositFoliosAmountsPos,
            null),
        Arguments.of(computationResult4, emptyDeposit, emptyDeposit,
            null));
  }

  private static DepositFolio mockDepositFolio(List<DepositFolioCharge> dfCharges) {
    return DepositFolio.builder()
        .hotelId(HOTEL_ID)
        .reservationId("2023-01-01")
        .paymentId("Payment001")
        .charges(dfCharges)
        .build();
  }

  private static DepositFolioCharge mockDepositFolioCharge(int amount) {
    return DepositFolioCharge.builder()
        .transactionCode("9016")
        .quantity(1)
        .reference("2023-01-01")
        .currencyAmount(new CurrencyAmount(BigDecimal.valueOf(amount), "GBP"))
        .build();
  }

  @NotNull
  private static DepositFoliosResponse mockDepositFoliosResponse(DepositFolio df) {
    DepositFoliosResponse depositFoliosAmounts = new DepositFoliosResponse();
    List<DepositFolio> foliosAmounts = new ArrayList<>();
    foliosAmounts.add(df);
    depositFoliosAmounts.setDepositFolios(foliosAmounts);
    return depositFoliosAmounts;
  }

  private static DepositFolioComputationResult mockDepositFolioComputationResult(Integer amount1,
      Integer amount2, Integer amount3) {
    Map<String, Map<String, List<BigDecimal>>> depositFoliosAfterAmend = new HashMap<>();
    Map<String, List<BigDecimal>> depositFolioAfterAddingNewRoom = new HashMap<>();
    depositFolioAfterAddingNewRoom.put("2023-01-01-9016",
        List.of(BigDecimal.valueOf(amount1), BigDecimal.valueOf(amount2),
            BigDecimal.valueOf(amount3)));
    depositFoliosAfterAmend.put("2023-01-01", depositFolioAfterAddingNewRoom);

    return DepositFolioComputationResult.builder()
        .paymentId("Payment001")
        .markAsPayOnArrival(Boolean.TRUE)
        .depositFoliosAfterAmend(depositFoliosAfterAmend)
        .currencyCode("GBP")
        .totalRefundAmt(BigDecimal.valueOf(30))
        .build();
  }
}
