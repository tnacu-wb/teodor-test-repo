package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.ContentEntityServiceOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.content.dto.GlobalConfigResponseDto;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.content.exception.ContentException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.content.exception.NoHeaderDataException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.content.impl.ContentEntityServiceOutPortImpl;

import java.util.List;

class ContentEntityServiceOutPortImplTest {

    private WireMockServer wireMockServer;
    private ContentEntityServiceOutPort contentEntityService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WebClient webClient = WebClient.builder()
                .baseUrl(wireMockServer.baseUrl())
                .build();
        contentEntityService = new ContentEntityServiceOutPortImpl(webClient, "/v1/content/global-config");
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void getHotelsWithCityTax_shouldReturnHotels_whenServiceReturns200() throws JsonProcessingException {
        // Arrange
        String responseBody = objectMapper.writeValueAsString(new GlobalConfigResponseDto(List.of("EDIROS", "EDIHAY")));
        wireMockServer.stubFor(get(urlPathEqualTo("/v1/content/global-config"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(responseBody)));

        // Act
        Mono<List<String>> result = contentEntityService.getHotelsWithCityTax("PI", "gb", "en", "PI");

        // Assert
        StepVerifier.create(result)
                .assertNext(hotels -> {
                    assertEquals(2, hotels.size());
                    assertEquals("EDIROS", hotels.get(0));
                    assertEquals("EDIHAY", hotels.get(1));
                })
                .verifyComplete();
    }

    @Test
    void getHotelsWithCityTax_shouldThrowNoHeaderDataException_whenServiceReturns404() {
        // Arrange
        wireMockServer.stubFor(get(urlPathMatching("/v1/content/global-config.*")).willReturn(aResponse().withStatus(404)));

        // Act
        Mono<List<String>> result = contentEntityService.getHotelsWithCityTax("PI", "gb", "en", "PI");

        // Assert
        StepVerifier.create(result)
                .expectError(NoHeaderDataException.class)
                .verify();
    }

    @Test
    void getHotelsWithCityTax_shouldThrowContentException_whenServiceReturns500() {
        // Arrange
        wireMockServer.stubFor(get(urlPathMatching("/v1/content/global-config.*")).willReturn(aResponse().withStatus(500)));

        // Act
        Mono<List<String>> result = contentEntityService.getHotelsWithCityTax("PI", "gb", "en", "PI");

        // Assert
        StepVerifier.create(result)
                .expectError(ContentException.class)
                .verify();
    }

    @Test
    void getHotelsWithCityTax_shouldThrowContentException_whenBodyIsEmpty() {
        // Arrange
        wireMockServer.stubFor(get(urlPathMatching("/v1/content/global-config.*"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(""))); // Return 200 OK with an empty body

        // Act
        Mono<List<String>> result = contentEntityService.getHotelsWithCityTax("PI", "gb", "en", "PI");

        // Assert
        StepVerifier.create(result)
                .expectError(ContentException.class)
                .verify();
    }

    @Test
    void getHotelsWithCityTax_shouldPropagateError_whenDecodingFails() {
        // Arrange
        String malformedResponseBody = "{\"hotelsWithCityTax\": [\"EDIROS\","; // Malformed JSON
        wireMockServer.stubFor(get(urlPathMatching("/v1/content/global-config.*"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(malformedResponseBody)));

        // Act
        Mono<List<String>> result = contentEntityService.getHotelsWithCityTax("PI", "gb", "en", "PI");

        // Assert
        StepVerifier.create(result)
                .expectError() // Expect a decoding error to be propagated
                .verify();
    }
}
