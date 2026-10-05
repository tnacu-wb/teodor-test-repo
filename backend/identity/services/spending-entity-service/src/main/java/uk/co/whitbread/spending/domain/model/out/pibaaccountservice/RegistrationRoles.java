package uk.co.whitbread.spending.domain.model.out.pibaaccountservice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RegistrationRoles {

  ACCOUNT_HOLDER("AccountHolder"),
  CARD_HOLDER("CardHolder"),
  FINANCE_USER_CARD_HOLDER("AccountCardHolderWithReportsAndInvoices"),
  FINANCE_USER("ReportsAndInvoices"),
  ACCOUNT_CARD_HOLDER("AccountCardHolder"),
  COST_CENTRE_USER("CostCentreHolder");
  private final String registrationRole;
}