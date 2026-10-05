package uk.co.whitbread.content.infrastructure.rest.client.snowdrop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.SnowdropResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.adapter.SnowdropClient;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.model.in.HotelLocationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.model.out.HotelLocationResponseDto;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.properties.SnowdropProperties;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.SNOWDROP_HOTEL_SEARCH_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class SnowdropClientTest {

    private SnowdropClient snowdropClient;

    @Mock
    private WebClient snowdropWebClient;
    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private SnowdropProperties snowdropProperties;

    private final HotelLocationRequestDto requestDto =
            new HotelLocationRequestDto(10.0, 20.0, "30", "mi");

    @BeforeEach
    public void init() {
        this.snowdropClient = new SnowdropClient(snowdropWebClient, snowdropProperties);
    }

    @Test
    void testGetHotelsLocationByLatLong_success() {
        // Arrange
        when(snowdropWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(new ParameterizedTypeReference<List<HotelLocationResponseDto>>(){
            })).thenReturn(mockHotelLocationResponseDto());

        // Act
        List<HotelLocationResponseDto> result = snowdropClient.getHotelsLocationByLatLong(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("LONEUS", result.get(0).getCode());
    }

    @Test
    void testGetHotelsLocationByLatLong_errorResponse() {
        // Arrange
        when(snowdropWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenThrow(new SnowdropResponseException(
                SNOWDROP_HOTEL_SEARCH_EXCEPTION, "Resource not found in SnowDrop",
                new Exception()));

        // Act & Assert
        assertThrows(SnowdropResponseException.class, () -> snowdropClient.getHotelsLocationByLatLong(requestDto));
    }

    private Mono<List<HotelLocationResponseDto>> mockHotelLocationResponseDto() {
        HotelLocationResponseDto hotelLocationResponseDto = new HotelLocationResponseDto();
        hotelLocationResponseDto.setCode("LONEUS");
        return Mono.just(List.of(hotelLocationResponseDto));
    }
}