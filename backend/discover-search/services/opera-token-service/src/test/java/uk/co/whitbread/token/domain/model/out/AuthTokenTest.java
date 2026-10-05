package uk.co.whitbread.token.domain.model.out;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class AuthTokenTest {

  @Test
  void constructor_validData_shouldCreateInstance() {
    // Act
    AuthToken token = new AuthToken(
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.uXrIhrveuvbR4tD1ULholQObboLVC-wIfJOEVElEzcs",
        "Bearer",
        3600L,
        "2025-01-01T00:00:00Z"
    );

    // Assert
    assertNotNull(token);
    assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.uXrIhrveuvbR4tD1ULholQObboLVC-wIfJOEVElEzcs", token.getAccessToken());
    assertEquals("Bearer", token.getTokenType());
    assertEquals(3600L, token.getExpiresIn());
    assertEquals("2025-01-01T00:00:00Z", token.getIssuedAt());
  }

  @Test
  void constructor_minimalData_shouldCreateInstance() {
    // Act
    AuthToken token = new AuthToken(
        "test-token",
        "Bearer",
        0L,
        null
    );

    // Assert
    assertNotNull(token);
    assertEquals("test-token", token.getAccessToken());
    assertEquals("Bearer", token.getTokenType());
    assertEquals(0L, token.getExpiresIn());
    assertNull(token.getIssuedAt());
  }

  @Test
  void constructor_allFields_shouldCreateInstance() {
    // Act
    AuthToken token = new AuthToken(
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.uXrIhrveuvbR4tD1ULholQObboLVC-wIfJOEVElEzcs",
        "Bearer",
        7200L,
        "2025-01-01T12:00:00Z"
    );

    // Assert
    assertNotNull(token);
    assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.uXrIhrveuvbR4tD1ULholQObboLVC-wIfJOEVElEzcs", token.getAccessToken());
    assertEquals("Bearer", token.getTokenType());
    assertEquals(7200L, token.getExpiresIn());
    assertEquals("2025-01-01T12:00:00Z", token.getIssuedAt());
  }

  @Test
  void constructor_nullValues_shouldCreateInstance() {
    // Act
    AuthToken token = new AuthToken(
        null,
        null,
        0L,
        null
    );

    // Assert
    assertNotNull(token);
    assertNull(token.getAccessToken());
    assertNull(token.getTokenType());
    assertEquals(0L, token.getExpiresIn());
    assertNull(token.getIssuedAt());
  }
}
