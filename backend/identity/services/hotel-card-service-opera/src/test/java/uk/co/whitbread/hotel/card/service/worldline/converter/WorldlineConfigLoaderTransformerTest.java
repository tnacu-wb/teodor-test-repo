package uk.co.whitbread.hotel.card.service.worldline.converter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.whitbread.hotel.card.utils.WorldlineUtils;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.properties.WorldLineProperties.De;
import uk.co.whitbread.piba.api.properties.WorldLineProperties.Gb;
import uk.co.whitbread.piba.api.properties.WorldLineProperties.Piba;
import uk.co.whitbread.piba.api.properties.WorldLineProperties.Piba.Service;

@ExtendWith(MockitoExtension.class)
public class WorldlineConfigLoaderTransformerTest {

  private static final String URL = "https://test.worldline/mockurl";

  @Mock
  private WorldLineProperties worldLineProperties;

  @Mock
  private WorldlineUtils worldlineUtils;

  private Set<String> supportedCountries = new HashSet<>()
  {{ add("gb");}};

  @Test
  public void shouldLoadWorldlineHeaderData() {

    Mockito.when(worldLineProperties.getPiba()).thenReturn(getPiba());
    Mockito.when(worldLineProperties.getDe()).thenReturn(new De());
    Mockito.when(worldLineProperties.getGb()).thenReturn(new Gb());
    var testClass = new WorldlineConfigLoaderTransformer(worldLineProperties, worldlineUtils);
    ReflectionTestUtils.setField(testClass, "supportedCountries", supportedCountries);

    testClass.loadWorldlineHeaderData();

    assertNotNull(ReflectionTestUtils.getField(testClass, "worldlineConfigs"));
  }

  private Piba getPiba() {
    Piba piba = new Piba();
    Service service = new Service();
    service.setUrl(URL);
    piba.setService(service);
    piba.setUsername("");
    piba.setPassword("");
    return piba;
  }

}
