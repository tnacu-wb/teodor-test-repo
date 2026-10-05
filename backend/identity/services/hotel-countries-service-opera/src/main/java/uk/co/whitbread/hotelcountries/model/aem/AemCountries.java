package uk.co.whitbread.hotelcountries.model.aem;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "countries")
public class AemCountries{

    @XmlElement(name = "country")
    private List<AemCountry> countries;

    public List<AemCountry> getCountries() {
        return countries;
    }

}