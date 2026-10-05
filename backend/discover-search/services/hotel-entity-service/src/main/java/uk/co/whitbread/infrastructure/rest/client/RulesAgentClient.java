package uk.co.whitbread.infrastructure.rest.client;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.infrastructure.config.RulesAgentProperties;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.exceptions.RulesAgentException;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.model.in.RoomSubstitutionRuleRequestDto;
import uk.co.whitbread.infrastructure.rest.client.utils.WebClientUtils;
import uk.co.whitbread.rules.agent.generated.models.MaxNightsRuleResponseDto;
import uk.co.whitbread.rules.agent.generated.models.MaxRoomOccupancyResponseDto;
import uk.co.whitbread.rules.agent.generated.models.MaxRoomsRuleResponseDto;
import uk.co.whitbread.rules.agent.generated.models.MultiOccupancySupplementRequestDto;
import uk.co.whitbread.rules.agent.generated.models.MultiOccupancySupplementResponseDto;
import uk.co.whitbread.rules.agent.generated.models.RateSuppressionRuleResponseDto;
import uk.co.whitbread.rules.agent.generated.models.RoomSubstitutionRuleResponseDto;

@Slf4j
@RequiredArgsConstructor
public class RulesAgentClient {

  private final WebClient rulesAgentWebClient;
  private final RulesAgentProperties rulesAgentProperties;

  private static final String CHANNEL_ID = "channelId";

  public RoomSubstitutionRuleResponseDto getSubstitutionRoomRules(
      RoomSubstitutionRuleRequestDto roomSubstitutionRuleRequestDto) {
    log.debug(
        "Entered getRoomSubstitutionRule with adults={}, children={}, roomType={}, pms={}",
        roomSubstitutionRuleRequestDto.getAdults(), roomSubstitutionRuleRequestDto.getChildren(),
        roomSubstitutionRuleRequestDto.getRoomType(), roomSubstitutionRuleRequestDto.getPms());

    return rulesAgentWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(rulesAgentProperties.getRoomSubstitutionEndpoint())
            .queryParam("adults", roomSubstitutionRuleRequestDto.getAdults())
            .queryParam("children", roomSubstitutionRuleRequestDto.getChildren())
            .queryParam("roomType", roomSubstitutionRuleRequestDto.getRoomType())
            .queryParam("pms", roomSubstitutionRuleRequestDto.getPms())
            .queryParam("channel", roomSubstitutionRuleRequestDto.getChannel())
            .build()
        ).retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(RulesAgentException.class);
        })
        .bodyToMono(RoomSubstitutionRuleResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to get room substitution rule response for adults=%s, children=%s, "
                + "roomType=%s, pms=%s, channel=%s", roomSubstitutionRuleRequestDto.getAdults(),
            roomSubstitutionRuleRequestDto.getChildren(),
            roomSubstitutionRuleRequestDto.getRoomType(), roomSubstitutionRuleRequestDto.getPms(),
            roomSubstitutionRuleRequestDto.getChannel())))
        .block();
  }

  public MaxNightsRuleResponseDto getMaxNightsRule(
      String channelId) {
    log.debug(
        "Entered getMaxNightsRule with channelId={}",
        channelId);

    return rulesAgentWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(rulesAgentProperties.getMaxNightsEndpoint())
            .queryParam(CHANNEL_ID, channelId)
            .build()
        ).retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(RulesAgentException.class);
        })
        .bodyToMono(MaxNightsRuleResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to get max nights rule response for channelId=%s",
            channelId)))
        .block();
  }

  public MaxRoomsRuleResponseDto getMaxRoomsRule(String channelId) {
    log.debug(
        "Entered getMaxRoomsRule with channelId={}",
        channelId);

    return rulesAgentWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(rulesAgentProperties.getMaxRoomsEndpoint())
            .queryParam(CHANNEL_ID, channelId)
            .build()
        ).retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(RulesAgentException.class);
        })
        .bodyToMono(MaxRoomsRuleResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to get max rooms rule response for channelId=%s",
            channelId)))
        .block();
  }

  public MaxRoomOccupancyResponseDto getMaxRoomOccupancyResponse(
      String channelId) {
    log.debug(
        "Entered getMaxRoomOccupancyRule with channelId={}",
        channelId);

    return rulesAgentWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(rulesAgentProperties.getMaxRoomOccupancyEndpoint())
            .queryParam(CHANNEL_ID, channelId)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(RulesAgentException.class);
        })
        .bodyToMono(MaxRoomOccupancyResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to get max room occupancy rule response for channelId=%s",
            channelId)))
        .block();
  }

  public RateSuppressionRuleResponseDto getRateSuppressions() {
    log.debug("Entered getRateSuppressions");

    return rulesAgentWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(rulesAgentProperties.getRateSuppressionEndpoint())
            .build()
        ).retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(RulesAgentException.class);
        })
        .bodyToMono(RateSuppressionRuleResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to get rate suppressions"))
        .block();
  }

  public MultiOccupancySupplementResponseDto getMultiOccupancySupplementPricing(
      MultiOccupancySupplementRequestDto request) {
    log.debug("Entered getMultiOccupancySupplementPricing with hotelIds={}", request.getHotelIds());
    return rulesAgentWebClient
        .post()
        .uri(rulesAgentProperties.getMultiOccupancySupplementEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(request), MultiOccupancySupplementRequestDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(RulesAgentException.class);
        })
        .bodyToMono(MultiOccupancySupplementResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
                "Error while trying to get Occupancy Supplement Pricing for hotelIds=%s",
            request.getHotelIds())))
        .block();
  }
}