package uk.co.whitbread.address.lookup.infrastructure.rest.controller.address;


import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.address.lookup.domain.ports.primary.AddressLookupInPort;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.mapper.AddressFormatDtoMapper;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.mapper.AddressSearchDtoMapper;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.in.AddressSearchRequestDto;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.out.AddressFormatResponseDto;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.out.AddressSearchResponseDto;

@RequestMapping("/v1")
@RestController
@Slf4j
@RequiredArgsConstructor
public class AddressLookupController implements AddressLookupApiDocumentation {

  private final AddressLookupInPort addressSearchInPort;
  private final AddressSearchDtoMapper addressSearchDtoMapper;
  private final AddressFormatDtoMapper addressFormatDtoMapper;

  @Override
  @GetMapping(value = ADDRESS_LOOKUP_BY_POSTCODE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AddressSearchResponseDto>> getAddressesByPostcode(
      @Valid @ParameterObject AddressSearchRequestDto addressSearchRequestDto) {

    log.info("Returning list of addresses for postcode {}",
        addressSearchRequestDto.getSearchTerm());

    var request = addressSearchDtoMapper.toModel(addressSearchRequestDto);
    final var addressSearchResponse = addressSearchInPort.getAddressesByPostcode(request);
    final var addressSearchResponseDto = addressSearchDtoMapper.toDto(
        addressSearchResponse);

    return ResponseEntity.status(HttpStatus.OK).body(addressSearchResponseDto);
  }

  @Override
  @GetMapping(value = FORMAT_ADDRESS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AddressFormatResponseDto> getFormattedAddress(
      @PathVariable("monikerId") String monikerId) {

    log.info("Returning formatted address for moniker-id {}", monikerId);

    final var addressFormatResponse = addressSearchInPort.getFormattedAddress(monikerId);
    final var formattedAddress = addressFormatDtoMapper.toDto(addressFormatResponse);
    return ResponseEntity.status(HttpStatus.OK).body(formattedAddress);
  }
}
