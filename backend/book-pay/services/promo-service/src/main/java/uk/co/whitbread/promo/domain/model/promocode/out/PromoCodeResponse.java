package uk.co.whitbread.promo.domain.model.promocode.out;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromoCodeResponse {

  private String code;
  private UUID batchId;
  private PromoCodeStatus status;
  private String bookingReference;
  private OffsetDateTime redeemedAt;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
}