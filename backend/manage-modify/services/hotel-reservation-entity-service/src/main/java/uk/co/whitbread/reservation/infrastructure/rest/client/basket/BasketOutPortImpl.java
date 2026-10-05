package uk.co.whitbread.reservation.infrastructure.rest.client.basket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.lang.Nullable;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AddAmendedReservationsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AddBasketItemDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AddBasketItemRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AddBasketItemTypeDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AmountDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketError;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BookingDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BusinessSiteDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CardDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ChangeBasketIdContextDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto.BasketStatusEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto.PaymentOptionEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestReservationDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromoKind;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromotionsInformationRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.RefundDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.RefundRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.RefundRequestDto.RefundTypeEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.UpdateBasketItemOccupancyRequestDto;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.model.amend.in.ConfirmAmendLogicRequest;
import uk.co.whitbread.reservation.domain.model.basket.allowances.UpdateAllowancesRequest;
import uk.co.whitbread.reservation.domain.model.in.EmailRequest;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.CopyReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.CopyReservationsResponse;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.model.out.Deposits;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationCreationResponse;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.RefundResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;
import uk.co.whitbread.reservation.domain.model.payment.in.PaymentRequest;
import uk.co.whitbread.reservation.domain.model.payment.in.PaymentsConfirmation;
import uk.co.whitbread.reservation.domain.model.payment.in.RefundRequest;
import uk.co.whitbread.reservation.domain.model.payment.out.InitiatePaymentResponse;
import uk.co.whitbread.reservation.domain.model.payment.out.ccui.PaymentCcuiResponse;
import uk.co.whitbread.reservation.domain.ports.secondary.BasketOutPort;
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

@Slf4j
@RequiredArgsConstructor
public class BasketOutPortImpl implements BasketOutPort {

  private static final String CONTROL_CHARACTER_REGEX = "\\p{Cntrl}";
  private final BasketClient basketClient;
  private final BasketMapper basketMapper;
  private final PaymentMapper paymentMapper;
  private final RefundMapper refundMapper;
  private final BasketUpdateRequestMapper basketUpdateRequestMapper;
  private final BasketProperties basketProperties;
  private final DepositFoliosRequestMapper depositFoliosRequestMapper;
  private final DepositFoliosResponseMapper depositFoliosResponseMapper;
  private final EmailRequestMapper emailRequestMapper;
  private final CancelBasketRequestMapper cancelBasketRequestMapper;


  @Override
  public Optional<BasketResponse> getBasketByReference(final String bookingReference) {
    var basketFromClient = basketClient.sendGetBasketByReference(bookingReference);
    if (basketFromClient.getBody() != null) {
      var eTag = basketFromClient.getHeaders().getETag();
      return Optional.of(basketMapper.toModel(basketFromClient.getBody()))
          .map(b -> {
            b.setETag(eTag);
            return b;
          });
    }
    return Optional.empty();
  }

  @Override
  public BasketResponse getBasketById(final String basketReference) {
    var basketFromClientResponse = basketClient.sendGetBasket(basketReference);
    var basketFromClient = basketFromClientResponse.getT1();
    var eTag = basketFromClientResponse.getT2();
    var basketResponse = basketMapper.toModel(basketFromClient);
    basketResponse.setETag(eTag);

    return basketResponse;
  }

  @Override
  public BasketResponse createBasket(final String hotelId, final String originalBasketId,
                                     final BasketStatusEnum basketStatus, final String migratedResNo,
                                     final PaymentOptionEnum paymentOption,
                                     final String paymentId, final String channel, String subChannel) {
    log.debug("Entered createBasket with hotelId={}", hotelId);

    final var createBasketRequest = new CreateBasketRequestDto();
    createBasketRequest.setHotelId(hotelId);
    createBasketRequest.setOriginalBasketId(originalBasketId);
    createBasketRequest.setBasketStatus(basketStatus);
    createBasketRequest.setMigratedResNo(migratedResNo);
    createBasketRequest.setPaymentOption(paymentOption);
    createBasketRequest.setPaymentId(paymentId);
    createBasketRequest.setChannel(channel);
    createBasketRequest.setSubChannel(subChannel);

    var basketResponse = basketClient.createBasket(createBasketRequest);
    var basket = basketMapper.toModel(basketResponse.getT1());
    basket.setETag(basketResponse.getT2());

    return basket;
  }

