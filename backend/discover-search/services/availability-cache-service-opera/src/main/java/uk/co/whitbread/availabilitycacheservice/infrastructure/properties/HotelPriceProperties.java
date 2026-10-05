package uk.co.whitbread.availabilitycacheservice.infrastructure.properties;


import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "rt")
@Component
@Data
public class HotelPriceProperties {

  private List<String> roomTypes;
  private int maxDays;
}

