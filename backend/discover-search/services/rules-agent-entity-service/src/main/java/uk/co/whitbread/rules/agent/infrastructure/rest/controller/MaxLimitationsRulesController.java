package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.rules.agent.domain.ports.primary.MaxLimitationsRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.documentation.MaxLimitationsRulesApiDocumentation;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.MaxArrivalDateRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.MaxNightsRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.MaxRoomOccupancyDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.MaxRoomsRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxArrivalDateRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxNightsRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxRoomOccupancyRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxRoomsRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxArrivalDateRuleResponseDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxNightsRuleResponseDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxRoomOccupancyResponseDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxRoomsRuleResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/rules")
public class MaxLimitationsRulesController implements MaxLimitationsRulesApiDocumentation {

  private final MaxNightsRuleDtoMapper maxNightsRuleDtoMapper;
  private final MaxRoomsRuleDtoMapper maxRoomsRuleDtoMapper;
  private final MaxLimitationsRuleInPort maxLimitationsRuleInPort;
  private final MaxRoomOccupancyDtoMapper roomOccupancyDtoMapper;
  private final MaxArrivalDateRuleDtoMapper maxArrivalDateRuleDtoMapper;


  @GetMapping(value = "/max-nights", produces = MediaType.APPLICATION_JSON_VALUE)
  public MaxNightsRuleResponseDto getMaxNightsRule(
      @Valid @ParameterObject MaxNightsRuleRequestDto maxNightsRuleRequestDto) {

    var domainMaxNightsRuleRequest = maxNightsRuleDtoMapper.toModel(
        maxNightsRuleRequestDto);
    var domainMaxNightsRuleResponse = maxLimitationsRuleInPort.getMaxNightsRule(
        domainMaxNightsRuleRequest);

    return maxNightsRuleDtoMapper.toDto(domainMaxNightsRuleResponse);
  }

  @GetMapping(value = "/max-rooms", produces = MediaType.APPLICATION_JSON_VALUE)
  public MaxRoomsRuleResponseDto getMaxRoomsRule(
      @Valid @ParameterObject MaxRoomsRuleRequestDto maxRoomsRuleRequestDto) {

    var domainMaxRoomsRuleRequest = maxRoomsRuleDtoMapper.toModel(
        maxRoomsRuleRequestDto);
    var domainMaxRoomsRuleResponse = maxLimitationsRuleInPort.getMaxRoomsRule(
        domainMaxRoomsRuleRequest);

    return maxRoomsRuleDtoMapper.toDto(domainMaxRoomsRuleResponse);
  }

  @GetMapping(value = "/max-room-occupancy", produces = MediaType.APPLICATION_JSON_VALUE)
  public MaxRoomOccupancyResponseDto getMaxRoomOccupancyRule(
      @Valid @ParameterObject MaxRoomOccupancyRequestDto roomOccupancyRequestDto) {

    var domainMaxRoomOccupancyRequest = roomOccupancyDtoMapper.toModel(
        roomOccupancyRequestDto);
    var domainMaxRoomOccupancyResponse = maxLimitationsRuleInPort.getMaxRoomOccupancyRule(
        domainMaxRoomOccupancyRequest);

    return roomOccupancyDtoMapper.toDto(domainMaxRoomOccupancyResponse);
  }

  @GetMapping(value = "/max-arrival-date", produces = MediaType.APPLICATION_JSON_VALUE)
  public MaxArrivalDateRuleResponseDto getMaxArrivalDateRule(
      @Valid @ParameterObject MaxArrivalDateRuleRequestDto maxArrivalDateRequestDto) {

    var domainMaxArrivalDateRuleRequest = maxArrivalDateRuleDtoMapper.toModel(
        maxArrivalDateRequestDto);
    var domainMaxArrivalDateRuleResponse = maxLimitationsRuleInPort.getMaxArrivalDateRule(
        domainMaxArrivalDateRuleRequest);

    return maxArrivalDateRuleDtoMapper.toDto(domainMaxArrivalDateRuleResponse);
  }
}
