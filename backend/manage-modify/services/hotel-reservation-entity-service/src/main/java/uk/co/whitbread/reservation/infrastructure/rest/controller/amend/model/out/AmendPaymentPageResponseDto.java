package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmendPaymentPageResponseDto {

  private BigDecimal discount;
  private PaymentOptionDto paymentOption;
  private PaymentTypeDto paymentType;
  private CardPresentDto cardPresent;
  private EckohDto eckoh;
  private CardHolderNameDto cardHolderName;
  private BillingAddressDto billingAddress;
  private EmailPreferenceDto emailPreference;
  private AllowancesDto allowances;
  private PurchaseOrderDto purchaseOrder;
  private CompanyRefDto companyRef;
  private A2cDetailsDto a2cDetails;
  private PreAuthChargesDto preAuthCharges;
  private String hotelName;
  private String hotelCode;
  private String brand;
  private String companyId;
  private AvailablePaymentTypeDto availablePaymentType;
}
