package uk.co.whitbread.company.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class PriceCapLocations {

    @Valid
    @JsonProperty("uKWide")
    @JsonAlias("ukWide")
    private Price uKWide;

    @Valid
    private Price greaterLondon;

    @Valid
    private Price ireland;

    public Price getuKWide() {
        return uKWide;
    }

    public void setuKWide(Price uKWide) {
        this.uKWide = uKWide;
    }

    public Price getGreaterLondon() {
        return greaterLondon;
    }

    public void setGreaterLondon(Price greaterLondon) {
        this.greaterLondon = greaterLondon;
    }

    public Price getIreland() {
        return ireland;
    }

    public void setIreland(Price ireland) {
        this.ireland = ireland;
    }
}
