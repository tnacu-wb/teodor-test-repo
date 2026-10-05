package uk.co.whitbread.reservation.domain.logic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketError;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.exceptions.AmendErrorCode;
import uk.co.whitbread.reservation.domain.exceptions.AmendReservationException;
import uk.co.whitbread.reservation.domain.logic.utils.PaymentUtils;
import uk.co.whitbread.reservation.domain.model.amend.in.DepositFolioComputationResult;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.HotelPaymentInformation;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.CurrencyAmount;
import uk.co.whitbread.reservation.domain.model.out.DepositFolio;
import uk.co.whitbread.reservation.domain.model.out.DepositFolioCharge;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.model.out.RefundResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.payment.in.Amount;
import uk.co.whitbread.reservation.domain.model.payment.in.Booking;
import uk.co.whitbread.reservation.domain.model.payment.in.BusinessSite;
import uk.co.whitbread.reservation.domain.model.payment.in.Card;
import uk.co.whitbread.reservation.domain.model.payment.in.ReasonEnum;
import uk.co.whitbread.reservation.domain.model.payment.in.Refund;
import uk.co.whitbread.reservation.domain.model.payment.in.RefundRequest;
import uk.co.whitbread.reservation.domain.model.payment.in.RefundTypeEnum;
import uk.co.whitbread.reservation.domain.model.payment.in.TypeEnum;
import uk.co.whitbread.reservation.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelReservationOhipOutPort;

@Slf4j
@AllArgsConstructor
public class AmendPayNowLogic {

  private final HotelReservationOhipOutPort hotelReservationOhipOutPort;

  private final BasketOutPort basketOutPort;

