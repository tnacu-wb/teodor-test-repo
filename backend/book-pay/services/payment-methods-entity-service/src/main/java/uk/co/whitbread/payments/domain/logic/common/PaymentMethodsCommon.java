package uk.co.whitbread.payments.domain.logic.common;

import static java.util.stream.IntStream.range;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections.CollectionUtils;
import uk.co.whitbread.payments.domain.model.out.AcceptedCardType;
import uk.co.whitbread.payments.domain.model.out.Country;
import uk.co.whitbread.payments.domain.model.out.HotelInfo;
import uk.co.whitbread.payments.domain.model.out.PaymentMethods;
import uk.co.whitbread.payments.domain.model.out.PaymentOption;

public class PaymentMethodsCommon {

  private static final String PAYMENT_PROVIDER_3CP = "3CP";
  private static final String PAYMENT_PROVIDER_DATATRANS = "Datatrans";

  public List<AcceptedCardType> acceptedCards(final HotelInfo hotelInfo) {
    return hotelInfo.getPaymentProviders() == null ? null : hotelInfo.getPaymentProviders()
        .stream()
        .filter(provider -> PAYMENT_PROVIDER_3CP.equals(provider.getProviderId()))
        .map(paymentProvider -> paymentProvider.getPaymentMethods()
            .stream()
            .map(acceptedCard -> {
              final var acceptedCardType = new AcceptedCardType();
              acceptedCardType.setType(acceptedCard.getCode());
              acceptedCardType.setLogoSrc(acceptedCard.getSchemeLogo());
              acceptedCardType.setName(acceptedCard.getName());
              return acceptedCardType;
            })
            .toList())
        .flatMap(Collection::stream)
        .toList();
  }

  public List<AcceptedCardType> datatransAcceptedCards(final HotelInfo hotelInfo) {
    if (hotelInfo.getPaymentProviders() == null) {
      return List.of();
    }
    return hotelInfo.getPaymentProviders().stream()
        .filter(provider -> PAYMENT_PROVIDER_DATATRANS.equals(provider.getProviderId()))
        .flatMap(provider -> provider.getPaymentMethods().stream())
        .map(card -> {
          final var acceptedCardType = new AcceptedCardType();
          acceptedCardType.setType(card.getCode());
          acceptedCardType.setLogoSrc(card.getSchemeLogo());
          acceptedCardType.setName(card.getName());
          return acceptedCardType;
        })
        .toList();
  }

  public List<AcceptedCardType> acceptedCardTypes(final HotelInfo hotelInfo) {
    return hotelInfo.getAcceptedCreditCards()
        .stream()
        .map(acceptedCard -> {
          final var acceptedCardType = new AcceptedCardType();
          acceptedCardType.setType(acceptedCard.getCode());
          acceptedCardType.setLogoSrc(acceptedCard.getSchemeLogo());
          acceptedCardType.setName(acceptedCard.getName());
          return acceptedCardType;
        }).toList();
  }

  public Country hotelCountry(final HotelInfo hotelInfo) {
    return switch (hotelInfo.getAddress().getCountry()) {
      case "Ireland" -> Country.IE;
      case "Germany", "Deutschland" -> Country.DE;
      default -> Country.GB;
    };
  }

  public PaymentMethods updatePaymentMethodOrder(final PaymentMethods paymentMethods) {
    var paymentMethodList = paymentMethods.getPaymentMethods();
    Optional.ofNullable(paymentMethodList).orElse(Collections.emptyList())
        .forEach(paymentMethod -> {
          boolean isAllPaymentOptionDisabled = Optional.ofNullable(paymentMethod.getPaymentOptions())
              .orElse(Collections.emptyList())
              .stream()
              .noneMatch(PaymentOption::isEnabled);
          if (isAllPaymentOptionDisabled) {
            paymentMethod.setEnabled(false);
          }
        });
    range(0, CollectionUtils.isNotEmpty(paymentMethodList) ? paymentMethodList.size() : 0)
        .forEach(i -> Optional.ofNullable(paymentMethodList).ifPresent(list -> list.get(i).setOrder(i + 1)));
    return paymentMethods;
  }
}
