package uk.co.whitbread.ocd.infrastructure.rest.controller.tax;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.ocd.domain.ports.primary.TaxInfoInPort;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.mapper.TaxRequestMapper;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.mapper.TaxResponseMapper;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.model.in.TaxRequestDto;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.model.out.TaxResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class TaxController implements TaxApiDocumentation {

  private final TaxRequestMapper taxRequestMapper;

  private final TaxResponseMapper taxResponseMapper;

  private final TaxInfoInPort taxInfoInPort;

  @Override
  @GetMapping(value = "/hotels/{hotelId}/offer", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TaxResponseDto> getTaxDetails(
                           @PathVariable("hotelId") String hotelId,
                           @Valid @ParameterObject TaxRequestDto taxRequestDto) {
    final var taxRequest = taxRequestMapper.toModel(hotelId, taxRequestDto);
    var taxResponse = taxInfoInPort.getTaxDetails(taxRequest);
    final var taxResponseDto = taxResponseMapper.toDto(taxResponse);
    return ResponseEntity.status(HttpStatus.OK).body(taxResponseDto);
  }
}
