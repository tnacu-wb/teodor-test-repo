package uk.co.whitbread.availabilitycacheservice.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.availabilitycacheservice.infrastructure.client.exception.ContentException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.client.utils.CustomTestResponseSpec;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.content.ContentServiceProperties;
import uk.co.whitbread.content.entity.service.generated.models.content.GlobalConfigDto;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelInformationExtendedDto;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ContentClientWebFluxTest {

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;
  @Mock
  private ContentServiceProperties properties;
  @Mock
  private ContentClientWebFlux client;

  @BeforeEach
  void setUp() {
    client = new ContentClientWebFlux(webClient, properties);
  }

  @Test
  void testGetGlobalConfig__Success() {
    GlobalConfigDto dto = new GlobalConfigDto();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GlobalConfigDto.class)).thenReturn(Mono.just(dto));

    GlobalConfigDto result = client.getGlobalConfig("gb", "en");
    assertThat(result).isSameAs(dto);
  }

  @Test
  void  getGetGlobalConfig__ShouldReturnException() {

    //Arrange
    String error = "Error while trying to get global config";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(GlobalConfigDto.class)).thenReturn(
        Mono.error(new ContentException("message", error, new Exception(), 1)));

    //Act
    Exception exception = Assertions.assertThrows(ContentException.class,
        () -> client.getGlobalConfig("gb", "en"));

    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void testGetHotelInformation__Success() {
    HotelInformationExtendedDto dto = new HotelInformationExtendedDto();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelInformationExtendedDto.class)).thenReturn(Mono.just(dto));

    HotelInformationExtendedDto result = client.getHotelInformation("gb", "en", "HOTEL1");
    assertThat(result).isSameAs(dto);
  }

  @Test
  void  getGetHotelInformation__ShouldReturnException() {

    //Arrange
    String error = "Error while trying to get the hotel information";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(HotelInformationExtendedDto.class)).thenReturn(
        Mono.error(new ContentException("message", error, new Exception(), 1)));

    //Act
    Exception exception = Assertions.assertThrows(ContentException.class,
        () -> client.getHotelInformation("gb", "en", "HOTEL1"));

    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void testConstructor_assignsFields() {
    assertThat(client).isNotNull();
  }

  @Test
  void testGetGlobalConfig_buildsUriWithDefaults() {
    when(properties.getGlobalConfigEndpoint()).thenReturn("/global-config");
    GlobalConfigDto dto = new GlobalConfigDto();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
      Function<org.springframework.web.util.UriBuilder, ?> uriFunc = invocation.getArgument(0);
      var builder = new org.springframework.web.util.DefaultUriBuilderFactory().builder();
      uriFunc.apply(builder);
      var uri = builder.build();
      assertThat(uri.getPath()).isEqualTo("/global-config");
      assertThat(uri.getQuery()).contains("country=gb");
      assertThat(uri.getQuery()).contains("language=en");
      assertThat(uri.getQuery()).contains("brand=pi");
      assertThat(uri.getQuery()).contains("channelId=PI");
      return requestHeadersSpec;
    });
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GlobalConfigDto.class)).thenReturn(Mono.just(dto));

    GlobalConfigDto result = client.getGlobalConfig(null, null);
    assertThat(result).isSameAs(dto);
  }

  @Test
  void testGetHotelInformation_buildsUriWithParams() {
    when(properties.getHotelInformationEndpoint()).thenReturn("/hotel-info/{hotelId}");
    HotelInformationExtendedDto dto = new HotelInformationExtendedDto();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
      Function<org.springframework.web.util.UriBuilder, ?> uriFunc = invocation.getArgument(0);
      var builder = new org.springframework.web.util.DefaultUriBuilderFactory().builder();
      uriFunc.apply(builder);
      var uri = builder.build("HOTEL1");
      assertThat(uri.getPath()).isEqualTo("/hotel-info/HOTEL1");
      assertThat(uri.getQuery()).contains("country=gb");
      assertThat(uri.getQuery()).contains("language=en");
      assertThat(uri.getQuery()).contains("channel=PI");
      assertThat(uri.getQuery()).contains("subchannel=WEB");
      return requestHeadersSpec;
    });
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HotelInformationExtendedDto.class)).thenReturn(Mono.just(dto));

    HotelInformationExtendedDto result = client.getHotelInformation("gb", "en", "HOTEL1");
    assertThat(result).isSameAs(dto);
  }

  @Test
  void testGetHotelInformation_handles5xxError() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> {
      Predicate<HttpStatus> predicate = invocation.getArgument(0);
      Function<org.springframework.web.reactive.function.client.ClientResponse, Mono<ContentException>> func = invocation.getArgument(1);
      if (predicate.test(HttpStatus.INTERNAL_SERVER_ERROR)) {
        return responseSpec;
      }
      return responseSpec;
    });
    when(responseSpec.bodyToMono(HotelInformationExtendedDto.class)).thenReturn(
        Mono.error(new ContentException("msg", "Error", new Exception(), 500))
    );

    org.junit.jupiter.api.Assertions.assertThrows(ContentException.class, () ->
        client.getHotelInformation("gb", "en", "HOTEL1")
    );
  }
}
