package uk.co.whitbread.address.lookup.infrastructure.rest.client.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockGetAddressResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressFormatQasMapperTest {

  @InjectMocks
  private AddressFormatQasMapper addressFormatQasMapper;

  @Test
  void toDto__ShouldReturnOK() {
    var result = addressFormatQasMapper.toDto("moniker123");
    assertEquals("moniker123", result.getMoniker());
  }

  @Test
  void toModel__ShouldReturnOK() {
    var result = addressFormatQasMapper.toModel(mockGetAddressResponse());
    assertEquals("TEST_LINE1", result.get().getAddressLine1());
    assertEquals("TEST_LINE2", result.get().getAddressLine2());
    assertEquals("TEST_LINE3", result.get().getAddressLine3());
    assertEquals("TEST_TOWN", result.get().getAddressLine4());
    assertEquals("TEST_POSTCODE", result.get().getPostalCode());
    assertEquals("TEST_COMPANY", result.get().getCompanyName());
    assertEquals(
        "TEST_COMPANY, TEST_LINE1, TEST_LINE2, TEST_LINE3, TEST_TOWN, TEST_COUNTY, TEST_POSTCODE",
        result.get().getLabel());
    assertEquals("GB", result.get().getCountry());
  }

  @Test
  void toModel__whenEmpty__ShouldReturnOK() {
    var result = addressFormatQasMapper.toModel(null);
    assertFalse(result.isPresent());
  }

}

