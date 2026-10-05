package uk.co.whitbread.basket.infrastructure.queue.processor;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.payments.out.BookingConfirmationDetails;
import uk.co.whitbread.basket.infrastructure.queue.exception.BasketOrderException;
import uk.co.whitbread.basket.infrastructure.queue.model.BasketOrderEvent;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Component
@Slf4j
public class BasketOrderMapper {

  static final String PROCESS_ERROR_MESSAGE = "Unable to process item";
  static final String TOKEN = "token";
  static final String CARD_TYPE = "cardType";
  static final String PAYMENT_TYPE = "paymentType";
  static final String PAYMENT_METHOD = "paymentMethod";
  static final String CARD_HOLDER_NAME = "cardHolderName";
  static final String CARD_NUMBER_LAST_4_DIGITS = "cardNumberLast4Digits";
  static final String EXPIRATION_DATE = "expirationDate";
  static final String PAYMENT_OPTION = "paymentOption";
  static final String PAYMENT_ID = "paymentID";
  static final String CIT_ID = "citId";
  static final String CC_AGENT_ID = "ccAgentId";
  static final String THREE_DS_INDICATOR = "threeDSIndicator";

  private Set<String> rootFields;

  public BasketOrderMapper(
      @Value("${basket.root.fields}") Set<String> rootFields) {
    this.rootFields = rootFields;
  }

  public List<BasketOrderEvent> processBasket(final Basket basket, final String reqAction,
      final BookingConfirmationDetails bookingConfirmationDetails, final String ccAgentId) {
    return Optional.ofNullable(basket.getItems())
        .orElseThrow(() -> {
          var exception = new BasketOrderException(
              ErrorCode.DIGITAL_BASKET_ORDER_NO_ITEMS_EXCEPTION, "No items to process");
          ExceptionLogger.log(log, exception);
          return exception;
        })
        .stream().map(item -> extractOrder(item, basket, reqAction, bookingConfirmationDetails, ccAgentId))
        .toList();
  }

  private BasketOrderEvent extractOrder(final BasketItem item, final Basket basket,
      final String reqAction, final BookingConfirmationDetails bookingConfirmationDetails,
      final String ccAgentId) {
    log.debug("item= {}, basket={}, reqAction={}, bookingConfirmationDetails={}", item, basket, reqAction,
        bookingConfirmationDetails);
    final Map<String, String> data = Optional.ofNullable(basket.getItemTypes())
        .map(types -> types.get(item.getType()))
        .orElseThrow(() -> {
          var exception = new BasketOrderException(
              ErrorCode.DIGITAL_BASKET_ORDER_EXTRACTS_ITEMS_EXCEPTION, PROCESS_ERROR_MESSAGE);
          ExceptionLogger.log(log, exception);
          return exception;
        })
        .stream()
        .collect(toMap(this::extractFieldName, field -> getField(item, basket, field)));
    if (bookingConfirmationDetails != null
        && bookingConfirmationDetails.getCardData() != null) {
      Map<String, String> cardDataMap = new HashMap<>();
      cardDataMap.put(TOKEN, bookingConfirmationDetails.getCardData().getToken());
      cardDataMap.put(CARD_TYPE, bookingConfirmationDetails.getCardType());
      cardDataMap.put(PAYMENT_TYPE, bookingConfirmationDetails.getPaymentType());
      cardDataMap.put(CARD_HOLDER_NAME, bookingConfirmationDetails.getCardData().getCardHolderName());
      cardDataMap.put(CARD_NUMBER_LAST_4_DIGITS, bookingConfirmationDetails.getCardData().getLast4Digits());
      cardDataMap.put(EXPIRATION_DATE, bookingConfirmationDetails.getCardData().getExpirationDate());
      cardDataMap.put(PAYMENT_OPTION, basket.getPaymentOption());
      cardDataMap.put(PAYMENT_METHOD, bookingConfirmationDetails.getPaymentMethod());
      cardDataMap.put(THREE_DS_INDICATOR, StringUtils.isEmpty(basket.getThreeDSIndicator())
          ? EMPTY : basket.getThreeDSIndicator());
      data.putAll(cardDataMap);
      if (StringUtils.isNotBlank(basket.getPaymentID())) {
        data.put(PAYMENT_ID, basket.getPaymentID());
      }
    } else {
      data.put(PAYMENT_OPTION, basket.getPaymentOption());
    }
    data.put("reqAction", reqAction);
    data.put(CC_AGENT_ID, StringUtils.isEmpty(ccAgentId) ? "" : ccAgentId);
    Optional.ofNullable(bookingConfirmationDetails)
        .flatMap(bookingConfDetails -> Optional.ofNullable(bookingConfDetails.getCitId()))
        .ifPresent(citId -> data.put(CIT_ID, citId));
    final String id = String.join("#", item.getType(), item.getSourceId());

    return BasketOrderEvent.builder()
        .eventId(id)
        .bookingReference(basket.getReference())
        .basketReference(basket.getBasketId())
        .data(data)
        .build();
  }

