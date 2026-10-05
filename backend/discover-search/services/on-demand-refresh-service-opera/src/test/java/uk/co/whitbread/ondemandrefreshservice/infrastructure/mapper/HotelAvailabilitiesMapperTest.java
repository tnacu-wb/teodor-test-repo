package uk.co.whitbread.ondemandrefreshservice.infrastructure.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.RateCategory;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions.HotelAvailabilitiesMapperException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.HotelInventory;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.inventory.HotelInventoryResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RateRestrictionResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.DailyRates;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.RatePlanMasterInfo;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static uk.co.whitbread.ondemandrefreshservice.utils.MockDataReader.generateMockRsp;
import static uk.co.whitbread.ondemandrefreshservice.utils.MockDataReader.readFromInputStream;

@Slf4j
public class HotelAvailabilitiesMapperTest {

  private static final ObjectMapper mapper = new ObjectMapper();

  private static final String hotelCode = "TKINPT";

  @Test
  public void testMapToHotelAvailabilitiesEntities_WithNoLengthOfStayRestrictions(){

    final InputStream flexDailyRatesInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/flexrate_daily_rates.json");
    final InputStream nonFlexDailyRatesInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/nonflexrate_daily_rates.json");
    Map<String, DailyRates> dailyRatesMap = new HashMap<>();
    final InputStream emptyLosRestrictionsStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/los_restriction_empty_rsp.json");
    try {
      final HotelInventoryResponse hotelInventoryRsp = generateMockRsp("/mock_data/inventory.json", HotelInventoryResponse.class);
      final HotelInventory hotelInventory = hotelInventoryRsp.getHotelInventories().stream().findFirst().get();
      final LocalDate availableDate = LocalDate.parse(hotelInventory.getRoomTypeInventories().stream().findFirst().get().getInventoryCounts().stream().findFirst().get().getStartDate());

      final DailyRates flexDailyRates = mapper.readValue(readFromInputStream(flexDailyRatesInputStream), DailyRates.class);
      dailyRatesMap.put(RateCategory.FLEXRATE.name(),flexDailyRates);

      final DailyRates nonFlexDailyRates = mapper.readValue(readFromInputStream(nonFlexDailyRatesInputStream), DailyRates.class);
      dailyRatesMap.put(RateCategory.NONFLEX.name(),nonFlexDailyRates);

      final RateRestrictionResponse rateRestrictionRsp = mapper.readValue(readFromInputStream(emptyLosRestrictionsStream), RateRestrictionResponse.class);

      final Optional<HotelEntity> hotelEntity = HotelAvailabilitiesMapper.mapToHotelAvailabilitiesEntities(hotelCode,availableDate,hotelInventory,dailyRatesMap, rateRestrictionRsp);
      assertTrue(hotelEntity.isPresent());
      assertEquals(hotelEntity.get().getDate(),availableDate);
      final int expectedLosValue = 0;
      assertEquals(expectedLosValue, hotelEntity.get().getRates().stream().findFirst().get().getMinNights());
      assertEquals(expectedLosValue, hotelEntity.get().getRates().stream().findFirst().get().getMaxNights());
        assertFalse(hotelEntity.get().getRooms().isEmpty());
    } catch (IOException e) {
      logError(e);
      fail();
    }

  }


  @Test
  public void testMapToHotelAvailabilitiesEntities_WithLengthOfStayRestrictions(){
    final InputStream inventoryInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/inventory.json");
    final InputStream flexDailyRatesInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/flexrate_daily_rates.json");
    final InputStream nonFlexDailyRatesInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/nonflexrate_daily_rates.json");
    Map<String, DailyRates> dailyRatesMap = new HashMap<>();
    final InputStream losRestrictionsStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/los_restriction_rsp.json");
    try {
      final HotelInventoryResponse hotelInventoryRsp = mapper.readValue(readFromInputStream(inventoryInputStream), HotelInventoryResponse.class);
      final HotelInventory hotelInventory = hotelInventoryRsp.getHotelInventories().stream().findFirst().get();
      final LocalDate availableDate = LocalDate.parse(hotelInventory.getRoomTypeInventories().stream().findFirst().get().getInventoryCounts().stream().findFirst().get().getStartDate());

      final DailyRates flexDailyRates = mapper.readValue(readFromInputStream(flexDailyRatesInputStream), DailyRates.class);
      dailyRatesMap.put(RateCategory.FLEXRATE.name(),flexDailyRates);

      final DailyRates nonFlexDailyRates = mapper.readValue(readFromInputStream(nonFlexDailyRatesInputStream), DailyRates.class);
      dailyRatesMap.put(RateCategory.NONFLEX.name(),nonFlexDailyRates);

      final RateRestrictionResponse rateRestrictionRsp = mapper.readValue(readFromInputStream(losRestrictionsStream), RateRestrictionResponse.class);

      final Optional<HotelEntity> hotelEntity = HotelAvailabilitiesMapper.mapToHotelAvailabilitiesEntities(hotelCode,availableDate,hotelInventory,dailyRatesMap, rateRestrictionRsp);
      assertTrue(hotelEntity.isPresent());
      assertEquals(hotelEntity.get().getDate(),availableDate);
        assertFalse(hotelEntity.get().getRooms().isEmpty());
      int expectedLoaValue = 3;
      assertEquals(expectedLoaValue, hotelEntity.get().getRates().stream().findFirst().get().getMinNights());
    } catch (IOException e) {
      logError(e);
      fail();
    }

  }

