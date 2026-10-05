package uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder;

import java.math.BigDecimal;

public record RateCodeMinimumRate(String rateCode, BigDecimal minimumRate) {}
