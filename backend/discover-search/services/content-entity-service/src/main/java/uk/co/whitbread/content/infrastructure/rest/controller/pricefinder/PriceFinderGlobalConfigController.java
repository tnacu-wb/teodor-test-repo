package uk.co.whitbread.content.infrastructure.rest.controller.pricefinder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.in.PriceFinderGlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.mapper.PriceFinderGlobalConfigDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.out.PriceFinderGlobalConfigDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PriceFinderGlobalConfigController implements PriceFinderApiDocumentation {

  private final PriceFinderGlobalConfigDtoMapper priceFinderGlobalConfigDtoMapper;
  private final ContentInPort contentInPort;

  @Override
  @GetMapping(value = "/content/price-finder/global-config", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PriceFinderGlobalConfigDto> getPriceFinderGlobalConfig(
            @Valid @ParameterObject PriceFinderGlobalConfigRequestDto priceFinderConfigRequestDto) {

    var priceFinderConfigRequest = priceFinderGlobalConfigDtoMapper.toDomainModel(priceFinderConfigRequestDto);
    var priceFinderConfig = contentInPort.getPriceFinderConfig(priceFinderConfigRequest);
    var priceFinderConfigDto = priceFinderGlobalConfigDtoMapper.toDtoModel(priceFinderConfig);
    return ResponseEntity.ok(priceFinderConfigDto);
  }
}
