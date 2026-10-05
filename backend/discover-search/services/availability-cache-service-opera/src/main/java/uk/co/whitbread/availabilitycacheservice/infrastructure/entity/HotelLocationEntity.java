package uk.co.whitbread.availabilitycacheservice.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "hotel_location", schema = "avail_cache")
@IdClass(HotelLocationId.class)
public class HotelLocationEntity {

  @Id
  @Column(name = "hotel_code", length = 6, nullable = false)
  private String hotelCode;

  @Id
  @Column(name = "place_id", nullable = false)
  private String placeId;

}
