package uk.co.whitbread.promo.infrastructure.repository.model;

import java.util.Arrays;

public enum PromoBatchSortField {
  BATCH_ID("batchId"),
  OPERA_PROMO_CODE("operaPromoCode"),
  BATCH_COUNT("batchCount"),
  PREFIX("prefix"),
  STATUS("status"),
  CREATED_AT("createdAt"),
  UPDATED_AT("updatedAt"),
  EXPIRY_DATE("expiryDate"),
  CAMPAIGN_NAME("campaignName"),
  REQUESTED_BY("requestedBy"),
  IS_MULTIPLE("isMultiple"),
  MAX_REDEMPTION_LIMIT("maxRedemptionLimit");

  private final String field;

  PromoBatchSortField(String field) {
    this.field = field;
  }

  public static boolean isValid(String value) {
    return Arrays.stream(values())
        .anyMatch(f -> f.field.equalsIgnoreCase(value));
  }
}
