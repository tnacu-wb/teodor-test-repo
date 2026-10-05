package uk.co.whitbread.ondemandrefreshservice.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.DailyRatesOutPort;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.HotelInventoryOutPort;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.RateRestrictionOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters.DailyRatesService;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters.RateRestrictionService;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters.HotelInventoryService;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.inventory.opera.HotelInventoryClient;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.raterestriction.opera.HotelRateRestrictionClient;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.rates.opera.HotelDailyRatesClient;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BeanConfiguration {

  @Bean
  public HotelInventoryOutPort hotelInventoryPort(final HotelInventoryClient hotelInventoryClient) {
    return new HotelInventoryService(hotelInventoryClient);
  }

  @Bean
  public DailyRatesOutPort dailyRatesPort(final HotelDailyRatesClient hotelDailyRatesClient) {
    return new DailyRatesService(hotelDailyRatesClient);

  }

  @Bean
  public RateRestrictionOutPort rateRestrictionPort(final HotelRateRestrictionClient hotelRateRestrictionClient) {
    return new RateRestrictionService(hotelRateRestrictionClient);

  }

}
