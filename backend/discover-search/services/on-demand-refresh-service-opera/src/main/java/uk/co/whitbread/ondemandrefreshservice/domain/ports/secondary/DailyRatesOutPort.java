package uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary;

import java.time.LocalDate;
import java.util.Map;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.DailyRates;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.DailyRatesInput;

public interface DailyRatesOutPort {

  DailyRates getDailyRates(DailyRatesInput dailyRatesInput);

  Map<String, DailyRates> getOperaDailyRates(final String hotelCode, final LocalDate startDate,
      final LocalDate endDate);
}
