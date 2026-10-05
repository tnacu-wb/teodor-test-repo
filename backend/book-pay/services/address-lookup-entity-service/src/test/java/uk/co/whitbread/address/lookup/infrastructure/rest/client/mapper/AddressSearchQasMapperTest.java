package uk.co.whitbread.address.lookup.infrastructure.rest.client.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockQASearchResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.config.QasProperties;

@ExtendWith(MockitoExtension.class)
class AddressSearchQasMapperTest {

  @Mock
  private QasProperties qasProperties;
  @InjectMocks
  private AddressSearchQasMapper addressSearchQasMapper;

  @Test
  void toDto__ShouldReturnOK() {
    when(qasProperties.getCountryBusinessDataSetId()).thenReturn("GB_BUS_TEST");
    when(qasProperties.getLayoutLeisure()).thenReturn("QADefault_LEIS_TEST");

    var result = addressSearchQasMapper.toDto("POSTCODE");
    assertEquals("POSTCODE", result.getSearch());
    assertEquals("GB_BUS_TEST", result.getCountry());
    assertEquals("QADefault_LEIS_TEST", result.getLayout());
    assertEquals("Default", result.getEngine().getPromptSet().value());
    assertEquals("Singleline", result.getEngine().getValue().value());
    assertTrue(result.getEngine().isFlatten());
  }

  @Test
  void toModel__ShouldReturnOK() {
    var result = addressSearchQasMapper.toModel(mockQASearchResponse());
    assertThat(result, hasSize(2));
    assertEquals("moniker123", result.get(0).getMonikerId());
    assertEquals("moniker123123", result.get(1).getMonikerId());
  }

}
