package uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read;

import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.DBBatchUtil.FETCH_HOTEL_AVAILABILITIES_FOR_DATA_VALIDATION;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.DBBatchUtil.FETCH_HOTEL_AVAILABILITIES_FOR_DISTRIBUTION;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.DBBatchUtil.FETCH_HOTEL_AVAILABILITIES_FOR_DISTRIBUTION_WITH_RESTRICTION;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.DistributionHotelAvailResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.DistributionHotelAvailResultWithRestrictionSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;


@Repository
public interface HotelAvailabilitiesJpaRepository extends JpaRepository<HotelEntity, String> {

  @Query(nativeQuery = true)
  List<HotelAvailabilitiesResultSet> findAvailabilitiesForOpera(
      @Param("operaHotelCodes") List<String> operaHotelCodes,
      @Param("arrival_date") LocalDate arrivalDate,
      @Param("departure_date") LocalDate departureDate,
      @Param("operaRoomTypes") Set<String> operaRoomTypeList);

  @Query(FETCH_HOTEL_AVAILABILITIES_FOR_DISTRIBUTION_WITH_RESTRICTION)
  List<DistributionHotelAvailResultWithRestrictionSet> findAvailabilitiesForDistributionWithRestriction(
      @Param("hotelCodes") List<String> hotelCodes,
      @Param("arrival_date") LocalDate arrivalDate,
      @Param("departure_date") LocalDate departureDate,
      @Param("roomTypes") Set<String> roomTypeList,
      @Param("rateCodes") Set<String> rateCodes);

  @Query(FETCH_HOTEL_AVAILABILITIES_FOR_DISTRIBUTION)
  List<DistributionHotelAvailResultSet> findAvailabilitiesForDistribution(
      @Param("hotelCodes") List<String> hotelCodes,
      @Param("arrival_date") LocalDate arrivalDate,
      @Param("departure_date") LocalDate departureDate,
      @Param("roomTypes") Set<String> roomTypeList,
      @Param("rateCodes") Set<String> rateCodes);

  @Query(FETCH_HOTEL_AVAILABILITIES_FOR_DATA_VALIDATION)
  List<HotelAvailabilitiesResultSet> findDataValidationHotelAvailabilities(
      @Param("hotelCodes") Set<String> hotelCodes,
      @Param("availFromDate") LocalDate availabilityFromDate,
      @Param("availUntilDate") LocalDate availabilityUntilDate,
      @Param("roomTypes") Set<String> roomTypeList,
      @Param("rateCodes") Set<String> rateCodes);
}
