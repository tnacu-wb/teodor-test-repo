package uk.co.whitbread.availabilitycacheservice.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "location_price", schema = "avail_cache")
@IdClass(LocationPriceId.class)
public class LocationPriceEntity {

  @Id
  @Column(name = "place_id", nullable = false)
  private String placeId;

  @Id
  @Column(name = "avail_date", nullable = false)
  private LocalDate date;

  @Column(name = "currency", length = 1)
  private String currency;

  @Column(name = "price")
  private BigDecimal price;

}
