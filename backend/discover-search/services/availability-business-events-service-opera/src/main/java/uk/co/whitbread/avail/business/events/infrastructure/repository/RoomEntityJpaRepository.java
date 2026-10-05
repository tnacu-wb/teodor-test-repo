package uk.co.whitbread.avail.business.events.infrastructure.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RoomEntity;

@Repository
public interface RoomEntityJpaRepository extends JpaRepository<RoomEntity, String> {

  @Query(value =
      "select distinct \"type\" from avail_cache.room r where hotel_id = :hotelId",
      nativeQuery = true)
  List<String> getHotelTypesByHotelCode(@Param("hotelId") String hotelId);

}
