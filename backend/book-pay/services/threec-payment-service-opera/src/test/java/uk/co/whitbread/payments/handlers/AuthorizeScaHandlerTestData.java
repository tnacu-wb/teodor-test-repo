package uk.co.whitbread.payments.handlers;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.model.AuthorizeScaRequest;
import uk.co.whitbread.payments.model.PaymentResponse;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthorizeScaHandlerTestData {
    protected static Stream<Arguments> authorizeSca() {
        return Stream.of(
//        success scenario
                arguments(
                        new AuthorizeScaTestData(
                                AuthorizeScaRequest.builder()
                                        .requestId("123")
                                        .environment("LOCAL")
                                        .country("de")
                                        .bookingReference("GAA7360661")
                                        .build(),
                                unitTest -> when(unitTest.defaultPaymentService
                                        .authorizeSca(any(AuthorizeScaRequest.class)))
                                        .thenReturn(Mono.just(new PaymentResponse())),
                                response -> {
                                    Assert.notNull(response, "Response is null");
                                    Assert.isTrue(response.statusCode().is2xxSuccessful(),
                                            "Response is not successful");
                                },
                                false,
                                null)
                ),
//        validation failure scenario
                arguments(
                        new AuthorizeScaTestData(
                                AuthorizeScaRequest.builder().build(),
                                null,
                                null,
                                true,
                                PaymentServiceException.class)
                ),
//        Exception in authorizeSca service call
                arguments(
                        new AuthorizeScaTestData(
                                AuthorizeScaRequest.builder()
                                        .requestId("123")
                                        .environment("LOCAL")
                                        .country("de")
                                        .bookingReference("GAA7360661")
                                        .build(),
                                unitTest -> when(unitTest.defaultPaymentService
                                        .authorizeSca(any(AuthorizeScaRequest.class)))
                                        .thenThrow(new RuntimeException()),
                                null,
                                true,
                                RuntimeException.class)
                ),
//         Missing mandatory field: requestId
                arguments(
                        new AuthorizeScaTestData(
                                AuthorizeScaRequest.builder()
                                        .environment("LOCAL")
                                        .language("en")
                                        .country("gb")
                                        .bookingReference("GAA7360661")
                                        .build(),
                                null,
                                null,
                                true,
                                PaymentServiceException.class)
                ),
                // Missing mandatory field: environment
                arguments(
                    new AuthorizeScaTestData(
                        AuthorizeScaRequest.builder()
                            .requestId("123")
                            .country("de")
                            .bookingReference("GAA7360661")
                            .build(),
                        null,
                        null,
                        true,
                        PaymentServiceException.class)
                ),
                arguments(
                    new AuthorizeScaTestData(
                        AuthorizeScaRequest.builder()
                            .requestId("456")
                            .environment("LOCAL")
                            .country("gb")
                            .bookingReference("GAI7841336")
                            .build(),
                        unitTest -> when(unitTest.defaultPaymentService
                            .authorizeSca(any(AuthorizeScaRequest.class)))
                            .thenReturn(Mono.just(new PaymentResponse())),
                        response -> {
                          Assert.notNull(response, "Response is null");
                          Assert.isTrue(response.statusCode().is2xxSuccessful(),
                              "Response is not successful");
                        },
                        false,
                        null)
                )
        );
    }

    protected record AuthorizeScaTestData(AuthorizeScaRequest authorizeScaRequest,
                                          Consumer<CardHandlerTest> mocks, Consumer<ServerResponse> assertions,
                                          boolean isException, Class<? extends Throwable> exceptionType) {
    }
}
