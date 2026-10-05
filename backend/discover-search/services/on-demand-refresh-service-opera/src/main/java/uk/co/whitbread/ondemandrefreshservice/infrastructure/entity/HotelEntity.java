package uk.co.whitbread.ondemandrefreshservice.infrastructure.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Builder
@Getter
@Setter
@Table(name = "hotel_ac", schema = "avail_cache")
public class HotelEntity implements Persistable<String>, Serializable {

  @Id
  @Column(length = 64)
  private String id;

  @Column(name = "hotel_code", length = 6, nullable = false)
  private String hotelCode;

  @Column(name = "avail_date", nullable = false)
  private LocalDate date;

  @Column(name = "pms_source", length = 16, nullable = false)
  private String pmsSource;

  @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RatePlanEntity> rates;

  @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RoomEntity> rooms;

  @Builder.Default
  private @Transient boolean isNew = true;

  @Override
  public boolean isNew() {
    return isNew;
  }

  @PrePersist
  @PostLoad
  void markNotNew() {
    this.isNew = false;
  }

}
