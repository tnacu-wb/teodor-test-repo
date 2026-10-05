package uk.co.whitbread.reservation.infrastructure.rest.client.cdh.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchCriteriaDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.GetEmployeeRequestDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.GetEmployeeResponseDto;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.service.properties.CdhAdapterProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.exceptions.CdhReservationException;
import uk.co.whitbread.reservation.infrastructure.rest.utils.WebClientUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class CdhAdapterClient {

  private final CdhAdapterProperties cdhAdapterProperties;
  private final WebClient cdhAdapterWebClient;

  public CdhReservationSearchDto searchReservation(CdhReservationSearchCriteriaDto request) {

    return cdhAdapterWebClient
        .post()
        .uri(cdhAdapterProperties.getReservationSearchEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(request), CdhReservationSearchCriteriaDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(CdhReservationException.class);
        })
        .bodyToMono(CdhReservationSearchDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            "Error while retrieving reservation details from CDH"))
        .block();
  }

  public GetEmployeeResponseDto getEmployee(GetEmployeeRequestDto request) {
    return cdhAdapterWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(cdhAdapterProperties.getEmployeeEndpoint())
            .queryParam("companyAccountId", request.getCompanyAccountId())
            .queryParam("employeeAccountId", request.getEmployeeAccountId())
            .queryParam("accessContext", request.getAccessContext())
            .queryParam("accessedBy", request.getAccessedBy())
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          log.error(
              "Error while retrieving employee details from CDH, companyAccountId: {}, employeeAccountId: {}",
              request.getCompanyAccountId(), request.getEmployeeAccountId());
          return Mono.empty();
        })
        .bodyToMono(GetEmployeeResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while retrieving employee details from CDH, companyAccountId: %s, employeeAccountId: %s",
            request.getCompanyAccountId(), request.getEmployeeAccountId())))
        .block();
  }
}
