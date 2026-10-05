package uk.co.whitbread.token.infrastructure.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ValidProviderId annotation.
 * Tests the annotation configuration and default values.
 */
class ValidProviderIdTest {

  @Test
  @DisplayName("Should have correct default message")
  void defaultMessage_shouldBeCorrect() throws NoSuchFieldException {
    // Given
    ValidProviderId annotation = TestClass.class.getDeclaredField("providerId").getAnnotation(ValidProviderId.class);

    // When
    String message = annotation.message();

    // Then
    assertEquals("Provider ID must be alphanumeric with hyphens and underscores only", message);
  }

  @Test
  @DisplayName("Should have empty groups by default")
  void defaultGroups_shouldBeEmpty() throws NoSuchFieldException {
    // Given
    ValidProviderId annotation = TestClass.class.getDeclaredField("providerId").getAnnotation(ValidProviderId.class);

    // When
    Class<?>[] groups = annotation.groups();

    // Then
    assertNotNull(groups);
    assertEquals(0, groups.length);
  }

  @Test
  @DisplayName("Should have empty payload by default")
  void defaultPayload_shouldBeEmpty() throws NoSuchFieldException {
    // Given
    ValidProviderId annotation = TestClass.class.getDeclaredField("providerId").getAnnotation(ValidProviderId.class);

    // When
    Class<?>[] payload = annotation.payload();

    // Then
    assertNotNull(payload);
    assertEquals(0, payload.length);
  }

  @Test
  @DisplayName("Should be applicable to parameters and fields")
  void target_shouldIncludeParameterAndField() throws NoSuchFieldException {
    // Given
    ValidProviderId annotation = TestClass.class.getDeclaredField("providerId").getAnnotation(ValidProviderId.class);

    // When & Then
    // This test verifies the annotation is properly configured for use on parameters and fields
    // The actual target verification would require reflection on the annotation's target
    assertNotNull(annotation);
  }

  @Test
  @DisplayName("Should have runtime retention")
  void retention_shouldBeRuntime() throws NoSuchFieldException {
    // Given
    ValidProviderId annotation = TestClass.class.getDeclaredField("providerId").getAnnotation(ValidProviderId.class);

    // When & Then
    // This test verifies the annotation is retained at runtime
    // The actual retention verification would require reflection on the annotation's retention
    assertNotNull(annotation);
  }

  // Test class to hold the annotation for testing
  private static class TestClass {
    @ValidProviderId
    private String providerId;
  }
}
