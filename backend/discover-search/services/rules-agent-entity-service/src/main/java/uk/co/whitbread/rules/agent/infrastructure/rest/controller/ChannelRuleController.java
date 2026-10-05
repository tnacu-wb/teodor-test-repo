package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.rules.agent.domain.ports.primary.ChannelRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.documentation.ChannelRuleApiDocumentation;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.ChannelRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.ChannelRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.ChannelRuleResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/rules")
public class ChannelRuleController implements ChannelRuleApiDocumentation {

  private final ChannelRuleInPort channelRuleInPort;
  private final ChannelRuleDtoMapper channelRuleDtoMapper;

  @GetMapping(value = "/channel-info", produces = MediaType.APPLICATION_JSON_VALUE)
  public ChannelRuleResponseDto getChannelRule(
      @Valid @ParameterObject ChannelRuleRequestDto channelRuleRequestDto) {

    var domainAmendmentRuleRequest = channelRuleDtoMapper.toModel(
        channelRuleRequestDto);
    var domainAmendmentRuleResponse = channelRuleInPort.getChannelRule(
        domainAmendmentRuleRequest);

    return channelRuleDtoMapper.toDto(domainAmendmentRuleResponse);
  }

  @GetMapping(value = "/source-info", produces = MediaType.APPLICATION_JSON_VALUE)
  public ChannelRuleResponseDto getChannelRuleBasedOnSourceId(
      @Valid @ParameterObject String sourceId) {
    var domainAmendmentRuleResponse = channelRuleInPort.getChannelRuleBasedOnSourceId(sourceId);
    return channelRuleDtoMapper.toDto(domainAmendmentRuleResponse);
  }

}
