package uk.co.whitbread.promo.infrastructure.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.promo.domain.model.promobatch.Channel;
import uk.co.whitbread.promo.domain.model.promobatch.Platform;
import uk.co.whitbread.promo.domain.model.promobatch.Region;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "batch_eligibility", schema = "promotions")
@IdClass(BatchEligibilityId.class)
public class BatchEligibilityEntity {

  @Id
  @Column(name = "batch_id", nullable = false)
  private UUID batchId;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(name = "region", nullable = false)
  private Region region;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(name = "channel", nullable = false)
  private Channel channel;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(name = "platform", nullable = false)
  private Platform platform;
}