  @Override
  public BasketResponse createBasket(final CreateBasketRequestDto createBasketRequest) {
    log.debug("Entered createBasket with hotelId={}", createBasketRequest.getHotelId());

    var basketResponse = basketClient.createBasket(createBasketRequest);
    var basket = basketMapper.toModel(basketResponse.getT1());
    basket.setETag(basketResponse.getT2());
    return basket;
  }

  @Override
  public BasketResponse addReservationsToBasket(final String reference,
      final String lastModifiedETag, final OhipReservationResponse ohipReservationResponse,
      boolean isMigratedReservation, boolean isOccupancySupplementApplicable) {

    return addReservationsToBasket(reference, lastModifiedETag, ohipReservationResponse, isMigratedReservation,
        isOccupancySupplementApplicable, null, false);
  }

  @Override
  public BasketResponse addReservationsToBasket(final String reference,
      final String lastModifiedETag, final OhipReservationResponse ohipReservationResponse,
      boolean isMigratedReservation, boolean isOccupancySupplementApplicable, Integer noOfAdults,
      boolean isOta) {
    log.debug("Entered addReservationsToBasket with reference={}, eTag={}",
        sanitizeForLog(reference),
        sanitizeForLog(lastModifiedETag));

    final var reservationIds = extractReservationIds(ohipReservationResponse, isOccupancySupplementApplicable,
        noOfAdults, isOta);
    final var lockingTime = extractLockingTime(ohipReservationResponse);

    final var addBasketItemRequest = buildAddBasketItemRequestDto(
        reference, lockingTime, reservationIds, isMigratedReservation);

    var addItemsResponse = basketClient.addBasketItems(reference, lastModifiedETag, addBasketItemRequest);
    var basketResponse = basketMapper.toModel(addItemsResponse.getT1());
    basketResponse.setETag(addItemsResponse.getT2());

    return basketResponse;
  }

  @Override
  public BasketResponse addReservationsToBasket(String reference,
      String lastModifiedETag, CopyReservationsResponse copyReservationsResponse,
      boolean isOccupancySupplementApplicable, Map<String, Boolean> hasOccupancySupMap) {
    log.debug(
        "Entered addReservationsToBasket with reference={}, eTag={}, copyReservationsResponse={}, "
            + "isOccupancySupplementApplicable={}, hasOccupancySupMap={}",
        reference, lastModifiedETag, copyReservationsResponse, isOccupancySupplementApplicable, hasOccupancySupMap);

    if (hasOccupancySupMap == null) {
      hasOccupancySupMap = extractReservationIds(copyReservationsResponse, isOccupancySupplementApplicable);
    }
    final var lockingTime = extractLockingTime(copyReservationsResponse);

    final var addBasketItemRequest = buildAddBasketItemRequestDto(
        reference, lockingTime, hasOccupancySupMap, false);

    var addItemResponse = basketClient.addBasketItems(reference, lastModifiedETag, addBasketItemRequest);
    var basketResponse = basketMapper.toModel(addItemResponse.getT1());
    basketResponse.setETag(addItemResponse.getT2());
    return basketResponse;
  }

  @Override
  public BasketResponse linkAmendReservationsInBasket(String reference, String channel,
      String lastModifiedETag, Map<String, String> linkAmendReservations) {

    log.debug("Entered addReservationsToBasket with reference={}, eTag={}",
        reference, lastModifiedETag);

    var linkAmendResRequest = new AddAmendedReservationsRequestDto();
    linkAmendResRequest.setChannel(channel);
    linkAmendResRequest.setLinkAmendReservations(linkAmendReservations);

    var linkAmendReservationsResponse = basketClient.linkAmendReservationsInBasket(
        reference, lastModifiedETag, linkAmendResRequest);

    var basketResponse = basketMapper.toModel(linkAmendReservationsResponse.getT1());
    basketResponse.setETag(linkAmendReservationsResponse.getT2());
    return basketResponse;
  }

  @Override
  public void saveCharges(DepositFoliosResponse depositFolios) {
    var charges = depositFoliosRequestMapper.toDto(depositFolios);
    basketClient.saveCharges(charges);
  }

  @Override
  public DepositFoliosResponse getCharges(String reservationId) {
    return depositFoliosResponseMapper.toModel(basketClient.getCharges(reservationId));
  }

