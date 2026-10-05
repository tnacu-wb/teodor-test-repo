package uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary;

import reactor.core.publisher.Mono;

import java.util.List;

public interface ContentEntityServiceOutPort {
    Mono<List<String>> getHotelsWithCityTax(String channelId, String country, String language, String brand);

    Mono<List<String>> getHotelsWithCityTax(String channelId, String brand);
}
