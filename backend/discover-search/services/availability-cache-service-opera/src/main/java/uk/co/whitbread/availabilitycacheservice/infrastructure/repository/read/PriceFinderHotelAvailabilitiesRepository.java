package uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder.PriceFinderResultSet;

@Repository
public interface PriceFinderHotelAvailabilitiesRepository extends JpaRepository<HotelEntity, String> {

  @Query(nativeQuery = true)
  List<PriceFinderResultSet> findAvailabilitiesForPriceFinder(
      @Param("operaHotelCodes") List<String> operaHotelCodes,
      @Param("arrival_date") LocalDate arrivalDate,
      @Param("departure_date") LocalDate departureDate);
}
