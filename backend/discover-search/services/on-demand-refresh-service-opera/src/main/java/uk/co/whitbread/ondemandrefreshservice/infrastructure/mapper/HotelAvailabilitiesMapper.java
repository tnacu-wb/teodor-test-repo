package uk.co.whitbread.ondemandrefreshservice.infrastructure.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RateCategory;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RatePlanEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RestrictionType;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RoomEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions.HotelAvailabilitiesMapperException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.HotelInventory;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.InventoryCount;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.RoomTypeInventory;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.ActualTimeSpan;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RateRestrictionResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RestrictionControl;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RestrictionSets;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RestrictionStatus;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.DailyRates;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.RatePlanEntityDto;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.RatePlanSchedule;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.RatePlanScheduleDetail;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.enums.CurrencyCode;

@Slf4j
@Component
public final class HotelAvailabilitiesMapper {

  private static final String OPERA = "OPERA";

  private static final int CTA_LOS_VAL = 999;

  private static final int CL_LOS_VAL = 666;

  private static final String UNDERSCORE = "_";

  private static final String LOS_RESTRICTION_APPLIED_LOGGER = "Los restrictions is applicable for roomType:{} and ratePlanCode:{} and ratePlanCategory:{}";


  private HotelAvailabilitiesMapper(){
  }

  public static Optional<HotelEntity> mapToHotelAvailabilitiesEntities(final String hotelCode, final LocalDate availableDate, final HotelInventory hotelInventory, final Map<String,DailyRates> dailyRatesMap, final RateRestrictionResponse rateRestrictionRsp){
    log.debug("Map HotelEntity for hotel: {}, available-date: {}", hotelCode, availableDate);
    final List<RoomEntity> rooms = new ArrayList<>();
    List<RatePlanEntity> rates = new ArrayList<>();
    //Map to Hotel Entity
    final Optional<HotelEntity> hotel = mapToHotelEntity(hotelCode, availableDate);
    if(hotel.isPresent() && null != hotelInventory) {
      //for each roomType get inventory
      for (final RoomTypeInventory roomInventory : hotelInventory.getRoomTypeInventories()) {
        final String roomType = roomInventory.getCode();
        final Optional<InventoryCount> inventoryCountOpt = roomInventory.getInventoryCounts().stream()
            .filter(availDateFilter(availableDate)).findFirst();
        //Map room Entity
        if(inventoryCountOpt.isPresent()) {
          final InventoryCount inventoryCount = inventoryCountOpt.get();
          //Map to Room Entity
          final Optional<RoomEntity> room = mapToRoomEntity(hotel.get(), roomType, availableDate,
              inventoryCount.getAvailableCount());
          if (room.isPresent()) {
            //Map to RatePlan entity for the given room type. there will be roughly 9 rateplan codes for each room type
            rates = mapToRateEntities(
                availableDate, dailyRatesMap, hotel.get(), roomType, inventoryCount,
                rateRestrictionRsp);
            room.get().setRates(rates);
            rooms.add(room.get());
          }
        }
      }
      hotel.get().setRooms(rooms);
      hotel.get().setRates(rates);
    }
    return hotel;
  }

