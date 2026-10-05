package uk.co.whitbread.basket.domain.logic;

import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static uk.co.whitbread.basket.domain.logic.utils.BusinessAllowancesUtils.buildBusinessItemsFromBusinessAccount;
import static uk.co.whitbread.basket.domain.logic.utils.PaymentUtils.createDistributionConfirmationPaymentDetails;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketErrorType.CONFIRMATION;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.AMENDED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.AMEND_FAILED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.CANCELLED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.CIOL_FAILED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.COMPLETED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.FAILED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.PRE_CHECKED_IN;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.PRE_CHECKED_OUT;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.SECURE_FAILED;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.BART_FORMAT_LETTER;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.BASKET_ERROR_CODE;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.BASKET_TIMEOUT;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.BD;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.BU;
import static uk.co.whitbread.basket.utils.SanitizingUtils.sanitize;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.util.Strings;
import uk.co.whitbread.basket.domain.exception.BasketItemException;
import uk.co.whitbread.basket.domain.exception.BasketReferenceNotValidException;
import uk.co.whitbread.basket.domain.exception.BookingReferenceNotFoundException;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PreCheckinOutstandingBalanceException;
import uk.co.whitbread.basket.domain.exception.PreconditionFailedException;
import uk.co.whitbread.basket.domain.exception.PrepaidBookingChargesItemException;
import uk.co.whitbread.basket.domain.logic.config.DistributionProperties;
import uk.co.whitbread.basket.domain.logic.utils.BusinessAllowancesUtils;
import uk.co.whitbread.basket.domain.model.basket.in.AddAmendedReservationsRequest;
import uk.co.whitbread.basket.domain.model.basket.in.AddBasketItem;
import uk.co.whitbread.basket.domain.model.basket.in.AddBasketItemRequest;
import uk.co.whitbread.basket.domain.model.basket.in.AddBasketItemType;
import uk.co.whitbread.basket.domain.model.basket.in.CancelBasketRequest;
import uk.co.whitbread.basket.domain.model.basket.in.ConfirmItemProcessingRequest;
import uk.co.whitbread.basket.domain.model.basket.in.CreateBasketRequest;
import uk.co.whitbread.basket.domain.model.basket.in.PackagesSelection;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDeposit;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDeposits;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDepositsRequest;
import uk.co.whitbread.basket.domain.model.basket.in.PromotionsInformationRequest;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationGuestRequest;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationPackagesRequest;
import uk.co.whitbread.basket.domain.model.basket.in.RoomsSelections;
import uk.co.whitbread.basket.domain.model.basket.in.StayingGuest;
import uk.co.whitbread.basket.domain.model.basket.in.UpdateAllowancesRequest;
import uk.co.whitbread.basket.domain.model.basket.in.UpdateBasketItemSupplement;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketError;
import uk.co.whitbread.basket.domain.model.basket.out.BasketErrorType;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatusResponse;
import uk.co.whitbread.basket.domain.model.basket.out.CleanUpTime;
import uk.co.whitbread.basket.domain.model.basket.out.ConfirmReservationRequest;
import uk.co.whitbread.basket.domain.model.basket.out.Country;
import uk.co.whitbread.basket.domain.model.basket.out.PaymentCard;
import uk.co.whitbread.basket.domain.model.basket.out.PromoKind;
import uk.co.whitbread.basket.domain.model.basket.out.ReservationProfiles;
import uk.co.whitbread.basket.domain.model.business.in.BusinessItems;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.AccountCompanyItems;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CcuiExtraItems;
import uk.co.whitbread.basket.domain.model.content.out.BusinessNotesResponse;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;
import uk.co.whitbread.basket.domain.model.email.out.EmailNotificationEventType;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.basket.domain.model.marketing.out.Customer;
import uk.co.whitbread.basket.domain.model.marketing.out.MarketingPreferences;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentOption;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.out.BasketPaymentStatus;
import uk.co.whitbread.basket.domain.model.payments.out.BasketRequestAction;
import uk.co.whitbread.basket.domain.model.payments.out.BookingConfirmationDetails;
import uk.co.whitbread.basket.domain.model.promotion.out.RedeemPromoCodeResponse;
import uk.co.whitbread.basket.domain.model.refund.in.Amount;
import uk.co.whitbread.basket.domain.model.refund.in.Refund;
import uk.co.whitbread.basket.domain.model.refund.in.RefundReason;
import uk.co.whitbread.basket.domain.model.refund.in.RefundRequest;
import uk.co.whitbread.basket.domain.model.refund.in.RefundType;
import uk.co.whitbread.basket.domain.model.reservation.in.Alert;
import uk.co.whitbread.basket.domain.model.reservation.in.ReservationAlertsRequest;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceRule;
import uk.co.whitbread.basket.domain.ports.primary.BasketInPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOrderOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BookingCompletedOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.DepositFolioOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.MarketingOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.PromoOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RefundOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.basket.generated.models.ohip.CharacterUdfDto;
import uk.co.whitbread.basket.generated.models.ohip.PreCheckInRequestDto;
import uk.co.whitbread.basket.generated.models.ohip.UdfsRequestDto;
import uk.co.whitbread.basket.generated.models.promotion.RedeemStatus;
import uk.co.whitbread.basket.infrastructure.repository.exception.BasketNotFoundException;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Slf4j
@AllArgsConstructor
public class BasketInPortImpl implements BasketInPort {

  public static final String UTC = "UTC";
  private final Long basketStatusPolling;
  private final CleanUpTime cleanUpTime;
  private final BasketOutPort basketOutPort;
  private final DepositFolioOutPort depositFolioOutPort;
  private final EmailNotificationService emailNotificationService;
  private final RefundOutPort refundOutPort;
  private final BasketOrderOutPort basketOrderOutPort;
  private final BookingCompletedOutPort bookingCompletedOutPort;
  private final HotelReservationOutPort reservationOutPort;
  private final MarketingOutPort marketingOutPort;
  private final ConcurrentTracer concurrentTracer;
  private final AuthenticatedUserService authenticatedUserService;
  private final Long basketValidity;
  private final RulesAgentOutPort rulesAgentOutPort;
  private final ContentOutPort contentOutPort;
  private final DistributionProperties distributionProperties;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final PromoOutPort promoOutPort;

  public static final String CCUI_BOOKING_CHANNEL = "CCUI";
  public static final String BASKET_ITEM_TYPE_STAY = "STAY";
  public static final String ALERT_AREA_CHECKIN = "CHECKIN";
  public static final String ALERT_CODE_CIOL = "CIOL";
  public static final String ALERT_DESCRIPTION_CIOL =
      "Checked In Online - This guest has Checked in Online using the Premier Inn App";
  public static final String ALERT_AREA_CHECKOUT = "CHECKOUT";
  public static final String ALERT_CODE_COOL = "COOL";
  public static final String ALERT_DESCRIPTION_COOL =
      "Guest Vacated - This guest has confirmed they have vacated their room using the Premier Inn App";
  public static final Integer FOLIO_VIEW_PIBA_CNP = 2;

  @Override
  public Basket createBasket(final CreateBasketRequest createBasketRequest) {
    return basketOutPort.createBasket(createBasketRequest);
  }

  @Override
  public Optional<Basket> getBasketByReference(final String reference) {
    return basketOutPort.getBasketByReference(reference);
  }

