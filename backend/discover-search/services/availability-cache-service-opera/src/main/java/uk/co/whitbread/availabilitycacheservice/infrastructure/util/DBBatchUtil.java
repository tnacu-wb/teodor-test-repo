package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DBBatchUtil {

  public static final String FETCH_OPERA_HOTELS_QUERY =
      " select "
          + " hotel.id as hotelId,hotel.hotel_code as hotelCode,hotel.avail_date as availableDate,hotel.pms_source "
          + "as pmsSource, "
          + " rate.id as rateId,rate.avail as availability,rate.rate_category as rateClassification,rate.rate_code "
          + "as rateCode,rate.amount as amount,rate.amount_with_city_tax as amountWithCityTax,"
          + "rate.currency as currency,"
          + " rate.min_nights as minNights,rate.max_nights as maxNights, "
          + " room.id as roomId,room.quantity as quantity,room.type as roomType"
          + " from avail_cache.hotel_ac hotel "
          + " inner join avail_cache.room room on room.hotel_id=hotel.id "
          + " inner join avail_cache.rate rate on rate.hotel_id=hotel.id "
          + " where hotel.hotel_code in :operaHotelCodes "
          + " and hotel.avail_date >=:arrival_date"
          + " and hotel.avail_date <:departure_date"
          + " and hotel.pms_source = 'OPERA' "
          + " and room.type in :operaRoomTypes and room.quantity != 0 and room.id = rate.room_id";

  public static final String FETCH_HOTEL_AVAILABILITIES_FOR_MULTI_ROOMS =
      "select new uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability"
          + ".HotelAvailabilitiesResultSet("
          + " hotel.id as hotelId, hotel.hotelCode as hotelCode, hotel.date as availableDate, hotel.pmsSource as "
          + "pmsSource, "
          + " rate.id as rateId, rate.availability, rate.rateClassification, rate.rateCode, rate.amount, rate"
          + ".currency, rate.minNights, "
          + " rate.maxNights, room.id as roomId, room.quantity, room.roomType, rate.amountWithCityTax) "
          + " from HotelEntity hotel "
          + " inner join RatePlanEntity rate on rate.hotel=hotel "
          //Inner Join to pick up hotels which has at least one Rate available
          + " inner join RoomEntity room on room.hotel=hotel "
          + " where hotel.hotelCode in :hotelCodes "
          + " and hotel.date >=:arrival_date"
          + " and hotel.date <:departure_date"
          + " and hotel.pmsSource = 'BART' " //Condition to pick only BART hotels
          + " and room.roomType in :roomTypes and rate.availability=true and room.quantity > 0";

  public static final String FETCH_OPERA_HOTELS_FOR_GQT_QUERY =
      " select "
          + " hotel.id as hotelId, hotel.hotel_code as hotelCode, hotel.avail_date as availableDate,"
          + " hotel.pms_source as pmsSource, rate.id as rateId, rate.avail as availability,"
          + " rate.rate_category as rateClassification, rate.rate_code as rateCode,"
          + " rate.amount as amount, rate.currency as currency, rate.min_nights as minNights, "
          + " rate.max_nights as maxNights, room.id as roomId, room.quantity as quantity, "
          + " room.type as roomType, rate.amount_with_city_tax as amountWithCityTax "
          + " from avail_cache.hotel_ac hotel "
          + " inner join avail_cache.room room on room.hotel_id=hotel.id "
          + " inner join avail_cache.rate rate on rate.hotel_id=hotel.id "
          + " where hotel.hotel_code in :operaHotelCodes "
          + " and hotel.avail_date >=:arrival_date"
          + " and hotel.avail_date <:departure_date"
          + " and hotel.pms_source = 'OPERA' "
          + " and room.quantity != 0 and room.id = rate.room_id";

  public static final String FETCH_PRICE_FINDER_MIN_RATE =
      "select "
          + " hotel.hotel_code AS hotelCode, "
          + " rate.amount as minimumRate, "
          + " rate.amount_with_city_tax as minimumRateWithCityTax, "
          + " rate.currency as currency, "
          + " hotel.avail_date AS availableDate, "
          + " rate.rate_code as rateCode, "
          + " rate.rate_category as rateClassification, "
          + " room.type as roomType, "
          + " room.quantity as quantity, "
          + " rate.min_nights as minNights, "
          + " rate.max_nights as maxNights "
          + " from avail_cache.hotel_ac hotel "
          + " inner join avail_cache.room room on room.hotel_id=hotel.id "
          + " inner join avail_cache.rate rate on rate.hotel_id=hotel.id "
          + " where hotel.hotel_code in :operaHotelCodes "
          + " and hotel.avail_date between :arrival_date and :departure_date "
          + " and hotel.pms_source = 'OPERA' "
          + " and room.quantity > 0 "
          + " and room.id = rate.room_id ";

  //TODO:DIST extend it to include min, max
  public static final String FETCH_HOTEL_AVAILABILITIES_FOR_DISTRIBUTION_WITH_RESTRICTION =
      "select new uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability"
          + ".DistributionHotelAvailResultWithRestrictionSet("
          + " hotel.id as hotelId, hotel.hotelCode as hotelCode, hotel.date as availableDate, "
          + " hotel.pmsSource as pmsSource, "
          + " rate.id as rateId, rate.availability, rate.rateClassification, rate.rateCode, rate.amount, "
          + " rate.currency, rate.minNights, rate.maxNights, "
          + " room.id as roomId, room.quantity, room.roomType, rate.amountWithCityTax) "
          + " from HotelEntity hotel "
          + " inner join RatePlanEntity rate on rate.hotel=hotel "
          + " inner join RoomEntity room on room.hotel=hotel "
          + " where hotel.hotelCode in :hotelCodes "
          + " and hotel.date >=:arrival_date"
          + " and hotel.date <:departure_date"
          + " and hotel.pmsSource = 'OPERA' " //Condition to pick only OPERA hotels
          + " and room.roomType in :roomTypes and room.quantity != 0 and rate.room = room"
          + " and (:rateCodes IS NULL or rate.rateCode in :rateCodes)";

  public static final String FETCH_HOTEL_AVAILABILITIES_FOR_DISTRIBUTION =
      "select new uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability"
          + ".DistributionHotelAvailResultSet("
          + " hotel.id as hotelId, hotel.hotelCode as hotelCode, hotel.date as availableDate, hotel.pmsSource as "
          + "pmsSource, "
          + " rate.id as rateId, rate.availability, rate.rateClassification, rate.rateCode, rate.amount, rate"
          + ".currency, "
          + " room.id as roomId, room.quantity, room.roomType) "
          + " from HotelEntity hotel "
          + " inner join RatePlanEntity rate on rate.hotel=hotel "
          + " inner join RoomEntity room on room.hotel=hotel "
          + " where hotel.hotelCode in :hotelCodes "
          + " and hotel.date >=:arrival_date"
          + " and hotel.date <:departure_date"
          + " and hotel.pmsSource = 'OPERA' "  //Condition to pick only OPERA hotels
          + " and room.roomType in :roomTypes and room.quantity != 0 and rate.room = room"
          + " and (:rateCodes IS NULL or rate.rateCode in :rateCodes)";

  public static final String FETCH_HOTEL_AVAILABILITIES_FOR_DATA_VALIDATION =
      "select new uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability"
          + ".HotelAvailabilitiesResultSet("
          + " hotel.id as hotelId, hotel.hotelCode as hotelCode, hotel.date as availableDate, hotel.pmsSource as "
          + "pmsSource, "
          + " rate.id as rateId, rate.availability, rate.rateClassification, rate.rateCode, rate.amount, rate"
          + ".currency, rate.minNights as minNights, rate.maxNights as maxNights, room.id as roomId, room.quantity, "
          + "room.roomType, rate.amountWithCityTax) "
          + " from HotelEntity hotel "
          + " inner join RatePlanEntity rate on rate.hotel=hotel "
          + " inner join RoomEntity room on room.hotel=hotel "
          + " where hotel.hotelCode in :hotelCodes"
          + " and hotel.date >=:availFromDate"
          + " and hotel.date <=:availUntilDate"
          + " and hotel.pmsSource = 'OPERA' "  //Condition to pick only OPERA hotels
          + " and room.roomType in :roomTypes and rate.rateCode in :rateCodes and rate.room = room";

  private DBBatchUtil() {

  }

}
