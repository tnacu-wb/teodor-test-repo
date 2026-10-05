package uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.utils;

import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardTypeType;
import uk.co.whitbread.ohip.domain.model.eckoh.in.CardScheme;
import uk.co.whitbread.ohip.domain.model.eckoh.in.EckohCardType;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;

public class EckohToOperaCardTypeHelper {

  private EckohToOperaCardTypeHelper() {
  }

  public enum PaymentMethod {
    VA,
    AX,
    MC,
    BU,
    DI
  }

  private static final Map<Pair<String, EckohCardType>, Pair<String, PaymentMethod>> MAPPING =
      Map.of(
          Pair.of(CardScheme.VISA.name(), EckohCardType.ANY), Pair.of(CardTypeType.VA.name(), PaymentMethod.VA),
          Pair.of(CardScheme.ELECTRON.name(), EckohCardType.ANY), Pair.of(CardTypeType.VA.name(), PaymentMethod.VA),
          Pair.of(CardScheme.AMEX.name(), EckohCardType.ANY), Pair.of(CardTypeType.AX.name(), PaymentMethod.AX),
          Pair.of(CardScheme.MASTERCARD.name(), EckohCardType.ANY),
          Pair.of(CardTypeType.MC.name(), PaymentMethod.MC),
          Pair.of(CardScheme.DINERS.name(), EckohCardType.ANY), Pair.of(CardTypeType.DC.name(), PaymentMethod.DI));

  public static Pair<String, PaymentMethod> getOperaCardTypeFrom(String eckohCardScheme,
      EckohCardType eckohCardType,  UnleashWrapper<FeatureFlag> unleashWrapper) {
    var scheme = eckohCardScheme.toUpperCase();
    return Optional.ofNullable(MAPPING.get(Pair.of(scheme, eckohCardType)))
        .or(() -> Optional.ofNullable(MAPPING.get(Pair.of(scheme, EckohCardType.ANY))))
        .orElse(getCardType(unleashWrapper));
  }

  private static Pair<String, PaymentMethod> getCardType(UnleashWrapper<FeatureFlag> unleashWrapper) {
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getPibaBooking())) {
      return Pair.of("BU", PaymentMethod.BU);
    } else {
      return Pair.of(CardTypeType.ZZ.getValue(), PaymentMethod.BU);
    }
  }

}