  private static List<RatePlanEntity> mapToRateEntities(final LocalDate availableDate,
      final Map<String, DailyRates> dailyRatesMap, final HotelEntity hotel, final String roomType,
      final InventoryCount inventoryCount, final RateRestrictionResponse rateRestrictionRsp) {
    log.trace("Executing getRatePlanEntities()......");
    final List<RatePlanEntity> rates = new ArrayList<>();
    //0. Check if daily rates are available
    if(dailyRatesMap.entrySet().isEmpty()){
        log.debug("No daily rates returned for date:{}, roomType:{}",availableDate, roomType);
        return rates;
    }
    //1. Get the currency code
    final String currencyCode = retrieveCurrencyCode(dailyRatesMap);
    log.trace("Currency Code: {}, for hotel: {}",currencyCode, hotel.getHotelCode());
    //2. Create RatePlanEntity for a given roomtype & available date
    for(final Map.Entry<String,DailyRates> ratePlanCodeEntry : dailyRatesMap.entrySet()){
      final String ratePlanCode = ratePlanCodeEntry.getKey();
      log.trace("Mapping to RatePlanEntity for rate-plan code:{}, room-type:{}, available-date:{}", ratePlanCode, roomType, availableDate);
      final DailyRates dailyRates = ratePlanCodeEntry.getValue();
      final List<RatePlanSchedule> ratePlanSchedules = dailyRates.getRatePlanScheduleList().getRatePlanSchedule();
      //filter daily rate for matching roomtype & available date
      final Optional<RatePlanSchedule> ratePlanSchedule = ratePlanSchedules.stream().filter(ratePlanFilter(
          availableDate,roomType)).findFirst();
      if(ratePlanSchedule.isPresent()) {
        final RatePlanScheduleDetail ratePlanSchDetail = ratePlanSchedule.get().getRatePlanScheduleDetail();
        //3. Calculate los restrictions
        int minLos = 0;
        int maxLos = 0;
        final Optional<RestrictionSets> restrictionSetOpt = retrieveRatePlanRestriction(rateRestrictionRsp, availableDate, roomType, ratePlanCode);
        if(restrictionSetOpt.isPresent()) {
          final RestrictionSets restrictionSet = restrictionSetOpt.get();
          log.debug("Los restrictions:{} available for date:{}",restrictionSet, availableDate);
          if (RestrictionType.CTA_LOS.los().equalsIgnoreCase(restrictionSet.getRestrictionStatus().getCode())) {
            minLos = CTA_LOS_VAL;
          } else if(RestrictionType.CL_LOS.los().equalsIgnoreCase(restrictionSet.getRestrictionStatus().getCode())) {
            minLos = CL_LOS_VAL;
          }else {
            minLos = getRestrictionStatusVal(restrictionSet.getRestrictionStatus(),
                    RestrictionType.MIN_LOS);
          }
          maxLos = getRestrictionStatusVal(restrictionSet.getRestrictionStatus(),
                  RestrictionType.MAX_LOS);
        }
         final RatePlanEntityDto ratePlanEntityDto = RatePlanEntityDto.builder()
                .hotelEntity(hotel)
                .roomType(roomType)
                .rateCode(ratePlanCode)
                .currencyCode(currencyCode)
                .isAvailable(inventoryCount.isAvailable())
                .minLos(minLos)
                .maxLos(maxLos)
                .ratePlanScheduleDetail(ratePlanSchDetail)
                .build();
        //Map to RatePlan entity
        final Optional<RatePlanEntity> ratePlanEntity = mapToRateEntity(ratePlanEntityDto);
        if(ratePlanEntity.isPresent()) {
          rates.add(ratePlanEntity.get());
        }
      }
    }
    return rates;
  }

  private static Optional<RestrictionSets> retrieveRatePlanRestriction(final RateRestrictionResponse rateRestrictionRsp, final LocalDate availableDate, final String roomType, final String ratePlanCode){
    if (null != rateRestrictionRsp && null != rateRestrictionRsp.getRestrictionsByDateRange()) {
      //1. Retrieve los restriction for the given "available date"
      final Set<RestrictionSets> restrictionSets = rateRestrictionRsp.getRestrictionsByDateRange()
              .getRestrictionsByDateRange().getRestrictionSets().stream()
              .filter(availDateBetweenFilter(availableDate)).collect(Collectors.toSet());
      //2. Check if the los restriction is applicable for the given roomType & rateplancode
      if (CollectionUtils.isNotEmpty(restrictionSets)) {
        for (RestrictionSets restrictionSet : restrictionSets) {
          if (isRestrictionApplicable(restrictionSet.getRestrictionControl(), roomType, ratePlanCode)) {
            return Optional.of(restrictionSet);
          }
        }
      }
    }
    return Optional.empty();
  }

  private static String retrieveCurrencyCode(final Map<String, DailyRates> dailyRatesMap) throws HotelAvailabilitiesMapperException {
    //1. Get the currency code
    final DailyRates dailyRt = dailyRatesMap.values().stream().filter(dailyRate -> {
      log.trace("Retrieve currency code for hotel : {}", dailyRate.getRatePlanScheduleList().getHotelId());
      return StringUtils.isNotEmpty(dailyRate.getRatePlanMasterInfo().getCurrencyCode());
    }).findFirst().orElseThrow(() -> new HotelAvailabilitiesMapperException("Missing currency code from Opera Hotel Availability Response"));
    return dailyRt.getRatePlanMasterInfo().getCurrencyCode();
  }

