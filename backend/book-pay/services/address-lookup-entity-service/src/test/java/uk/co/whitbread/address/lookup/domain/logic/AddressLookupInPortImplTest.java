package uk.co.whitbread.address.lookup.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockAddressFormatResponse;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockAddressSearchRequest;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockAddressSearchResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.address.lookup.domain.ports.secondary.AddressLookupOutPort;

@ExtendWith(MockitoExtension.class)
class AddressLookupInPortImplTest {

  @Mock
  private AddressLookupOutPort addressLookupOutPort;

  @InjectMocks
  private AddressLookupInPortImpl addressLookupInPort;

  @Test
  void getAddressesByPostcode__shouldReturnOk() {
    //Arrange
    when(this.addressLookupOutPort.getAddressesByPostcode(anyString())).thenReturn(
        mockAddressSearchResponse());

    //Act
    final var addressSearchResponse = addressLookupInPort.getAddressesByPostcode(
        mockAddressSearchRequest());
    assertThat(addressSearchResponse, notNullValue());
    assertEquals(
        "GBX|0f670cfa-f262-4cad-b168-f91f80633833|7.730MOGBXDwfmBwAAAAABAwEAAAABouxHkgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTiAyVEQAAAAAAA--$8",
        addressSearchResponse.getFirst().getMonikerId());
    assertEquals("Money Pensions Service, 120 Holborn, LONDON, EC1N 2TD",
        addressSearchResponse.getFirst().getAddress());

    verify(addressLookupOutPort, times(1)).getAddressesByPostcode(anyString());
  }

  @Test
  void getFormattedAddress__shouldReturnOk() {
    //Arrange
    when(this.addressLookupOutPort.getFormattedAddress(anyString())).thenReturn(
        mockAddressFormatResponse());

    //Act
    final var addressFormatResponse =
        addressLookupInPort.getFormattedAddress(
            "GBX|0f670cfa-f262-4cad-b168-f91f80633833|7.730MOGBXDwfmBwAAAAABAwEAAAABouxHkgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTiAyVEQAAAAAAA--$8");
    assertThat(addressFormatResponse, notNullValue());
    assertEquals("120 Holborn", addressFormatResponse.getAddressLine1());
    assertEquals("LONDON", addressFormatResponse.getAddressLine4());
    assertEquals("EC1N 2TD", addressFormatResponse.getPostalCode());
    assertEquals("GB", addressFormatResponse.getCountry());

    verify(addressLookupOutPort, times(1)).getFormattedAddress(anyString());
  }

}