  @Test
  public void testMapToHotelAvailabilitiesEntities_WithClosedRestrictions(){
    final InputStream inventoryInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/inventory.json");
    final InputStream flexDailyRatesInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/flexrate_daily_rates.json");
    final InputStream nonFlexDailyRatesInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/nonflexrate_daily_rates.json");
    Map<String, DailyRates> dailyRatesMap = new HashMap<>();
    final InputStream losRestrictionsStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/los_restriction_closed_rsp.json");
    try {
      final HotelInventoryResponse hotelInventoryRsp = mapper.readValue(readFromInputStream(inventoryInputStream), HotelInventoryResponse.class);
      final HotelInventory hotelInventory = hotelInventoryRsp.getHotelInventories().stream().findFirst().get();
      final LocalDate availableDate = LocalDate.parse(hotelInventory.getRoomTypeInventories().stream().findFirst().get().getInventoryCounts().stream().findFirst().get().getStartDate());

      final DailyRates flexDailyRates = mapper.readValue(readFromInputStream(flexDailyRatesInputStream), DailyRates.class);
      dailyRatesMap.put(RateCategory.FLEXRATE.name(),flexDailyRates);

      final DailyRates nonFlexDailyRates = mapper.readValue(readFromInputStream(nonFlexDailyRatesInputStream), DailyRates.class);
      dailyRatesMap.put(RateCategory.NONFLEX.name(),nonFlexDailyRates);

      final RateRestrictionResponse rateRestrictionRsp = mapper.readValue(readFromInputStream(losRestrictionsStream), RateRestrictionResponse.class);

      final Optional<HotelEntity> hotelEntity = HotelAvailabilitiesMapper.mapToHotelAvailabilitiesEntities(hotelCode,availableDate,hotelInventory,dailyRatesMap, rateRestrictionRsp);
      assertTrue(hotelEntity.isPresent());
      assertEquals(hotelEntity.get().getDate(),availableDate);
        assertFalse(hotelEntity.get().getRooms().isEmpty());
      int expectedLoaValue = 666;
      assertEquals(expectedLoaValue, hotelEntity.get().getRates().stream().findFirst().get().getMinNights());
    } catch (IOException e) {
      logError(e);
      fail();
    }

  }

  @Test
  public void testMapToHotelAvailabilitiesEntities_Exception() throws IOException {
    final InputStream inventoryInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/inventory.json");
    final InputStream flexDailyRatesInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/flexrate_daily_rates.json");
    final InputStream nonFlexDailyRatesInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/nonflexrate_daily_rates.json");
    Map<String, DailyRates> dailyRatesMap = new HashMap<>();
    final InputStream losRestrictionsStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/los_restriction_rsp.json");
    final HotelInventoryResponse hotelInventoryRsp = mapper.readValue(readFromInputStream(inventoryInputStream), HotelInventoryResponse.class);
    final HotelInventory hotelInventory = hotelInventoryRsp.getHotelInventories().stream().findFirst().get();
    final LocalDate availableDate = LocalDate.parse(hotelInventory.getRoomTypeInventories().stream().findFirst().get().getInventoryCounts().stream().findFirst().get().getStartDate());

    final DailyRates flexDailyRates = mapper.readValue(readFromInputStream(flexDailyRatesInputStream), DailyRates.class);
    final RatePlanMasterInfo ratePlanMasterInfo = new RatePlanMasterInfo();
    ratePlanMasterInfo.setCurrencyCode("");
    flexDailyRates.setRatePlanMasterInfo(ratePlanMasterInfo);
    dailyRatesMap.put(RateCategory.FLEXRATE.name(), flexDailyRates);

    final DailyRates nonFlexDailyRates = mapper.readValue(readFromInputStream(nonFlexDailyRatesInputStream), DailyRates.class);
    nonFlexDailyRates.setRatePlanMasterInfo(ratePlanMasterInfo);
    dailyRatesMap.put(RateCategory.NONFLEX.name(), nonFlexDailyRates);

    final RateRestrictionResponse rateRestrictionRsp = mapper.readValue(readFromInputStream(losRestrictionsStream), RateRestrictionResponse.class);

    assertThrows(HotelAvailabilitiesMapperException.class,
            () -> HotelAvailabilitiesMapper.mapToHotelAvailabilitiesEntities(hotelCode, availableDate, hotelInventory, dailyRatesMap, rateRestrictionRsp));

  }


