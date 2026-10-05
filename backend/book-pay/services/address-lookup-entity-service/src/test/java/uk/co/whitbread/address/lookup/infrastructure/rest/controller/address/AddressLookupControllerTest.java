package uk.co.whitbread.address.lookup.infrastructure.rest.controller.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockAddressFormatResponse;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockAddressFormatResponseDto;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockAddressSearchRequestDto;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockAddressSearchResponse;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockAddressSearchResponseDto;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.address.lookup.domain.model.in.AddressSearchRequest;
import uk.co.whitbread.address.lookup.domain.ports.primary.AddressLookupInPort;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.mapper.AddressFormatDtoMapper;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.mapper.AddressSearchDtoMapper;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.out.AddressFormatResponseDto;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.out.AddressSearchResponseDto;

@ExtendWith(MockitoExtension.class)
class AddressLookupControllerTest {

  @Mock
  private AddressLookupInPort addressLookupInPort;
  @Mock
  private AddressSearchDtoMapper addressSearchDtoMapper;
  @Mock
  private AddressFormatDtoMapper addressFormatDtoMapper;
  @InjectMocks
  private AddressLookupController addressLookupController;

  @Test
  void getAddressesByPostcode__ShouldReturnOK() {
    // Arrange
    when(addressSearchDtoMapper.toModel(ArgumentMatchers.any())).thenReturn(
        AddressSearchRequest.builder().searchTerm("EC1N 2TD").build());
    when(addressLookupInPort.getAddressesByPostcode(ArgumentMatchers.any())).thenReturn(
        mockAddressSearchResponse());
    when(addressSearchDtoMapper.toDto(mockAddressSearchResponse())).thenReturn(
        mockAddressSearchResponseDto());

    // Act
    ResponseEntity<List<AddressSearchResponseDto>> response = addressLookupController.getAddressesByPostcode(
        mockAddressSearchRequestDto());

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void getFormattedAddress__ShouldReturnOK() {
    // Arrange
    when(addressLookupInPort.getFormattedAddress(anyString())).thenReturn(
        mockAddressFormatResponse());
    when(addressFormatDtoMapper.toDto(mockAddressFormatResponse())).thenReturn(
        mockAddressFormatResponseDto());

    // Act
    ResponseEntity<AddressFormatResponseDto> response = addressLookupController.getFormattedAddress(
        "GBX|0f670cfa-f262-4cad-b168-f91f80633833|7.730MOGBXDwfmBwAAAAABAwEAAAABouxHkgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTiAyVEQAAAAAAA--$8");

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
