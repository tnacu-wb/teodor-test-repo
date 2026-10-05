package uk.co.whitbread.payments.infrastructure.repository.paymentmethods.model.out;

import java.util.List;
import lombok.Data;

@Data
public class PaymentMethodDto {
  private String name;
  private String type;
  private String subType;
  private boolean enabled;
  private List<PaymentOptionDto> paymentOptions;
}
