package uk.co.whitbread.basket.infrastructure.rest.controller.email;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.basket.domain.ports.primary.EmailNotificationInPort;
import uk.co.whitbread.basket.infrastructure.rest.controller.email.mapper.EmailMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.email.model.in.EmailRequestDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/baskets")
public class EmailNotificationController implements EmailNotificationApiDocumentation {

  private final EmailMapper emailMapper;
  private final EmailNotificationInPort emailNotificationInPort;

  @Override
  @PostMapping(value = "/email", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> triggerEmailNotificationProcess(
      @Valid @RequestBody final EmailRequestDto emailRequestDto) {
    var emailRequest = emailMapper.toDomainModel(emailRequestDto);
    emailNotificationInPort.triggerEmailNotification(emailRequest);
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

}
