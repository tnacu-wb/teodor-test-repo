package uk.co.whitbread.avail.business.events.infrastructure.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventHeader;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "hotel_ac", schema = "avail_cache")
public class HotelEntity {

  @Id
  @Column(length = 64)
  private String id;

  @Column(name = "hotel_code", length = 6, nullable = false)
  private String hotelCode;

  @Column(name = "avail_date", nullable = false)
  private LocalDate date;

  @Column(name = "pms_source", nullable = false)
  private String pmsSource;

  @Column(name = "time_updated")
  private Instant timeUpdated;

  @ToString.Exclude
  @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
  private List<RatePlanEntity> rates;

  @ToString.Exclude
  @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
  private List<RoomEntity> rooms;

  @Transient
  private EventHeader eventHeader;
}
