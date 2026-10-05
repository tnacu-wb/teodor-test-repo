package uk.co.whitbread.hotel.account.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "booking-history")
public class BookingHistoryProperties {

  private Integer upcomingDays;
  private Integer pastDays;
  private Integer cancelledDays;

}