  private static Optional<HotelEntity> mapToHotelEntity(final String hotelCode, final LocalDate availableDate){
    log.trace("Executing mapToHotelEntity().....");
    if(availableDate != null && StringUtils.isNotEmpty(hotelCode)) {
      final String hotelId = new StringBuilder(hotelCode)
          .append(UNDERSCORE).append(OPERA)
          .append(UNDERSCORE).append(availableDate).toString();
      final HotelEntity hotel = HotelEntity.builder()
          .id(hotelId).hotelCode(hotelCode).pmsSource(OPERA).date(availableDate).build();
      return Optional.of(hotel);
    }
    return Optional.empty();
  }

  private static Optional<RoomEntity> mapToRoomEntity(final HotelEntity hotel, final String roomType, final LocalDate availableDate, final int availableRoomQty){
    log.trace("Executing mapToRoomEntity().....");
    if(hotel != null && availableDate != null && !StringUtils.isEmpty(roomType)) {
      log.trace("Mapping to RoomEntity for room type:{}, hotel:{}, available date:{} with quantity:{}", roomType, hotel.getHotelCode(), availableDate, availableRoomQty);
      final String roomId = new StringBuilder(roomType)
          .append(UNDERSCORE).append(hotel.getHotelCode())
          .append(UNDERSCORE).append(OPERA)
          .append(UNDERSCORE).append(availableDate).toString();
      final RoomEntity room = RoomEntity.builder()
          .id(roomId).roomType(roomType)
          .quantity(availableRoomQty).build();
      return Optional.ofNullable(room);
    }
    return Optional.empty();
  }

  private static Optional<RatePlanEntity> mapToRateEntity(final RatePlanEntityDto ratePlanEntityDto){
    log.trace("Executing mapToRateEntity().....");
    if(null != ratePlanEntityDto.getHotelEntity() && null != ratePlanEntityDto.getRatePlanScheduleDetail()) {
      final String rateCategory = RateCategory.valueOf(ratePlanEntityDto.getRateCode()).rateCategory();
      final String rateId = new StringBuilder(ratePlanEntityDto.getRateCode())
          .append(UNDERSCORE).append(ratePlanEntityDto.getHotelEntity().getHotelCode())
          .append(UNDERSCORE).append(ratePlanEntityDto.getRoomType())
          .append(UNDERSCORE).append(OPERA)
          .append(UNDERSCORE).append(LocalDate.parse(ratePlanEntityDto.getRatePlanScheduleDetail().getStart())).toString();
      final RatePlanEntity ratePlanEntity = RatePlanEntity.builder()
          .id(rateId)
          .rateCode(ratePlanEntityDto.getRateCode())
          .rateClassification(rateCategory)
          .amount(BigDecimal.valueOf(ratePlanEntityDto.getRatePlanScheduleDetail().getRateAmounts().getOnePersonRate()))
          .availability(ratePlanEntityDto.isAvailable())
          .currency(CurrencyCode.valueOf(ratePlanEntityDto.getCurrencyCode()).currencyCode())
          .minNights(ratePlanEntityDto.getMinLos())
          .maxNights(ratePlanEntityDto.getMaxLos())
          .build();
      return Optional.of(ratePlanEntity);
    }
    return Optional.empty();
  }

  private static Predicate<InventoryCount> availDateFilter(final LocalDate availableDate){
    return inventoryCount -> isAvailableDate(LocalDate.parse(inventoryCount.getStartDate()),availableDate);
  }

  private static boolean isAvailableDate(final LocalDate date1, final LocalDate date2){
    return (date1.compareTo(date2) == 0);
  }

  private static Predicate<RatePlanSchedule> ratePlanFilter(final LocalDate availableDate, final String roomType){
    return ratePlanSchedule -> {
      final String type = ratePlanSchedule.getRatePlanScheduleDetail().getRoomTypeList().stream().findFirst().get();
      final LocalDate startDate = LocalDate.parse(ratePlanSchedule.getRatePlanScheduleDetail().getStart());
      boolean isRateAvailForRoomType = false;
      if(type.equalsIgnoreCase(roomType) && isAvailableDate(startDate,availableDate)){
        isRateAvailForRoomType = true;
      }
      return isRateAvailForRoomType;
    };
  }

