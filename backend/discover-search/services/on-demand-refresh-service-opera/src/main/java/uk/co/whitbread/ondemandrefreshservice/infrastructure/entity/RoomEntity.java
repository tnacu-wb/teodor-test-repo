package uk.co.whitbread.ondemandrefreshservice.infrastructure.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
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
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Getter
@Setter
@Table(name = "room", schema = "avail_cache")
public class RoomEntity implements Persistable<String>, Serializable {

  @Id
  @Column(name = "id", length = 64)
  @EqualsAndHashCode.Include
  private String id;

  @Column(name = "quantity")
  private int quantity;

  @Column(name = "type", length = 6)
  @EqualsAndHashCode.Include
  private String roomType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_room_hotel"))
  private HotelEntity hotel;

  @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
  private List<RatePlanEntity> rates;

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
