package uk.co.whitbread.basket.infrastructure.rest.controller.basket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.PRE_CHECKED_IN;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.PRE_CHECKED_OUT;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.basket.domain.exception.BookingReferenceNotFoundException;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.model.basket.in.AddBasketItemType;
import uk.co.whitbread.basket.domain.model.basket.in.ConfirmItemProcessingRequest;
import uk.co.whitbread.basket.domain.model.basket.in.CreateBasketRequest;
import uk.co.whitbread.basket.domain.model.basket.in.PromotionsInformationRequest;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketError;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatusResponse;
import uk.co.whitbread.basket.domain.model.basket.out.PromoKind;
import uk.co.whitbread.basket.domain.ports.primary.BasketInPort;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.AddPromotionToBasketRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.BasketMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.BasketStatusResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.ConfirmItemProcessingRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.CreateBasketRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.CreateBasketResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.BasketItemsRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.ChangeBasketIdContextDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.ChangeBasketStatusDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.ConfirmItemProcessingRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.CreateBasketRequestReservationDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.ErroredBookingDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.PreAuthChargesDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.PromotionsInformationRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.ReservationBasketInfoDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.UserInfoDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.BasketDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.BasketStatusDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.BasketStatusResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.CreateBasketResponseDto;

@ExtendWith(MockitoExtension.class)
class BasketControllerTest {

  private static final String ID_CONTEXT = "3rd party";
  private final String basketId = "ABC-".concat(UUID.randomUUID().toString());

  @InjectMocks
  private BasketController basketController;

  @Mock
  private BasketInPort basketInPort;

  @Mock
  private BasketMapper basketMapper;

  @Mock
  private CreateBasketRequestMapper createBasketRequestMapper;

  @Mock
  private CreateBasketResponseMapper createBasketResponseMapper;

  @Mock
  private ConfirmItemProcessingRequestMapper confirmItemProcessingRequestMapper;

  @Mock
  private BasketStatusResponseMapper basketStatusResponseMapper;

  @Mock
  private AddPromotionToBasketRequestMapper addPromotionToBasketRequestMapper;

  @Test
  void getBasket_success() {

    when(basketInPort.getBasketById(any())).thenReturn(mockBasketResponse());
    when(basketMapper.toDto(any())).thenReturn(mockBasketDtoResponse());

    var basket = basketController.getBasket("basketRef");

    verify(basketInPort, times(1)).getBasketById(eq("basketRef"));
    assertThat(basket, notNullValue());
  }

  @Test
  void removeItem_success() {

    when(basketInPort.removeBasketItems(any(), anyList(), anyString())).thenReturn(mockBasketResponse());
    when(basketMapper.toDto(any())).thenReturn(mockBasketDtoResponse());

    var basket = basketController.removeItem("basketRef", "itemId", "If-Match");

    verify(basketInPort, times(1)).removeBasketItems("basketRef", List.of("itemId"), "If-Match");
    assertThat(basket, notNullValue());
  }

  @Test
  void removeItems_success() {

    when(basketInPort.removeBasketItems(any(), anyList(), anyString())).thenReturn(
        mockBasketResponse());
    when(basketMapper.toDto(any())).thenReturn(mockBasketDtoResponse());

    var basket = basketController.removeItems("basketRef",
        BasketItemsRequestDto.builder().itemIds(List.of("itemId")).build(), "If-Match");

    verify(basketInPort, times(1)).removeBasketItems("basketRef", List.of("itemId"), "If-Match");
    assertThat(basket, notNullValue());
  }

  @Test
  void deleteBasket_success() {

    var result = basketController.deleteBasket("basketRef", "If-Match");

    verify(basketInPort, times(1)).deleteBasket("basketRef", "If-Match");
    assertThat(result, notNullValue());
  }

  @Test
  void errorBooking_success() {

    var result = basketController.setErroredBooking("basketId", mockErroredBookingDto());

    verify(basketInPort, times(1)).setErroredBooking("basketId", true, BasketError.builder().build());
    assertThat(result, notNullValue());
  }

  @Test
  void setPreAuthCharges_success() {
    var result = basketController.setPreAuthCharges("basketRef", mockPreAuthChargesDto());

    verify(basketInPort, times(1)).setPreAuthCharges("basketRef", "charges");
    assertThat(result, notNullValue());
  }

  @Test
  void confirmProcessingItem_changePay_success() {
    final var request = ConfirmItemProcessingRequestDto.builder()
        .status(0)
        .reqAction("CHANGE_PAY")
        .build();
    var modelRequest = mockConfirmItemProcessingRequest("CHANGE_PAY");

    when(confirmItemProcessingRequestMapper.toDomainModel(any())).thenReturn(modelRequest);

    var result = basketController.confirmItem(basketId, "123", request);

    verify(basketInPort, times(1)).confirmChangePaymentProcessing(basketId, modelRequest);
    assertThat(result, notNullValue());
  }

