package uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read;

import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.DBBatchUtil.FETCH_HOTEL_AVAILABILITIES_FOR_MULTI_ROOMS;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.BestPricedHotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.CalendarBestPriceHotel;

@Repository
public interface HotelJpaRepository extends JpaRepository<HotelEntity, String> {

  @Query(FETCH_HOTEL_AVAILABILITIES_FOR_MULTI_ROOMS)
  List<HotelAvailabilitiesResultSet> findByHotelCodesAndMultiRoomType(
      @Param("hotelCodes") List<String> hotelCodes,
      @Param("arrival_date") LocalDate arrivalDate,
      @Param("departure_date") LocalDate departureDate,
      @Param("roomTypes") Set<String> roomTypeList);

  @Query("select  "
      + " new uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.BestPricedHotel"
      + " (hotel.hotelCode, min(rate.amount), rate.currency, rate.amountWithCityTax) "
      + " from HotelEntity hotel"
      + " inner join RatePlanEntity rate on rate.hotel=hotel"
      + " inner join RoomEntity room on room.hotel=hotel"
      + " where hotel.hotelCode in :hotelCodes"
      + " and hotel.date between :arrival and :departure "
      + " and lower(room.roomType) = LOWER(:roomType) "
      + " group by hotel.hotelCode, rate.currency, rate.amountWithCityTax "
      + " having min(rate.amount) is not null and min(rate.amount) > 0")
  List<BestPricedHotel> findBestPriceForHotel(@Param("hotelCodes") List<String> hotelCodes,
      @Param("arrival") LocalDate arrival,
      @Param("departure") LocalDate departure,
      @Param("roomType") String roomType);

  @Query("select  "
      + " new uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.CalendarBestPriceHotel"
      + " (hotel.hotelCode, hotel.date, min(rate.amount), rate.amountWithCityTax, rate.currency) "
      + " from HotelEntity hotel"
      + " inner join RatePlanEntity rate on rate.hotel=hotel"
      + " inner join RoomEntity room on room.hotel=hotel"
      + " where hotel.hotelCode = :hotelCode"
      + " and hotel.date between :arrival and :departure "
      + " and lower(room.roomType) = LOWER(:roomType) "
      + " group by hotel.hotelCode, hotel.date, rate.amountWithCityTax, rate.currency "
      + " having min(rate.amount) is not null and min(rate.amount) > 0")
  List<CalendarBestPriceHotel> findBestPricesForGivenHotelCode(
      @Param("hotelCode") String hotelCode,
      @Param("arrival") LocalDate arrival,
      @Param("departure") LocalDate departure,
      @Param("roomType") String roomType);

}
