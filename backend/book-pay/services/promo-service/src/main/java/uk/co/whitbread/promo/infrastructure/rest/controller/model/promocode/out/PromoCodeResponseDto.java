package uk.co.whitbread.promo.infrastructure.rest.controller.model.promocode.out;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.promo.domain.model.promocode.out.PromoCodeStatus;

@Data
@Builder
@AllArgsConstructor
public class PromoCodeResponseDto {

  @Id
  @Column(name = "code", nullable = false, updatable = false)
  private String code;

  private UUID batchId;

  @Enumerated(EnumType.STRING)
  private PromoCodeStatus status;

  private OffsetDateTime redeemedAt;
  private String bookingReference;

  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
  private LocalDate expiredAt;
}
