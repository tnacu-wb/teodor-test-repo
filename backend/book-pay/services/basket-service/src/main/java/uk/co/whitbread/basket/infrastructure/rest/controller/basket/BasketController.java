package uk.co.whitbread.basket.infrastructure.rest.controller.basket;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.basket.domain.exception.BasketItemException;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.payments.out.BasketRequestAction;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.basket.domain.ports.primary.BasketInPort;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.ReservationResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.AddAmendedReservationsRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.AddBasketItemRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.AddPromotionToBasketRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.BasketMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.BasketStatusResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.CancelBasketMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.ConfirmItemProcessingRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.CreateBasketRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.CreateBasketResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.UpdateAllowancesRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.UpdateReservationRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.UpdateReservationsRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.AddAmendedReservationsRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.AddBasketItemRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.BasketItemsRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.CancelBasketDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.ChangeBasketIdContextDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.ChangeBasketStatusDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.ConfirmItemProcessingRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.CreateBasketRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.CreateBasketRequestReservationDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.ErroredBookingDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.PreAuthChargesDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.PromotionsInformationRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.SendEmailDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.UpdateAllowancesRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.UpdateBasketItemOccupancyRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.UpdateReservationSingleCallRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.BasketDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.BasketStatusResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.CreateBasketResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.mapper.PaymentMapper;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@RestController
@RequestMapping("/v1/baskets")
@RequiredArgsConstructor
@Slf4j
public class BasketController implements BasketControllerApiDocumentation {

  private final BasketInPort basketInPort;
  private final CreateBasketRequestMapper createBasketRequestMapper;
  private final CreateBasketResponseMapper createBasketResponseMapper;
  private final BasketStatusResponseMapper basketStatusResponseMapper;
  private final BasketMapper basketMapper;
  private final AddBasketItemRequestMapper addBasketItemRequestMapper;
  private final AddAmendedReservationsRequestMapper addAmendedReservationsRequestMapper;
  private final ConfirmItemProcessingRequestMapper confirmItemProcessingRequestMapper;
  private final CancelBasketMapper cancelBasketMapper;
  private final UpdateReservationRequestMapper updateReservationRequestMapper;
  private final PaymentMapper paymentMapper;
  private final ReservationResponseMapper reservationResponseMapper;
  private final UpdateAllowancesRequestMapper updateAllowancesRequestMapper;
  private final UpdateReservationsRequestMapper updateReservationsRequestMapper;
  private final AddPromotionToBasketRequestMapper addPromotionToBasketRequestMapper;

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CreateBasketResponseDto> createBasket(
      @RequestBody @Valid CreateBasketRequestDto createBasketRequestDto) {
    final var createBasketRequest = createBasketRequestMapper.toDomainModel(createBasketRequestDto);
    final var createdBasket = basketInPort.createBasket(createBasketRequest);
    final var response = createBasketResponseMapper.toDto(createdBasket);
    final var eTag = Long.toString(Instant.parse(createdBasket.getCreatedAt()).toEpochMilli());

    return ResponseEntity.status(HttpStatus.CREATED).eTag(eTag).body(response);
  }

