package uk.co.whitbread.availabilitycacheservice.infrastructure.client;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.rulesagent.RulesAgentProperties;

@Slf4j
@Component

public class RulesAgentClientWebFlux {

  private final RulesAgentProperties rulesAgentProperties;
  private final WebClient rulesAgentWebClient;

  public RulesAgentClientWebFlux(RulesAgentProperties rulesAgentProperties,
                                 @Qualifier("rulesAgentWebClient") WebClient rulesAgentWebClient) {
    this.rulesAgentProperties = rulesAgentProperties;
    this.rulesAgentWebClient = rulesAgentWebClient;
    log.info("[PF-RULES-AGENT] RulesAgentClientWebFlux constructed with host: {} and endpoint: {}",
            rulesAgentProperties.getHost(), rulesAgentProperties.getRoomSubstitutionEndpoint());
  }

  @Cacheable(value = "rules-agent-cache", key = "{#roomType, #adults, #children}", cacheManager = "cacheManager12Hours")
  public Mono<Map<String, Object>> getRoomSubstitutions(String roomType, int adults, int children) {
    return rulesAgentWebClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path(rulesAgentProperties.getRoomSubstitutionEndpoint())
                    .queryParam("adults", adults)
                    .queryParam("children", children)
                    .queryParam("pms", "OP")
                    .queryParam("roomType", roomType)
                    .queryParam("channel", "PI")
                    .build())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<>() {
            });
  }
}
