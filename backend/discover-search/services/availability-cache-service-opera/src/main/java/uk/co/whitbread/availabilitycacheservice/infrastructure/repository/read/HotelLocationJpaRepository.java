package uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelLocationEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelLocationId;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.LocationBestPrice;

@Repository
public interface HotelLocationJpaRepository extends JpaRepository<HotelLocationEntity, HotelLocationId> {


  @Query("select distinct  "
      + " new uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.LocationBestPrice"
      + " (hl.hotelCode, hl.placeId, min(lp.price), lp.currency) "
      + " from HotelLocationEntity hl"
      + " inner join LocationPriceEntity lp on lp.placeId=hl.placeId"
      + " where lp.date between :arrival and :departure "
      + " and lp.price <= :price "
      + " group by hl.hotelCode , hl.placeId , lp.currency "
      + " having min(lp.price) is not null and min(lp.price) > 0")
  List<LocationBestPrice> findBestPricedHotelForLocations(
      @Param("arrival") LocalDate arrival,
      @Param("departure") LocalDate departure,
      @Param("price") BigDecimal price);

}
