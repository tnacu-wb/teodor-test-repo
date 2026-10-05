package uk.co.whitbread.basket.infrastructure.rest.controller.basket;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.basket.domain.ports.primary.BasketInPort;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.PrepaidDepositsRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.PrepaidDepositsResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.DepositFoliosRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.PrepaidDepositsDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.PrepaidDepositsRequestDto;

@RestController
@RequestMapping("/v1/baskets")
@RequiredArgsConstructor
public class BasketDepositFolioController implements BasketDepositFolioControllerApiDocumentation {

  private final BasketInPort basketInPort;
  private final PrepaidDepositsRequestMapper prepaidDepositsRequestMapper;
  private final PrepaidDepositsResponseMapper prepaidDepositsResponseMapper;

  @PostMapping(value = "/deposit-folios")
  public ResponseEntity<Void> saveCharges(
      @RequestBody @Valid PrepaidDepositsRequestDto prepaidDepositsRequestDto) {
    var prepaidDepositRequest = prepaidDepositsRequestMapper.toModel(prepaidDepositsRequestDto);
    basketInPort.saveCharges(prepaidDepositRequest);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping(value = "/deposit-folios/{reservationId}")
  public ResponseEntity<PrepaidDepositsDto> getCharges(
      @PathVariable("reservationId") String reservationId) {

    var reservationDeposits = basketInPort.getCharges(reservationId);

    return ResponseEntity.status(HttpStatus.OK)
        .body(prepaidDepositsResponseMapper.toDto(reservationDeposits));
  }

  @GetMapping(value = "/deposit-folios")
  public ResponseEntity<PrepaidDepositsDto> getChargesForReservations(
      @ParameterObject @Valid DepositFoliosRequestDto depositFoliosRequestDto) {

    var reservationDeposits = basketInPort.getCharges(depositFoliosRequestDto.getReservationIds());

    return ResponseEntity.status(HttpStatus.OK)
        .body(prepaidDepositsResponseMapper.toDto(reservationDeposits));
  }

}
