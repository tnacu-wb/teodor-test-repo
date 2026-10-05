package uk.co.whitbread.hotelcountries.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Country implements Serializable {

    private static final long serialVersionUID = 9192963550535980602L;

    private String countryCode;
    private String countryCodeISO;
    private String countryLegend;
    private boolean passportRequired;
    private String dialingCode;
    private String flagImg;
}
