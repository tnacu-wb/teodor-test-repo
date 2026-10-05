package uk.co.whitbread.address.lookup.infrastructure.rest.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockAddressFormatResponse;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockAddressSearchResponse;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.address.QasClient;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.mapper.AddressFormatQasMapper;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.mapper.AddressSearchQasMapper;
import uk.co.whitbread.qas.addresslookup.api.Address;
import uk.co.whitbread.qas.addresslookup.api.QAGetAddress;
import uk.co.whitbread.qas.addresslookup.api.QASearch;
import uk.co.whitbread.qas.addresslookup.api.QASearchResult;

@ExtendWith(MockitoExtension.class)
class AddressLookupOutPortImplTest {

  @Mock
  private QasClient qasClient;
  @Mock
  private AddressSearchQasMapper addressSearchMapper;
  @Mock
  private AddressFormatQasMapper addressFormatQasMapper;
  @InjectMocks
  private AddressLookupOutPortImpl addressLookupOutPort;

  @Test
  void getAddressesByPostcode__ShouldReturnOK() {
    // Arrange
    when(addressSearchMapper.toDto(any())).thenReturn(new QASearch());
    when(qasClient.search(any())).thenReturn(new QASearchResult());
    when(addressSearchMapper.toModel(any(QASearchResult.class))).thenReturn(
        mockAddressSearchResponse());

    // Act
    var response = addressLookupOutPort.getAddressesByPostcode("LU1 1BN");

    // Assert
    verifyNoMoreInteractions(addressSearchMapper);
    verifyNoMoreInteractions(qasClient);
    assertThat(response.getFirst().getMonikerId(),
        is("GBX|0f670cfa-f262-4cad-b168-f91f80633833|7.730MOGBXDwfmBwAAAAABAwEAAAABouxHkgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTiAyVEQAAAAAAA--$8"));
    assertThat(response.getFirst().getAddress(),
        is("Money Pensions Service, 120 Holborn, LONDON, EC1N 2TD"));
  }

  @Test
  void getFormattedAddress__ShouldReturnOK() {
    // Arrange
    when(addressFormatQasMapper.toDto(any())).thenReturn(new QAGetAddress());
    when(qasClient.getFormattedAddress(any()))
        .thenReturn(new Address());
    when(addressFormatQasMapper.toModel(any(Address.class))).thenReturn(
        Optional.ofNullable(mockAddressFormatResponse()));

    // Act
    var response = addressLookupOutPort.getFormattedAddress(
        "R0JSfjcuNzMwdE9HQlJEd2ZtQndBQUFBQUJBd0VBQUFBQUU2QnZVZ0FoRUFJUUFDQUFBQUFBQUFBQUFQLi5aQUFBQUFELi4uLi5BQUFBQUFBQUFBQUFBQUFBQUFBQVRGVXhJREZDVGdBQUFBQUF-N34xMDA");

    // Assert
    verifyNoMoreInteractions(qasClient);
    verifyNoMoreInteractions(addressFormatQasMapper);
    assertThat(response.getAddressLine1(), is("120 Holborn"));
    assertThat(response.getAddressLine4(), is("LONDON"));
    assertThat(response.getPostalCode(), is("EC1N 2TD"));
    assertThat(response.getCountry(), is("GB"));
  }
}
