package uk.co.whitbread.basket.infrastructure.rest.controller.basket;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.basket.domain.ports.primary.BackgroundChargeInPort;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.BackgroundChargeRequestDto;

@RestController
@RequestMapping("/v1/basket")
@RequiredArgsConstructor
@Slf4j
public class BackgroundChargeController implements BackgroundChargeControllerApiDocumentation {

  private final BackgroundChargeInPort backgroundChargeInPort;

  /**
   * Processes background charge for a basket.
   *
   * @param backgroundChargeRequestDto the background charge request containing basketReference,
   *                                   token
   * @return ResponseEntity with 204 No Content status
   */
  @PostMapping(value = "/background-charge")
  public ResponseEntity<Void> backgroundCharge(
      @RequestBody @Valid BackgroundChargeRequestDto backgroundChargeRequestDto) {
    backgroundChargeInPort.processBackgroundCharge(
        backgroundChargeRequestDto.getBasketReference(),
        backgroundChargeRequestDto.getToken());
    return ResponseEntity.noContent().build();
  }

}
