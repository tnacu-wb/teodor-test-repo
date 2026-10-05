package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.content.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.ContentEntityServiceOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.content.dto.GlobalConfigResponseDto;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.content.exception.ContentException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.content.exception.NoHeaderDataException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.util.WebClientUtils;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ContentEntityServiceOutPortImpl implements ContentEntityServiceOutPort {
    private final WebClient webClient;
    private final String globalConfigEndpoint;

    private static final String CHANNEL_ID = "channelId";
    private static final String COUNTRY = "country";
    private static final String LANGUAGE = "language";
    private static final String BRAND = "brand";

    public ContentEntityServiceOutPortImpl(
            WebClient contentEntityWebClient,
            @Value("${config.service.content-entity.globalConfigEndpoint}") String globalConfigEndpoint) {
        this.webClient = contentEntityWebClient;
        this.globalConfigEndpoint = globalConfigEndpoint;
    }

    @Override
    public Mono<List<String>> getHotelsWithCityTax(String channelId, String country, String language, String brand) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(globalConfigEndpoint)
                        .queryParam(CHANNEL_ID, channelId)
                        .queryParamIfPresent(COUNTRY, Optional.ofNullable(country))
                        .queryParamIfPresent(LANGUAGE, Optional.ofNullable(language))
                        .queryParam(BRAND, brand)
                        .build())
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    WebClientUtils.logErrorResponse(log, response);
                    return response.createException()
                            .flatMap(ex -> Mono.error(new NoHeaderDataException("Hotels no found when fetching with " +
                                    "city tax", ex.getResponseBodyAsString(), ex, ex.getStatusCode().value())));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    WebClientUtils.logErrorResponse(log, response);
                    return response.createException()
                            .flatMap(ex -> Mono.error(new ContentException("Content service error when fetching " +
                                    "hotels with city tax", ex.getResponseBodyAsString(), ex,
                                    ex.getStatusCode().value())));
                })
                .bodyToMono(GlobalConfigResponseDto.class)
                .map(GlobalConfigResponseDto::hotelsWithCityTax)
                .switchIfEmpty(Mono.error(new ContentException(
                        "Empty response body when fetching hotels with city tax",
                        null,
                        null,
                        HttpStatus.NO_CONTENT.value())))
                .doOnError(ex -> log.error("Error while trying to get hotels with city tax", ex));
    }

    @Override
    public Mono<List<String>> getHotelsWithCityTax(String channelId, String brand) {
        return getHotelsWithCityTax(channelId, null, null, brand);
    }
}
