package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.content.domain.model.globalconfig.in.GlobalConfigRequest;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.in.GlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.mapper.GlobalConfigDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.mapper.GlobalConfigRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.mapper.RoomClassConfigDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.mapper.SearchRulesDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.GlobalConfigDto;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.RoomClassConfigDto;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.SearchRulesDto;
import uk.co.whitbread.content.infrastructure.rest.controller.promoconfig.model.in.PromoConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.promoconfig.model.out.PromotionsInformationResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class GlobalConfigController implements GlobalConfigApiDocumentation {

  private final GlobalConfigRequestDtoMapper globalConfigRequestDtoMapper;
  private final SearchRulesDtoMapper searchRulesDtoMapper;
  private final RoomClassConfigDtoMapper roomClassConfigDtoMapper;
  private final GlobalConfigDtoMapper globalConfigDtoMapper;
  private final ContentInPort contentInPort;

  @GetMapping(value = "/content/searchrules", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SearchRulesDto> getSearchRules(
      @Valid @ParameterObject GlobalConfigRequestDto globalConfigRequestDto) {
    GlobalConfigRequest globalConfigRequest = globalConfigRequestDtoMapper
        .toDomainModel(globalConfigRequestDto);
    var searchRules = contentInPort.getSearchRules(globalConfigRequest);
    SearchRulesDto response = searchRulesDtoMapper.toDtoModel(searchRules);

    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping(value = "/content/room-class-config", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RoomClassConfigDto> getRoomClassConfig(
      @Valid @ParameterObject GlobalConfigRequestDto globalConfigRequestDto) {

    var roomClassConfigRequest = globalConfigRequestDtoMapper.toDomainModel(globalConfigRequestDto);
    var roomClassConfig = contentInPort.getRoomClassConfig(roomClassConfigRequest);
    var roomClassConfigDto = roomClassConfigDtoMapper.toDtoModel(roomClassConfig);
    return ResponseEntity.ok(roomClassConfigDto);
  }

  @Override
  @GetMapping(value = "/content/global-config", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GlobalConfigDto> getGlobalConfig(
      @Valid @ParameterObject GlobalConfigRequestDto globalConfigRequestDto) {

    var globalConfigRequest = globalConfigRequestDtoMapper.toDomainModel(globalConfigRequestDto);
    var globalConfig = contentInPort.getGlobalConfig(globalConfigRequest);
    var globalConfigDto = globalConfigDtoMapper.toDtoModel(globalConfig);
    return ResponseEntity.ok(globalConfigDto);
  }

  @Override
  @GetMapping(value = "/content/promo-config", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PromotionsInformationResponseDto> getPromoConfig(
      @Valid @ParameterObject GlobalConfigRequestDto globalConfigRequestDto,
      @Valid @ParameterObject PromoConfigRequestDto promoConfigRequestDto) {

    var promoConfigRequest = globalConfigRequestDtoMapper.toDomainModel(globalConfigRequestDto, promoConfigRequestDto);
    var promoConfig = contentInPort.getPromoConfig(promoConfigRequest);
    var promoConfigDto = globalConfigDtoMapper.toDtoModel(promoConfig);
    return ResponseEntity.ok(promoConfigDto);
  }
}
