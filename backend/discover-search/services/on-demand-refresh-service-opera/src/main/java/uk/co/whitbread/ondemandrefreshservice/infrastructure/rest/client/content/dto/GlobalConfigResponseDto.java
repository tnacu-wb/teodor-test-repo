package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.content.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GlobalConfigResponseDto(List<String> hotelsWithCityTax) {
    @JsonCreator
    public GlobalConfigResponseDto(@JsonProperty("hotelsWithCityTax") List<String> hotelsWithCityTax) {
        this.hotelsWithCityTax = hotelsWithCityTax;
    }
}
