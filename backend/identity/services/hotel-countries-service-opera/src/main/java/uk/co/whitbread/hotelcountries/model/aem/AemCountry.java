package uk.co.whitbread.hotelcountries.model.aem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AemCountry {

    private String countryCode;
    private String dialingCode;
    private String flagImg;
    private String legend;
    private String isoCode;
    private boolean passportRequired;

}