  private String extractFieldName(final String path) {
    validateFieldPath(path);

    return path.substring(path.lastIndexOf(".") + 1);
  }

  private String getField(final Object item, final Basket basket, final String path) {
    validateFieldPath(path);

    try {
      if (isBasketField(path)) {
        return getBasketField(basket, path);
      } else {
        final Queue<String> fieldPath = new ArrayDeque<>(asList(path.split("\\.")));
        return extractField(item, fieldPath);
      }
    } catch (NoSuchFieldException | IllegalAccessException e) {
      var exception = new BasketOrderException(
          ErrorCode.DIGITAL_BASKET_ORDER_EXTRACTS_FIELD_NAME_EXCEPTION,
          String.format(PROCESS_ERROR_MESSAGE + " %s",
              ". Error while trying to extract the order field name."));
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private String getBasketField(final Basket basket, final String fieldName)
      throws NoSuchFieldException, IllegalAccessException {
    final Field field = basket.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);

    var fieldValue = (String) field.get(basket);

    if (fieldValue == null) {
      var exception = new BasketOrderException(
          ErrorCode.DIGITAL_BASKET_ORDER_EXTRACTS_NULL_EXCEPTION,
          PROCESS_ERROR_MESSAGE);
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    return fieldValue;
  }

  private boolean isBasketField(final String fieldName) {
    return rootFields.contains(fieldName);
  }

  private String extractField(final Object item, final Queue<String> path)
      throws NoSuchFieldException, IllegalAccessException {
    if (item == null) {
      var exception = new BasketOrderException(
          ErrorCode.DIGITAL_BASKET_ORDER_EXTRACTS_FIELD_NULL_EXCEPTION,
          PROCESS_ERROR_MESSAGE);
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    if (path.isEmpty()) {
      return (String) item;
    }

    final String next = path.remove();

    if (item instanceof Map) {
      return (String) Optional.ofNullable(((Map) item).get(next))
          .orElseThrow(() -> {
            var exception = new BasketOrderException(
                ErrorCode.DIGITAL_BASKET_ORDER_EXTRACTS_MAP_NULL_EXCEPTION,
                PROCESS_ERROR_MESSAGE);
            ExceptionLogger.log(log, exception);
            return exception;
          });
    }

    final Field field = item.getClass().getDeclaredField(next);
    field.setAccessible(true);

    return extractField(field.get(item), path);
  }

  private void validateFieldPath(String path) {
    if (Strings.isEmpty(path)) {
      var exception = new BasketOrderException(
          ErrorCode.DIGITAL_BASKET_ORDER_EXTRACTS_PATH_NULL_EXCEPTION,
          PROCESS_ERROR_MESSAGE);
      ExceptionLogger.log(log, exception);
      throw  exception;
    }
  }

}
