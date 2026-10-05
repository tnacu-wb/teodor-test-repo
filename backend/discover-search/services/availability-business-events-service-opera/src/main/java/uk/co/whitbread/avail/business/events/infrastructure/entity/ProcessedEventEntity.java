package uk.co.whitbread.avail.business.events.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigInteger;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "processed_events", schema = "avail_cache")
public class ProcessedEventEntity {

  @Id
  @Column(name = "app_key", length = 64)
  private String appKey;

  @Column(name = "received_on", nullable = false)
  private LocalDateTime receivedOn;

  @Column(name = "event_offset", nullable = false)
  private BigInteger offset;

}
