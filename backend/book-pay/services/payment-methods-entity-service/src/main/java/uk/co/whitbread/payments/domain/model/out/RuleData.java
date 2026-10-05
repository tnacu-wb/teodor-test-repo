package uk.co.whitbread.payments.domain.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payments.domain.model.in.UserType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleData {
  private Country country;
  private List<AcceptedCardType> acceptedCardType;
  private List<AcceptedCardType> threecAcceptedCardType;
  private Reservation reservation;
  private List<PaymentMethod> paymentMethods;
  private boolean allowIndividualCards;
  private boolean allowCentralCreditCard;
  private UserType userType;
  private BookingAllowances bookingAllowances;
  private String hotelBrand;
  private String channelId;
  private String hotelId;
  private PaypalRequest paypalRequest;
  private PaymentMethod defaultPaymentMethod;
  private boolean isPibaEuroEnabled;
  private String clientChannel;
  private List<PaymentMethodsConfiguration> paymentMethodsConfiguration;
  private boolean changePaymentBIC;
  private boolean isCcuiDeHotelPaymentsWithin72hEnabled;
  private boolean isCcuiUkHotelPaymentsWithin72hEnabled;
  private boolean isPoaForHubEnabled;
  private boolean isDataTransEnabled;
  private List<AcceptedCardType> datatransAcceptedCardType;
  private DataTransConfig dataTransProperties;

  /**
   * Returns the correct accepted card source for the given CardOption name.
   * When Datatrans is enabled and the option is not in the exclusion list, returns the Datatrans
   * card list. Otherwise returns the 3CP list.
   * This ensures all rules automatically use the right source when the
   * datatrans.not-supported-card-options config changes — no code change required.
   */
  public List<AcceptedCardType> resolveCardSource(String cardOptionName) {
    if (isDataTransEnabled
        && dataTransProperties != null
        && dataTransProperties.isDatatransSupported(cardOptionName)) {
      return datatransAcceptedCardType != null ? datatransAcceptedCardType : List.of();
    }
    return threecAcceptedCardType != null ? threecAcceptedCardType : List.of();
  }

  /**
   * Returns the PaymentProviderType that corresponds to the card source resolved for the given
   * CardOption name. Mirrors resolveCardSource() so callers can set paymentProvider in one call.
   */
  public PaymentProviderType resolvePaymentProvider(String cardOptionName) {
    if (isDataTransEnabled
        && dataTransProperties != null
        && dataTransProperties.isDatatransSupported(cardOptionName)) {
      return PaymentProviderType.DATATRANS;
    }
    return PaymentProviderType.PLANET_3CP;
  }
}