  @Override
  public Basket getBasketById(final String basketId) {
    return basketOutPort.getBasketById(basketId);
  }

  @Override
  public Basket updateBasketItemOccupancy(String basketReference,
      List<UpdateBasketItemSupplement> updateBasketItemRequest,
      String requestModifyTimestamp) {
    var basket = basketOutPort.getBasketById(basketReference);
    validateBasketLastModifyPrecondition(requestModifyTimestamp, basket);
    var itemMappingOnReservationId = new HashMap<String, Boolean>();
    updateBasketItemRequest.forEach(
        item -> itemMappingOnReservationId.put(item.getSourceId(), item.getHasOccupancySup()));

    basket.getItems().forEach(item -> {
      if (itemMappingOnReservationId.containsKey(item.getSourceId())) {
        item.setHasOccupancySup(itemMappingOnReservationId.get(item.getSourceId()));
      }
    });
    var newBasketConstruct = basket.toBuilder()
        .lastModifiedAt(now().toString())
        .build();

    return basketOutPort.updateBasket(newBasketConstruct);
  }

  @Override
  public List<Basket> getBasketsByReferences(List<String> bookingReferences) {
    return basketOutPort.getBasketsByReferences(bookingReferences);
  }

  @Override
  public Basket addBasketItem(AddBasketItemRequest addBasketItemRequest,
      String requestModifyTimestamp) {
    final var basket = basketOutPort.getBasketById(addBasketItemRequest.getReference());
    final boolean basketStatCancelled = basket.getStatus().equals(CANCELLED);

    if (!addBasketItemRequest.isMigratedReservation()
        && !(basketStatCancelled && compliesWithBartResFormat(
        addBasketItemRequest.getReference()))) {
      validateBasketStatusPrecondition(basket);
    }

    validateBasketLastModifyPrecondition(requestModifyTimestamp, basket);

    final var basketBuilder = basket.toBuilder();

    final List<AddBasketItem> addItems = addBasketItemRequest.getItems();
    final List<AddBasketItemType> addItemTypes = addBasketItemRequest.getItemTypes();

    addItems.forEach(item -> basketBuilder.item(BasketItem.builder()
        .type(item.getType())
        .sourceId(item.getSourceId())
        .details(item.getDetails())
        .hasOccupancySup(item.getHasOccupancySup())
        .build()));

    addItemTypes.forEach(
        itemType -> basketBuilder.itemType(itemType.getType(),
            Set.copyOf(itemType.getConfirmationData())));

    final var lockingTime = addBasketItemRequest.getLockingTime();
    if (Strings.isNotEmpty(lockingTime)
        && basket.getStatus() == BasketStatus.OPEN) {
      basketBuilder.cleanUpTime(
          cleanUpTime.getCleanUpTime(basket.getStatus(), Instant.parse(lockingTime)));
      basketBuilder.lockingTime(lockingTime);
    }
    basketBuilder.lastModifiedAt(now().toString());

    final var basketUpdated = basketBuilder.build();

    return basketOutPort.updateBasket(basketUpdated);
  }

  @Override
  public Basket updateAllowances(final String basketReference,
      final UpdateAllowancesRequest updateAllowancesRequest,
      final String requestModifyTimestamp) {
    final var basket = basketOutPort.getBasketById(basketReference);
    validateBasketLastModifyPrecondition(requestModifyTimestamp, basket);

    basket.setBookingAllowances(updateAllowancesRequest.getBookingAllowances());
    basket.setLastModifiedAt(now().toString());

    return basketOutPort.updateBasket(basket);
  }

  @Override
  public Basket linkAmendReservations(String basketReference,
      AddAmendedReservationsRequest addBasketItemRequest,
      String requestModifyTimestamp) {

    final var basket = basketOutPort.getBasketById(basketReference);
    final boolean basketStatCancelled = basket.getStatus().equals(CANCELLED);

    if (!(basketStatCancelled && compliesWithBartResFormat(basketReference))) {
      validateBasketStatusPrecondition(basket);
    }
    validateBasketLastModifyPrecondition(requestModifyTimestamp, basket);

    final var basketBuilder = basket.toBuilder();

    basketBuilder.linkAmendReservations(addBasketItemRequest.getLinkAmendReservations());

    final var basketUpdated = basketBuilder.build();

    return basketOutPort.updateBasket(basketUpdated);
  }

  @Override
  public Basket removeBasketItems(String basketId, List<String> itemIds,
      String requestModifyTimestamp) {
    final var basket = basketOutPort.getBasketById(basketId);

    validateBasketStatusPrecondition(basket);
    validateBasketLastModifyPrecondition(requestModifyTimestamp, basket);

    final var itemList = new ArrayList<>(basket.getItems());
    final var itemTypeList = new HashMap<>(basket.getItemTypes());

    itemIds.forEach(itemId -> itemList.removeIf(item -> item.getSourceId().equals(itemId)));

    itemTypeList.keySet().removeIf(type ->
        itemList.stream().noneMatch(item -> item.getType().equals(type)));

    basket.setItems(itemList);
    basket.setItemTypes(itemTypeList);
    basket.setLastModifiedAt(now().toString());

    return basketOutPort.updateBasket(basket);
  }

