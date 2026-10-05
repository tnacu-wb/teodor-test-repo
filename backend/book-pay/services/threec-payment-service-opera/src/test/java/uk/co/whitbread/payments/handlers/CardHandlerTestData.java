package uk.co.whitbread.payments.handlers;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.model.Address;
import uk.co.whitbread.payments.model.PaymentResponse;
import uk.co.whitbread.payments.model.SaveCardRequest;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CardHandlerTestData {
  protected static Stream<Arguments> saveCard() {
    return Stream.of(
//        success scenario
        arguments(
            new SaveCardTestData(
                SaveCardRequest.builder()
                    .requestId("123")
                    .cardType("PIBA")
                    .billingAddress(Address.builder()
                        .build())
                    .environment("LOCAL")
                    .build(),
                unitTest -> when(unitTest.defaultPaymentService.saveCard(any(SaveCardRequest.class))).thenReturn(Mono.just(new PaymentResponse())),
                response -> {
                  Assert.notNull(response, "Response is null");
                  Assert.isTrue(response.statusCode().is2xxSuccessful(), "Response is not successful");
                },
                false,
                null)
        ),
//        validation failure scenario
        arguments(
            new SaveCardTestData(
                SaveCardRequest.builder().build(),
                null,
                null,
                true,
                PaymentServiceException.class)
        ),
//        Exception in saveCard service call
        arguments(
            new SaveCardTestData(
                SaveCardRequest.builder()
                    .requestId("123")
                    .cardType("PIBA")
                    .billingAddress(Address.builder()
                        .build())
                    .environment("LOCAL")
                    .build(),
                unitTest -> when(unitTest.defaultPaymentService.saveCard(any(SaveCardRequest.class))).thenThrow(new RuntimeException()),
                null,
                true,
                RuntimeException.class)
        )
    );
  }

  protected record SaveCardTestData(SaveCardRequest saveCardRequest,
                                    Consumer<CardHandlerTest> mocks, Consumer<ServerResponse> assertions,
                                    boolean isException, Class<? extends Throwable> exceptionType) {
  }
}
