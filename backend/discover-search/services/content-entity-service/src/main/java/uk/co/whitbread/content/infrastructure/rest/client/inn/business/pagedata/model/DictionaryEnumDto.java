package uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.model;

import lombok.Getter;

@Getter
public enum DictionaryEnumDto {
  LAYOUT_DICTIONARY("layoutEndpoint"),
  CARD_MANAGEMENT_DICTIONARY("cardManagementEndpoint"),
  COMMON_ICONS_DICTIONARY("commonIconsEndpoint"),
  USER_MANAGEMENT_DICTIONARY("userManagementEndpoint"),
  PROFILE_MANAGEMENT_DICTIONARY("profileManagementEndpoint"),
  COMPANY_MANAGEMENT_DICTIONARY("companyManagementEndpoint"),
  HOMEPAGE_DICTIONARY("homepageEndpoint"),
  SPENDING_REPORTING_DICTIONARY("spendingReportingEndpoint"),
  PAY_APPLICATION_DICTIONARY("payApplicationEndpoint"),
  AUTH_DICTIONARY("authEndpoint"),
  NOTIFICATIONS_DICTIONARY("notificationsEndpoint"),
  CONTACT_US_DICTIONARY("innbContactUsEndpoint");

  final String field;

  DictionaryEnumDto(final String field) {
    this.field = field;
  }
}
