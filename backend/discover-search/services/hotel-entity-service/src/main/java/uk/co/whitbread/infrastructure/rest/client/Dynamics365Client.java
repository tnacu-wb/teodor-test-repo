package uk.co.whitbread.infrastructure.rest.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.infrastructure.config.Dynamics365Properties;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.exception.GroupBookingException;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.model.in.GroupBookingRequestDynamicsDto;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.model.out.GroupBookingResponseDynamicsDto;
import uk.co.whitbread.infrastructure.rest.client.microsoftoauth.OAuthProvider;
import uk.co.whitbread.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
public class Dynamics365Client {

  private static final String SELECT_QUERY_PARAM = "$select";
  private static final String BEARER_PREFIX = "Bearer";
  private final WebClient dynamics365WebClient;
  private final Dynamics365Properties dynamics365Properties;
  private final OAuthProvider oAuthProvider;

  public ResponseEntity<String> createIncident(GroupBookingRequestDynamicsDto request) {
    log.debug("Entered createIncident with groupBookingRequest={}", request);

    return dynamics365WebClient.post()
        .uri(dynamics365Properties.getCreateBookingEndpoint())
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + oAuthProvider.getBearerToken())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(request), GroupBookingRequestDynamicsDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(GroupBookingException.class);
        })
        .toEntity(String.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to call Dynamics365"))
        .block();
  }

  public GroupBookingResponseDynamicsDto retrieveIncident(String incidentId) {
    log.debug("Entered retrieveIncident with incidentId={}", incidentId);

    return dynamics365WebClient.get()
        .uri(uriBuilder -> uriBuilder.path(
                dynamics365Properties.getGetTicketEndpoint())
            .queryParam(SELECT_QUERY_PARAM, "ticketnumber")
            .build(incidentId))
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + oAuthProvider.getBearerToken())
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(GroupBookingException.class);
        })
        .bodyToMono(GroupBookingResponseDynamicsDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to retrieve the group booking Ticket Number!"))
        .block();
  }

}