  @Test
  public void testMapToHotelAvailabilitiesEntities_WithLosOnInvalidRatePlanCategory() {

    final InputStream emerald1DailyRatesInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream(
        "/mock_data/emerald1_daily_rates.json");
    final InputStream employeeDailyRatesInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream(
        "/mock_data/employee_daily_rates.json");
    Map<String, DailyRates> dailyRatesMap = new HashMap<>();
    final InputStream losOnInvalidRatePlanCategory = HotelAvailabilitiesMapperTest.class.getResourceAsStream(
        "/mock_data/los_restriction_invalid_rateplancategory.json");
    try {
      final HotelInventoryResponse hotelInventoryRsp = generateMockRsp(
          "/mock_data/inventory_2023-03-17.json", HotelInventoryResponse.class);
      final HotelInventory hotelInventory = hotelInventoryRsp.getHotelInventories().stream()
          .findFirst().get();
      final LocalDate availableDate = LocalDate.parse(
          hotelInventory.getRoomTypeInventories().stream().findFirst().get().getInventoryCounts()
              .stream().findFirst().get().getStartDate());

      final DailyRates employeeDailyRates = mapper.readValue(
          readFromInputStream(employeeDailyRatesInputStream), DailyRates.class);
      dailyRatesMap.put(RateCategory.EMPLOYEE.name(), employeeDailyRates);

      final DailyRates emerald1DailyRates = mapper.readValue(
          readFromInputStream(emerald1DailyRatesInputStream), DailyRates.class);
      dailyRatesMap.put(RateCategory.EMERALD1.name(), emerald1DailyRates);

      final RateRestrictionResponse rateRestrictionRsp = mapper.readValue(
          readFromInputStream(losOnInvalidRatePlanCategory), RateRestrictionResponse.class);

      final Optional<HotelEntity> hotelEntity = HotelAvailabilitiesMapper.mapToHotelAvailabilitiesEntities(
          hotelCode, availableDate, hotelInventory, dailyRatesMap, rateRestrictionRsp);
      assertTrue(hotelEntity.isPresent());
      assertEquals(hotelEntity.get().getDate(), availableDate);
      final int expectedLosValue = 0;
      assertEquals(expectedLosValue,
          hotelEntity.get().getRates().stream().findFirst().get().getMinNights());
      assertEquals(expectedLosValue,
          hotelEntity.get().getRates().stream().findFirst().get().getMaxNights());
        assertFalse(hotelEntity.get().getRooms().isEmpty());
    } catch (IOException e) {
      logError(e);
      fail();
    }
  }

