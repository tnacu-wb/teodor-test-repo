package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement;

import static uk.co.whitbread.content.domain.utils.SanitizingUtils.sanitize;

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
import uk.co.whitbread.content.domain.ports.primary.CardManagementInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.mapper.CardManagementRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.mapper.CardManagementResponseDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.in.CardManagementRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.CardManagementResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1")
public class CardManagementController implements CardManagementApiDocumentation {

  private final CardManagementRequestDtoMapper cardManagementRequestDtoMapper;
  private final CardManagementInPort cardManagementInPort;
  private final CardManagementResponseDtoMapper cardManagementResponseDtoMapper;

  @GetMapping(value = "/content/innb/cardmgmt", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CardManagementResponseDto> getCardManagementInfo(
      @Valid @ParameterObject CardManagementRequestDto cardManagementRequestDto) {

    log.debug("Request to get InnBusiness card management information with language={} started.",
        sanitize(cardManagementRequestDto.getLanguage()));

    final var cardManagementRequest = cardManagementRequestDtoMapper.toModel(cardManagementRequestDto);
    var cardManagementInfoDto = cardManagementResponseDtoMapper.toDto(
        cardManagementInPort.getCardManagementInfo(cardManagementRequest));

    return ResponseEntity.status(HttpStatus.OK).body(cardManagementInfoDto);
  }
}
