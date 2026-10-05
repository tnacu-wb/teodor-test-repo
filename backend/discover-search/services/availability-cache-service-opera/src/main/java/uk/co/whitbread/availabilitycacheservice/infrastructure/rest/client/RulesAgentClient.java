package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.rulesagent.RulesAgentProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.rulesagent.exceptions.RulesAgentException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.rulesagent.model.in.RoomSubstitutionRuleRequestDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.rules.agent.generated.models.RoomSubstitutionRuleResponseDto;

@Slf4j
@RequiredArgsConstructor
public class RulesAgentClient {

  private final WebClient rulesAgentWebClient;
  private final RulesAgentProperties rulesAgentProperties;

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

}