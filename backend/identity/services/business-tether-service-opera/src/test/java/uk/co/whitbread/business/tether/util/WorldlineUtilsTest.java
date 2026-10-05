package uk.co.whitbread.business.tether.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.co.whitbread.business.tether.utils.WorldlineUtils;
import worldline.mst.bsm.api.b2b.pi.data.TrustedPartnerCredentialsType;

class WorldlineUtilsTest {

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private WorldlineUtils worldlineUtils;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testSerializeObject() throws JsonProcessingException {
    // Arrange
    TrustedPartnerCredentialsType object = new TrustedPartnerCredentialsType();
    object.setUsername("username");
    object.setPassword("password");
    Mockito.when(objectMapper.writeValueAsString(any())).thenReturn("{\"Username\":\"username\",\"Password\":\"password\"}");

    // Act
    String result = worldlineUtils.serializeObject(object);

    // Assert
    assertEquals("{\"Username\":\"username\",\"Password\":\"password\"}", result);
  }

  @Test
  void testSerializeObjectShouldThrowException() throws JsonProcessingException {
    // Arrange
    Object obj = new Object();
    Mockito.when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

    // Act
    String result = worldlineUtils.serializeObject(obj);

    // Assert
    assertNull(result);
  }

}