  @Test
  public void testMapToHotelAvailabilitiesEntities_WithLosOnValidRatePlanCategory() {

    final InputStream emerald1DailyRatesInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream(
        "/mock_data/emerald1_daily_rates.json");
    final InputStream employeeDailyRatesInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream(
        "/mock_data/employee_daily_rates.json");
    final InputStream flexDailyRatesInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/flexrate_daily_rates_2023-03-17.json");
    final InputStream nonFlexDailyRatesInputStream = HotelAvailabilitiesMapperTest.class.getResourceAsStream("/mock_data/nonflexrate_daily_rates_2023-03-17.json");
    Map<String, DailyRates> dailyRatesMap = new HashMap<>();
    final InputStream losOnInvalidRatePlanCategory = HotelAvailabilitiesMapperTest.class.getResourceAsStream(
        "/mock_data/los_restriction_valid_rateplancategory.json");
    try {
      final HotelInventoryResponse hotelInventoryRsp = generateMockRsp(
          "/mock_data/inventory_2023-03-17.json", HotelInventoryResponse.class);
      final HotelInventory hotelInventory = hotelInventoryRsp.getHotelInventories().stream()
          .findFirst().get();
      final LocalDate availableDate = LocalDate.parse(
          hotelInventory.getRoomTypeInventories().stream().findFirst().get().getInventoryCounts()
              .stream().findFirst().get().getStartDate());

      final DailyRates employeeDailyRates = mapper.readValue(
          readFromInputStream(employeeDailyRatesInputStream), DailyRates.class);
      dailyRatesMap.put(RateCategory.EMPLOYEE.name(), employeeDailyRates);

      final DailyRates emerald1DailyRates = mapper.readValue(
          readFromInputStream(emerald1DailyRatesInputStream), DailyRates.class);
      dailyRatesMap.put(RateCategory.EMERALD1.name(), emerald1DailyRates);

      final DailyRates flexDailyRates = mapper.readValue(readFromInputStream(flexDailyRatesInputStream), DailyRates.class);
      final RatePlanMasterInfo ratePlanMasterInfo = new RatePlanMasterInfo();
      ratePlanMasterInfo.setCurrencyCode("");
      flexDailyRates.setRatePlanMasterInfo(ratePlanMasterInfo);
      dailyRatesMap.put(RateCategory.FLEXRATE.name(), flexDailyRates);

      final DailyRates nonFlexDailyRates = mapper.readValue(readFromInputStream(nonFlexDailyRatesInputStream), DailyRates.class);
      nonFlexDailyRates.setRatePlanMasterInfo(ratePlanMasterInfo);
      dailyRatesMap.put(RateCategory.NONFLEX.name(), nonFlexDailyRates);

      final RateRestrictionResponse rateRestrictionRsp = mapper.readValue(
          readFromInputStream(losOnInvalidRatePlanCategory), RateRestrictionResponse.class);

      final Optional<HotelEntity> hotelEntity = HotelAvailabilitiesMapper.mapToHotelAvailabilitiesEntities(
          hotelCode, availableDate, hotelInventory, dailyRatesMap, rateRestrictionRsp);

      assertTrue(hotelEntity.isPresent());
      assertEquals(hotelEntity.get().getDate(), availableDate);
      final int minLosExpectedLosValue = 2;
      final int maxLosExpectedLosValue = 0;
      //Assert for Employee RateCode
      assertEquals(minLosExpectedLosValue,
          hotelEntity.get().getRates().stream().filter(rate -> rate.getRateCode().equalsIgnoreCase(RateCategory.EMPLOYEE.name())).findFirst().get().getMinNights());
      assertEquals(maxLosExpectedLosValue,
          hotelEntity.get().getRates().stream().filter(rate -> rate.getRateCode().equalsIgnoreCase(RateCategory.EMPLOYEE.name())).findFirst().get().getMaxNights());
      //Assert for Emerald1 RateCode
      assertEquals(minLosExpectedLosValue,
          hotelEntity.get().getRates().stream().filter(rate -> rate.getRateCode().equalsIgnoreCase(RateCategory.EMERALD1.name())).findFirst().get().getMinNights());
      assertEquals(maxLosExpectedLosValue,
          hotelEntity.get().getRates().stream().filter(rate -> rate.getRateCode().equalsIgnoreCase(RateCategory.EMERALD1.name())).findFirst().get().getMaxNights());
      //Assert for FlexRate RateCode
      assertEquals(0,
          hotelEntity.get().getRates().stream().filter(rate -> rate.getRateCode().equalsIgnoreCase(RateCategory.FLEXRATE.name())).findFirst().get().getMinNights());
      assertEquals(maxLosExpectedLosValue,
          hotelEntity.get().getRates().stream().filter(rate -> rate.getRateCode().equalsIgnoreCase(RateCategory.FLEXRATE.name())).findFirst().get().getMaxNights());
      //Assert for NonFlexRate RateCode
      assertEquals(0,
          hotelEntity.get().getRates().stream().filter(rate -> rate.getRateCode().equalsIgnoreCase(RateCategory.NONFLEX.name())).findFirst().get().getMinNights());
      assertEquals(maxLosExpectedLosValue,
          hotelEntity.get().getRates().stream().filter(rate -> rate.getRateCode().equalsIgnoreCase(RateCategory.NONFLEX.name())).findFirst().get().getMaxNights());
        assertFalse(hotelEntity.get().getRooms().isEmpty());
    } catch (IOException e) {
      logError(e);
      fail();
    }
  }

  private static void logError(IOException e) {
    log.error("Received ICException:{}", e.getMessage());
  }
}