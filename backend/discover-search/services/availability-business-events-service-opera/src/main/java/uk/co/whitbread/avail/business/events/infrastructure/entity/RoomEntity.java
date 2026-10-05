package uk.co.whitbread.avail.business.events.infrastructure.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "room", schema = "avail_cache")
public class RoomEntity {

  @Id
  @Column(name = "id", length = 64)
  private String id;

  @Column(name = "quantity")
  private int quantity;

  @Column(name = "type", length = 6)
  @EqualsAndHashCode.Include
  private String roomType;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id", referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_room_hotel"))
  private HotelEntity hotel;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "room", cascade = CascadeType.ALL)
  private List<RatePlanEntity> rates;
}
