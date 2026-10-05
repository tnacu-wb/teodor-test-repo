package uk.co.whitbread.ohip.infrastructure.rest.client.token.service.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class TokenResponseTest {

  @Test
  void shouldCalculateExpirationTime_WhenBothFieldsArePresent() {
    // Given
    TokenResponse tokenResponse = new TokenResponse();
    Instant issuedAt = Instant.parse("2025-01-15T10:00:00Z");
    int expiresIn = 3600; // 1 hour

    tokenResponse.setIssuedAt(issuedAt.toString());
    tokenResponse.setExpiresIn(expiresIn);

    // When
    Instant expirationTime = tokenResponse.getExpirationTime();

    // Then
    assertThat(expirationTime)
        .isEqualTo(issuedAt.plusSeconds(expiresIn))
        .isEqualTo(Instant.parse("2025-01-15T11:00:00Z"));
  }

  @Test
  void shouldCalculateExpirationTime_WithDifferentExpiryDurations() {
    // Given - Test with 30 minutes
    TokenResponse tokenResponse = new TokenResponse();
    Instant issuedAt = Instant.parse("2025-01-15T10:00:00Z");
    int expiresIn = 1800; // 30 minutes

    tokenResponse.setIssuedAt(issuedAt.toString());
    tokenResponse.setExpiresIn(expiresIn);

    // When
    Instant expirationTime = tokenResponse.getExpirationTime();

    // Then
    assertThat(expirationTime)
        .isEqualTo(issuedAt.plusSeconds(expiresIn))
        .isEqualTo(Instant.parse("2025-01-15T10:30:00Z"));
  }

  @Test
  void shouldThrowException_WhenIssuedAtIsNull() {
    // Given
    TokenResponse tokenResponse = new TokenResponse();
    tokenResponse.setIssuedAt(null);
    tokenResponse.setExpiresIn(3600);

    // When/Then
    assertThatThrownBy(tokenResponse::getExpirationTime)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("issuedAt")
        .hasMessageContaining("Cannot determine token expiration time");
  }

  @Test
  void shouldThrowException_WhenExpiresInIsNull() {
    // Given
    TokenResponse tokenResponse = new TokenResponse();
    tokenResponse.setIssuedAt(Instant.now().toString());
    tokenResponse.setExpiresIn(null);

    // When/Then
    assertThatThrownBy(tokenResponse::getExpirationTime)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("expiresIn")
        .hasMessageContaining("Cannot determine token expiration time");
  }

  @Test
  void shouldThrowException_WhenBothFieldsAreNull() {
    // Given
    TokenResponse tokenResponse = new TokenResponse();
    tokenResponse.setIssuedAt(null);
    tokenResponse.setExpiresIn(null);

    // When/Then - Should fail on issuedAt first (checked first)
    assertThatThrownBy(tokenResponse::getExpirationTime)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("issuedAt")
        .hasMessageContaining("Cannot determine token expiration time");
  }

  @Test
  void shouldCalculateExpirationTime_WithZeroExpiresIn() {
    // Given - Edge case: token expires immediately
    TokenResponse tokenResponse = new TokenResponse();
    Instant issuedAt = Instant.parse("2025-01-15T10:00:00Z");
    int expiresIn = 0;

    tokenResponse.setIssuedAt(issuedAt.toString());
    tokenResponse.setExpiresIn(expiresIn);

    // When
    Instant expirationTime = tokenResponse.getExpirationTime();

    // Then
    assertThat(expirationTime).isEqualTo(issuedAt);
  }

  @Test
  void shouldCalculateExpirationTime_WithNegativeExpiresIn() {
    // Given - Edge case: negative expiresIn (should still calculate, even if unusual)
    TokenResponse tokenResponse = new TokenResponse();
    Instant issuedAt = Instant.parse("2025-01-15T10:00:00Z");
    int expiresIn = -3600; // -1 hour

    tokenResponse.setIssuedAt(issuedAt.toString());
    tokenResponse.setExpiresIn(expiresIn);

    // When
    Instant expirationTime = tokenResponse.getExpirationTime();

    // Then
    assertThat(expirationTime)
        .isEqualTo(issuedAt.plusSeconds(expiresIn))
        .isEqualTo(Instant.parse("2025-01-15T09:00:00Z"));
  }

  @Test
  void shouldThrowException_WhenIssuedAtIsMalformed() {
    TokenResponse tokenResponse = new TokenResponse();
    tokenResponse.setIssuedAt("not-a-timestamp");
    tokenResponse.setExpiresIn(3600);

    assertThatThrownBy(tokenResponse::getExpirationTime)
        .isInstanceOf(Exception.class);
  }

  @Test
  void jsonDeserialization_ShouldPopulateFields() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    String json = """
        {
          "accessToken": "abc123",
          "tokenType": "Bearer",
          "expiresIn": 7200,
          "issuedAt": "2025-01-01T00:00:00Z"
        }
        """;

    TokenResponse tokenResponse = mapper.readValue(json, TokenResponse.class);

    assertThat(tokenResponse.getAccessToken()).isEqualTo("abc123");
    assertThat(tokenResponse.getTokenType()).isEqualTo("Bearer");
    assertThat(tokenResponse.getExpiresIn()).isEqualTo(7200);
    assertThat(tokenResponse.getIssuedAt()).isEqualTo("2025-01-01T00:00:00Z");

    // And expiration time is computed correctly
    assertThat(tokenResponse.getExpirationTime()).isEqualTo(
        Instant.parse("2025-01-01T00:00:00Z").plusSeconds(7200)
    );
  }

  @Test
  void shouldHaveWorkingGettersAndSetters() {
    // Given
    TokenResponse tokenResponse = new TokenResponse();

    // When
    tokenResponse.setAccessToken("test-token");
    tokenResponse.setTokenType("Bearer");
    tokenResponse.setExpiresIn(3600);
    tokenResponse.setIssuedAt("2025-01-01T12:00:00Z");

    // Then
    assertThat(tokenResponse.getAccessToken()).isEqualTo("test-token");
    assertThat(tokenResponse.getTokenType()).isEqualTo("Bearer");
    assertThat(tokenResponse.getExpiresIn()).isEqualTo(3600);
    assertThat(tokenResponse.getIssuedAt()).isEqualTo("2025-01-01T12:00:00Z");
  }

  @Test
  void shouldHaveWorkingEqualsAndHashCode() {
    // Given
    TokenResponse tokenResponse1 = new TokenResponse();
    tokenResponse1.setAccessToken("token1");
    tokenResponse1.setTokenType("Bearer");
    tokenResponse1.setExpiresIn(3600);
    tokenResponse1.setIssuedAt("2025-01-01T12:00:00Z");

    TokenResponse tokenResponse2 = new TokenResponse();
    tokenResponse2.setAccessToken("token1");
    tokenResponse2.setTokenType("Bearer");
    tokenResponse2.setExpiresIn(3600);
    tokenResponse2.setIssuedAt("2025-01-01T12:00:00Z");

    TokenResponse tokenResponse3 = new TokenResponse();
    tokenResponse3.setAccessToken("token2");
    tokenResponse3.setTokenType("Bearer");
    tokenResponse3.setExpiresIn(3600);
    tokenResponse3.setIssuedAt("2025-01-01T12:00:00Z");

    // Then
    assertThat(tokenResponse1).isEqualTo(tokenResponse2)
        .isNotEqualTo(tokenResponse3);
    assertThat(tokenResponse1.hashCode()).isEqualTo(tokenResponse2.hashCode())
        .isNotEqualTo(tokenResponse3.hashCode());
  }

  @Test
  void shouldHaveWorkingToString() {
    // Given
    TokenResponse tokenResponse = new TokenResponse();
    tokenResponse.setAccessToken("test-token");
    tokenResponse.setTokenType("Bearer");
    tokenResponse.setExpiresIn(3600);
    tokenResponse.setIssuedAt("2025-01-01T12:00:00Z");

    // When
    String toString = tokenResponse.toString();

    // Then
    assertThat(toString).contains("test-token")
        .contains("Bearer")
        .contains("3600")
        .contains("2025-01-01T12:00:00Z");
  }
}

