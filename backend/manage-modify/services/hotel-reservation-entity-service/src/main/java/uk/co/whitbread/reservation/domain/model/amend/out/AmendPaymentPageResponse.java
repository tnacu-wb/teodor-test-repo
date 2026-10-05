package uk.co.whitbread.reservation.domain.model.amend.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.payment.out.A2cDetails;
import uk.co.whitbread.reservation.domain.model.payment.out.Allowances;
import uk.co.whitbread.reservation.domain.model.payment.out.AvailablePaymentType;
import uk.co.whitbread.reservation.domain.model.payment.out.BillingAddress;
import uk.co.whitbread.reservation.domain.model.payment.out.CardHolderName;
import uk.co.whitbread.reservation.domain.model.payment.out.CardPresent;
import uk.co.whitbread.reservation.domain.model.payment.out.CompanyRef;
import uk.co.whitbread.reservation.domain.model.payment.out.Eckoh;
import uk.co.whitbread.reservation.domain.model.payment.out.EmailPreference;
import uk.co.whitbread.reservation.domain.model.payment.out.PaymentOption;
import uk.co.whitbread.reservation.domain.model.payment.out.PaymentType;
import uk.co.whitbread.reservation.domain.model.payment.out.PreAuthCharges;
import uk.co.whitbread.reservation.domain.model.payment.out.PurchaseOrder;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmendPaymentPageResponse {

  private BigDecimal discount;
  private PaymentOption paymentOption;
  private PaymentType paymentType;
  private CardPresent cardPresent;
  private Eckoh eckoh;
  private CardHolderName cardHolderName;
  private BillingAddress billingAddress;
  private EmailPreference emailPreference;
  private Allowances allowances;
  private PurchaseOrder purchaseOrder;
  private CompanyRef companyRef;
  private A2cDetails a2cDetails;
  private PreAuthCharges preAuthCharges;
  private String hotelName;
  private String hotelCode;
  private String brand;
  private String companyId;
  private AvailablePaymentType availablePaymentType;
}
