package uk.co.whitbread.hotel.card.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AddressCorrespondenceEnum {
  COMPANY_REGISTERED_ADDRESS("CompanyRegisteredAddress"),
  COMPANY_CORRESPONDENCE_ADDRESS("CompanyCorrespondenceAddress"),
  CARDHOLDER_ALTERNATIVE_ADDRESS("CardholderAlternativeAddress");

  private final String value;

}
