package uk.co.whitbread.company.infrastructure.rest.client.ohip.profile;

import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.company.exceptions.CompanyNotFoundException;
import uk.co.whitbread.company.exceptions.CompanyServiceException;
import uk.co.whitbread.company.exceptions.NegotiatedRateNotAvailableException;
import uk.co.whitbread.company.infrastructure.rest.client.ohip.config.OhipProperties;
import uk.co.whitbread.company.infrastructure.rest.client.utils.WebClientUtils;
import uk.co.whitbread.ohip.generated.models.CompaniesProfileDto;
import uk.co.whitbread.ohip.generated.models.CompanyProfileDto;
import uk.co.whitbread.ohip.generated.models.NegotiatedRatesResponseDto;

@Component
@Slf4j
public class OhipProfileClient {

  private final WebClient ohipServiceWebClient;
  private final OhipProperties ohipProperties;

  public OhipProfileClient(
      @Qualifier("ohipServiceWebClient") WebClient ohipServiceWebClient,
      OhipProperties ohipProperties) {
    this.ohipServiceWebClient = ohipServiceWebClient;
    this.ohipProperties = ohipProperties;
  }

  public CompaniesProfileDto getCompaniesProfile(String hotelId, String arNumber, String companyName, int limit) {
    return ohipServiceWebClient.get().uri(
            uriBuilder -> uriBuilder.path(ohipProperties.getCompaniesProfile())
                .queryParam("hotelId", hotelId)
                .queryParam("arNumber", arNumber)
                .queryParam("companyName", companyName)
                .queryParam("limit", Integer.toString(limit))
                .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(CompanyServiceException.class);
        })
        .bodyToMono(CompaniesProfileDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error while fetching company profile with hotelId = %s, arNumber =%s, companyName=%s, limit=%s",
                hotelId, arNumber, companyName, limit)))
        .block();
  }

  public CompanyProfileDto getCompanyProfileByCorporateId(final String corporateId) {
    return ohipServiceWebClient.get().uri(
            uriBuilder -> uriBuilder.path(ohipProperties.getCompanyProfile())
                .build(corporateId))
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatus.NOT_FOUND), clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(CompanyNotFoundException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(CompanyServiceException.class);
        })
        .bodyToMono(CompanyProfileDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format("Error while fetching company profile with corporation id = %s",
                corporateId)))
        .block();
  }

  public CompanyProfileDto getCompanyProfileByCompanyId(final String companyId) {
    return ohipServiceWebClient.get().uri(
            uriBuilder -> uriBuilder.path(ohipProperties.getCompanyProfileByCompanyId())
                .build(companyId))
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatus.NOT_FOUND), clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(CompanyNotFoundException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(CompanyServiceException.class);
        })
        .bodyToMono(CompanyProfileDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format("Error while fetching company profile with company id = %s",
                companyId)))
        .block();
  }

  public NegotiatedRatesResponseDto getNegotiatedRatesForCompanyProfile(final String profileId) {
    return ohipServiceWebClient.get().uri(
            uriBuilder -> uriBuilder.path(ohipProperties.getNegotiatedRatesByCompanyProfileId())
                .build(profileId))
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatus.NOT_FOUND), clientResponse -> {
          WebClientUtils.logErrorHeader(log, clientResponse);
          return clientResponse.bodyToMono(NegotiatedRateNotAvailableException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(CompanyServiceException.class);
        })
        .bodyToMono(NegotiatedRatesResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while fetching negotiated rates for company"))
        .block();
  }
}
