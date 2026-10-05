package uk.co.whitbread.payment.orchestrator.domain.model.payment.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.payment.ReturnUrlValidator;

/**
 * Tests for {@link ConditionalValidationValidator}.
 *
 * <p>This validator works at the class (TYPE) level on {@link PaymentInitRequest} instances.
 * Field-level validation on sealed interface hierarchies causes
 * {@code jakarta.validation.UnexpectedTypeException} because the Bean Validation provider
 * cannot resolve a single {@code ConstraintValidator} applicable to all permitted subtypes'
 * field types. The class-level approach receives the full record instance and dispatches
 * validation logic based on the concrete subtype via {@code instanceof} checks.
 */
@ExtendWith(MockitoExtension.class)
class ConditionalValidationValidatorTest {

  @Mock
  private ReturnUrlValidator returnUrlValidator;

  @Mock
  private ConstraintValidatorContext context;

  @Mock
  private ConstraintViolationBuilder violationBuilder;

  @Mock
  private NodeBuilderCustomizableContext nodeBuilder;

  @InjectMocks
  private ConditionalValidationValidator validator;

  @BeforeEach
  void setUp() {
    lenient().when(context.buildConstraintViolationWithTemplate(anyString()))
        .thenReturn(violationBuilder);
    lenient().when(violationBuilder.addPropertyNode(anyString()))
        .thenReturn(nodeBuilder);
  }

  @Nested
  class NullRequest {

    @Test
    void nullRequest_isValid() {
      assertThat(validator.isValid(null, context)).isTrue();
    }
  }

  @Nested
  class NewCardWebRequest {

    @Test
    void validReturnUrl_isValid() {
      var request = new NewCardWebInitRequest(
          "basket-123", "https://www.premierinn.com/return",
          "gb", "en", "LEISURE", "PI");

      when(returnUrlValidator.isValidReturnUrl("https://www.premierinn.com/return"))
          .thenReturn(true);

      assertThat(validator.isValid(request, context)).isTrue();
    }

    @Test
    void invalidReturnUrl_isNotValid() {
      var request = new NewCardWebInitRequest(
          "basket-123", "http://evil.com/phishing",
          "gb", "en", "LEISURE", "PI");

      when(returnUrlValidator.isValidReturnUrl("http://evil.com/phishing"))
          .thenReturn(false);

      assertThat(validator.isValid(request, context)).isFalse();
      verify(context).disableDefaultConstraintViolation();
      verify(context).buildConstraintViolationWithTemplate(
          "returnUrl must use HTTPS and belong to an allowed host");
      verify(violationBuilder).addPropertyNode("returnUrl");
      verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void nullReturnUrl_delegatesToNotBlank() {
      // When returnUrl is null, the validator skips (allows @NotBlank to handle it)
      var request = new NewCardWebInitRequest(
          "basket-123", null, "gb", "en", "LEISURE", "PI");

      assertThat(validator.isValid(request, context)).isTrue();
      verify(returnUrlValidator, never()).isValidReturnUrl(anyString());
    }

    @Test
    void blankReturnUrl_delegatesToNotBlank() {
      // When returnUrl is blank, the validator skips (allows @NotBlank to handle it)
      var request = new NewCardWebInitRequest(
          "basket-123", "  ", "gb", "en", "LEISURE", "PI");

      assertThat(validator.isValid(request, context)).isTrue();
      verify(returnUrlValidator, never()).isValidReturnUrl(anyString());
    }
  }

  @Nested
  class NewCardMobileRequest {

    @Test
    void mobileRequest_isAlwaysValid() {
      // Mobile requests have no returnUrl — validation should pass through
      var request = new NewCardMobileInitRequest(
          "basket-456", "gb", "en", "LEISURE", "APPS_IOS");

      assertThat(validator.isValid(request, context)).isTrue();
      verify(returnUrlValidator, never()).isValidReturnUrl(anyString());
    }
  }
}
