package uk.co.whitbread.payapp.infrastructure.rest.client.worldline;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.TrustedPartnerCredentialsDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineHeadersDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.utils.WorldlineUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class WorldlineUtilsTest {

  @Test
  void serializeHeader_HappyCase_ReturnsSerializedJson() {
    // Arrange
    TrustedPartnerCredentialsDto authHeader = TrustedPartnerCredentialsDto.builder()
        .username("testUser")
        .password("testPass")
        .build();

    // Act
    String result = WorldlineUtils.serializeHeader(authHeader);

    // Assert
    assertEquals("{\"Username\":\"testUser\",\"Password\":\"testPass\"}", result);
  }

  @Test
  void serializeHeader_WhenExceptionThrown_ReturnsNull() {
    // Arrange
    TrustedPartnerCredentialsDto authHeader = new TrustedPartnerCredentialsDto() {
      @Override
      public String getUsername() {
        throw new RuntimeException("Simulated exception");
      }
    };

    // Act
    String result = WorldlineUtils.serializeHeader(authHeader);

    // Assert
    assertEquals(null, result);
  }

  @Test
  void setWorldlineHeaders_HappyCase_SetsHeadersCorrectly() {
    // Arrange
    WorldlineHeadersDto headersDto = WorldlineHeadersDto.builder()
        .companyNumber(12345)
        .trustedPartnerCredentialsDto(TrustedPartnerCredentialsDto.builder()
            .username("testUser")
            .password("testPass")
            .build())
        .cultureCode("en-GB")
        .ipAddress("127.0.0.1")
        .build();

    HttpHeaders httpHeaders = new HttpHeaders();

    // Act
    WorldlineUtils.setWorldlineHeaders(headersDto).accept(httpHeaders);

    // Assert
    assertEquals("application/json", httpHeaders.getFirst("Content-Type"));
    assertEquals("12345", httpHeaders.getFirst("CompanyNumber"));
    assertTrue(httpHeaders.getFirst("TrustedPartnerCredentials").contains("\"Username\":\"testUser\""));
    assertEquals("en-GB", httpHeaders.getFirst("CultureCode"));
    assertEquals("127.0.0.1", httpHeaders.getFirst("IPAddress"));
  }
}