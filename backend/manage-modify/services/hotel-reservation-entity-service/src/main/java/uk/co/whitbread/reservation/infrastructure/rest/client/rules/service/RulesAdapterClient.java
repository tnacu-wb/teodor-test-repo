package uk.co.whitbread.reservation.infrastructure.rest.client.rules.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.exceptions.RulesException;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.service.properties.RulesAdapterProperties;
import uk.co.whitbread.reservation.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.rules.entity.service.generated.models.agent.AmendmentRuleResponseDto;
import uk.co.whitbread.rules.entity.service.generated.models.agent.BusinessAllowanceRuleResponseDto;
import uk.co.whitbread.rules.entity.service.generated.models.agent.ChannelRuleResponseDto;
import uk.co.whitbread.rules.entity.service.generated.models.agent.MaxNightsRuleResponseDto;
import uk.co.whitbread.rules.entity.service.generated.models.agent.MaxRoomOccupancyResponseDto;
import uk.co.whitbread.rules.entity.service.generated.models.agent.MaxRoomsRuleResponseDto;
import uk.co.whitbread.rules.entity.service.generated.models.agent.SingleOccupancySupplementResponseDto;
import uk.co.whitbread.rules.entity.service.generated.models.agent.VatRuleResponseDto;

@Slf4j
@Component
public class RulesAdapterClient {

  public static final String RATE_TYPE = "rateType";
  public static final String ARRIVAL_DATE = "arrivalDate";
  public static final String HOTEL_COUNTRY_CODE = "hotelCountryCode";
  public static final String HOTEL_LOCAL_TIME = "hotelLocalDateTime";
  public static final String CHANNEL_ID = "channelId";
  public static final String SOURCE_ID = "sourceId";
  private static final String BRAND = "brand";
  public static final String VAT_REGION = "vatRegion";
  public static final String PKG_CODE_ARR = "pkgCodeArr";

  public static final String HOTEL_ID = "hotelId";
  private final WebClient rulesAdapterWebClient;
  private final RulesAdapterProperties rulesAdapterProperties;

  public RulesAdapterClient(@Qualifier("rulesAdapterWebClient") WebClient rulesAdapterWebClient,
                            RulesAdapterProperties rulesAdapterProperties) {
    this.rulesAdapterWebClient = rulesAdapterWebClient;
    this.rulesAdapterProperties = rulesAdapterProperties;
  }

  public AmendmentRuleResponseDto isBookingAmendable(String rateType, String arrivalDate,
                                                     String hotelLocalDateTime, String hotelCountryCode) {
    return rulesAdapterWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(rulesAdapterProperties.getAmendmentRulesEndpoint())
            .queryParam(RATE_TYPE, rateType)
            .queryParam(ARRIVAL_DATE, arrivalDate)
            .queryParam(HOTEL_LOCAL_TIME, hotelLocalDateTime)
            .queryParam(HOTEL_COUNTRY_CODE, hotelCountryCode)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(RulesException.class);
        })
        .bodyToMono(AmendmentRuleResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error calling rules-service with parameters: rateType=%s, "
                    + "arrivalDate=%s, hotelLocalDateTime=%s, hotelCountryCode=%s", rateType,
                arrivalDate,
                hotelLocalDateTime,
                hotelCountryCode)))
        .block();
  }

  public MaxRoomsRuleResponseDto getMaxRoomsRule(String channelId) {
    return rulesAdapterWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(rulesAdapterProperties.getMaxRoomRulesEndpoint())
            .queryParam(CHANNEL_ID, channelId)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(RulesException.class);
        })
        .bodyToMono(MaxRoomsRuleResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error calling rules-service with parameters: channelId=%s", channelId)))
        .block();
  }

  public MaxNightsRuleResponseDto getMaxNightsRule(String channelId) {
    return rulesAdapterWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(rulesAdapterProperties.getMaxNightsRulesEndpoint())
            .queryParam(CHANNEL_ID, channelId)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(RulesException.class);
        })
        .bodyToMono(MaxNightsRuleResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error calling rules-service with parameters: channelId=%s", channelId)))
        .block();
  }

  public MaxRoomOccupancyResponseDto getMaxRoomOccupancyResponse(
      String channelId, String brand) {
    log.debug(
        "Entered getMaxRoomOccupancyRule with channelId={}",
        channelId);

    return rulesAdapterWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(rulesAdapterProperties.getMaxRoomOccupancyEndpoint())
            .queryParam(CHANNEL_ID, channelId)
            .queryParam(BRAND, brand)
            .build())
        .retrieve()
        .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(RulesException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(RulesException.class);
        })
        .bodyToMono(MaxRoomOccupancyResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex, String.format(
            "Error while trying to get max room occupancy rule response for channelId=%s",
            channelId)))
        .block();
  }

  public ChannelRuleResponseDto getChannelBasedOnSourceId(String sourceId) {
    log.debug("Entered getChannelBasedOnSourceId with sourceId={}", sourceId);

    return rulesAdapterWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(rulesAdapterProperties.getChannelBasedOnSourceIdEndpoint())
            .queryParam(SOURCE_ID, sourceId)
            .build())
        .retrieve()
        .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(RulesException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(RulesException.class);
        })
        .bodyToMono(ChannelRuleResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to get information for channel with source id =%s",
                sourceId)))
        .block();
  }

  public VatRuleResponseDto getVatCodesForPackage(String vatRegion, String packageCode) {
    return rulesAdapterWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(rulesAdapterProperties.getVatCodesEndpoint())
            .queryParam(VAT_REGION, vatRegion)
            .queryParam(PKG_CODE_ARR, packageCode)
            .build())
        .retrieve()
        .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(RulesException.class);
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(RulesException.class);
        })
        .bodyToMono(VatRuleResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            String.format("Error while trying to get information for channel with source id =%s",
                vatRegion)))
        .block();
  }

  public BusinessAllowanceRuleResponseDto getBusinessAllowances() {
    return rulesAdapterWebClient.get().uri(uriBuilder -> uriBuilder
            .path(rulesAdapterProperties.getBusinessAllowanceEndpoint())
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(RulesException.class);
        })
        .bodyToMono(BusinessAllowanceRuleResponseDto.class)
        .doOnError(
            ex -> ExceptionLogger.log(log, ex, "Error while trying to get business allowances."))
        .block();
  }

  public SingleOccupancySupplementResponseDto getSingleOccupancySupplement(String hotelId) {
    return rulesAdapterWebClient.get().uri(uriBuilder -> uriBuilder
            .path(rulesAdapterProperties.getSingleOccupancySupplementEndpoint())
            .queryParam(HOTEL_ID, hotelId)
            .build())
         .retrieve()
         .onStatus(HttpStatusCode::isError, response -> {
           WebClientUtils.logErrorResponse(log, response);
           return response.bodyToMono(RulesException.class);
         })
         .bodyToMono(SingleOccupancySupplementResponseDto.class)
         .doOnError(
            ex -> ExceptionLogger.log(log, ex, "Error while trying to get single occupancy supplement."))
         .block();
  }

}