  @Override
  public DepositFoliosResponse getChargesByReservationIds(List<String> reservationIds) {
    return depositFoliosResponseMapper.toModel(basketClient.getChargesByReservationIds(reservationIds));
  }

  @Override
  public void triggerEmailConfirmation(EmailRequest emailRequest) {
    basketClient.triggerEmailConfirmation(emailRequestMapper.toDto(emailRequest));
  }

  private AddBasketItemRequestDto buildAddBasketItemRequestDto(String reference,
      String lockingTime,
      Map<String, Boolean> reservationIds,
      boolean isMigratedReservation) {
    final var addBasketItemRequest = new AddBasketItemRequestDto();
    addBasketItemRequest.addItemTypesItem(buildAddBasketItemTypeDto());

    reservationIds.forEach((reservationId, hasSupplement) -> {
      final var item = new AddBasketItemDto();
      item.setType(basketProperties.getStayItemType());
      item.setSourceId(reservationId);
      item.setHasOccupancySup(hasSupplement);
      addBasketItemRequest.addItemsItem(item);
    });

    addBasketItemRequest.setReference(reference);
    addBasketItemRequest.setLockingTime(lockingTime);
    addBasketItemRequest.setMigratedReservation(isMigratedReservation);
    return addBasketItemRequest;
  }

  private AddBasketItemTypeDto buildAddBasketItemTypeDto() {
    final var itemType = new AddBasketItemTypeDto();
    itemType.type(basketProperties.getStayItemType());
    itemType.setConfirmationData(basketProperties.getStayConfirmationData());
    return itemType;
  }

  @Override
  public void cancelBasket(final String reference, final boolean isFailed, final boolean sendMail,
      final List<Deposits> refundedDeposits) {
    basketClient.sendCancelBasket(reference,
        cancelBasketRequestMapper.toDto(isFailed, sendMail, refundedDeposits));
  }

  @Override
  public void setErroredBooking(final String reference, final boolean isErroredBooking, final BasketError basketError) {
    basketClient.setErroredBooking(reference, basketMapper.toDto(isErroredBooking, basketError));
  }

  @Override
  public RefundResponse triggerRefundRequest(ReservationByBasketRefResponse reservations,
      String basketReference) {
    return refundMapper.toResponseModel(
        basketClient.triggerRefund(basketReference, createRefundDto(reservations)));
  }

  @Override
  public RefundResponse triggerRefundRequest(String basketReference, RefundRequest refundRequest) {
    return refundMapper.toResponseModel(
        basketClient.triggerRefund(
            basketReference, refundMapper.toRequestDto(refundRequest)));
  }

  @Override
  public BasketResponse removeItem(String basketReference, String basketItem,
      String lastModifiedETag) {
    var removeItemResponse = basketClient.removeItem(basketReference, basketItem, lastModifiedETag);
    var basketResponse = basketMapper.toModel(removeItemResponse.getT1());
    basketResponse.setETag(removeItemResponse.getT2());

    return basketResponse;
  }

  @Override
  public BasketResponse removeItems(String basketReference, List<String> basketItems,
      String lastModifiedETag) {
    var removeItemResponse = basketClient.removeItems(basketReference, basketItems, lastModifiedETag);
    var basketResponse = basketMapper.toModel(removeItemResponse.getT1());
    basketResponse.setETag(removeItemResponse.getT2());

    return basketResponse;
  }

  @Override
  public void deleteBasket(String basketReference, String lastModifiedETag) {
    basketClient.deleteBasket(basketReference, lastModifiedETag);
  }

  @Override
  public InitiatePaymentResponse initiatePayment(String basketReference,
      PaymentRequest paymentRequest) {
    var paymentRequestDto = paymentMapper.toDto(paymentRequest);
    var initiatePaymentResponseDto = basketClient.initiatePayment(basketReference,
        paymentRequestDto);
    return paymentMapper.toModel(initiatePaymentResponseDto);
  }

  @Override
  public void processAmend(String basketReference, PaymentsConfirmation paymentsConfirmation,
      @Nullable String ccAgentId) {
    var paymentConfirmationDto = paymentMapper.toDto(paymentsConfirmation, ccAgentId);
    basketClient.processAmend(basketReference, paymentConfirmationDto);
  }