  @Test
  void confirmProcessingItem_rollback_success() {
    final var request = ConfirmItemProcessingRequestDto.builder()
        .status(0)
        .reqAction("ROLLBACK")
        .build();
    var modelRequest = mockConfirmItemProcessingRequest("ROLLBACK");

    when(confirmItemProcessingRequestMapper.toDomainModel(any())).thenReturn(modelRequest);

    var result = basketController.confirmItem(basketId, "123", request);

    verify(basketInPort, times(1)).confirmRefundProcessing(basketId, modelRequest);
    assertThat(result, notNullValue());
  }

  @Test
  void getBasketsByReferences_success() {

    when(basketInPort.getBasketsByReferences(any())).thenReturn(List.of(mockBasketResponse()));
    when(basketMapper.toDto(any())).thenReturn(mockBasketDtoResponse());

    var basket = basketController.getBasketsByBookingReferences(List.of("basketId1","basketId2"));

    verify(basketInPort, times(1)).getBasketsByReferences(List.of("basketId1","basketId2"));
    assertThat(basket, notNullValue());
  }

  @Test
  void preCheckInBasket_success() {
    final String basketRef = "basketRef";
    final BasketStatusResponse basketStatusResponse = new BasketStatusResponse(basketRef,
        PRE_CHECKED_IN, "now", null, null);
    when(basketInPort.preCheckInBasket(basketRef, Boolean.FALSE)).thenReturn(basketStatusResponse);
    final BasketStatusResponseDto basketStatusResponseDto = new BasketStatusResponseDto(basketRef,
        BasketStatusDto.PRE_CHECKED_IN, "now", null, null);
    when(basketStatusResponseMapper.toDto(any())).thenReturn(basketStatusResponseDto);

    var response = basketController.preCheckInBasket(basketRef, Boolean.FALSE);

    verify(basketInPort, times(1)).preCheckInBasket(basketRef, Boolean.FALSE);
    assertThat(response, notNullValue());
    assertThat(response.getBody().getBasketStatus(), comparesEqualTo(BasketStatusDto.PRE_CHECKED_IN));
  }

  @Test
  void preCheckOuBasket_success() {
    final String basketRef = "basketRef";
    final BasketStatusResponse basketStatusResponse = new BasketStatusResponse(basketRef,
        PRE_CHECKED_OUT, "now", null, null);
    when(basketInPort.preCheckOutBasket(basketRef)).thenReturn(basketStatusResponse);
    final BasketStatusResponseDto basketStatusResponseDto = new BasketStatusResponseDto(basketRef,
        BasketStatusDto.PRE_CHECKED_OUT, "now", null, null);
    when(basketStatusResponseMapper.toDto(any())).thenReturn(basketStatusResponseDto);

    var response = basketController.preCheckOut(basketRef);

    verify(basketInPort, times(1)).preCheckOutBasket(basketRef);
    assertThat(response, notNullValue());
    assertThat(response.getBasketStatus(), comparesEqualTo(BasketStatusDto.PRE_CHECKED_OUT));
  }

  @Test
  void changeStatus_success() {
    final var bookingRef = "basketRef";
    final var status = new ChangeBasketStatusDto("CIOL_FAILED");
    final var basketStatusRequest = mockBasketResponse();
    final var basketResponse = mockBasketDtoResponse();
    when(basketInPort.changeStatus(bookingRef,"CIOL_FAILED")).thenReturn(basketStatusRequest);
    when(basketMapper.toDto(any(Basket.class))).thenReturn(basketResponse);

    var response = basketController.changeStatus(bookingRef,status);

    verify(basketInPort, times(1)).changeStatus(bookingRef,"CIOL_FAILED");
    assertThat(response, notNullValue());
  }

  @Test
  void addPromotionToBasket_success() {
    final String basketRef = "basketRef";
    final var promotionsReq = mockPromotionsReq();
    var mockBasketDtoResponse = mockBasketDtoResponse();
    mockBasketDtoResponse.setPromoKind(PromoKind.SITE_WIDE);
    mockBasketDtoResponse.setPromotionCode("TEST");
    when(addPromotionToBasketRequestMapper.toDomainModel(any())).thenReturn(
        promotionsReq);
    when(basketInPort.addPromotionToBasket(basketRef, promotionsReq, "")).thenReturn(
        mockBasketResponse());
    when(basketMapper.toDto(any(Basket.class))).thenReturn(mockBasketDtoResponse);
    var response = basketController.addPromotionToBasket(basketRef, any(), "");

    assertThat(response, notNullValue());
    assertThat(response.getBody(), notNullValue());
    assertThat(response.getBody().getPromoKind(), comparesEqualTo(PromoKind.SITE_WIDE));
  }

  @Test
  void addPromotionToBasket_missingPromoKind_throwsConstraintViolationException() {
    final String basketRef = "basketRef";
    final var promotionsReqDto = mockPromotionsReqDto();
    promotionsReqDto.setPromoKind(null);

    assertThrows(NullPointerException.class, () ->
        basketController.addPromotionToBasket(basketRef, promotionsReqDto, "etag")
    );
  }

