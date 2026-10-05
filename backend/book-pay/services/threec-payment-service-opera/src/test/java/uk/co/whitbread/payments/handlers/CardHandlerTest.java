package uk.co.whitbread.payments.handlers;

import jakarta.validation.Validation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.service.impl.DefaultPaymentService;
import uk.co.whitbread.payments.service.impl.DefaultValidationService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CardHandlerTest {

  @InjectMocks
  protected CardHandler cardHandler;
  @Spy
  protected DefaultValidationService defaultValidationService = new DefaultValidationService(
      Validation.buildDefaultValidatorFactory().getValidator());
  @Mock
  protected DefaultPaymentService defaultPaymentService;

  @InjectMocks
  protected ScaHandler scaHandler;

  @ParameterizedTest
  @MethodSource("uk.co.whitbread.payments.handlers.CardHandlerTestData#saveCard")
  void saveCard(CardHandlerTestData.SaveCardTestData saveCardTestData) {
    //Arrange
    var request = MockServerRequest.builder()
        .body(Mono.just(saveCardTestData.saveCardRequest()));
    Optional.ofNullable(saveCardTestData.mocks())
        .ifPresent(mocks -> mocks.accept(this));
    //Act & Assert
    if (saveCardTestData.isException()) {
      assertThrows(saveCardTestData.exceptionType(), () -> cardHandler.saveCard(request).block());
    } else {
      var response = cardHandler.saveCard(request).block();
      Optional.ofNullable(saveCardTestData.assertions())
          .ifPresent(assertions -> assertions.accept(response));
    }
  }

  @ParameterizedTest
  @MethodSource("uk.co.whitbread.payments.handlers.AuthorizeScaHandlerTestData#authorizeSca")
  void authorizeSca(AuthorizeScaHandlerTestData.AuthorizeScaTestData authorizeScaTestData) {
    //Arrange
    var request = MockServerRequest.builder()
            .body(Mono.just(authorizeScaTestData.authorizeScaRequest()));
    Optional.ofNullable(authorizeScaTestData.mocks())
            .ifPresent(mocks -> mocks.accept(this));
    //Act & Assert
    if (authorizeScaTestData.isException()) {
      assertThrows(authorizeScaTestData.exceptionType(), () -> scaHandler.authorizeSca(request).block());
    } else {
      var response = scaHandler.authorizeSca(request).block();
      Optional.ofNullable(authorizeScaTestData.assertions())
              .ifPresent(assertions -> assertions.accept(response));
    }
  }
}