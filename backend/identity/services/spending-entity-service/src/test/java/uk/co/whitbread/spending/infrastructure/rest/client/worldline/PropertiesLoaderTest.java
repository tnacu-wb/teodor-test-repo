package uk.co.whitbread.spending.infrastructure.rest.client.worldline;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.spending.infrastructure.config.WorldlineProperties;
import uk.co.whitbread.spending.infrastructure.rest.client.worldline.service.PropertiesLoader;

@ExtendWith(MockitoExtension.class)
class PropertiesLoaderTest {

  @Mock
  private WorldlineProperties worldlineProperties;

  @InjectMocks
  private PropertiesLoader propertiesLoader;

  private WorldlineProperties.PropertiesByLocation deProperties;
  private WorldlineProperties.PropertiesByLocation gbProperties;

  @BeforeEach
  void setUp() {
    deProperties = new WorldlineProperties.PropertiesByLocation("de-DE", "user1", "pass1", "url1", "35");
    gbProperties = new WorldlineProperties.PropertiesByLocation("en-GB", "user2", "pass2", "url2", "91");
  }

  @Test
  void testGetPropertiesByLocation_ShouldPickDE() {
    String location = "DE";
    when(worldlineProperties.getDe()).thenReturn(deProperties);
    WorldlineProperties.PropertiesByLocation result = propertiesLoader.getPropertiesByLocation(location);
    assertEquals(deProperties, result);
    verify(worldlineProperties).getDe();
  }

  @Test
  void testGetPropertiesByLocation_ShouldPickGB() {
    String location = "GB";
    when(worldlineProperties.getGb()).thenReturn(gbProperties);
    WorldlineProperties.PropertiesByLocation result = propertiesLoader.getPropertiesByLocation(location);
    assertEquals(gbProperties, result);
    verify(worldlineProperties).getGb();
  }

  @Test
  void testGetPropertiesByLocation_ShouldWorkCaseInsensitive() {
    String location = "dE";
    when(worldlineProperties.getDe()).thenReturn(deProperties);
    WorldlineProperties.PropertiesByLocation result = propertiesLoader.getPropertiesByLocation(location);
    assertEquals(deProperties, result);
    verify(worldlineProperties).getDe();
  }

  @Test
  void testGetPropertiesByLocation_ByDefaultShouldPickGB() {
    String location = "FR";
    when(worldlineProperties.getGb()).thenReturn(gbProperties);
    WorldlineProperties.PropertiesByLocation result = propertiesLoader.getPropertiesByLocation(location);
    assertEquals(gbProperties, result);
    verify(worldlineProperties).getGb();
  }
}
