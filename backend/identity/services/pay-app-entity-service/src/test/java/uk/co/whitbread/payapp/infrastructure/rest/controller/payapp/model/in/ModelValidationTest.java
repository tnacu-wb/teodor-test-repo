package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payapp.domain.model.in.Scheme;

@ExtendWith(MockitoExtension.class)
class ModelValidationTest {

  @Test
  void constructor_appInit_shouldSelfValidateOk() {
    assertDoesNotThrow(() -> {
      var initializeApplicationRequestDto = new InitializeApplicationRequestDto(
          "john.doe@email.com", Scheme.GB, null, null);
      initializeApplicationRequestDto.validate();
    });
  }

}