  @Override
  public PaymentCcuiResponse initiateCcuiPaymentProcess(
      ConfirmAmendLogicRequest confirmAmendLogicRequest, String hotelId) {
    var ccuiPaymentRequestDto = paymentMapper.toCcuiDto(confirmAmendLogicRequest, hotelId);
    var paymentResponseDto = basketClient.initiateCcuiPayment(confirmAmendLogicRequest.getTempBookingRef(),
        ccuiPaymentRequestDto);
    return paymentMapper.toCcuiModel(paymentResponseDto);
  }

  @Override
  public String updateAllowances(String basketReference, UpdateAllowancesRequest updateAllowancesRequest,
      final String lastModifiedETag) {
    var updateAllowancesRequestDto = basketUpdateRequestMapper
        .toUpdateAllowancesRequestDto(updateAllowancesRequest);
    return basketClient.updateAllowances(basketReference, updateAllowancesRequestDto, lastModifiedETag);
  }

  @Override
  public String updateOccupancySupplementFlag(final String basketRef,
                                              final BasketItemResponse basketReservationReference,
                                              final boolean newSupplementFlagValue,
                                              final String lastModifiedETag) {

    var updateRequest = new UpdateBasketItemOccupancyRequestDto();
    var updateBasketItem =
        basketUpdateRequestMapper.toUpdateBasketItemOccupancyRequestDto(basketReservationReference);
    updateBasketItem.setHasOccupancySup(newSupplementFlagValue);
    updateRequest.addItemsItem(
        updateBasketItem);
    return basketClient.updateOccupancySupplement(basketRef, updateRequest, lastModifiedETag);
  }

  @Override
  public String updateOccupancySupplementFlag(final String basketRef,
      final List<BasketItemResponse> items,
      final String lastModifiedETag) {

    var updateRequest = new UpdateBasketItemOccupancyRequestDto();
    items.forEach(item -> {
      var updateBasketItem = basketUpdateRequestMapper.toUpdateBasketItemOccupancyRequestDto(item);
      updateBasketItem.setHasOccupancySup(item.getHasOccupancySup());
      updateRequest.addItemsItem(updateBasketItem);
    });
    return basketClient.updateOccupancySupplement(basketRef, updateRequest, lastModifiedETag);
  }

  @Override
  public BasketResponse addPromotionToBasket(String basketReference, String promotionCode,
      PromoKind promoKind, String lastModifiedETag
  ) {
    var promotionRequest = new PromotionsInformationRequestDto();
    promotionRequest.setPromotionCode(promotionCode);
    promotionRequest.setPromoKind(promoKind);

    var updateResponse = basketClient.addPromotionToBasket(basketReference, promotionRequest,
        lastModifiedETag);

    var basket = basketMapper.toModel(updateResponse.getT1());
    basket.setETag(updateResponse.getT2());

    return basket;
  }

  @Override
  public void changeIdContext(String reference, String idContex) {
    var idContextDto = new ChangeBasketIdContextDto();
    idContextDto.setIdContext(idContex);
    basketClient.changeIdContext(reference, idContextDto);
  }

  @Override
  public BasketDto createBasketReservation(final CreateBasketRequestReservationDto createBasketReqReservDto) {
    log.debug("Entered createBasket reservation with hotelId={}", createBasketReqReservDto.getHotelId());
    return  basketClient.createBasketReservation(createBasketReqReservDto);
  }

  @Override
  public Pair<List<BasketItemDto>, List<AddBasketItemTypeDto>>
      addReservationsBasketItemsAndTypes(ReservationsDetailsEnhancedResponse operaReservation) {
    List<BasketItemDto> basketItems = new ArrayList<>();
    List<AddBasketItemTypeDto> itemTypes = new ArrayList<>();
    var reservations = operaReservation.getReservations();
    if (reservations != null && reservations.getReservationInfo() != null) {
      reservations.getReservationInfo().forEach(resInfo -> {
        String reservationId = resInfo.getReservationIdList().get(0).getId();
        BasketItemDto itemDto = new BasketItemDto()
            .sourceId(reservationId)
            .type(basketProperties.getStayItemType())
            .hasOccupancySup(false);
        basketItems.add(itemDto);
        AddBasketItemTypeDto typeDto = new AddBasketItemTypeDto()
            .type(basketProperties.getStayItemType());
        typeDto.confirmationData(basketProperties.getStayConfirmationData());
        itemTypes.add(typeDto);
      });
    }
    return Pair.of(basketItems, itemTypes);
  }

