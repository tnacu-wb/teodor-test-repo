package uk.co.whitbread.hotel.card.utils;

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
import uk.co.whitbread.hotel.card.client.worldline.model.TrustedPartnerCredentials;

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
  void testFormatId() {
    // Arrange
    String id = "id";

    // Act
    String result = worldlineUtils.formatId(id);

    // Assert
    assertEquals("{id}", result);
  }

  @Test
  void testFormatIdWithNullValue() {

    // Act
    String result = worldlineUtils.formatId(null);

    // Assert
    assertNull(result);
  }

  @Test
  void testSerializeHeader() throws JsonProcessingException {
    // Arrange
    TrustedPartnerCredentials authHeader = new TrustedPartnerCredentials();
    authHeader.setUsername("username");
    authHeader.setPassword("password");
    Mockito.when(objectMapper.writeValueAsString(any())).thenReturn("{\"Username\":\"username\",\"Password\":\"password\"}");

    // Act
    String result = worldlineUtils.serializeHeader(authHeader);

    // Assert
    assertEquals("{\"Username\":\"username\",\"Password\":\"password\"}", result);
  }

  @Test
  void testSerializeHeaderShouldThrowException() throws JsonProcessingException {
    // Arrange
    TrustedPartnerCredentials authHeader = new TrustedPartnerCredentials();
    authHeader.setUsername("username");
    authHeader.setPassword("password");
    Mockito.when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

    // Act
    String result = worldlineUtils.serializeHeader(authHeader);

    // Assert
    assertNull(result);
  }

  @Test
  void testSerializeObject() throws JsonProcessingException {
    // Arrange
    TrustedPartnerCredentials object = new TrustedPartnerCredentials();
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