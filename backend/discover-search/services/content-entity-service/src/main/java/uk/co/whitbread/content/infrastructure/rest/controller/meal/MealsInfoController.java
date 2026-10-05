package uk.co.whitbread.content.infrastructure.rest.controller.meal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.content.domain.ports.primary.MealsInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.meal.mapper.MealsInfoResponseDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.meal.mapper.MealsRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.meal.model.in.MealsRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.meal.model.out.MealsInfoResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class MealsInfoController implements MealsInfoApiDocumentation {

  private final MealsRequestDtoMapper mealsRequestDtoMapper;
  private final MealsInPort mealsInPort;
  private final MealsInfoResponseDtoMapper mealsInfoResponseDtoMapper;

  @GetMapping(value = "/content/meals", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MealsInfoResponseDto> getMealsInfo(
      @Valid @ParameterObject MealsRequestDto mealsRequestDto) {

    final var mealsInfoRequest = mealsRequestDtoMapper.toDomainModel(mealsRequestDto);
    var mealsInfoDto = mealsInfoResponseDtoMapper.toDto(
        mealsInPort.getMealsInfo(mealsInfoRequest));
    return ResponseEntity.status(HttpStatus.OK).body(mealsInfoDto);
  }
}
