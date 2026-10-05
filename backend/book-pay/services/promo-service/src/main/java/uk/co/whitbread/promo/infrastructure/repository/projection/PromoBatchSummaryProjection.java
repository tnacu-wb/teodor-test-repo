package uk.co.whitbread.promo.infrastructure.repository.projection;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface PromoBatchSummaryProjection {

  UUID getBatchId();

  String getCampaignName();

  String getOperaPromoCode();

  String getPrefix();

  Integer getBatchCount();

  String getStatus();

  String getS3Key();

  String getNotes();

  Boolean getDownloaded();

  String getPassword();

  String getRequestedBy();

  OffsetDateTime getCreatedAt();

  Boolean getIsMultiple();

  Integer getMaxRedemptionLimit();
}