  @GetMapping(value = "{basket-reference}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BasketDto> getBasket(
      @PathVariable("basket-reference") @NotNull String basketReference) {

    final var basket = basketInPort.getBasketById(basketReference);
    return getBasketDtoResponseEntity(basket);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BasketDto> getBasketByBookingReference(@RequestParam String bookingReference) {

    final var basket = basketInPort.getBasketByReference(bookingReference);
    return basket.map(this::getBasketDtoResponseEntity)
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @PostMapping(value = "/{basket-reference}/items", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BasketDto> addItem(
      @PathVariable("basket-reference") @NotNull String reference,
      @RequestHeader("If-Match") @NotNull String ifMatch,
      @RequestBody @Valid AddBasketItemRequestDto addBasketItemRequestDto) {

    final var addBasketItemRequest =
        addBasketItemRequestMapper.toDomainModel(addBasketItemRequestDto);
    final var basket = basketInPort.addBasketItem(addBasketItemRequest, ifMatch);
    final var eTag = Long.toString(Instant.parse(basket.getLastModifiedAt()).toEpochMilli());
    return ResponseEntity.status(HttpStatus.OK).eTag(eTag)
        .body(basketMapper.toDto(basket));
  }

  @PutMapping(value = "/{basket-reference}/occupancy", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateItemOccupancy(
      @PathVariable("basket-reference") @NotNull String reference,
      @RequestHeader("If-Match") @NotNull String ifMatch,
      @RequestBody @Valid UpdateBasketItemOccupancyRequestDto updateBasketItemOccupancyRequestDto) {

    final var updateBasketItems = updateBasketItemOccupancyRequestDto.getItems().stream()
        .map(addBasketItemRequestMapper::toDomainModel)
        .toList();
    final var basket = basketInPort.updateBasketItemOccupancy(reference, updateBasketItems, ifMatch);
    final var eTag = Long.toString(Instant.parse(basket.getLastModifiedAt()).toEpochMilli());
    return ResponseEntity.noContent().eTag(eTag).build();
  }

  @PutMapping(value = "/{basket-reference}/allowances", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateAllowances(
      @PathVariable("basket-reference") @NotNull String reference,
      @RequestHeader("If-Match") @NotNull String ifMatch,
      @RequestBody @Valid UpdateAllowancesRequestDto updateAllowancesRequestDto) {
    final var updateAllowancesRequest =
        updateAllowancesRequestMapper.toDomainModel(updateAllowancesRequestDto);
    final var basket = basketInPort.updateAllowances(reference, updateAllowancesRequest, ifMatch);

    final var eTag = Long.toString(Instant.parse(basket.getLastModifiedAt()).toEpochMilli());
    return ResponseEntity.noContent().eTag(eTag).build();
  }

  @PostMapping(value = "/{basket-reference}/linkAmendReservations", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BasketDto> linkAmendReservations(
      @PathVariable("basket-reference") @NotNull String reference,
      @RequestHeader("If-Match") @NotNull String ifMatch,
      @RequestBody @Valid AddAmendedReservationsRequestDto addAmendedReservationsRequestDto) {

    final var amendedReservationsRequest =
        addAmendedReservationsRequestMapper.toDomainModel(addAmendedReservationsRequestDto);
    final var basket = basketInPort.linkAmendReservations(reference, amendedReservationsRequest, ifMatch);
    final var eTag = Long.toString(Instant.parse(basket.getLastModifiedAt()).toEpochMilli());
    return ResponseEntity.status(HttpStatus.OK).eTag(eTag)
        .body(basketMapper.toDto(basket));
  }

  @DeleteMapping(value = "/{basket-reference}/items/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BasketDto> removeItem(
      @PathVariable("basket-reference") @NotNull String basketId,
      @PathVariable("itemId") @NotNull String itemId,
      @RequestHeader("If-Match") @NotNull String ifMatch) {
    final var basket = basketInPort.removeBasketItems(basketId, List.of(itemId), ifMatch);
    final var eTag = Long.toString(Instant.parse(basket.getLastModifiedAt()).toEpochMilli());
    return ResponseEntity.status(HttpStatus.OK).eTag(eTag)
        .body(basketMapper.toDto(basket));
  }

  @DeleteMapping(value = "/{basket-reference}/items", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BasketDto> removeItems(
      @PathVariable("basket-reference") @NotNull String basketId,
      @Valid @ParameterObject BasketItemsRequestDto basketItemsRequestDto,
      @RequestHeader("If-Match") @NotNull String ifMatch) {
    final var basket = basketInPort.removeBasketItems(basketId,
        basketItemsRequestDto.getItemIds(), ifMatch);
    final var eTag = Long.toString(Instant.parse(basket.getLastModifiedAt()).toEpochMilli());
    return ResponseEntity.status(HttpStatus.OK).eTag(eTag)
        .body(basketMapper.toDto(basket));
  }

  @DeleteMapping(value = "/{basket-reference}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> deleteBasket(
      @PathVariable("basket-reference") @NotNull String basketId,
      @RequestHeader("If-Match") @NotNull String ifMatch) {

    basketInPort.deleteBasket(basketId, ifMatch);

    return ResponseEntity.noContent().build();
  }

  @PostMapping(value = "/{basket-reference}/items/{itemId}/acks", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> confirmItem(
      @PathVariable("basket-reference") @NotNull String basketId,
      @PathVariable("itemId") @NotNull String itemId,
      @RequestBody @Valid ConfirmItemProcessingRequestDto confirmItemProcessingRequestDto) {
    var confirmItemProcessingRequest =
        confirmItemProcessingRequestMapper.toDomainModel(confirmItemProcessingRequestDto);
    switch (BasketRequestAction.valueOf(confirmItemProcessingRequestDto.getReqAction())) {
      case ROLLBACK -> basketInPort
          .confirmRefundProcessing(basketId, confirmItemProcessingRequest);
      case COMMIT, CANCEL -> basketInPort
          .confirmItemProcessing(basketId, itemId, confirmItemProcessingRequest);
      case AMEND -> basketInPort
          .confirmAmendProcessing(basketId, confirmItemProcessingRequest);
      case CHANGE_PAY -> basketInPort
          .confirmChangePaymentProcessing(basketId, confirmItemProcessingRequest);
      default -> {
        var exception = new BasketItemException(ErrorCode.DIGITAL_BASKET_ITEM_UNKNOWN_ACL_EXCEPTION,
            "Cannot process unknown ack");
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
    return ResponseEntity.noContent().build();
  }

  @Override
  @PutMapping(value = "/{basket-reference}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> cancelBasket(
      @PathVariable("basket-reference") @NotNull String basketReference,
      @RequestBody CancelBasketDto cancelBasketDto) {

    basketInPort.cancelBasket(cancelBasketMapper.toDomainModel(basketReference, cancelBasketDto));

    return ResponseEntity.status(HttpStatus.OK).build();
  }


  @PutMapping(value = "/{basket-reference}/emailNotifications", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> sendEmailOption(
      @PathVariable("basket-reference") @NotNull String basketId,
      @RequestBody @Valid SendEmailDto sendEmailDto) {
    basketInPort.sendEmailNotificationOption(basketId, sendEmailDto.getSendEmail());
    return ResponseEntity.noContent().build();
  }

  @GetMapping(value = "/{basketReference}/checkStatus", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BasketStatusResponseDto> checkStatus(
      @PathVariable(name = "basketReference") @NotNull String basketReference) {
    final var checkBasketStatus = basketInPort.checkBasketStatus(basketReference);
    final var response = basketStatusResponseMapper.toDto(checkBasketStatus);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PutMapping(value = "/{basket-reference}/erroredBooking", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> setErroredBooking(
      @PathVariable("basket-reference") @NotNull String basketId,
      @RequestBody @Valid ErroredBookingDto erroredBookingDto) {
    basketInPort.setErroredBooking(basketId, erroredBookingDto.getIsErroredBooking(),
        erroredBookingDto.getBasketError());

    return ResponseEntity.noContent().build();
  }

  @PutMapping(value = "/{basket-reference}/preAuthCharges", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> setPreAuthCharges(
      @PathVariable("basket-reference") @NotNull String basketId,
      @RequestBody @Valid PreAuthChargesDto preAuthChargesDto) {
    basketInPort.setPreAuthCharges(basketId, preAuthChargesDto.getPreAuthCharges());

    return ResponseEntity.noContent().build();
  }

  private ResponseEntity<BasketDto> getBasketDtoResponseEntity(Basket basket) {
    final var response = basketMapper.toDto(basket);
    final var eTag = response.getLastModifiedAt() != null
        ? Long.toString(Instant.parse(response.getLastModifiedAt()).toEpochMilli())
        : Long.toString(Instant.parse(response.getCreatedAt()).toEpochMilli());

    return ResponseEntity.status(HttpStatus.OK).eTag(eTag).body(response);
  }

  @PutMapping(value = "/reservations/update", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationByBasketRefResponse> updateReservation(
      @RequestBody @Valid UpdateReservationSingleCallRequestDto updateReservationSingleCallRequestDto) {
    final var basketReference = updateReservationSingleCallRequestDto.getBasketReference();
    final var hotelId = updateReservationSingleCallRequestDto.getHotelId();
    final var requestId = updateReservationSingleCallRequestDto.getRequestId();
    final var guestReservationRequest = updateReservationsRequestMapper.toModel(
        updateReservationSingleCallRequestDto.getReservationGuest());
    final var updatePackageReservationRequest = updateReservationsRequestMapper.toModel(
        updateReservationSingleCallRequestDto.getAncillaries());
    final var paymentRequest = paymentMapper.toPaymentRequestModel(
        updateReservationSingleCallRequestDto.getCreatePayment());
    final var priceBreakDownNeeded = updateReservationSingleCallRequestDto.getPriceBreakdownNeeded();
    final var reservationsByBasketRefResponse = basketInPort.updateReservation(basketReference,
        hotelId, requestId,
        guestReservationRequest, updatePackageReservationRequest,
        paymentRequest, priceBreakDownNeeded);
    return ResponseEntity.status(HttpStatus.OK).body(reservationsByBasketRefResponse);
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/bookingReferences", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<BasketDto> getBasketsByBookingReferences(
      @RequestParam List<String> bookingReferences) {

    final var basket = basketInPort.getBasketsByReferences(bookingReferences);

    return basket.stream()
        .map(this::getBasketDtoResponseEntity)
        .map(HttpEntity::getBody)
        .toList();
  }

  @PutMapping(value = "/{basket-reference}/preCheckIn", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BasketStatusResponseDto> preCheckInBasket(
      @PathVariable("basket-reference") @NotNull String reference,
      @RequestParam(value = "isCiol", required = false) Boolean isCiol) {
    var basketStatus = basketInPort.preCheckInBasket(reference, isCiol);
    final var response = basketStatusResponseMapper.toDto(basketStatus);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PutMapping(value = "/{basket-reference}/preCheckOut", produces = MediaType.APPLICATION_JSON_VALUE)
  public BasketStatusResponseDto preCheckOut(
      @PathVariable("basket-reference") @NotNull String reference) {
    var basketStatus = basketInPort.preCheckOutBasket(reference);
    return basketStatusResponseMapper.toDto(basketStatus);
  }

  @Override
  @PutMapping("/{bookingRef}/changeStatus")
  public BasketDto changeStatus(@PathVariable @Size(min = 5, max = 15) String bookingRef,
      @RequestBody @Valid ChangeBasketStatusDto statusDto) {
    var response = basketInPort.changeStatus(bookingRef, statusDto.status());
    return basketMapper.toDto(response);
  }

  @PutMapping(value = "/{basket-reference}/promotions", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BasketDto> addPromotionToBasket(
      @PathVariable("basket-reference") @NotNull String reference,
      @RequestBody @Valid PromotionsInformationRequestDto promotionsInformationRequestDto,
      @RequestHeader("If-Match") @NotNull String ifMatch) {
    final var promotionsInfoRequest =
        addPromotionToBasketRequestMapper.toDomainModel(promotionsInformationRequestDto);
    final var basket = basketInPort.addPromotionToBasket(reference, promotionsInfoRequest,
        ifMatch);
    final var eTag = Long.toString(Instant.parse(basket.getLastModifiedAt()).toEpochMilli());
    return ResponseEntity.status(HttpStatus.OK).eTag(eTag)
        .body(basketMapper.toDto(basket));
  }

  @Override
  @PutMapping("/{bookingRef}/changeIdContext")
  public BasketDto changeIdContext(@PathVariable @Size(min = 5, max = 15) String bookingRef,
      @RequestBody @Valid ChangeBasketIdContextDto idContextDto) {
    var response = basketInPort.changeIdContext(bookingRef, idContextDto.idContext());
    return basketMapper.toDto(response);
  }

  @PostMapping(value = "/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CreateBasketResponseDto> createBasketReservation(
      @RequestBody @Valid CreateBasketRequestReservationDto createBasketRequestReservationDto) {
    final var createBasketRequest = createBasketRequestMapper.toDomainModel(createBasketRequestReservationDto);
    final var createdBasket = basketInPort.createBasketReservation(createBasketRequest);
    final var response = createBasketResponseMapper.toDto(createdBasket);
    final var eTag = Long.toString(Instant.parse(createdBasket.getCreatedAt()).toEpochMilli());

    return ResponseEntity.status(HttpStatus.CREATED).eTag(eTag).body(response);
  }

}