  public DepositFolioComputationResult calculateDfForPayNow(
      List<String> originalResIds,
      String hotelId,
      BasketResponse temporaryBasket,
      Map<String, String> linkBetweenReservationIds,
      ReservationByBasketRefResponse originalResByBasket,
      String paymentOption,
      String channel, HotelPaymentInformation hotelPaymentMethodInfo) {

    var originalDepositFolios = getDepositFoliosFromDb(originalResIds);
    originalDepositFolios.getDepositFolios().forEach(deposit -> {
      deposit.setHotelId(hotelId);
      deposit.setVatRegion(originalResByBasket.getReservationByIdList().get(0).getCashiering().getTaxType()
          .getCode());
    });

    var tempDepositFolios = hotelReservationOhipOutPort.getGeneratedDepositFolios(
        temporaryBasket.getHotelId(),
        temporaryBasket.getItems().stream().map(BasketItemResponse::getSourceId)
            .collect(Collectors.toSet())
    );

    //map of maps : key = reservation Id; value = map of DF's for this reservation Id
    //map of DF's : key = date + "-" + transactioncode; value = amount
    var originalDFMap = consolidateDepositFolios(originalDepositFolios);

    var tempDFMap = tempDepositFolios.getDepositFolios().stream().collect(Collectors.toMap(
        DepositFolio::getReservationId,
        value ->
            value.getCharges().stream().collect(
                    Collectors.groupingBy(
                        dfc -> dfc.getReference() + "-" + dfc.getTransactionCode(),
                        Collectors.reducing(
                            BigDecimal.ZERO,
                            df -> df.getCurrencyAmount().getAmount(),
                            BigDecimal::add))
                ).entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())))
    );

    var originalRsv = originalResByBasket.getReservationByIdList().stream()
        .filter(rsv -> rsv.getPaymentCard() != null).findFirst().orElse(null);
    var originalPaymentMethod = originalRsv != null && originalRsv.getPaymentCard() != null
        ? originalRsv.getPaymentCard().getPaymentMethod() : null;

    DepositFolioComputationResult computationResult = calculateDepositFolios(originalDFMap,
        tempDFMap, linkBetweenReservationIds, paymentOption);
    if (!originalDepositFolios.getDepositFolios().isEmpty()) {
      computationResult
          .setCurrencyCode(originalDepositFolios.getDepositFolios().get(0).getCharges().get(0)
              .getCurrencyAmount().getCurrencyCode());
    }
    computationResult.setDefaultPaymentMethod(originalPaymentMethod != null
        ? PaymentUtils.getDefaultPaymentMethod(originalPaymentMethod, hotelPaymentMethodInfo, channel) : null);
    return computationResult;
  }

  private DepositFoliosResponse getDepositFoliosFromDb(List<String> reservationIds) {
    List<DepositFolio> depositFolios = new ArrayList<>();
    var deposits = basketOutPort.getChargesByReservationIds(reservationIds);
    if (Objects.nonNull(deposits)) {
      depositFolios = deposits.getDepositFolios();
    }
    return DepositFoliosResponse.builder()
        .depositFolios(depositFolios).build();
  }

  public Map<String, Map<String, BigDecimal>> consolidateDepositFolios(
      DepositFoliosResponse depositFoliosResponse) {
    Map<String, Map<String, BigDecimal>> depositFoliosConsolidated = new HashMap<>();
    var reservationIds = depositFoliosResponse.getDepositFolios().stream()
        .map(DepositFolio::getReservationId).toList();

    reservationIds.forEach(reservationId -> {
      var depositFoliosPerResId = depositFoliosResponse.getDepositFolios().stream()
          .filter(deposit -> deposit.getReservationId().equals(reservationId))
          .toList();
      var chargesByKey = depositFoliosPerResId.stream()
          .map(DepositFolio::getCharges)
          .toList()
          .stream()
          .flatMap(List::stream)
          .toList()
          .stream()
          .collect(Collectors
              .groupingBy(charge -> charge.getReference() + "-" + charge.getTransactionCode()));

      var dfConsolidated = chargesByKey.entrySet().stream()
          .collect(Collectors.toMap(Map.Entry::getKey,
              value -> value.getValue().stream()
                  .mapToDouble(charge -> charge.getCurrencyAmount().getAmount().doubleValue())
                  .sum()))
          .entrySet()
          .stream()
          .collect(
              Collectors.toMap(Map.Entry::getKey, value -> BigDecimal.valueOf(value.getValue())));
      depositFoliosConsolidated.put(reservationId, dfConsolidated);

    });
    return depositFoliosConsolidated;
  }

  private DepositFolioComputationResult calculateDepositFolios(
      Map<String, Map<String, BigDecimal>> originalDFMap,
      Map<String, Map<String, BigDecimal>> tempDFMap,
      Map<String, String> linkAmendReservations, String paymentOption) {

    Map<String, Map<String, List<BigDecimal>>> depositFoliosAfterAmend = new HashMap<>();
    RefundAmount totalRefundAmount = new RefundAmount(BigDecimal.ZERO);
    PaymentAmount totalPaymentAmount = new PaymentAmount(BigDecimal.ZERO);
    List<Boolean> markAsPayOnArrival = new ArrayList<>();

    // check if there are removed rooms, and check in which basket to put the money
    var currentReservations = linkAmendReservations.keySet().stream().toList();
    var deletedReservations = originalDFMap.keySet().stream().filter(
        reservation -> !currentReservations.contains(reservation)).toList();

    if (!CollectionUtils.isEmpty(deletedReservations)) {
      deletedReservations.forEach(deletedRes -> {
        Map<String, List<BigDecimal>> depositFolioAfterDeletion = new HashMap<>();
        var refundAmount = BigDecimal.valueOf(originalDFMap.get(deletedRes).values()
            .stream()
            .mapToDouble(BigDecimal::doubleValue)
            .sum());

        for (String key : originalDFMap.get(deletedRes).keySet()) {
          var dfValue = BigDecimal.ZERO.subtract(originalDFMap.get(deletedRes).get(key));
          depositFolioAfterDeletion
              .putAll(addNewEntryInDf(key, List.of(dfValue), depositFolioAfterDeletion));
        }
        if (depositFolioAfterDeletion.size() > 0) {
          depositFoliosAfterAmend.put(deletedRes, depositFolioAfterDeletion);
        }
        totalRefundAmount
            .setNewTotalRefundAmt(totalRefundAmount.getNewTotalRefundAmt().add(refundAmount));
      });
    }
    List<String> originalResIdToCheck = originalDFMap.keySet().stream()
        .filter(resId -> !deletedReservations.contains(resId))
        .toList();
    for (String resId : originalResIdToCheck) {
      Bucket bucket = new Bucket(BigDecimal.ZERO, Boolean.FALSE);
      Map<String, List<BigDecimal>> newDfEntriesAfterAmend = new HashMap<>();
      var tempResId = linkAmendReservations.get(resId);
      var originalDFByResId = originalDFMap.get(resId);
      // here we calculate DF for copied reservation in temp basket
      var tempDFByResId = tempDFMap.get(tempResId);
      for (String key : originalDFByResId.keySet()) {
        var tempValue = tempDFByResId.get(key);
        // some DF have been removed from temp basket, and we calculate where to put the amount paid
        if (tempValue == null) {
          newDfEntriesAfterAmend
              .putAll(calculateDfForRemovedItems(originalDFByResId, key, newDfEntriesAfterAmend,
                  bucket));
        } else {
          newDfEntriesAfterAmend
              .putAll(calculateDfForExistingItemsWithDiffValues(originalDFByResId, key, tempValue,
                  bucket, newDfEntriesAfterAmend, paymentOption, totalPaymentAmount));
        }
      }

      //check if we can pay the new changes with remaining amount from previous changes
      newDfEntriesAfterAmend.putAll(useBucketsForNewItems(tempDFByResId, originalDFByResId, bucket,
          newDfEntriesAfterAmend, paymentOption, totalPaymentAmount));

      if (newDfEntriesAfterAmend.size() != 0) {
        depositFoliosAfterAmend.put(resId, newDfEntriesAfterAmend);
      }
      markAsPayOnArrival.add(bucket.getMarkAsPayOnArrival());

      if (bucket.getRefBucket().compareTo(BigDecimal.ZERO) < 0) {
        if (PaymentOption.PAY_NOW.toString().equalsIgnoreCase(paymentOption)) {
          markAsPayOnArrival.add(Boolean.FALSE);
          totalPaymentAmount.setNewTotalPaymentAmt(
              totalPaymentAmount.getNewTotalPaymentAmt().subtract(bucket.getRefBucket()));
          bucket.setRefBucket(BigDecimal.ZERO);
        } else {
          markAsPayOnArrival.add(Boolean.TRUE);
        }
      } else if (bucket.getRefBucket().compareTo(BigDecimal.ZERO) > 0) {
        totalRefundAmount.setNewTotalRefundAmt(
            totalRefundAmount.getNewTotalRefundAmt().add(bucket.getRefBucket()));
      }
    }

    // check if new rooms have been added to a reservation
    var oldReservations = originalDFMap.keySet().stream()
        .map(linkAmendReservations::get).toList();

    var newTempReservations = tempDFMap.keySet().stream().filter(
        reservation -> !oldReservations.contains(reservation)).toList();

    if (!CollectionUtils.isEmpty(newTempReservations)) {
      if (PaymentOption.PAY_ON_ARRIVAL.toString().equalsIgnoreCase(paymentOption)) {
        markAsPayOnArrival.add(Boolean.TRUE);
      } else if (PaymentOption.PAY_NOW.toString().equalsIgnoreCase(paymentOption)) {
        markAsPayOnArrival.add(Boolean.FALSE);
        newTempReservations.forEach(newReservation -> {
          var newTotalPaymentAmount = BigDecimal.valueOf(tempDFMap.get(newReservation).values()
              .stream()
              .mapToDouble(BigDecimal::doubleValue)
              .sum());
          totalPaymentAmount
              .setNewTotalPaymentAmt(
                  totalPaymentAmount.getNewTotalPaymentAmt().add(newTotalPaymentAmount));

          Map<String, List<BigDecimal>> dfForNewlyAddedRoom = tempDFMap.get(newReservation)
              .entrySet()
              .stream()
              .collect(Collectors.toMap(Map.Entry::getKey, value -> List.of(value.getValue())));
          depositFoliosAfterAmend.put(newReservation, dfForNewlyAddedRoom);
        });
      }
    }

    DepositFolioComputationResult depositFolioComputationResult = new DepositFolioComputationResult();
    depositFolioComputationResult.setMarkAsPayOnArrival(
        markAsPayOnArrival.stream().anyMatch(Boolean::booleanValue));
    depositFolioComputationResult.setDepositFoliosAfterAmend(depositFoliosAfterAmend);
    depositFolioComputationResult.setTotalRefundAmt(totalRefundAmount.getNewTotalRefundAmt());

    return depositFolioComputationResult;
  }

  public DepositFolioComputationResult triggerRefund(ReservationByBasketRefResponse origResByBasket,
      DepositFolioComputationResult depositFolioComputationResult, String originalBasketReference,
      String tempBasketReference) {
    var refundRequest = generateRefundRequest(origResByBasket,
        depositFolioComputationResult.getTotalRefundAmt());
    try {
      RefundResponse refundResponse = basketOutPort
          .triggerRefundRequest(origResByBasket.getBookingReference(), refundRequest);
      if (!refundResponse.getRefunded()) {
        buildAmendRefundException(originalBasketReference, tempBasketReference);
      }
      depositFolioComputationResult.setPaymentId(refundResponse.getRefundId());
    } catch (Exception ex) {
      buildAmendRefundException(originalBasketReference, tempBasketReference);
    }
    return depositFolioComputationResult;
  }

  public DepositFoliosResponse saveDepositFolios(
      DepositFolioComputationResult computationResult,
      String hotelId,
      List<String> originalReservationIdsToDelete,
      List<String> reservationIdsToBeAdded) {
    log.info("Save deposit folios: {}: ", computationResult);
    DepositFoliosResponse depositFolios = new DepositFoliosResponse(new ArrayList<>());
    computationResult.getDepositFoliosAfterAmend().keySet().forEach(reservationId -> {
      DepositFolio depositFolio = DepositFolio.builder()
          .reservationId(reservationId)
          .hotelId(hotelId)
          .paymentId(computationResult.getPaymentId())
          .defaultPaymentMethod(computationResult.getDefaultPaymentMethod())
          .charges(new ArrayList<>())
          .build();
      Map<String, List<BigDecimal>> charges = computationResult
          .getDepositFoliosAfterAmend().get(reservationId);

      charges.keySet().forEach(keyCharge -> {

        List<BigDecimal> bigDecimals = charges.get(keyCharge);
        bigDecimals.forEach(
            amount -> {
              if (amount.compareTo(BigDecimal.ZERO) != 0) {
                DepositFolioCharge depositFolioCharge = DepositFolioCharge.builder()
                    .currencyAmount(CurrencyAmount.builder()
                        .amount(amount)
                        .currencyCode(computationResult.getCurrencyCode())
                        .build())
                    .build();

                depositFolioCharge.setTransactionCode(
                    keyCharge.substring(keyCharge.lastIndexOf("-") + 1));
                depositFolioCharge.setReference(keyCharge.substring(0, keyCharge.lastIndexOf("-")));
                //value for quantity is always 1
                depositFolioCharge.setQuantity(1);
                depositFolio.getCharges().add(depositFolioCharge);
              }
            }
        );
      });
      //Only send deposit folios if there are charges
      log.info("Reservation: {} with charges: {}", reservationId, charges);
      if (!charges.isEmpty()) {
        if (!depositFolio.getCharges().isEmpty()
            && !depositFolio.getCharges().get(0).getCurrencyAmount().getAmount()
            .equals(BigDecimal.ZERO)) {
          depositFolios.getDepositFolios().add(depositFolio);
        } else {
          log.info("Do not save DF with 0 amount in Opera");
        }
      }
    });

    DepositFoliosResponse nonCancelledDepositFolios = getNonCancelledReservations(depositFolios,
        originalReservationIdsToDelete, reservationIdsToBeAdded);

    if (!depositFolios.getDepositFolios().isEmpty()) {
      basketOutPort.saveCharges(depositFolios);
    }

    if (!nonCancelledDepositFolios.getDepositFolios().isEmpty()) {
      saveOperaCharges(nonCancelledDepositFolios);
    }

    return depositFolios;
  }

  /**
   * Save charges from DepositFoliosResponse applying the fix agreed with Opera for the
   * reallocations of funds. When the sum of all charges from a reservation is 0, the charges are
   * split into two lists—one for negative values and one for positive values, resulting in two
   * deposit folios. In this case, the hotelReservationOhipOutPort.saveCharges method is called
   * twice, sequentially, for each deposit folio. When the sum of all charges from a reservation is
   * not equal to zero, the hotelReservationOhipOutPort.saveCharges method is called once on the
   * existing deposit folios
   *
   * @param nonCancelledDepositFolios DepositFoliosResponse
   */
  private void saveOperaCharges(DepositFoliosResponse nonCancelledDepositFolios) {
    DepositFoliosResponse depositFoliosToUpdate = new DepositFoliosResponse();
    List<DepositFolio> folios = new ArrayList<>();

    Map<String, List<DepositFolioCharge>> dfMap = getDeposiFoliosMap(nonCancelledDepositFolios);

    for (Map.Entry<String, List<DepositFolioCharge>> entry : dfMap.entrySet()) {
      String reservationId = entry.getKey();
      var depositFolioCharges = entry.getValue();

      var sum = sumOfCharges(depositFolioCharges);

      var deposit = nonCancelledDepositFolios.getDepositFolios().stream()
          .filter(dp -> reservationId.equals(dp.getReservationId())).findFirst();

      if (deposit.isPresent()) {

        if (sum == 0) {

          Map<Boolean, List<DepositFolioCharge>> chargesMap = depositFolioCharges.stream()
              .collect(Collectors.partitioningBy(
                  a -> a.getCurrencyAmount().getAmount().doubleValue() > 0));
          saveDepositFolioWithCharges(deposit.get(), chargesMap.get(false));
          saveDepositFolioWithCharges(deposit.get(), chargesMap.get(true));
        } else {
          folios.add(deposit.get());
        }
      }
    }

    if (!folios.isEmpty()) {
      depositFoliosToUpdate.setDepositFolios(folios);
      hotelReservationOhipOutPort.saveCharges(depositFoliosToUpdate);
    }
  }

  /**
   * Computes the sum of all charges.
   *
   * @param depositFolioCharges charges
   * @return the sum of all charges
   */
  private double sumOfCharges(List<DepositFolioCharge> depositFolioCharges) {
    return depositFolioCharges.stream().map(charge -> charge.getCurrencyAmount().getAmount())
        .reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
  }

  /**
   * Build a map where the key is the reservation ID and the value is a list of DepositFolioCharge
   * from a DepositFoliosResponse.
   *
   * @param nonCancelledDepositFolios DepositFoliosResponse
   * @return map
   */
  private Map<String, List<DepositFolioCharge>> getDeposiFoliosMap(
      DepositFoliosResponse nonCancelledDepositFolios) {
    return nonCancelledDepositFolios.getDepositFolios().stream()
        .collect(Collectors.toMap(df -> df.getReservationId(), df -> df.getCharges()));
  }

  /**
   * Sends a DepositFolios to Opera with a specific list of charges.
   *
   * @param depositFolio depositFolios
   * @param charges      list of charges
   */
  private void saveDepositFolioWithCharges(DepositFolio depositFolio,
      List<DepositFolioCharge> charges) {
    DepositFoliosResponse depositFoliosAmounts = new DepositFoliosResponse();
    List<DepositFolio> foliosAmounts = new ArrayList<>();
    DepositFolio dp = new DepositFolio(depositFolio.getHotelId(), depositFolio.getReservationId(),
        depositFolio.getVatRegion(), depositFolio.getPaymentId(),
        depositFolio.getDefaultPaymentMethod(), charges);
    foliosAmounts.add(dp);
    depositFoliosAmounts.setDepositFolios(foliosAmounts);
    hotelReservationOhipOutPort.saveCharges(depositFoliosAmounts);
  }

  private void buildAmendRefundException(String originalBasketReference,
      String tempBasketReference) {
    log.error("Error while trying to call refund endpoint");
    var basketError = new BasketError();
    basketError.setCode(AmendErrorCode.AMEND_REFUND_EXCEPTION.name());
    basketError.setType(BasketError.TypeEnum.AMEND_REFUND);
    basketOutPort.setErroredBooking(originalBasketReference, false, basketError);
    basketOutPort.setErroredBooking(tempBasketReference, false, basketError);
    var exception = new AmendReservationException(ErrorCode.DIGITAL_AMEND_LOGIC_REFUND_EXCEPTION,
        "Refund could not be been processed.");
    ExceptionLogger.log(log, exception);
    throw exception;
  }

  private DepositFoliosResponse getNonCancelledReservations(
      DepositFoliosResponse depositFolios, List<String> originalReservationIdsToDelete,
      List<String> reservationIdsToBeAdded) {
    DepositFoliosResponse depositFoliosToUpdate = new DepositFoliosResponse();
    List<DepositFolio> folios = new ArrayList<>();
    for (DepositFolio df : depositFolios.getDepositFolios()) {
      if (!originalReservationIdsToDelete.contains(df.getReservationId())
          && (!reservationIdsToBeAdded.contains(df.getReservationId()))) {
        folios.add(df);
      }
    }
    depositFoliosToUpdate.setDepositFolios(folios);

    return depositFoliosToUpdate;
  }

  private Map<String, List<BigDecimal>> calculateDfForRemovedItems(
      Map<String, BigDecimal> originalDFByResId,
      String key,
      Map<String, List<BigDecimal>> newDfEntry, Bucket bucket) {
    var bucketValue = originalDFByResId.get(key);
    var dfValue = BigDecimal.ZERO.subtract(originalDFByResId.get(key));
    return addDiffToRefBucketAndAddDf(bucket, bucketValue, List.of(dfValue), key, newDfEntry);
  }

  private Map<String, List<BigDecimal>> calculateDfForExistingItemsWithDiffValues(
      Map<String, BigDecimal> originalDFByResId,
      String key, BigDecimal tempValue,
      Bucket bucket, Map<String, List<BigDecimal>> newDfByResId, String paymentOption,
      PaymentAmount totalPaymentAmount) {
    var valueFromDf = originalDFByResId.get(key);
    // some DF cost less, and we calculate where to put the difference
    if (originalDFByResId.get(key).compareTo(tempValue) > 0) {
      var amountToBucket = valueFromDf.subtract(tempValue);
      var dfFirstValue = BigDecimal.ZERO.subtract(valueFromDf);
      return addDiffToRefBucketAndAddDf(bucket, amountToBucket, List.of(dfFirstValue, tempValue),
          key,
          newDfByResId);
    } else {
      if (originalDFByResId.get(key).compareTo(tempValue) < 0) {
        var amountFromBucket = tempValue.subtract(valueFromDf);
        var dfFirstValue = BigDecimal.ZERO.subtract(valueFromDf);
        return useMoneyFromBucketForSameCharge(bucket, amountFromBucket, key,
            List.of(dfFirstValue),
            newDfByResId, paymentOption, totalPaymentAmount, tempValue);
      }
    }
    return newDfByResId;
  }

  private Map<String, List<BigDecimal>> useBucketsForNewItems(
      Map<String, BigDecimal> tempDFByResId,
      Map<String, BigDecimal> originalDFByResId,
      Bucket bucket, Map<String, List<BigDecimal>> newDfByResId,
      String paymentOption, PaymentAmount totalPaymentAmount) {
    // new DF found in the basket, sorted descending by value
    var newDFKeys = tempDFByResId.entrySet().stream()
        .filter(key -> !originalDFByResId.keySet().contains(key.getKey()))
        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
        .map(Map.Entry::getKey)
        .toList();

    if (!newDFKeys.isEmpty()) {
      for (String key : newDFKeys) {
        if (PaymentOption.PAY_NOW.toString().equalsIgnoreCase(paymentOption)) {
          newDfByResId.putAll(
              useMoneyFromBucketForNewChargesPayNowOption(bucket,
                  tempDFByResId.get(key), key, newDfByResId, totalPaymentAmount));
        } else {
          newDfByResId.putAll(
              useMoneyFromBucketForNewChargesPayOnArrivalOption(bucket,
                  tempDFByResId.get(key), key, newDfByResId));
        }
      }
    }
    return newDfByResId;
  }

  private Map<String, List<BigDecimal>> useMoneyFromBucketForNewChargesPayNowOption(
      Bucket bucket,
      BigDecimal itemValue,
      String key, Map<String,
      List<BigDecimal>> newEntryByResId,
      PaymentAmount totalPaymentAmount) {
    bucket.setMarkAsPayOnArrival(Boolean.FALSE);
    if (bucket.getRefBucket().compareTo(BigDecimal.ZERO) > 0) {
      bucket.setRefBucket(bucket.getRefBucket().subtract(itemValue));
      if (bucket.getRefBucket().compareTo(BigDecimal.ZERO) < 0) {
        totalPaymentAmount.setNewTotalPaymentAmt(
            totalPaymentAmount.getNewTotalPaymentAmt().subtract(bucket.getRefBucket()));
        bucket.setRefBucket(BigDecimal.ZERO);
      }
    } else {
      totalPaymentAmount.setNewTotalPaymentAmt(
          totalPaymentAmount.getNewTotalPaymentAmt().add(itemValue));
    }
    newEntryByResId.putAll(
        addNewEntryInDf(key, List.of(itemValue), newEntryByResId));
    return newEntryByResId;
  }

  private Map<String, List<BigDecimal>> addDiffToRefBucketAndAddDf(
      Bucket bucket,
      BigDecimal bucketAmount, List<BigDecimal> dfValue,
      String key,
      Map<String, List<BigDecimal>> newDfByResId) {
    bucket.setRefBucket(bucket.getRefBucket().add(bucketAmount));
    return addNewEntryInDf(key, dfValue, newDfByResId);
  }

  private Map<String, List<BigDecimal>> addNewEntryInDf(
      String key, List<BigDecimal> dfValue,
      Map<String, List<BigDecimal>> newDfByResId) {
    newDfByResId.put(key, dfValue);
    return newDfByResId;
  }

  private Map<String, List<BigDecimal>> useMoneyFromBucketForNewChargesPayOnArrivalOption(
      Bucket bucket,
      BigDecimal itemValue,
      String key,
      Map<String, List<BigDecimal>> newEntryByResId) {
    if (bucket.getRefBucket().compareTo(BigDecimal.ZERO) > 0) {
      var bucketBeforeSubtraction = bucket.getRefBucket();
      bucket.setRefBucket(bucket.getRefBucket().subtract(itemValue));
      if (bucket.getRefBucket().compareTo(BigDecimal.ZERO) < 0) {
        bucket.setMarkAsPayOnArrival(Boolean.TRUE);
      }
      newEntryByResId.putAll(
          addNewEntryInDf(key, List.of(itemValue.min(bucketBeforeSubtraction)), newEntryByResId));
    } else {
      bucket.setMarkAsPayOnArrival(Boolean.TRUE);
    }
    return newEntryByResId;
  }

  private Map<String, List<BigDecimal>> useMoneyFromBucketForSameCharge(
      Bucket bucket,
      BigDecimal valueFromBucket,
      String key, List<BigDecimal> dfValues,
      Map<String, List<BigDecimal>> newEntryByResId,
      String paymentOption,
      PaymentAmount totalPaymentAmount,
      BigDecimal newItemValue) {
    var dfValuesList = new ArrayList<>(dfValues);

    if (bucket.getRefBucket().compareTo(BigDecimal.ZERO) > 0) {
      var bucketBeforeSubtraction = bucket.getRefBucket();
      bucket.setRefBucket(bucket.getRefBucket().subtract(valueFromBucket));
      if (bucket.getRefBucket().compareTo(BigDecimal.ZERO) < 0) {
        if (PaymentOption.PAY_NOW.toString().equalsIgnoreCase(paymentOption)) {
          bucket.setMarkAsPayOnArrival(Boolean.FALSE);
          totalPaymentAmount.setNewTotalPaymentAmt(
              totalPaymentAmount.getNewTotalPaymentAmt().subtract(bucket.getRefBucket()));
          bucket.setRefBucket(BigDecimal.ZERO);
          dfValuesList.add(newItemValue);
          newEntryByResId.putAll(addNewEntryInDf(key, dfValuesList, newEntryByResId));
        } else {
          bucket.setMarkAsPayOnArrival(Boolean.TRUE);
          var dfValueUsedFromBucket = valueFromBucket.min(bucketBeforeSubtraction);
          dfValuesList.add(dfValueUsedFromBucket);
          newEntryByResId.putAll(addNewEntryInDf(key, dfValuesList, newEntryByResId));
        }
      }
    } else {
      if (PaymentOption.PAY_NOW.toString().equalsIgnoreCase(paymentOption)) {
        bucket.setMarkAsPayOnArrival(Boolean.FALSE);
        dfValuesList.add(newItemValue);
        totalPaymentAmount.setNewTotalPaymentAmt(
            totalPaymentAmount.getNewTotalPaymentAmt().add(newItemValue));
        newEntryByResId.putAll(addNewEntryInDf(key, dfValuesList, newEntryByResId));
      } else {
        bucket.setMarkAsPayOnArrival(Boolean.TRUE);
      }
    }
    return newEntryByResId;
  }

  private RefundRequest generateRefundRequest(
      ReservationByBasketRefResponse reservations, BigDecimal amountToRefund) {
    var refundRequest = new RefundRequest();
    refundRequest.setHotelCode(reservations.getHotelId());
    refundRequest.setRefundType(RefundTypeEnum.PARTIAL);

    var amount = new Amount();
    amount.setCurrency(reservations.getCurrencyCode());
    amount.setMinorUnits((amountToRefund.setScale(2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100)));

    var clientCard = reservations.getReservationByIdList().get(0).getPaymentCard();
    var card = new Card();
    card.setToken(clientCard.getToken());
    card.setExpiryMonth(Optional.ofNullable(clientCard.getExpirationDate()).isPresent()
        ? String.valueOf(clientCard.getExpirationDate().getMonth()) : "");
    card.setExpiryYear(Optional.ofNullable(clientCard.getExpirationDate()).isPresent()
        ? String.valueOf(clientCard.getExpirationDate().getYear()) : "");

    var refund = new Refund();
    refund.setReason(ReasonEnum.AMEND);
    refund.setAmount(amount);
    refund.setCard(card);
    refund.setType(TypeEnum.CARD);

    refundRequest.setRefund(refund);

    var businessSite = new BusinessSite();
    businessSite.setType("HOTEL");
    businessSite.setIdentifier(reservations.getHotelId());

    var booking = new Booking();
    booking.setChannel("WEB");
    booking.setJourney("REFUND");
    booking.setType("NOW");
    booking.setBusinessSite(businessSite);

    refundRequest.setBooking(booking);

    return refundRequest;
  }

  /**
   * Create an inner class as BigDecimal and boolean are immutable objects, and we need to declare
   * them as fields into an object in order to be able to change their values.
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  static class Bucket {

    BigDecimal refBucket;
    Boolean markAsPayOnArrival;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  static class RefundAmount {

    BigDecimal newTotalRefundAmt;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  static class PaymentAmount {

    BigDecimal newTotalPaymentAmt;
  }
}
