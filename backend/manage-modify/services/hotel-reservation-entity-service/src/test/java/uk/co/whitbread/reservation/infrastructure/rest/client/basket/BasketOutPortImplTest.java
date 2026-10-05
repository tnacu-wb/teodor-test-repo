package uk.co.whitbread.reservation.infrastructure.rest.client.basket;

import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
  import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.util.function.Tuples;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AddBasketItemTypeDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto.StatusEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketError;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CancelBasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CcuiPaymentRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ChangeBasketIdContextDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestReservationDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.EmailRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ErroredBookingDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.InitiatePaymentResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PrepaidDepositsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PrepaidDepositsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ProcessAmendRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromoKind;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromotionsInformationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.RefundRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.RefundResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.UpdateBasketItemOccupancyRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.UpdateBasketItemSupplementDto;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.model.amend.in.ConfirmAmendLogicRequest;
import uk.co.whitbread.reservation.domain.model.in.EmailRequest;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.CopyReservationsResponse;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationCreationResponse;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.RefundResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationInfo;
import uk.co.whitbread.reservation.domain.model.out.ReservationPaymentCardType;
import uk.co.whitbread.reservation.domain.model.out.Reservations;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomStay;
import uk.co.whitbread.reservation.domain.model.out.UniqueIDType;
import uk.co.whitbread.reservation.domain.model.payment.in.PaymentRequest;
import uk.co.whitbread.reservation.domain.model.payment.in.PaymentsConfirmation;
import uk.co.whitbread.reservation.domain.model.payment.in.RefundRequest;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.exceptions.BasketDigitalException;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.BasketMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.BasketUpdateRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.CancelBasketRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.DepositFoliosRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.DepositFoliosResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.PaymentMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.RefundMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.BasketClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.properties.BasketProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.EmailRequestMapper;

@ExtendWith(MockitoExtension.class)
class BasketOutPortImplTest {

  private static final String SOURCE_ID = "111";
  @Mock
  private BasketClient basketClient;

  @Mock
  private BasketMapper basketMapper;

  @Mock
  private DepositFoliosRequestMapper depositFoliosRequestMapper;

  @Mock
  private RefundMapper refundMapper;

  @Mock
  private PaymentMapper paymentMapper;

  @Mock
  private BasketProperties basketProperties;

  @Mock
  private DepositFoliosResponseMapper depositFoliosResponseMapper;

  @Mock
  private CancelBasketRequestMapper cancelBasketRequestMapper;

  @Mock
  private EmailRequestMapper emailRequestMapper;

  @Mock
  private BasketUpdateRequestMapper basketUpdateRequestMapper;

  @InjectMocks
  private BasketOutPortImpl basketOutPort;

  @Test
  void testCreateBasket_success() {
    // Arrange
    when(basketClient.createBasket(any(CreateBasketRequestDto.class))).thenReturn(
        Tuples.of(new BasketDto(), "123"));
    when(basketMapper.toModel(any(BasketDto.class))).thenReturn(new BasketResponse());

    // Act
    var response = basketOutPort.createBasket("TST", "PI", anyString());

    // Assert
    assertNotNull(response);
    assertThat(response.getETag(), is("123"));
  }