  private static boolean isRestrictionApplicable(final RestrictionControl restrictionCtrl, final String inputRoomType, final String inputRatePlanCode) {
    boolean isRestrictionApplicable = false;
    if (restrictionCtrl != null) {
      final String roomType = restrictionCtrl.getRoomType();
      final String ratePlanCode = restrictionCtrl.getRatePlanCode();
      final String ratePlanCategoryFrmOpera = restrictionCtrl.getRatePlanCategory();
      if (isRoomTypeAndRateCodeAvailable(roomType, ratePlanCode, inputRoomType, inputRatePlanCode) ||
              isOnlyRoomTypeAvailable(roomType, ratePlanCode, inputRoomType) ||
              isOnlyRateCodeAvailable(roomType, ratePlanCode, inputRatePlanCode) ||
              isOnlyRateCategoryAvailable(roomType, ratePlanCategoryFrmOpera, inputRatePlanCode) ||
          isRoomTypeRateCodeRatePlanCategoryNotAvailable(roomType, ratePlanCode, ratePlanCategoryFrmOpera)) {
          log.trace(LOS_RESTRICTION_APPLIED_LOGGER, inputRoomType, inputRatePlanCode, ratePlanCategoryFrmOpera);
          isRestrictionApplicable = true;
      }
    }
    return isRestrictionApplicable;
  }

  private static boolean isRoomTypeAndRateCodeAvailable(final String roomType, final String ratePlanCode,
                                                        final String inputRoomType, final String inputRatePlanCode){
    // Apply restriction only for given room type and rate code
    return (StringUtils.isNotBlank(roomType) && StringUtils.isNotBlank(ratePlanCode)) &&
            (roomType.equalsIgnoreCase(inputRoomType) && ratePlanCode.equalsIgnoreCase(inputRatePlanCode));
  }

  private static boolean isOnlyRoomTypeAvailable(final String roomType, final String ratePlanCode,
                                                 final String inputRoomType){
    // Apply restriction only for given room type and for all available rate code
    return (StringUtils.isNotBlank(roomType) && StringUtils.isBlank(ratePlanCode)) &&
            (roomType.equalsIgnoreCase(inputRoomType));
  }

  private static boolean isOnlyRateCodeAvailable(final String roomType, final String ratePlanCode,
                                                 final String inputRatePlanCode){
    // Apply restriction only for given rate code and for all available room type
    return (StringUtils.isBlank(roomType) && StringUtils.isNotBlank(ratePlanCode)) &&
            (ratePlanCode.equalsIgnoreCase(inputRatePlanCode));
  }

  private static boolean isOnlyRateCategoryAvailable(final String roomType, final String restrictedRtPlanCategory, final String inputRatePlanCode){
    // Apply restriction only for all rate plan codes for a given rate plan category and for all available room types
    final String ratePlanCategory = RateCategory.valueOf(inputRatePlanCode).rateCategory();
    return (StringUtils.isBlank(roomType)
        && StringUtils.isNotBlank(restrictedRtPlanCategory)
        && StringUtils.isNotBlank(ratePlanCategory)
        && restrictedRtPlanCategory.equalsIgnoreCase(ratePlanCategory));
  }

  private static boolean isRoomTypeRateCodeRatePlanCategoryNotAvailable(final String roomType, final String ratePlanCode, final String ratePlanCategory){
    // Apply restriction for all rate code and all available room type
    return StringUtils.isBlank(roomType) && StringUtils.isBlank(ratePlanCode) && StringUtils.isBlank(ratePlanCategory);
  }


  private static int getRestrictionStatusVal(final RestrictionStatus restrictionStatus, final RestrictionType restrictionType){
      final String restrictionStatusCode = restrictionStatus.getCode();
      int restrictionStatusVal = 0;
      if (StringUtils.isNotBlank(restrictionStatusCode) && restrictionStatusCode.equalsIgnoreCase(restrictionType.los())) {
        restrictionStatusVal = restrictionStatus.getUnit();
    }
    return restrictionStatusVal;
  }

  private static Predicate<RestrictionSets> availDateBetweenFilter(final LocalDate availableDate){
    return restrictionSet -> isAvailableDateBetween(availableDate, restrictionSet.getActualTimeSpan());
  }

  private static boolean isAvailableDateBetween(final LocalDate availableDate, final ActualTimeSpan actualTimeSpan){
    final LocalDate startDate = LocalDate.parse(actualTimeSpan.getStartDate());
    final LocalDate endDate = LocalDate.parse(actualTimeSpan.getEndDate());
    return availableDate.compareTo(startDate) >= 0 && availableDate.compareTo(endDate) <= 0;
  }


}