  @Test
  void addPromotionToBasket_basketNotFound_propagatesException() {
    final String basketRef = "basketRef";
    final var promotionsReqDto = mockPromotionsReqDto();
    var promotionsReq = mockPromotionsReq();
    when(addPromotionToBasketRequestMapper.toDomainModel(promotionsReqDto)).thenReturn(promotionsReq);
    var ex = new BookingReferenceNotFoundException(
        ErrorCode.BASKET_NOT_FOUND_EXCEPTION,
        "Basket with basket reference=" + basketRef
    );
    when(basketInPort.addPromotionToBasket(basketRef, promotionsReq, "etag"))
        .thenThrow(ex);
    assertThrows(BookingReferenceNotFoundException.class, () ->
        basketController.addPromotionToBasket(basketRef, promotionsReqDto, "etag")
    );
  }

  @Test
  void changeIdContext_success() {
    final var bookingRef = "basketRef";
    final var basketStatusRequest = mockBasketResponse();
    final var basketResponse = mockBasketDtoResponse();
    final var idContextDto = new ChangeBasketIdContextDto(ID_CONTEXT);
    when(basketInPort.changeIdContext(bookingRef, ID_CONTEXT)).thenReturn(basketStatusRequest);
    when(basketMapper.toDto(any(Basket.class))).thenReturn(basketResponse);

    var response = basketController.changeIdContext(bookingRef, idContextDto);

    verify(basketInPort, times(1)).changeIdContext(bookingRef, ID_CONTEXT);
    assertThat(response, notNullValue());
  }

  @Test
  void createBasketReservation_withItemsAndItemTypes_success() {

    UserInfoDto userInfoDto = UserInfoDto.builder()
        .userId("USR")
        .build();
    ReservationBasketInfoDto reservationBasketInfoDto = ReservationBasketInfoDto.builder()
        .migratedResNo("R231231")
        .build();

    CreateBasketRequestReservationDto requestDto =
        CreateBasketRequestReservationDto.builder()
            .hotelId("TST")
            .userInfoDto(userInfoDto)
            .reservationBasketInfoDto(reservationBasketInfoDto)
            .build();
    // Add basket items
    BasketItem item1 = BasketItem.builder()
        .sourceId("SRC1")
        .type("ROOM")
        .hasOccupancySup(false)
        .build();

    // Add basket item types
    AddBasketItemType type1 = AddBasketItemType.builder()
        .type("ROOM")
        .confirmationData(List.of("Room Type"))
        .build();

    // Domain request after mapping
    CreateBasketRequest domainRequest = CreateBasketRequest.builder()
        .hotelId("TST")
        .userId("USR")
        .migratedResNo("R231231")
        .basketItems(List.of(item1))
        .basketItemTypes(List.of(type1))
        .build();

    when(createBasketRequestMapper.toDomainModel(any(CreateBasketRequestReservationDto.class))).thenReturn(domainRequest);

    // Domain response from service
    Basket createdBasket = Basket.builder()
        .hotelId("TST")
        .userId("USR")
        .reference("R231231")
        .createdAt("2024-10-21T15:00:00Z")
        .items(List.of(item1))
        .build();

    when(basketInPort.createBasketReservation(any())).thenReturn(createdBasket);

    // Response DTO after mapping
    CreateBasketResponseDto responseDto = new CreateBasketResponseDto();
    when(createBasketResponseMapper.toDto(any())).thenReturn(responseDto);

    // Act
    ResponseEntity<CreateBasketResponseDto> response =
        basketController.createBasketReservation(requestDto);

    // Assert
    verify(createBasketRequestMapper).toDomainModel(requestDto);
    verify(basketInPort).createBasketReservation(domainRequest);
    verify(createBasketResponseMapper).toDto(createdBasket);

    assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    assertThat(response.getBody(), notNullValue());
    assertThat(response.getHeaders().getETag(), notNullValue());
  }

  public PromotionsInformationRequestDto mockPromotionsReqDto() {
    return PromotionsInformationRequestDto.builder()
        .promotionCode("TEST")
        .promoKind(PromoKind.SITE_WIDE)
        .build();
  }

  public PromotionsInformationRequest mockPromotionsReq() {
    return PromotionsInformationRequest.builder()
        .promotionCode("TEST")
        .promoKind(PromoKind.SITE_WIDE)
        .build();
  }

  private PreAuthChargesDto mockPreAuthChargesDto() {

    return PreAuthChargesDto.builder()
        .preAuthCharges("charges")
        .build();
  }

  private ErroredBookingDto mockErroredBookingDto() {

    return ErroredBookingDto.builder()
        .isErroredBooking(true)
        .basketError(BasketError.builder().build())
        .build();
  }

  private Basket mockBasketResponse() {
    return Basket.builder()
        .lastModifiedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .build();
  }

  private BasketDto mockBasketDtoResponse() {
    return BasketDto.builder()
        .lastModifiedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .build();
  }

  private ConfirmItemProcessingRequest mockConfirmItemProcessingRequest(String action){
    return ConfirmItemProcessingRequest.builder()
        .status(0)
        .reqAction(action)
        .build();
  }
}
