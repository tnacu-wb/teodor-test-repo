package uk.co.whitbread.hotel.info.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "hotel.info.configuration")
public class HotelConfiguration {

    private List<String> hubHotelCodes = new ArrayList<>();

    public List<String> getHubHotelCodes() {
        return hubHotelCodes;
    }

}
