package uk.co.whitbread.ohip.infrastructure.rest.controller.eckoh;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.ohip.domain.ports.primary.EckohInPort;
import uk.co.whitbread.ohip.infrastructure.rest.controller.eckoh.mapper.EckohMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.eckoh.model.in.EckohWebhookRequestDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class EckohController {

  private final EckohInPort eckohInPort;
  private final EckohMapper eckohMapper;

  @PostMapping(value = "/webhook/eckoh", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> eckohWebhook(
      @RequestBody @Valid EckohWebhookRequestDto eckohWebhookRequestDto) {
    eckohInPort.eckohWebhook(eckohMapper.toModel(eckohWebhookRequestDto));

    return ResponseEntity.noContent().build();
  }

}
