package uk.co.whitbread.ondemandrefreshservice.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "hotel_migration_status", schema = "avail_cache")
public class HotelMigrationStatusEntity {

  @Id
  @Column(name = "hotel_code", length = 6, nullable = false)
  private String hotelCode;

  @Column(name = "pms_source", length = 32, nullable = false)
  private String pmsSource;

  @Column(name = "updated_on")
  private LocalDateTime updatedOn;

  @Column(name = "on_sale")
  private boolean onSale;

}