  @Test
  void testAddReservationsToBasket_success() {
    // Arrange
    when(basketClient.addBasketItems(any(), any(), any())).thenReturn(
        Tuples.of(new BasketDto(), "343545"));
    when(basketMapper.toModel(any())).thenReturn(new BasketResponse());
    when(basketProperties.getStayItemType()).thenReturn("STAY");
    when(basketProperties.getStayConfirmationData()).thenReturn(asList("test"));

    // Act
    var ohipReservationsResponse = new OhipReservationResponse();
    var reservation = mockReservationCreationResponse();
    ohipReservationsResponse.setReservations(asList(reservation));

    var response = basketOutPort.addReservationsToBasket("TST", "123", ohipReservationsResponse,
        false, false);

    // Assert
    assertNotNull(response);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2})
  void testAddReservationsWithOccSupplToBasket_success(int noOfAdults) {
    // Arrange
    when(basketClient.addBasketItems(any(), any(), any())).thenReturn(
        Tuples.of(new BasketDto(), "343545"));
    when(basketMapper.toModel(any())).thenReturn(new BasketResponse());
    when(basketProperties.getStayItemType()).thenReturn("STAY");
    when(basketProperties.getStayConfirmationData()).thenReturn(asList("test"));

    // Act
    var ohipReservationsResponse = new OhipReservationResponse();
    var reservation = mockReservationCreationResponse();
    ohipReservationsResponse.setReservations(asList(reservation));

    var response = basketOutPort.addReservationsToBasket("TST", "123", ohipReservationsResponse,
        false, true, noOfAdults, true);

    // Assert
    assertNotNull(response);
  }

  @Test
  void testAddReservationsToBasket_failed() {
    // Arrange
    var ohipReservationsResponse = new OhipReservationResponse();
    ohipReservationsResponse.setReservations(asList(null));
    // Act
    var ex = assertThrows(BasketDigitalException.class,
        () -> basketOutPort.addReservationsToBasket("TST", "123", ohipReservationsResponse,
            false, false));
    assertEquals(ErrorCode.DIGITAL_BASKET_ITEM_EXCEPTION.getCode(), ex.getErrorCode());
    assertEquals("Error while trying to find basket item locking time", ex.getMessage());
  }

  @Test
  void testCancelBasket_success() {
    // Arrange
    var basketRef = "ABC";
    var cancelBasketDto = new CancelBasketDto().isFailed(false).sendEmail(true);
    doNothing().when(basketClient).sendCancelBasket(basketRef, cancelBasketDto);
    when(cancelBasketRequestMapper.toDto(false, true, null))
        .thenReturn(cancelBasketDto);

    // Act
    basketOutPort.cancelBasket(basketRef, false, true, null);

    // Assert
    verify(basketClient).sendCancelBasket(basketRef, cancelBasketDto);
  }

  @Test
  void testUpdateOccupancySupplementFlag_success() {
    // Arrange
    var basketRef = "basketRef";
    var lastModifiedETag = "eTag";
    var responseOfUpdateBasketItemSupplementDtoMapper = new UpdateBasketItemSupplementDto();
    responseOfUpdateBasketItemSupplementDtoMapper.setHasOccupancySup(true);
    var expectedUpdateRequest = new UpdateBasketItemOccupancyRequestDto();
    expectedUpdateRequest.addItemsItem(responseOfUpdateBasketItemSupplementDtoMapper);
    when(basketUpdateRequestMapper.toUpdateBasketItemOccupancyRequestDto(any()))
        .thenReturn(new UpdateBasketItemSupplementDto());
    when(basketClient.updateOccupancySupplement(basketRef, expectedUpdateRequest, lastModifiedETag))
        .thenReturn(null);

    // Assert
    Assertions.assertDoesNotThrow(() -> basketOutPort.updateOccupancySupplementFlag(basketRef,
        null,
        true,
        lastModifiedETag));
  }

  @Test
  void testUpdateOccupancySupplementFlagOnMultipleItems_success() {
    // Arrange
    var basketRef = "basketRef";
    var lastModifiedETag = "eTag";
    var responseOfUpdateBasketItemSupplementDtoMapper = new UpdateBasketItemSupplementDto();
    responseOfUpdateBasketItemSupplementDtoMapper.setHasOccupancySup(true);
    responseOfUpdateBasketItemSupplementDtoMapper.setSourceId(SOURCE_ID);
    var expectedUpdateRequest = new UpdateBasketItemOccupancyRequestDto();
    expectedUpdateRequest.addItemsItem(responseOfUpdateBasketItemSupplementDtoMapper);
    when(basketUpdateRequestMapper.toUpdateBasketItemOccupancyRequestDto(any()))
        .thenReturn(responseOfUpdateBasketItemSupplementDtoMapper);
    when(basketClient.updateOccupancySupplement(basketRef, expectedUpdateRequest, lastModifiedETag))
        .thenReturn(null);
    var items = new ArrayList<BasketItemResponse>();
    items.add(BasketItemResponse.builder().hasOccupancySup(true).sourceId(SOURCE_ID).build());

    // Assert
    Assertions.assertDoesNotThrow(() -> basketOutPort.updateOccupancySupplementFlag(basketRef,
        items,
        lastModifiedETag));
  }

  @Test
  void testCancelBasket_failed() {
    // Arrange
    var basketRef = "ABC";
    var cancelBasketDto = new CancelBasketDto().isFailed(true).sendEmail(true);
    doNothing().when(basketClient).sendCancelBasket(basketRef, cancelBasketDto);
    when(cancelBasketRequestMapper.toDto(true, true, null))
        .thenReturn(cancelBasketDto);

    // Act
    basketOutPort.cancelBasket(basketRef, true, true, null);

    // Assert
    verify(basketClient).sendCancelBasket(basketRef, cancelBasketDto);
  }

  @Test
  void testSetErroredBooking_success() {
    // Arrange
    var basketRef = "ABC";
    var erroredBookingDto = new ErroredBookingDto();
    final BasketError basketError = new BasketError();
    erroredBookingDto.setIsErroredBooking(true);
    erroredBookingDto.setBasketError(basketError);
    doNothing().when(basketClient).setErroredBooking(basketRef, erroredBookingDto);
    when(basketMapper.toDto(true, basketError))
        .thenReturn(erroredBookingDto);

    // Act
    basketOutPort.setErroredBooking(basketRef, true, basketError);

    // Assert
    verify(basketClient).setErroredBooking(basketRef, erroredBookingDto);
  }

  @Test
  void testSetErroredBooking_failed() {
    // Arrange
    var basketRef = "ABC";
    var erroredBookingDto = new ErroredBookingDto();
    erroredBookingDto.setIsErroredBooking(false);
    doNothing().when(basketClient).setErroredBooking(basketRef, erroredBookingDto);
    when(basketMapper.toDto(false, null))
        .thenReturn(erroredBookingDto);

    // Act
    basketOutPort.setErroredBooking(basketRef, false, null);

    // Assert
    verify(basketClient).setErroredBooking(basketRef, erroredBookingDto);
  }

  @Test
  void removeItem_Success() {
    // Arrange
    when(basketClient.removeItem(anyString(), anyString(), anyString())).thenReturn(
        Tuples.of(new BasketDto(), "123"));
    when(basketMapper.toModel(any(BasketDto.class))).thenReturn(new BasketResponse());

    // Act
    var response = basketOutPort.removeItem("123", "123", "123");

    // Assert
    assertNotNull(response);
    verify(basketClient).removeItem("123", "123", "123");
  }

  @Test
  void removeItems_Success() {
    // Arrange
    when(basketClient.removeItems(anyString(), anyList(), anyString())).thenReturn(
        Tuples.of(new BasketDto(), "123"));
    when(basketMapper.toModel(any(BasketDto.class))).thenReturn(new BasketResponse());

    // Act
    var response = basketOutPort.removeItems("123", List.of("123"), "123");

    // Assert
    assertNotNull(response);
    verify(basketClient).removeItems("123", List.of("123"), "123");
  }

  @Test
  void triggerRefundRequest_Success() {
    // Arrange
    when(basketClient.triggerRefund(anyString(), any(RefundRequestDto.class)))
        .thenReturn(new RefundResponseDto());
    when(refundMapper.toRequestDto(any(RefundRequest.class))).thenReturn(new RefundRequestDto());
    when(refundMapper.toResponseModel(any(RefundResponseDto.class))).thenReturn(RefundResponse
        .builder().paymentId("123").refunded(true).refundId("123").build());

    // Act
    var response = basketOutPort.triggerRefundRequest("123", new RefundRequest());

    // Assert
    assertNotNull(response);
    verify(basketClient).triggerRefund(any(), any(RefundRequestDto.class));
  }

  @Test
  void triggerRefundRequest_Success2() {
    // Arrange
    when(basketClient.triggerRefund(anyString(), any(RefundRequestDto.class)))
        .thenReturn(new RefundResponseDto());

    // Act
    basketOutPort.triggerRefundRequest(ReservationByBasketRefResponse.builder()
            .hotelId("HOTELTEST")
            .currencyCode("code")
            .amountPaid(BigDecimal.TEN)
            .build(),
        "123");

    // Assert
    verify(basketClient).triggerRefund(anyString(), any(RefundRequestDto.class));
  }


  @Test
  void saveCharges_Success() {
    // Arrange
    doNothing().when(basketClient).saveCharges(any());
    when(depositFoliosRequestMapper.toDto(any())).thenReturn(new PrepaidDepositsRequestDto());

    // Act
    basketOutPort.saveCharges(DepositFoliosResponse.builder().build());

    // Assert
    verify(basketClient).saveCharges(any());
  }

  @Test
  void deleteBasket_Success() {
    // Arrange
    doNothing().when(basketClient).deleteBasket(anyString(), anyString());

    // Act
    basketOutPort.deleteBasket("123", "123");

    // Assert
    verify(basketClient).deleteBasket("123", "123");
  }

  @Test
  void testDeleteBasket() {
    // Arrange
    var basketRef = "ABC";
    var lastModifiedETag = "123";
    doNothing().when(basketClient).deleteBasket(basketRef, lastModifiedETag);

    // Act
    basketOutPort.deleteBasket(basketRef, lastModifiedETag);

    // Assert
    verify(basketClient).deleteBasket(basketRef, lastModifiedETag);
  }

  @Test
  void test_getCharges() {
    // Arrange
    var reservationId = "resId1";
    when(basketClient.getChargesByReservationIds(anyList())).thenReturn(new PrepaidDepositsDto());
    // Act
    basketOutPort.getChargesByReservationIds(List.of(reservationId));

    // Assert
    verify(basketClient).getChargesByReservationIds(List.of(reservationId));
  }

  @Test
  void triggerRefundRequest_Success3() {
    // Arrange
    when(basketClient.triggerRefund(anyString(), any(RefundRequestDto.class)))
        .thenReturn(new RefundResponseDto());

    // Act
    basketOutPort.triggerRefundRequest(ReservationByBasketRefResponse.builder()
            .reservationByIdList(List.of(ReservationByIdResponse.builder()
                .paymentCard(
                    ReservationPaymentCardType.builder().token("token").expirationDate(LocalDate.now())
                        .build()).build()))
            .hotelId("HOTELTEST")
            .currencyCode("code")
            .amountPaid(BigDecimal.TEN)
            .build(),
        "123");

    // Assert
    verify(basketClient).triggerRefund(anyString(), any(RefundRequestDto.class));
  }

  @Test
  void triggerRefundRequest_Success4() {
    // Arrange
    when(basketClient.triggerRefund(anyString(), any(RefundRequestDto.class)))
        .thenReturn(new RefundResponseDto());

    // Act
    basketOutPort.triggerRefundRequest(ReservationByBasketRefResponse.builder()
            .reservationByIdList(List.of(ReservationByIdResponse.builder()
                .paymentCard(
                    ReservationPaymentCardType.builder().token("token")
                        .build()).build()))
            .hotelId("HOTELTEST")
            .currencyCode("code")
            .amountPaid(BigDecimal.TEN)
            .build(),
        "123");

    // Assert
    verify(basketClient).triggerRefund(anyString(), any(RefundRequestDto.class));
  }

  @Test
  void getBasketByReference_Success() {
    // Arrange
    var responseBasket = ResponseEntity.of(Optional.of(new BasketDto()));
    when(basketClient.sendGetBasketByReference(anyString())).thenReturn(responseBasket);
    when(basketMapper.toModel(any(BasketDto.class))).thenReturn(new BasketResponse());

    // Act
    var response = basketOutPort.getBasketByReference("TST");

    // Assert
    assertTrue(response.isPresent());
  }

  @Test
  void getBasketByReference_Success2() {
    // Arrange
    var responseBasket = new ResponseEntity<BasketDto>(HttpStatus.OK);
    when(basketClient.sendGetBasketByReference(anyString())).thenReturn(responseBasket);
    // Act
    var response = basketOutPort.getBasketByReference("TST");

    // Assert
    assertTrue(response.isEmpty());
  }

  @Test
  void test_triggerEmailConfirmation() {
    // Arrange
    var reservationId = "resId1";
    EmailRequest emailRequest =
        EmailRequest.builder().bookingReference(reservationId).email("test@whitbread.com")
            .type("AMEND").build();
    when(emailRequestMapper.toDto(emailRequest)).thenReturn(new EmailRequestDto());
    // Act
    basketOutPort.triggerEmailConfirmation(emailRequest);

    // Assert
    verify(basketClient).triggerEmailConfirmation(new EmailRequestDto());
  }

  @Test
  void test_initiatePayment() {
    when(basketClient.initiatePayment(any(), any())).thenReturn(new InitiatePaymentResponseDto());

    basketOutPort.initiatePayment("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc77", new PaymentRequest());

    verify(basketClient, times(1)).initiatePayment(any(), any());
  }

  @Test
  void processAmend_ShouldReturnOk() {
    var paymentConfirmation = PaymentsConfirmation.builder().build();

    when(paymentMapper.toDto(paymentConfirmation, null)).thenReturn(new ProcessAmendRequestDto());

    basketOutPort.processAmend("basketRef", paymentConfirmation, null);

    verify(basketClient, times(1)).processAmend(anyString(), any());
  }

  @Test
  void initiateCcuiPaymentProcess_ShouldReturnOk() {
    var confirmAmendLogicRequest = ConfirmAmendLogicRequest.builder().tempBookingRef("basketRef")
        .build();

    when(paymentMapper.toCcuiDto(confirmAmendLogicRequest, "hotelId")).thenReturn(
        new CcuiPaymentRequestDto());

    basketOutPort.initiateCcuiPaymentProcess(confirmAmendLogicRequest, "hotelId");

    verify(basketClient, times(1)).initiateCcuiPayment(anyString(), any());
  }

  @Test
  void testAddReservationsToBasket_ThrowDigitalException() {
    // Arrange
    var copyReservations = new CopyReservationsResponse();
    copyReservations.setReservations(Collections.emptyList());
    // Act
    var exception = assertThrows(BasketDigitalException.class,
        () -> basketOutPort.addReservationsToBasket("reference", "lastModifiedETag",
            copyReservations, false, null));

    // Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getErrorCode(),
        is(ErrorCode.DIGITAL_FIND_BASKET_ITEM_EXCEPTION.getCode()));
    assertThat(exception.getMessage(), is("Error while trying to find basket item locking time"));
  }

  @Test
  void getBasketByReference_PRE_CHECKED_IN_Success() {
    // Arrange
    var basketDto = new BasketDto();
    basketDto.setStatus(StatusEnum.PRE_CHECKED_IN);
    var mappedBasketResponse = new BasketResponse();
    mappedBasketResponse.setStatus(StatusEnum.PRE_CHECKED_IN.toString());
    var responseBasket = ResponseEntity.of(Optional.of(basketDto));
    when(basketClient.sendGetBasketByReference(anyString())).thenReturn(responseBasket);
    when(basketMapper.toModel(any(BasketDto.class))).thenReturn(mappedBasketResponse);

    // Act
    var response = basketOutPort.getBasketByReference("TST");

    // Assert
    assertTrue(response.isPresent());
    assertEquals(response.get().getStatus(), StatusEnum.PRE_CHECKED_IN.toString());
  }

  @Test
  void addPromotionToBasket_Success() {
    // Arrange
    var promotionReq = new PromotionsInformationRequestDto();
    promotionReq.setPromoKind(PromoKind.SITE_WIDE);
    promotionReq.setPromotionCode("TEST");
    when(basketClient.addPromotionToBasket(anyString(), any(), anyString())).thenReturn(
        Tuples.of(new BasketDto(), "123"));
    when(basketMapper.toModel(any(BasketDto.class))).thenReturn(new BasketResponse());

    // Act
    var response = basketOutPort.addPromotionToBasket("123",
        "TEST", PromoKind.SITE_WIDE, "123");

    // Assert
    assertNotNull(response);
    verify(basketClient).addPromotionToBasket("123", promotionReq, "123");
  }

  @Test
  void addBasketChangeIdContext_Success() {
    // Arrange
    var promotionReq = new PromotionsInformationRequestDto();
    promotionReq.setPromoKind(PromoKind.SITE_WIDE);
    promotionReq.setPromotionCode("TEST");
    when(basketClient.changeIdContext(anyString(), any(ChangeBasketIdContextDto.class))).thenReturn(
        new BasketDto());

    // Act
    basketOutPort.changeIdContext("123", "TEST");

    // Assert
    verify(basketClient).changeIdContext(anyString(), any(ChangeBasketIdContextDto.class));
  }

  @Test
  void createBasketReservation_delegatesToBasketClient() {
    // Arrange
    CreateBasketRequestReservationDto dto = new CreateBasketRequestReservationDto();
    dto.setHotelId("H123");

    BasketDto expected = new BasketDto();
    when(basketClient.createBasketReservation(dto)).thenReturn(expected);

    // Act
    BasketDto result = basketOutPort.createBasketReservation(dto);

    // Assert
    assertSame(expected, result);
    verify(basketClient).createBasketReservation(dto);
  }

  @Test
  void addReservationsBasketItemsAndTypes_buildsItemsAndTypes() {
    // Arrange
    when(basketProperties.getStayItemType()).thenReturn("STAY");

    UniqueIDType id = new UniqueIDType("RES123", "Reservation");
    ReservationInfo info = ReservationInfo.builder()
        .reservationIdList(List.of(id))
        .externalReferences(null)
        .roomStay(null)
        .reservationGuest(null)
        .hotelId("H1")
        .hotelName("Hotel Name")
        .roomStayReservation(false)
        .reservationStatus(StatusEnum.OPEN.name())
        .build();

    Reservations reservations = Reservations.builder()
        .reservationInfo(List.of(info))
        .totalPages(1)
        .offset(0)
        .limit(10)
        .hasMore(false)
        .totalResults(1)
        .build();

    ReservationsDetailsEnhancedResponse opera = new ReservationsDetailsEnhancedResponse();
    opera.setReservations(reservations);

    // Act
    Pair<List<BasketItemDto>, List<AddBasketItemTypeDto>> result =
        basketOutPort.addReservationsBasketItemsAndTypes(opera);

    // Assert
    List<BasketItemDto> items = result.getFirst();
    List<AddBasketItemTypeDto> types = result.getSecond();

    assertEquals(1, items.size());
    assertEquals("RES123", items.get(0).getSourceId());
    assertEquals("STAY", items.get(0).getType());
    assertFalse(items.get(0).getHasOccupancySup());

    assertEquals(1, types.size());
    assertEquals("STAY", types.get(0).getType());
  }

  @Test
  void addReservationsBasketItemsAndTypes_returnsEmptyListsWhenReservationsNull() {
    // Arrange
    ReservationsDetailsEnhancedResponse opera = new ReservationsDetailsEnhancedResponse();
    opera.setReservations(null);

    // Act
    Pair<List<BasketItemDto>, List<AddBasketItemTypeDto>> result =
        basketOutPort.addReservationsBasketItemsAndTypes(opera);

    // Assert
    assertTrue(result.getFirst().isEmpty());
    assertTrue(result.getSecond().isEmpty());
  }

  private OhipReservationCreationResponse mockReservationCreationResponse() {
    return OhipReservationCreationResponse.builder()
        .reservationId("1234")
        .createDateTime("2022-06-19")
        .roomStay(mockRoomStay())
        .build();
  }

  private RoomStay mockRoomStay() {
    return RoomStay.builder()
        .arrivalDate(LocalDate.of(2022, 05, 05))
        .departureDate(LocalDate.of(2022, 05, 07))
        .adultCount(2)
        .childCount(0)
        .ratePlanCode("AXWS")
        .roomType("SINGLE")
        .build();
  }

}
