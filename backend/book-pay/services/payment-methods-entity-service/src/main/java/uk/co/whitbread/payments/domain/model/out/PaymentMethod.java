package uk.co.whitbread.payments.domain.model.out;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Generated;

@Data
@Generated
public class PaymentMethod {
  private String name;
  private String type;
  private String subType;
  private int order;
  private String logoSrc;
  private List<AcceptedCardType> acceptedCardTypes;
  private Card card;
  private List<PaymentOption> paymentOptions;
  private boolean enabled;
  private boolean cnpPreSelected = false; //TODO Set from bart -> clarify
  private boolean cnpOptionAvailable = false;
  private List<String> reasons = new ArrayList<>();
  private BookingAllowances bookingAllowances;
  private String clientToken;
  private String clientId;
  private PaymentProviderType paymentProvider;

  public void disablePaymentMethod(String reason) {
    setEnabled(false);
    paymentOptions.forEach(paymentOption -> paymentOption.setEnabled(false));
    if (reasons.isEmpty() || !reasons.contains(reason)) {
      reasons.add(reason);
      setReasons(reasons);
    }
  }
}