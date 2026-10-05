package uk.co.whitbread.token.domain.model.out;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class TokenResponseTest {

  @Test
  void builder_validData_shouldCreateInstance() {
    // Act
    TokenResponse response = TokenResponse.builder()
        .accessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
        .tokenType("Bearer")
        .expiresIn(3600)
        .issuedAt("2025-01-01T00:00:00Z")
        .build();

    // Assert
    assertNotNull(response);
    assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9", response.getAccessToken());
    assertEquals("Bearer", response.getTokenType());
    assertEquals(3600, response.getExpiresIn());
    assertEquals("2025-01-01T00:00:00Z", response.getIssuedAt());
  }

  @Test
  void builder_minimalData_shouldCreateInstance() {
    // Act
    TokenResponse response = TokenResponse.builder()
        .accessToken("test-token")
        .build();

    // Assert
    assertNotNull(response);
    assertEquals("test-token", response.getAccessToken());
    assertNull(response.getTokenType());
    assertEquals(0, response.getExpiresIn());
    assertNull(response.getIssuedAt());
  }

  @Test
  void builder_allFields_shouldCreateInstance() {
    // Act
    TokenResponse response = TokenResponse.builder()
        .accessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.uXrIhrveuvbR4tD1ULholQObboLVC-wIfJOEVElEzcs")
        .tokenType("Bearer")
        .expiresIn(7200)
        .issuedAt("2025-01-01T12:00:00Z")
        .build();

    // Assert
    assertNotNull(response);
    assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.uXrIhrveuvbR4tD1ULholQObboLVC-wIfJOEVElEzcs", response.getAccessToken());
    assertEquals("Bearer", response.getTokenType());
    assertEquals(7200, response.getExpiresIn());
    assertEquals("2025-01-01T12:00:00Z", response.getIssuedAt());
  }
}
