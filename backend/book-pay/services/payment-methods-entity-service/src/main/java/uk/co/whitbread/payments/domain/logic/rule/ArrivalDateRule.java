package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_NOW;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_ON_ARRIVAL;

import java.time.LocalDate;
import java.util.Collection;
import java.util.function.Predicate;
import uk.co.whitbread.payments.domain.model.out.CardOption;
import uk.co.whitbread.payments.domain.model.out.Country;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.RuleData;

public class ArrivalDateRule implements Rule {

  public static final int PERIOD_FOR_PAY_NOW = 3;

  public static final String PIBA_UK_NAME = "PIBA UK";

  @Override
  public RuleData calculate(RuleData request) {
    if (request.getCountry().equals(Country.DE)) {
      //process payment methods for Germany
      processPaymentsForDeHotel(request);
    } else if (request.getCountry().equals(Country.GB)
        || request.getCountry().equals(Country.IE)) {
      //process payment methods for UK
      processPaymentsForUkHotel(request);
    }
    return request;
  }

  private void processPaymentsForUkHotel(RuleData request) {
    if (!request.isCcuiUkHotelPaymentsWithin72hEnabled()) {
      enable72hrsFraudCheck(request);
    } else if (request.isCcuiUkHotelPaymentsWithin72hEnabled()) {
      disable72hrsFraudCheck(request);
    }
  }

  private void processPaymentsForDeHotel(RuleData request) {
    if (!request.isCcuiDeHotelPaymentsWithin72hEnabled()) {
      enable72hrsFraudCheck(request);
    } else if (request.isCcuiDeHotelPaymentsWithin72hEnabled()) {
      disable72hrsFraudCheck(request);
    }
  }

  private void disable72hrsFraudCheck(RuleData request) {

    final LocalDate currentDate = LocalDate.now();
    final LocalDate arrivalDate = request.getReservation().getArrivalDate();

    var paymentMethods = request.getPaymentMethods();
    final var hotelPaymentPolicies = request.getReservation().getHotelPaymentPolicies();

    final var isPnAndIsArrivalDateBefore72Hrs = hotelPaymentPolicies.contains(PAY_NOW)
        && currentDate.plusDays(PERIOD_FOR_PAY_NOW).isAfter(arrivalDate);

    final var isPoaAndIsArrivalDateBefore72Hrs = hotelPaymentPolicies.contains(PAY_ON_ARRIVAL)
        && currentDate.plusDays(PERIOD_FOR_PAY_NOW).isAfter(arrivalDate);

    final Predicate<PaymentMethod> poaDEHotel = paymentMethod ->
        !paymentMethod.getName().equalsIgnoreCase(PIBA_UK_NAME);

    final Predicate<PaymentMethod> poaUKHotel = paymentMethod ->
        (paymentMethod.getType().equalsIgnoreCase(CardOption.NEW_CARD.name()))
        || (paymentMethod.getType()
        .equalsIgnoreCase(CardOption.NEW_PIBA.name()) && paymentMethod.getName().equalsIgnoreCase(PIBA_UK_NAME));

    final Predicate<PaymentMethod> poaIEHotel = paymentMethod -> paymentMethod.getType()
        .equalsIgnoreCase(CardOption.NEW_CARD.name());

    //Disable fraud check for Pay Now
    if (isPnAndIsArrivalDateBefore72Hrs) {
      paymentMethods.stream()
          .filter(paymentMethod -> paymentMethod.getType().equalsIgnoreCase("NEW_CARD"))
          .map(PaymentMethod::getPaymentOptions)
          .flatMap(Collection::stream)
          .filter(paymentOption -> paymentOption.getType().equalsIgnoreCase(PAY_NOW.name()))
          .forEach(paymentOption -> paymentOption.setEnabled(true));
    }

    //Disable fraud check for Pay On Arrival
    if (isPoaAndIsArrivalDateBefore72Hrs
        && request.getCountry().equals(Country.DE)) {
      paymentMethods.stream()
          .filter(poaDEHotel)
          .map(PaymentMethod::getPaymentOptions)
          .flatMap(Collection::stream)
          .filter(paymentOption -> paymentOption.getType().equalsIgnoreCase(PAY_ON_ARRIVAL.name()))
          .forEach(paymentOption -> paymentOption.setEnabled(true));
    } else if (isPoaAndIsArrivalDateBefore72Hrs
        && request.getCountry().equals(Country.GB)) {
      paymentMethods.stream()
          .filter(poaUKHotel)
          .map(PaymentMethod::getPaymentOptions)
          .flatMap(Collection::stream)
          .filter(paymentOption -> paymentOption.getType().equalsIgnoreCase(PAY_ON_ARRIVAL.name()))
          .forEach(paymentOption -> paymentOption.setEnabled(true));
    } else if (isPoaAndIsArrivalDateBefore72Hrs
        && request.getCountry().equals(Country.IE)) {
      paymentMethods.stream()
          .filter(poaIEHotel)
          .map(PaymentMethod::getPaymentOptions)
          .flatMap(Collection::stream)
          .filter(paymentOption -> paymentOption.getType().equalsIgnoreCase(PAY_ON_ARRIVAL.name()))
          .forEach(paymentOption -> paymentOption.setEnabled(true));
    }
  }

  private void enable72hrsFraudCheck(RuleData request) {
    final LocalDate currentDate = LocalDate.now();
    final LocalDate arrivalDate = request.getReservation().getArrivalDate();

    var paymentMethods = request.getPaymentMethods();
    final var hotelPaymentPolicies = request.getReservation().getHotelPaymentPolicies();

    var isPnAndIsArrivalAfterLimit = hotelPaymentPolicies.size() == 1
        && hotelPaymentPolicies.contains(PAY_NOW)
        && currentDate.plusDays(PERIOD_FOR_PAY_NOW).isAfter(arrivalDate);

    var isPoaAndIsArrivalAfterLimit =
        hotelPaymentPolicies.contains(PAY_ON_ARRIVAL)
            && currentDate.plusDays(PERIOD_FOR_PAY_NOW).isAfter(arrivalDate);

    if (isPoaAndIsArrivalAfterLimit) {
      paymentMethods.stream()
          .map(PaymentMethod::getPaymentOptions)
          .flatMap(Collection::stream)
          .forEach(paymentOption -> paymentOption.setEnabled(
              PAY_ON_ARRIVAL.name().equals(paymentOption.getType()))
          );
    } else if (isPnAndIsArrivalAfterLimit) {
      paymentMethods.stream()
          .map(PaymentMethod::getPaymentOptions)
          .flatMap(Collection::stream)
          .forEach(paymentOption -> paymentOption.setEnabled(false)
          );
    }
  }
}