  private RefundRequestDto createRefundDto(ReservationByBasketRefResponse reservations) {
    var refundRequestDto = new RefundRequestDto();
    refundRequestDto.setHotelCode(reservations.getHotelId());
    refundRequestDto.setRefundType(RefundTypeEnum.PARTIAL);

    var amountDto = new AmountDto();
    amountDto.setCurrency(reservations.getCurrencyCode());
    amountDto.setMinorUnits(reservations.getAmountPaid().multiply(BigDecimal.valueOf(100)));

    var cardDto = new CardDto();
    if (Optional.ofNullable(reservations.getReservationByIdList()).isPresent()
        && Optional.ofNullable(reservations.getReservationByIdList().get(0)).isPresent()) {
      var reservationCard = reservations.getReservationByIdList().get(0).getPaymentCard();
      cardDto.setToken(reservationCard.getToken());
      cardDto.setExpiryMonth(Optional.ofNullable(reservationCard.getExpirationDate()).isPresent()
          ? String.valueOf(reservationCard.getExpirationDate().getMonth()) : "");
      cardDto.setExpiryYear(Optional.ofNullable(reservationCard.getExpirationDate()).isPresent()
          ? String.valueOf(reservationCard.getExpirationDate().getYear()) : "");
    }

    var refund = new RefundDto();
    refund.setReason(RefundDto.ReasonEnum.CANCEL);
    refund.setAmount(amountDto);
    refund.setCard(cardDto);
    refund.setType(RefundDto.TypeEnum.CARD);

    refundRequestDto.setRefund(refund);

    var businessSite = new BusinessSiteDto();
    businessSite.setType("HOTEL");
    businessSite.setIdentifier(reservations.getHotelId());

    var booking = new BookingDto();
    booking.setChannel("WEB");
    booking.setJourney("REFUND");
    booking.setType("NOW");
    booking.setBusinessSite(businessSite);

    refundRequestDto.setBooking(booking);

    return refundRequestDto;
  }

  private String extractLockingTime(
      OhipReservationResponse ohipReservationResponse) {
    return ohipReservationResponse.getReservations()
        .stream()
        .map(OhipReservationCreationResponse::getCreateDateTime)
        .max(Comparator.comparing(i -> i))
        .orElseThrow(() -> {
          var ex = new BasketDigitalException(ErrorCode.DIGITAL_BASKET_ITEM_EXCEPTION,
              "Error while trying to find basket item locking time");
          ExceptionLogger.log(log, ex);
          return ex;
        });
  }

  private String extractLockingTime(
      CopyReservationsResponse copyReservationsResponse) {
    return copyReservationsResponse.getReservations()
        .stream()
        .map(CopyReservationResponse::getCreateDateTime)
        .max(Comparator.comparing(i -> i))
        .orElseThrow(() -> {
          var ex = new BasketDigitalException(ErrorCode.DIGITAL_FIND_BASKET_ITEM_EXCEPTION,
              "Error while trying to find basket item locking time");
          ExceptionLogger.log(log, ex);
          return ex;
        });
  }

  private Map<String, Boolean> extractReservationIds(
      OhipReservationResponse ohipReservationResponse, boolean isOccupancySupplementApplicable, Integer noOfAduls,
      boolean isOta) {
    return ohipReservationResponse
        .getReservations()
        .stream()
        .collect(Collectors.toMap(OhipReservationCreationResponse::getReservationId,
            reservation  -> {
              if (!isOccupancySupplementApplicable) {
                return false;
              }
              if (isOta) {
                return true;
              }
              int adults = Objects.nonNull(noOfAduls)
                  ? noOfAduls.intValue() : reservation.getRoomStay().getAdultCount();
              return adults > 1;
            }));
  }

  private Map<String, Boolean> extractReservationIds(
      CopyReservationsResponse copyReservationsResponse, boolean isOccupancySupplementApplicable) {
    return copyReservationsResponse
        .getReservations()
        .stream()
        .collect(Collectors
            .toMap(CopyReservationResponse::getReservationId,
                reservation -> isOccupancySupplementApplicable));
  }

  private static String sanitizeForLog(final String input) {
    if (input == null) {
      return "[USER:null]";
    }
    String sanitized = input.replaceAll(CONTROL_CHARACTER_REGEX, "");
    return "[USER:" + sanitized + "]";
  }
}
