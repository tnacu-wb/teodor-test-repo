package uk.co.whitbread.infrastructure.rest.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.is;
import static wiremock.org.hamcrest.Matchers.notNullValue;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.infrastructure.config.Dynamics365Properties;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.exception.GroupBookingException;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.model.in.GroupBookingRequestDynamicsDto;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.model.out.GroupBookingResponseDynamicsDto;
import uk.co.whitbread.infrastructure.rest.client.microsoftoauth.OAuthProvider;
import uk.co.whitbread.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class Dynamics365ClientTest {

  public static final String TOKEN = "token";
  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private CustomTestResponseSpec responseSpec;
  @Mock
  private Dynamics365Properties dynamics365Properties;
  @Mock
  private OAuthProvider oAuthProvider;
  @InjectMocks
  private Dynamics365Client dynamics365Client;

  @Test
  void testCreateIncident() {
    GroupBookingRequestDynamicsDto request = new GroupBookingRequestDynamicsDto();
    // Arrange
    when(oAuthProvider.getBearerToken()).thenReturn(TOKEN);

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(dynamics365Properties.getCreateBookingEndpoint()))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), eq(GroupBookingRequestDynamicsDto.class)))
        .thenReturn(requestHeadersSpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toEntity(String.class))
        .thenReturn(Mono.just(new ResponseEntity<>(HttpStatusCode.valueOf(204))));

    // Act
    ResponseEntity<String> response = dynamics365Client.createIncident(request);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void testCreateIncident__ShouldThrowException() {
    String error = "Error while trying to create incident response";
    // Arrange
    when(oAuthProvider.getBearerToken()).thenReturn(TOKEN);

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(dynamics365Properties.getCreateBookingEndpoint()))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), eq(GroupBookingRequestDynamicsDto.class))).thenReturn(
        requestHeadersSpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toEntity(String.class))
        .thenReturn(Mono.error(new GroupBookingException("message", error, new Exception(), 1)));

    var groupBookingRequestDynamicsRequest = mock(GroupBookingRequestDynamicsDto.class);
    // Act
    GroupBookingException exception = Assertions.assertThrows(GroupBookingException.class,
        () -> dynamics365Client.createIncident(groupBookingRequestDynamicsRequest));

    // Assert
    assertEquals(exception.getMessage(), error);
  }

  @Test
  void testRetrieveIncident() {
    //Arrange
    when(oAuthProvider.getBearerToken()).thenReturn(TOKEN);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GroupBookingResponseDynamicsDto.class)).thenReturn(
        mockGroupBookingResponseDynamicsDto());

    //Act
    var retrieveIncidentResponse = this.dynamics365Client.retrieveIncident("CAS-23740-K4T1G9");

    //Assert
    assertThat(retrieveIncidentResponse, notNullValue());
    assertThat(retrieveIncidentResponse.getIncidentid(), is("CAS-23740-K4T1G9"));
    assertThat(retrieveIncidentResponse.getTicketnumber(),
        is("2596c141-ed48-ef11-bfe2-000d3aae8d3c"));
  }

  @Test
  void testRetrieveIncident__ShouldThrowException() {
    //Arrange
    String error = "Error while trying to retrieve incident response";
    when(oAuthProvider.getBearerToken()).thenReturn(TOKEN);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpec.bodyToMono(GroupBookingResponseDynamicsDto.class)).thenReturn(
        Mono.error(new GroupBookingException("message", error, new Exception(), 1)));

    //Act
    GroupBookingException exception = Assertions.assertThrows(GroupBookingException.class,
        () -> dynamics365Client.retrieveIncident("CAS-23740-K4T1G9"));

    // Assert
    assertEquals(exception.getMessage(), error);
  }

  private Mono<GroupBookingResponseDynamicsDto> mockGroupBookingResponseDynamicsDto() {
    var groupBookingResponseDynamicsDto = new GroupBookingResponseDynamicsDto();
    groupBookingResponseDynamicsDto.setIncidentid("CAS-23740-K4T1G9");
    groupBookingResponseDynamicsDto.setTicketnumber("2596c141-ed48-ef11-bfe2-000d3aae8d3c");
    return Mono.just(groupBookingResponseDynamicsDto);
  }
}
