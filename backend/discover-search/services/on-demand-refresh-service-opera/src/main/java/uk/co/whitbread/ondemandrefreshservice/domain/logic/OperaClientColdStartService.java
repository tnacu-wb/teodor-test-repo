package uk.co.whitbread.ondemandrefreshservice.domain.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.DailyRatesOutPort;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.HotelInventoryOutPort;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.RateRestrictionOutPort;

@Slf4j
@ConditionalOnProperty(
    prefix = "OperaClientColdStartService.runner",
    value = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@Component
@RequiredArgsConstructor
public class OperaClientColdStartService implements ApplicationRunner {

  private final HotelInventoryOutPort hotelInventoryOutPort;
  private final DailyRatesOutPort dailyRatesOutPort;
  private final RateRestrictionOutPort rateRestrictionOutPort;

  private static final String HOTEL_ID = "TKINPT";

  @Override
  public void run(ApplicationArguments args) throws JsonProcessingException {
    log.info("OperaClientColdStartService started: {}", args);

    /**final HotelInventoryInput hotelInventoryInput =
        HotelInventoryInput.builder()
            .hotelId(HOTEL_ID)
            .dateRangeStart("2022-09-22")
            .dateRangeEnd("2022-09-25")
            .dailyInventory(true)
            .roomCountRequested(5)
            .houseLevel(false)
            .build();
    hotelInventoryOutPort.getHotelInventory(hotelInventoryInput);

    final DailyRatesInput dailyRatesInput =
        DailyRatesInput.builder()
            .hotelId(HOTEL_ID)
            .ratePlanCode("FLEXRATE")
            .startDate("2022-10-19")
            .endDate("2022-10-30")
            .build();

    DailyRates dailyRates = dailyRatesOutPort.getDailyRates(dailyRatesInput);
    log.info("Daily Rates Response: {}", dailyRates);

    final RateRestrictionInput rateRestrictionInput =
        RateRestrictionInput.builder()
            .hotelId(HOTEL_ID)
            .startDate("2022-10-05")
            .endDate("2022-12-30")
            .build();

    RateRestrictionResponse rateRestrictionResponse =
        rateRestrictionOutPort.searchRateRestrictionCriteria(rateRestrictionInput);
    log.info("Rate Restriction Response: {}", rateRestrictionResponse);**/
  }
}
