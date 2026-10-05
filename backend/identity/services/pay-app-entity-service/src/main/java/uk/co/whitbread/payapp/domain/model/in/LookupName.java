package uk.co.whitbread.payapp.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LookupName {
  TITLE("title"),
  TRADING_STYLE("tradingStyle"),
  ESTIMATED_MONTHLY_SPEND("estimatedMonthlySpend"),
  HOTEL_BRAND_POLICIES("hotelBrandPolicies"),
  HOTEL_BOOKING_ROLES("hotelBookingRoles"),
  ISO_COUNTRY_CODES("isoCountryCodes"),
  TIME_TRADING("timeTrading"),
  INDUSTRY_SECTOR("industrySector"),
  REGISTRATION_QUESTIONS("registrationQuestions"),
  NUMBER_OF_EMPLOYEES("numberOfEmployees"),
  CANCELLATION_REASON("cancellationReason");

  final String name;
}