  @Override
  public void confirmItemProcessing(String basketId, String itemId,
      ConfirmItemProcessingRequest confirmItemProcessingRequest) {
    log.info("ConfirmItemProcessing for basketReference={}, itemId={} and "
            + "confirmItemProcessingRequest={}", sanitize(basketId), sanitize(itemId),
        confirmItemProcessingRequest);
    var basket = basketOutPort.getBasketById(basketId);

    var basketItem =
        basket.getItems().stream().filter(i -> itemId.equals(i.getSourceId())).findFirst()
            .orElseThrow(() -> {
                  var exception = new BasketItemException(
                      ErrorCode.DIGITAL_BASKET_ITEM_FAILURE_EXCEPTION,
                      "Basket item processing failure");
                  ExceptionLogger.log(log, exception);
                  return exception;
                }
            );

    basketItem.setAck(confirmItemProcessingRequest.getStatus());
    basketItem.setReqAction(confirmItemProcessingRequest.getReqAction());

    if (confirmItemProcessingRequest.getStatus() == 1
        && basket.getStatus() != BasketStatus.FAILED
        && confirmItemProcessingRequest.getReqAction()
        .equals(BasketRequestAction.COMMIT.getReqAction())) {
      var basketStatusFailed = FAILED;
      var shouldCancelReservations = true;
      if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCheckInOnline())
          && Boolean.TRUE.equals(basket.getIsCheckInOnlinePay())) {
        basketStatusFailed = CIOL_FAILED;
        shouldCancelReservations = false;
        log.error(
            "Item={} reported as failed at {}: {} for CIOL flow, booking will not be cancelled, "
                + "only the amount payed will be refunded",
            sanitize(itemId),
            confirmItemProcessingRequest.getReportedAt(), confirmItemProcessingRequest.getErrors());
      }
      if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSaveSecureBooking())
          && Boolean.TRUE.equals(basket.getIsSecureBooking())) {
        basketStatusFailed = SECURE_FAILED;
        shouldCancelReservations = false;
        log.error(
            "Item={} reported as failed at {}: {} for Secure Booking flow, booking will not be cancelled.",
            itemId,
            confirmItemProcessingRequest.getReportedAt(), confirmItemProcessingRequest.getErrors());
      }
      basket.setStatus(basketStatusFailed);
      basket.setCleanUpTime(cleanUpTime.getCleanUpTime(basket));
      Optional.ofNullable(confirmItemProcessingRequest.getErrors())
          .ifPresent(errors -> basket.setBasketError(
              new BasketError(BASKET_ERROR_CODE, errors.stream().findFirst().get(), CONFIRMATION)));

      if (basket.getPaymentOption().equals(PaymentOption.PAY_NOW.toString())) {
        var refundRequest = createRefundRequest(basket);
        refundOutPort.processRefund(basketId, refundRequest, basket.getPaymentID());
        basket.setPaymentStatus(BasketPaymentStatus.REFUNDING);
      }

      if (shouldCancelReservations) {
        cancelBooking(itemId, confirmItemProcessingRequest, basket);
      }
    } else {
      if (isAllItemsComplete(basket)) {
        updateBasketDetails(basket);
        if (isRedeemPromoEnabled()) {
          redeemUniquePromoIfPresent(basket);
        }
      }
    }
    basketOutPort.updateBasket(basket);
    publishBookingCompletedIfCompleted(basket);
  }

  @Override
  public Basket createBasketReservation(final CreateBasketRequest createBasketRequest) {
    return basketOutPort.createBasketReservation(createBasketRequest);
  }

  private boolean isRedeemPromoEnabled() {
    return Optional.ofNullable(unleashWrapper.featureFlag())
        .map(FeatureFlag::getRedeemPromoCode)
        .map(unleashWrapper::isEnabled)
        .orElse(false);
  }

  private void cancelBooking(String itemId, ConfirmItemProcessingRequest confirmItemProcessingRequest,
      Basket basket) {
    basketOrderOutPort.processOrder(basket, BasketRequestAction.CANCEL.getReqAction(), null,
        null);
    triggerEmailNotification(basket, EmailNotificationEventType.FAIL, false);
    log.error("Item={} reported as failed at {}: {}", sanitize(itemId),
        confirmItemProcessingRequest.getReportedAt(), confirmItemProcessingRequest.getErrors());
  }

  private void updateBasketDetails(Basket basket) {
    var checkInEnabled = unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCheckInOnline());
    var secureBookingEnabled = unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getSaveSecureBooking());
    boolean isSecureBooking =
        secureBookingEnabled && Boolean.TRUE.equals(basket.getIsSecureBooking());
    var completeStatus =
        checkInEnabled && Objects.equals(Boolean.TRUE, basket.getIsCheckInOnlinePay())
            ? BasketStatus.PRE_CHECKED_IN : BasketStatus.COMPLETED;
    basket.setStatus(completeStatus);
    basket.setCleanUpTime(cleanUpTime.getCleanUpTime(basket));
    basket.setPaymentStatus(BasketPaymentStatus.COMPLETED);
    if (basket.getStatus().equals(PRE_CHECKED_IN)) {
      triggerPreCheckInAsync(basket);
    } else {
      if (isSecureBooking) {
        triggerEmailNotification(basket, EmailNotificationEventType.SECURE_BOOKING, false);
      } else {
        triggerEmailNotification(basket, EmailNotificationEventType.CONFIRM, true);
      }
    }
    if (!isSecureBooking) {
      CompletableFuture.runAsync(concurrentTracer.wrap(() -> updateMarketingPreferences(basket)));
    }
  }

  private void publishBookingCompletedIfCompleted(Basket basket) {
    if (!unleashWrapper.isEnabled(unleashWrapper.featureFlag().getPublishDatatransBookingCompletedEvent())) {
      return;
    }
    if (COMPLETED.equals(basket.getStatus()) || FAILED.equals(basket.getStatus())) {
      bookingCompletedOutPort.publishBookingCompleted(basket);
    }
  }

  private void redeemUniquePromoIfPresent(Basket basket) {
    if (basket.getPromotionCode() != null
        && basket.getPromoKind() == PromoKind.UNIQUE) {
      RedeemPromoCodeResponse redeemResponse = promoOutPort.redeemPromoCode(
          basket.getPromotionCode(),
          basket.getReference()
      );

      if (redeemResponse.getStatus() != RedeemStatus.REDEEMED) {
        log.error(
            "Failed to redeem promo code [{}] for basket [{}]",
            basket.getPromotionCode(),
            basket.getBasketId()
        );
      }
    }
  }

  private void updateReservationAlerts(Basket basket) {
    log.info("update reservation alerts for basketReference={}", basket.getBasketId());
    var reservationIds = getReservationIds(basket);
    var request = ReservationAlertsRequest.builder()
        .hotelId(basket.getHotelId())
        .reservationIds(reservationIds)
        .alerts(List.of(Alert.builder()
            .area(ALERT_AREA_CHECKIN)
            .code(ALERT_CODE_CIOL)
            .description(ALERT_DESCRIPTION_CIOL)
            .screenNotification(true)
            .printerNotification(false)
            .build()))
        .build();
    reservationOutPort.updateReservationAlerts(request);
  }

  private boolean isAllItemsComplete(Basket basket) {
    return basket.getItems().stream().allMatch(i -> i.getAck() != null
        && i.getAck() == 0 && (i.getReqAction().equals(BasketRequestAction.COMMIT.getReqAction())
        || i.getReqAction().equals(BasketRequestAction.CHANGE_PAY.getReqAction())));
  }

  @Override
  public void confirmRefundProcessing(final String basketId,
      final ConfirmItemProcessingRequest confirmItemProcessingRequest) {
    log.info("ConfirmRefundProcessing for basketReference={} "
        + "and confirmItemProcessingRequest={}", sanitize(basketId), confirmItemProcessingRequest);
    var basket = basketOutPort.getBasketById(basketId);

    if (confirmItemProcessingRequest.getStatus() == 0) {
      log.info("Refund has completed successfully for basketReference={}", sanitize(basketId));
      basket.setPaymentStatus(BasketPaymentStatus.REFUNDED);
    } else {
      basket.setPaymentStatus(BasketPaymentStatus.FAILED_REFUND);
      log.error("Refund for basket reference={} couldn't be rolled back at {}: {}",
          sanitize(basketId),
          confirmItemProcessingRequest.getReportedAt(), confirmItemProcessingRequest.getErrors());
    }
    basketOutPort.updateBasket(basket);
    publishBookingCompletedIfCompleted(basket);
  }

  @Override
  public void confirmAmendProcessing(final String basketId,
      final ConfirmItemProcessingRequest confirmItemProcessingRequest) {
    log.info("ConfirmAmendProcessing for basketReference={} "
        + "and confirmItemProcessingRequest={}", sanitize(basketId), confirmItemProcessingRequest);
    var basket = basketOutPort.getBasketById(basketId);
    var basketStatusCompleted = basket.getOriginalBasketId() != null ? AMENDED : COMPLETED;

    if (confirmItemProcessingRequest.getStatus() == 0) {
      basket.setPaymentStatus(BasketPaymentStatus.COMPLETED);
      basket.setStatus(basketStatusCompleted);
      if (basketStatusCompleted == COMPLETED) {
        basket.setCleanUpTime(cleanUpTime.getCleanUpTime(basket));
      }
    } else {
      if (PaymentOption.PAY_NOW.toString().equals(basket.getPaymentOption())) {
        var refundRequest = createRefundRequest(basket);
        refundOutPort.processRefund(basketId, refundRequest, basket.getPaymentID());
        basket.setPaymentStatus(BasketPaymentStatus.REFUNDING);
      }
      var basketStatusFailed = basket.getOriginalBasketId() != null ? AMEND_FAILED : FAILED;
      basket.setStatus(basketStatusFailed);
      basket.setCleanUpTime(cleanUpTime.getCleanUpTime(basket));
    }
    basketOutPort.updateBasket(basket);
    publishBookingCompletedIfCompleted(basket);
  }

  @Override
  public void confirmChangePaymentProcessing(final String basketId,
      final ConfirmItemProcessingRequest confirmItemProcessingRequest) {
    log.info("ConfirmChangePaymentProcessing for basketReference={} "
        + "and confirmItemProcessingRequest={}", sanitize(basketId), confirmItemProcessingRequest);
    var basket = basketOutPort.getBasketById(basketId);

    if (confirmItemProcessingRequest.getStatus() == 0) {
      basket.setPaymentStatus(BasketPaymentStatus.COMPLETED);
      basket.setStatus(COMPLETED);
      basket.setCleanUpTime(cleanUpTime.getCleanUpTime(basket));
      triggerEmailNotification(basket, EmailNotificationEventType.CONFIRM, false);
    } else {
      basket.setStatus(FAILED);
      basket.setCleanUpTime(cleanUpTime.getCleanUpTime(basket));
    }
    basketOutPort.updateBasket(basket);
    publishBookingCompletedIfCompleted(basket);
  }

  @Override
  public void deleteBasket(final String basketId, final String requestModifyTimestamp) {
    final var basket = basketOutPort.getBasketById(basketId);

    validateBasketLastModifyPrecondition(requestModifyTimestamp, basket);

    this.basketOutPort.deleteBasket(basket.getThreeLetterHotelId(), basket.getSortKey());
  }

  @Override
  public void cancelBasket(final CancelBasketRequest cancelBasketRequest) {
    log.info("Cancelling basket {} with deposits {}", cancelBasketRequest.getBasketReference(),
        cancelBasketRequest.getDeposits());

    BasketStatus basketStatus = Boolean.TRUE.equals(cancelBasketRequest.getIsFailed())
        ? FAILED : CANCELLED;

    basketOutPort.updateBasketStatus(cancelBasketRequest.getBasketReference(),
        basketStatus, Optional.of(now()));
  }

  private void updateMarketingPreferences(final Basket basket) {

    final var reservationItem = basket.getItems().stream().findFirst();

    if (reservationItem.isPresent()) {
      final var reservationId = reservationItem.get().getSourceId();

      try {

        final var marketingPreferences = reservationOutPort
            .getMarketingPreferences(basket.getHotelId(), reservationId);

        if (marketingPreferences != null && marketingPreferences.getOptIn() != null
            && marketingPreferences.getContactValue() != null
            && marketingPreferences.getCustomer() != null) {

          final var preferences =
              MarketingPreferences.builder().contactValue(marketingPreferences.getContactValue())
                  .optIn(marketingPreferences.getOptIn()).customer(
                      Customer.builder().title(marketingPreferences.getCustomer().getTitle())
                          .firstName(marketingPreferences.getCustomer().getFirstName())
                          .lastName(marketingPreferences.getCustomer().getLastName())
                          .countryOfResidence(marketingPreferences.getCustomer().getCountry())
                          .language(marketingPreferences.getCustomer().getLanguage()).build()).build();

          marketingOutPort.updateMarketingInfo(preferences);

        } else {
          log.info("Marketing preferences saved for hotelId={} and reservationId={} are invalid!",
              basket.getHotelId(),
              reservationId);
        }
      } catch (Exception ex) {
        log.error(
            "Error occurred when trying to retrieve or to update marketing preferences for hotelId={} and "
                + "reservationId={}", basket.getHotelId(), reservationId, ex);
      }
    }
  }

  private void triggerEmailNotification(final Basket basket,
      final EmailNotificationEventType emailNotificationEventType,
      final boolean isTransactionDataRequired) {
    emailNotificationService.sendEmailNotificationEvent(basket, emailNotificationEventType,
        basket.getEmailAddress(),
        isTransactionDataRequired);
  }

  @Override
  public void sendEmailNotificationOption(final String basketId, final Boolean sendMail) {
    final var basket = basketOutPort.getBasketById(basketId);
    basket.setSendMail(sendMail);
    basketOutPort.updateBasket(basket);
  }

  @Override
  public void saveCharges(final PrepaidDepositsRequest request) {
    List<PrepaidDeposit> depositsToBeSaved = new ArrayList<>();
    List<PrepaidDeposit> depositsToBeUpdated = new ArrayList<>();
    if (!request.getPrepaidDeposits().isEmpty()) {
      request.getPrepaidDeposits().forEach(requestedDeposit -> {
        PrepaidDeposits existingDeposits = null;
        try {
          existingDeposits = depositFolioOutPort.getBasketPrepaidDeposit(
              requestedDeposit.getReservationId());
        } catch (BasketNotFoundException exception) {
          log.info("No PrepaidBookingCharges found in db for reservation Id: {} ",
              requestedDeposit.getReservationId());
        }
        //before saving prepaid deposits, it is checked whether they have already been saved or not
        if ((ObjectUtils.isNotEmpty(existingDeposits) && !existingDeposits.getPrepaidDeposits().isEmpty()
            && !depositAlreadySaved(existingDeposits.getPrepaidDeposits(), requestedDeposit))
            || ObjectUtils.isEmpty(existingDeposits)) {
          if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getPrepaidBookingChargesTtl())) {
            Long depositsCleanUpTime = calculateCleanUpTime(getAllDeposits(requestedDeposit, existingDeposits));
            depositsToBeSaved.add(requestedDeposit);
            updateDepositsCleanupTime(depositsToBeSaved, depositsCleanUpTime);
            depositsToBeUpdated.addAll(getExistingDepositsList(existingDeposits));
            updateDepositsCleanupTime(depositsToBeUpdated, depositsCleanUpTime);
          } else {
            depositsToBeSaved.add(requestedDeposit);
          }
        }
      });
      depositFolioOutPort.createBasketPrepaidDeposit(
          PrepaidDepositsRequest.builder().prepaidDeposits(depositsToBeSaved).build());
      depositFolioOutPort.updateBasketPrepaidDeposit(
          PrepaidDeposits.builder().prepaidDeposits(depositsToBeUpdated).build());
    }
  }

  private static void updateDepositsCleanupTime(List<PrepaidDeposit> deposits, Long cleanUpTime) {
    deposits.forEach(deposit -> deposit.setCleanUpTime(cleanUpTime));
  }

  private static List<PrepaidDeposit> getAllDeposits(PrepaidDeposit requestedDeposit,
      PrepaidDeposits existingDeposits) {
    List<PrepaidDeposit> existingDepositsList = getExistingDepositsList(existingDeposits);
    return Stream.concat(existingDepositsList.stream(), Stream.of(requestedDeposit)).toList();
  }

  private static List<PrepaidDeposit> getExistingDepositsList(PrepaidDeposits existingDeposits) {
    return ObjectUtils.isNotEmpty(existingDeposits)
        ? existingDeposits.getPrepaidDeposits()
        : Collections.emptyList();
  }

  private Long calculateCleanUpTime(List<PrepaidDeposit> prepaidDeposits) {
    Optional<LocalDateTime> maxPostingReferenceDate = prepaidDeposits.stream()
        .flatMap(deposit -> deposit.getCharges().stream())
        .map(charge -> LocalDate.parse(charge.getPostingReference()).atStartOfDay())
        .max(Comparator.naturalOrder());

    return maxPostingReferenceDate
        .map(date -> date.plusYears(1).atZone(ZoneId.of(UTC)).toInstant().getEpochSecond())
        .orElseThrow(() -> new PrepaidBookingChargesItemException(ErrorCode.PREPAID_CHARGES_EXCEPTION, "Could not "
            + "calculate TTL based on postingReference"));
  }

  @Override
  public PrepaidDeposits getCharges(String reservationId) {
    return depositFolioOutPort.getBasketPrepaidDeposit(reservationId);
  }

  @Override
  public PrepaidDeposits getCharges(List<String> reservationIds) {
    return depositFolioOutPort.getBasketPrepaidDeposits(reservationIds);
  }

  @Override
  public BasketStatusResponse checkBasketStatus(String basketReference) {
    final var basket = basketOutPort.getBasketById(basketReference);

    if (BasketStatus.PAY_PENDING.equals(basket.getStatus())) {
      if (Objects.isNull(basket.getPollingStartedAt())) {
        basket.setPollingStartedAt(now().toString());
        basketOutPort.updatePollingStartedAt(basketReference, now().toString());
      }

      if (shouldInvalidateBasket(basket)) {
        BasketError basketError = BasketError.builder().code(BASKET_ERROR_CODE)
            .description(BASKET_TIMEOUT).type(BasketErrorType.TIMEOUT).build();

        if (Boolean.TRUE.equals(basket.getIsCheckInOnlinePay())) {
          basket.setStatus(CIOL_FAILED);
        } else if (Boolean.TRUE.equals(basket.getIsSecureBooking())) {
          basket.setStatus(SECURE_FAILED);
        } else {
          basket.setStatus(FAILED);
        }
        basket.setCleanUpTime(cleanUpTime.getCleanUpTime(basket));
        basket.setBasketError(basketError);
        basketOutPort.updateBasket(basket);
      }
    }

    return BasketStatusResponse.builder().basketReference(basketReference)
        .basketStatus(basket.getStatus())
        .createdAt(now().toString())
        .retryPayment(basket.getRetryPayment())
        .basketError(basket.getBasketError()).build();
  }

  private boolean shouldInvalidateBasket(Basket basket) {
    final long pollingStartedAt = Instant.parse(basket.getPollingStartedAt()).toEpochMilli();
    return now().minusMillis(pollingStartedAt).isAfter(Instant.ofEpochMilli(basketStatusPolling));
  }

  private static void validateBasketLastModifyPrecondition(String requestModifyTimestamp,
      Basket basket) {

    final var basketLastModify = getLastModifyTimestamp(basket);
    if (!Objects.equals(requestModifyTimestamp, basketLastModify)) {
      var message = String.format("Request resource is out of sync with server resource. "
              + "Error for basket with reference %s. Mismatching eTags: request eTag->\"%s\"; "
              + "basket eTag->\"%s\"",
          basket.getReference(), requestModifyTimestamp, basketLastModify);
      var exception = new PreconditionFailedException(ErrorCode.DIGITAL_OUT_OF_SYNC_EXCEPTION,
          message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private static void validateBasketStatusPrecondition(Basket basket) {
    final var basketStatus = basket.getStatus();
    var basketStatusCompleted = basket.getOriginalBasketId() != null ? AMENDED : COMPLETED;
    if (basketStatus != BasketStatus.OPEN && basketStatus != basketStatusCompleted
        && basketStatus != CIOL_FAILED && basketStatus != SECURE_FAILED) {
      var message = String.format("Items not added to basket with basketReference=%s. "
          + "Basket status is basketStatus=%s.", basket.getReference(), basketStatus);
      var exception = new PreconditionFailedException(ErrorCode.DIGITAL_ITEM_NOT_ADDED_EXCEPTION,
          message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private static String getLastModifyTimestamp(Basket basket) {
    final var lastModifiedAt = basket.getLastModifiedAt();
    if (Strings.isEmpty(lastModifiedAt)) {
      return Long.toString(Instant.parse(basket.getCreatedAt()).toEpochMilli());
    }
    return Long.toString(Instant.parse(lastModifiedAt).toEpochMilli());
  }

  private static Instant now() {
    return Instant.now().truncatedTo(ChronoUnit.SECONDS);
  }

  private RefundRequest createRefundRequest(Basket basket) {
    return RefundRequest.builder()
        .refundType(RefundType.FULL)
        .hotelCode(basket.getHotelId())
        .refund(Refund
            .builder()
            .amount(Amount.builder().currency(basket.getCurrency())
                .minorUnits(new BigDecimal(basket.getTotalCost())).build())
            .reason(RefundReason.ROLLBACK)
            .build())
        .build();
  }

  private boolean compliesWithBartResFormat(String resNo) {
    return resNo.startsWith(BART_FORMAT_LETTER, 3);
  }

  private boolean depositAlreadySaved(List<PrepaidDeposit> prepaidDeposits,
      PrepaidDeposit newDeposit) {
    return prepaidDeposits.contains(newDeposit);
  }

  @Override
  public void setErroredBooking(final String basketId, final Boolean isErroredBooking,
      final BasketError basketError) {
    final var basket = basketOutPort.getBasketById(basketId);
    basket.setIsErroredBooking(isErroredBooking);
    basket.setBasketError(basketError);
    basketOutPort.updateBasket(basket);
  }

  @Override
  public void setPreAuthCharges(String reference, String preAuthCharges) {
    final var basket = basketOutPort.getBasketById(reference);

    if (basket.getCcuiExtraItems() == null) {
      basket.setCcuiExtraItems(new CcuiExtraItems());
    }

    if (basket.getCcuiExtraItems().getAccountCompanyItems() == null) {
      basket.getCcuiExtraItems().setAccountCompanyItems(new AccountCompanyItems());
    }

    Optional.of(basket)
        .map(Basket::getCcuiExtraItems)
        .map(CcuiExtraItems::getAccountCompanyItems)
        .ifPresent(accountCompanyItems -> accountCompanyItems.setCharges(preAuthCharges));

    basketOutPort.updateBasket(basket);
  }

  @Override
  public ReservationByBasketRefResponse updateReservation(String basketReference, String hotelId,
      String requestId,
      ReservationGuestRequest guestReservationRequest,
      ReservationPackagesRequest updatePackageReservationRequest, PaymentRequest paymentRequest,
      String priceBreakDownNeeded) {

    log.info("Entered updateReservation with basketReference={}",
        sanitize(basketReference));

    var basket = basketOutPort.getBasketById(basketReference);
    basket.setPaymentOption(paymentRequest.getBooking().getType());
    basket.setEmailAddress(paymentRequest.getPayment().getBilling().getEmail());
    basket.setPaymentChannel(paymentRequest.getBooking().getChannel());

    if (isBasketCompleted(basket) || isExpired(basket)) {
      var message = String.format("Basket is no longer valid basketReference=%s", basketReference);
      var exception = new BasketReferenceNotValidException(
          ErrorCode.DIGITAL_BASKET_NOT_LONGER_VALID_EXCEPTION,
          message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    if (isAnyBlank(basket.getHotelId(), basket.getReference())) {
      var exception = new BasketReferenceNotValidException(
          ErrorCode.DIGITAL_BASKET_MISSING_EXCEPTION,
          "The basket is not valid (hotelId/reference are missing!");
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    boolean pibaCardNotPresent = paymentRequest.getPayment().getPibaCardPresent() != null
        && !paymentRequest.getPayment().getPibaCardPresent();

    //BusinessItems Common Logic
    List<BusinessAllowanceRule> businessAllowanceRules;
    BusinessNotesResponse businessNotesResponse;
    BusinessItems businessItems;
    if (pibaCardNotPresent) {
      businessItems = buildBusinessItemsFromBusinessAccount(
          paymentRequest.getBusinessAccount(),
          distributionProperties.getMeals());
      businessAllowanceRules = rulesAgentOutPort.getBusinessAllowanceRules()
          .getBusinessAllowances();
      businessNotesResponse = contentOutPort.getBusinessNotes(
          paymentRequest.getBooking().getLanguage());
    } else {
      businessNotesResponse = null;
      businessAllowanceRules = null;
      businessItems = null;
    }

    //PaymentDetails - Common Logic
    var confirmationPaymentDetails = getDistributionConfirmationPaymentDetails(basket,
        paymentRequest);
    Optional.ofNullable(paymentRequest.getPayment().getSca())
        .ifPresent(sca -> confirmationPaymentDetails.setCitId(sca.getXid()));
    log.debug("ConfirmationPaymentDetails={}", confirmationPaymentDetails);

    if (BasketStatus.PAY_PENDING.equals(basket.getStatus())) {
      log.info(
          "Could not update guest/Ancillaries for basketReference={} when status is PAY_PENDING",
          sanitize(basketReference));
    } else {
      var reservationsIds = basket.getItems().stream()
          .map(BasketItem::getSourceId)
          .sorted(Comparator.comparingInt(Integer::parseInt))
          .toList();
      //Logic to create Profile
      guestReservationRequest.setHotelId(hotelId);
      guestReservationRequest.setBasketReference(basketReference);
      var guests = guestReservationRequest.getStayingGuests();
      IntStream.range(0, guests.size())
          .forEach(i -> guests.get(i).setReservationId(reservationsIds.get(i)));

      var profileIds = reservationOutPort.createProfileIds(guestReservationRequest);
      log.debug("ProfileIds Created in OPERA:: bookerProfileId={}, companyProfileId={}",
          profileIds.getBookerProfileId(), profileIds.getBookerProfileId());

      IntStream.range(0, reservationsIds.size())
          .forEach(i -> {
            var reservationId = reservationsIds.get(i);
            //Logic for Guest/BookerDetails
            var guest = guestReservationRequest.getStayingGuests().get(i);
            var guestResDetails = createGuestResDetails(guestReservationRequest, guest,
                basketReference, basket.getChannel(), profileIds);
            log.debug("CreateGuestReservation Details={}", guestResDetails);

            //Logic for Ancillaries
            var arrivalDate = updatePackageReservationRequest.getArrivalDate();
            var departureDate = updatePackageReservationRequest.getDepartureDate();
            var roomSelections = (!updatePackageReservationRequest.getRoomsSelections().isEmpty()
                && (updatePackageReservationRequest.getRoomsSelections().size() > i))
                ? List.of(updatePackageReservationRequest.getRoomsSelections().get(i))
                : Collections.<RoomsSelections>emptyList();
            var packageResDetails = createPackageResDetails(arrivalDate, departureDate,
                reservationId, hotelId, roomSelections);
            log.debug("Ancillaries Details={}", packageResDetails);

            //Logic for BusinessItems
            BusinessItems resBusinessItems = null;
            if (pibaCardNotPresent) {
              var packageCodes = (!updatePackageReservationRequest.getRoomsSelections().isEmpty()
                  && (updatePackageReservationRequest.getRoomsSelections().size() > i))
                  ? updatePackageReservationRequest.getRoomsSelections().get(i)
                  .getPackagesSelection().stream()
                  .map(PackagesSelection::getId)
                  .toList()
                  : Collections.<String>emptyList();
              resBusinessItems =
                  BusinessAllowancesUtils.getBusinessItems(
                      businessItems,
                      businessAllowanceRules,
                      businessNotesResponse,
                      packageCodes, paymentRequest.getBooking().getType());
            }
            log.debug("BusinessItems Details={}", resBusinessItems);

            //Logic for Payment
            var paymentDetails = createPaymentResDetails(confirmationPaymentDetails, basket,
                reservationId, paymentRequest.getPayment().getPibaCardPresent());
            log.debug("PaymentDetails={}", paymentDetails);

            //Logic to execute single endpoint to update the data
            callSingleUpdateReservation(guestResDetails,
                packageResDetails,
                resBusinessItems,
                paymentRequest.getSpecialRequests(),
                paymentRequest.getBookingNotes(),
                hotelId, reservationId, paymentDetails, basket);
          });
    }
    return basketUpdateAndTriggerNotification(basket, priceBreakDownNeeded);

  }

  public BasketStatusResponse preCheckInBasket(final String basketReference, Boolean isCiol) {
    var basket = basketOutPort.getBasketById(basketReference);

    boolean skipOutstandingBalance = false;
    if (Boolean.TRUE.equals(isCiol)) {
      log.info("log_skip_out_bal : isCiol is true for basketReference: {}", sanitize(basketReference));
      skipOutstandingBalance = isSkipOutstandingBalance(basket, skipOutstandingBalance);
    }
    if (!skipOutstandingBalance) {
      log.info("log_skip_out_bal : balance verification triggered for basketReference: {}", sanitize(basketReference));
      verifyOutstandingBalance(basketReference);
    }

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCheckInOnline())) {
      if (BasketStatus.isReadyForCheckIn(basket.getStatus())
          && Objects.equals(basket.getPaymentStatus(), BasketPaymentStatus.COMPLETED)) {
        basket.setStatus(PRE_CHECKED_IN);
        basket.setCleanUpTime(cleanUpTime.getCleanUpTime(basket));
        basketOutPort.updateBasket(basket);
        triggerPreCheckInAsync(basket);
      } else {
        var message = String.format(
            "Cannot confirm pre-check-in: basket %s is not in completed state.",
            basket.getReference());
        var exception = new PreconditionFailedException(
            ErrorCode.DIGITAL_BASKET_NOT_COMPLETED_EXCEPTION, message);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
    log.info("CIOL: successfully checked in booking:{}", basket.getReference());
    return BasketStatusResponse.builder().basketReference(basketReference)
        .basketStatus(basket.getStatus())
        .createdAt(now().toString())
        .retryPayment(basket.getRetryPayment())
        .basketError(basket.getBasketError()).build();
  }

  public boolean isSkipOutstandingBalance(Basket basket, boolean skipOutstandingBalance) {
    var items = basket.getItems();
    if (items != null && !items.isEmpty()) {
      var sourceIds = items.stream().map(BasketItem::getSourceId).filter(Objects::nonNull).collect(Collectors.toSet());
      if (!sourceIds.isEmpty()) {
        log.info("log_skip_out_bal : SourceIds found for basket {}: {}", basket.getHotelId(), sourceIds);
        var paymentTypes = basketOutPort.getPaymentType(basket.getHotelId(), sourceIds);
        skipOutstandingBalance = paymentTypes != null && paymentTypes.stream().anyMatch(infoPaymentType -> {
          if (infoPaymentType == null || infoPaymentType.getPaymentCardType() == null) {
            return false;
          }
          var cardType = infoPaymentType.getPaymentCardType();
          String method = cardType.getPaymentMethod();
          log.info("log_skip_out_bal : paymentType method: {}, folioView: {}", method, cardType.getFolioView());
          return (BU.equals(method) || BD.equals(method)) && FOLIO_VIEW_PIBA_CNP.equals(cardType.getFolioView());
        });
      }
    }
    log.info("log_skip_out_bal : BasketItem is Null or Empty.");
    return skipOutstandingBalance;
  }

  @Override
  public BasketStatusResponse preCheckOutBasket(String basketReference) {
    var basket = basketOutPort.getBasketById(basketReference);

    if (BasketStatus.isReadyForCheckOut(basket.getStatus())) {
      basket.setStatus(PRE_CHECKED_OUT);
      basket.setCleanUpTime(cleanUpTime.getCleanUpTime(basket));
      var savedBasket = basketOutPort.updateBasket(basket);
      var request = ReservationAlertsRequest.builder()
          .hotelId(basket.getHotelId())
          .reservationIds(getReservationIds(savedBasket))
          .alerts(List.of(Alert.builder()
              .area(ALERT_AREA_CHECKOUT)
              .code(ALERT_CODE_COOL)
              .description(ALERT_DESCRIPTION_COOL)
              .screenNotification(true)
              .printerNotification(false)
              .build()))
          .build();
      CompletableFuture.runAsync(concurrentTracer.wrap(() -> reservationOutPort.updateReservationAlerts(request)));
      log.info("COOL: successfully checked out booking:{}", savedBasket.getReference());
      return BasketStatusResponse.builder()
          .basketReference(savedBasket.getReference())
          .basketStatus(savedBasket.getStatus())
          .createdAt(savedBasket.getCreatedAt())
          .retryPayment(savedBasket.getRetryPayment())
          .basketError(savedBasket.getBasketError())
          .build();
    } else {
      var message = String.format(
          "Cannot perform check-out: booking %s has basket status %s.",
          basket.getReference(), basket.getStatus().toString());
      var exception = new PreconditionFailedException(
          ErrorCode.DIGITAL_BASKET_NOT_COMPLETED_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  @Override
  public Basket changeStatus(String bookingRef, String status) {
    var basketStatus = BasketStatus.valueOf(status);
    var basket = basketOutPort.getBasketByReference(bookingRef);

    if (basket.isPresent()) {
      var existingBasket = basket.get();
      existingBasket.setStatus(basketStatus);
      existingBasket.setCleanUpTime(cleanUpTime.getCleanUpTime(existingBasket));
      return basketOutPort.updateBasket(existingBasket);
    } else {
      var message = String.format("Basket with booking reference=%s", bookingRef);
      var exception = new BookingReferenceNotFoundException(
          ErrorCode.BASKET_NOT_FOUND_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  @Override
  public Basket addPromotionToBasket(String basketReference,
      PromotionsInformationRequest promotionsInformationRequest, String requestModifyTimestamp) {
    log.info("Entered addPromotionToBasket with basketReference={}", sanitize(basketReference));
    var basket = getExistingBasket(basketReference);

    validateBasketLastModifyPrecondition(requestModifyTimestamp, basket);
    validatePromotionRequest(promotionsInformationRequest);
    applyPromotion(basket, promotionsInformationRequest);
    try {
      return basketOutPort.updateBasket(basket);
    } catch (Exception e) {
      var message = String.format("Failed to add promotion details for basketReference: %s",
          basketReference);
      var exception = new PreconditionFailedException(
          ErrorCode.DIGITAL_BASKET_NOT_COMPLETED_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  @Override
  public Basket changeIdContext(String basketReference, String idContext) {
    var basket = basketOutPort.getBasketByReference(basketReference);

    if (basket.isPresent()) {
      var existingBasket = basket.get();
      existingBasket.setIdContext(idContext);
      existingBasket.setCleanUpTime(cleanUpTime.getCleanUpTime(existingBasket));
      return basketOutPort.updateBasket(existingBasket);
    } else {
      var message = String.format("Basket with booking reference=%s", basketReference);
      var exception = new BookingReferenceNotFoundException(
          ErrorCode.BASKET_NOT_FOUND_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private Basket getExistingBasket(String basketReference) {
    var basket = basketOutPort.getBasketById(basketReference);
    if (Objects.isNull(basket)) {
      var message = String.format("Basket with basket reference=%s not found", basketReference);
      var exception = new BookingReferenceNotFoundException(ErrorCode.BASKET_NOT_FOUND_EXCEPTION,
          message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return basket;
  }

  private void validatePromotionRequest(PromotionsInformationRequest req) {
    if (req.getPromotionCode() == null || req.getPromotionCode().isBlank()
        || req.getPromoKind() == null) {
      throw new PreconditionFailedException(ErrorCode.BASKET_INVALID_EXCEPTION,
          "Invalid promotions information");
    }
  }

  private void applyPromotion(Basket basket, PromotionsInformationRequest req) {
    basket.setPromotionCode(req.getPromotionCode());
    basket.setPromoKind(req.getPromoKind());
    basket.setLastModifiedAt(Instant.now().toString());
  }

  private void callSingleUpdateReservation(ReservationGuestRequest guestResDetails,
      ReservationPackagesRequest packageResDetails, BusinessItems resBusinessItems,
      List<String> specialRequests, List<String> bookingNotes, String hotelId,
      String reservationId, ConfirmReservationRequest paymentDetails, Basket basket) {
    var response = reservationOutPort.updateReservationRequest(guestResDetails,
        packageResDetails,
        resBusinessItems,
        Optional.ofNullable(specialRequests).orElse(Collections.<String>emptyList()),
        Optional.ofNullable(bookingNotes).orElse(Collections.<String>emptyList()),
        hotelId, reservationId, paymentDetails);
    log.info("Reservation Status for the reservation={}, is {}", reservationId,
        response.getReservationStatus());
    var basketItem = basket.getItems().stream()
        .filter(resId -> resId.getSourceId().equalsIgnoreCase(reservationId)).toList();
    basketItem.get(0).setAck(response.getReservationStatus().equalsIgnoreCase("RESERVED") ? 0 : 1);
    basketItem.get(0).setReqAction("COMMIT");
    if (basketItem.get(0).getAck() == 1) {
      basket.setBasketError(BasketError.builder()
          .code(BASKET_ERROR_CODE)
          .description("Reservation request status is:" + response.getReservationStatus())
          .type(CONFIRMATION)
          .build());
    }
  }

  private ReservationByBasketRefResponse basketUpdateAndTriggerNotification(Basket basket,
      String priceBreakDownNeeded) {
    ReservationByBasketRefResponse reservationByBasketRefResponse;
    if (isAllItemsComplete(basket)) {
      log.debug("All reservations were successfully reserved");
      updateBasketDetails(basket);
      reservationByBasketRefResponse = reservationOutPort.getReservationsByBasketReference(
          basket.getBasketId(), priceBreakDownNeeded, true);
    } else {
      basket.setStatus(BasketStatus.FAILED);
      basket.setCleanUpTime(cleanUpTime.getCleanUpTime(basket));
      basket.setPaymentStatus(BasketPaymentStatus.REFUNDING);
      basketOrderOutPort.processOrder(basket, BasketRequestAction.CANCEL.getReqAction(), null,
          null);
      triggerEmailNotification(basket, EmailNotificationEventType.FAIL, false);
      log.error("Basket is not reserved and the status is={}", basket.getStatus());
      reservationByBasketRefResponse = ReservationByBasketRefResponse.builder().build();
    }
    basketOutPort.updateBasket(basket);
    return reservationByBasketRefResponse;
  }

  private ConfirmReservationRequest createPaymentResDetails(
      BookingConfirmationDetails confirmationPaymentDetails, Basket basket, String reservationId,
      Boolean pibaCardPresent) {
    var paymentDetails = ConfirmReservationRequest.builder()
        .hotelId(basket.getHotelId())
        .reservationId(reservationId)
        .paymentOption(PaymentOption.valueOf(basket.getPaymentOption()))
        .pibaCardPresent(pibaCardPresent)
        .build();

    if (confirmationPaymentDetails != null
        && confirmationPaymentDetails.getCardData() != null) {
      paymentDetails.setPaymentCard(PaymentCard.builder()
          .token(confirmationPaymentDetails.getCardData().getToken())
          .cardType(confirmationPaymentDetails.getCardType())
          .cardHolderName(confirmationPaymentDetails.getCardData().getCardHolderName())
          .cardNumberLast4Digits(confirmationPaymentDetails.getCardData().getLast4Digits())
          .expirationDate(confirmationPaymentDetails.getCardData().getExpirationDate())
          .build()
      );
      paymentDetails.setPaymentMethod(confirmationPaymentDetails.getPaymentMethod());
      paymentDetails.setPaymentType(confirmationPaymentDetails.getPaymentType());
      if (confirmationPaymentDetails.getCitId() != null) {
        paymentDetails.getPaymentCard().setCitId(confirmationPaymentDetails.getCitId());
      }
    }
    return paymentDetails;
  }

  private ReservationPackagesRequest createPackageResDetails(
      String arrivalDate, String departureDate, String reservationId,
      String hotelId, List<RoomsSelections> roomsSelections) {
    return ReservationPackagesRequest.builder()
        .arrivalDate(arrivalDate)
        .departureDate(departureDate)
        .reservationsId(List.of(reservationId))
        .hotelId(hotelId)
        .roomsSelections(roomsSelections)
        .build();
  }

  private ReservationGuestRequest createGuestResDetails(
      ReservationGuestRequest guestReservationRequest,
      StayingGuest guest, String basketReference, String channel,
      ReservationProfiles profileIds) {
    // Change booking type and additional UDFs if user logged in on GDP
    // Not applicable for CCUI (see findBookingType)
    if (!CCUI_BOOKING_CHANNEL.equals(channel)
        && authenticatedUserService.isUserAuthenticated()) {
      authenticatedUserService.getCurrentUserAccount().ifPresent(account ->
          guestReservationRequest.setBookingType(channel)
      );
    }

    return ReservationGuestRequest.builder()
        .stayingGuests(List.of(guest))
        .hotelId(guestReservationRequest.getHotelId())
        .booker(guestReservationRequest.getBooker())
        .reasonForStay(guestReservationRequest.getReasonForStay())
        .sendEmailConfirmation(guestReservationRequest.getSendEmailConfirmation())
        .sendEmailInvoice(guestReservationRequest.getSendEmailInvoice())
        .companyProfileId(profileIds.getCompanyProfileId())
        .bookerProfileId(profileIds.getBookerProfileId())
        .basketReference(basketReference)
        .build();
  }

  private BookingConfirmationDetails getDistributionConfirmationPaymentDetails(Basket basket,
      PaymentRequest paymentRequest) {
    HotelPaymentInformation hotelPaymentInformation =
        contentOutPort.getHotelPaymentDetails(basket.getHotelId(),
            Country.valueOf(paymentRequest.getBooking().getLanguage().toUpperCase())
                .getCountryCode(),
            paymentRequest.getBooking().getLanguage().toLowerCase());
    return createDistributionConfirmationPaymentDetails(basket.getChannel(),
        hotelPaymentInformation,
        paymentRequest, unleashWrapper);
  }

  private boolean isExpired(Basket basket) {
    final long now = Instant.now().truncatedTo(ChronoUnit.SECONDS).toEpochMilli();
    final long createdAt = Instant.parse(basket.getCreatedAt()).toEpochMilli();
    return now - createdAt > basketValidity;
  }

  private boolean isBasketCompleted(Basket basket) {
    if (basket.getOriginalBasketId() != null) {
      return basket.getStatus().equals(AMENDED);
    }
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCheckInOnline())
        && Boolean.TRUE.equals(basket.getIsCheckInOnlinePay())) {
      return basket.getStatus().equals(PRE_CHECKED_IN) || basket.getStatus().equals(COMPLETED);
    }
    return basket.getStatus().equals(COMPLETED);
  }

  private void verifyOutstandingBalance(String basketReference) {
    var booking = reservationOutPort.getReservationsByBasketReference(basketReference, "false",
        false);
    var balance = booking.getBalanceOutstanding().stripTrailingZeros();

    if (BigDecimal.ZERO.compareTo(balance) != 0) {
      var message = String.format(
          "Cannot confirm pre-check-in: basket %s has outstanding balance.",
          basketReference);
      var exception = new PreCheckinOutstandingBalanceException(
          ErrorCode.DIGITAL_BASKET_NOT_COMPLETED_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private Set<String> getReservationIds(Basket basket) {
    return basket.getItems().stream()
        .filter(basketItem -> BASKET_ITEM_TYPE_STAY.equals(basketItem.getType()))
        .map(BasketItem::getSourceId)
        .collect(Collectors.toSet());
  }

  private UdfsRequestDto characterUdfsRequestDto(Basket basket) {
    CharacterUdfDto characterUdfDto = new CharacterUdfDto();
    characterUdfDto.setName("UDFC20");
    characterUdfDto.setValue("CIOL_COMPLETED");
    UdfsRequestDto udfsRequestDto = new UdfsRequestDto();
    udfsRequestDto.setHotelId(basket.getHotelId());
    udfsRequestDto.setReservationIds(getReservationIds(basket));
    udfsRequestDto.setUdfs(List.of(characterUdfDto));
    return udfsRequestDto;
  }

  private void triggerPreCheckInAsync(Basket basket) {
    updateReservationAlerts(basket);
    basketOutPort.updateCharacterUdfs(characterUdfsRequestDto(basket));
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getMobilePreRegisteredRepurpose())
        && basket.getItems() != null && !basket.getItems().isEmpty()) {
      LocalDate arrivalDate = (basket.getLockingTime() != null && !basket.getLockingTime().isBlank())
          ? OffsetDateTime.parse(basket.getLockingTime()).toLocalDate() : null;
      for (BasketItem item : basket.getItems()) {
        basketOutPort.postReservationPreregister(buildPreCheckInRequestDto(basket, item, arrivalDate));
      }
    }
  }

  private PreCheckInRequestDto buildPreCheckInRequestDto(Basket basket, BasketItem item, LocalDate arrivalDate) {
    PreCheckInRequestDto preCheckInRequestDto = new PreCheckInRequestDto();
    preCheckInRequestDto.setHotelId(basket.getHotelId());
    preCheckInRequestDto.setReservationId(item.getSourceId());
    preCheckInRequestDto.setArrivalTime(arrivalDate);
    return preCheckInRequestDto;
  }
}
