package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import jakarta.validation.Valid;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.rules.agent.domain.ports.primary.OccupancySupplementInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.documentation.OccupancySupplementApiDocumentation;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.OccupancySupplementDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MultiOccupancySupplementRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.OccupancySupplementRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MultiOccupancySupplementResponseDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.OccupancySupplementResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/rules")
public class OccupancySupplementController implements OccupancySupplementApiDocumentation {

  private final OccupancySupplementInPort occupancySupplementInPort;
  private final OccupancySupplementDtoMapper occupancySupplementDtoMapper;

  @GetMapping(value = "/occupancy-supplement", produces = MediaType.APPLICATION_JSON_VALUE)
  public OccupancySupplementResponseDto getOccupancySupplementPricing(
      @Valid @ParameterObject OccupancySupplementRequestDto occupancySupplementRequestDto) {

    var domainAmendmentRuleRequest = occupancySupplementDtoMapper.toModel(
        occupancySupplementRequestDto);
    var domainAmendmentRuleResponse = occupancySupplementInPort.getOccupancySupplementPricing(
        domainAmendmentRuleRequest);

    return occupancySupplementDtoMapper.toDto(domainAmendmentRuleResponse);
  }

  @PostMapping(value = "/multi-occupancy-supplement", produces = MediaType.APPLICATION_JSON_VALUE)
  public MultiOccupancySupplementResponseDto getMultiOccupancySupplementPricing(
      @Valid @RequestBody MultiOccupancySupplementRequestDto occupancySupplementRequestDto,
      @RequestParam(defaultValue = "true") boolean dictionary) {

    var occupancySupplementResponseDtoStream = occupancySupplementRequestDto.getHotelIds()
        .stream()
        .map(this::getOccupancySupplementPricing);
    var responseDto = MultiOccupancySupplementResponseDto.builder();
    if (dictionary) {
      responseDto.dictionary(occupancySupplementResponseDtoStream.collect(
          Collectors.toMap(OccupancySupplementResponseDto::getHotelId, OccupancySupplementResponseDto::getPricing)));
    } else {
      responseDto.list(occupancySupplementResponseDtoStream.toList());
    }
    return responseDto.build();
  }
}
