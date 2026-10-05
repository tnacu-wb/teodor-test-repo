package uk.co.whitbread.hotel.account.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@ConfigurationProperties("countries-requiring-postal-codes")
public class CountryCodesWithPostcodesProperties {

    private List<String> countryCodeList;
}
