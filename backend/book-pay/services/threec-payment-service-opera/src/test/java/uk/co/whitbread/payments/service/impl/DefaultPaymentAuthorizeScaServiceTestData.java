package uk.co.whitbread.payments.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.threec.InitialiseResponse;
import uk.co.whitbread.payments.properties.ProviderAccount;

import java.util.Base64;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class DefaultPaymentAuthorizeScaServiceTestData {

    protected static Stream<Arguments> authorizeSca() {
        return Stream.of(
//        success scenario - with response from calling Planet
                arguments(
                        new AuthorizeScaHandlerTestData(
                                AuthorizeScaRequest.builder()
                                        .requestId("123")
                                        .environment("LOCAL")
                                        .country("gb")
                                        .bookingReference("GAA7360661")
                                        .build(),
                                unitTest -> {
                                    var providerAccount = new ProviderAccount();
                                    var paymentId = "123";
                                    var initialiseResponse = new InitialiseResponse();
                                    initialiseResponse.setIpgResultCode(0);
                                    when(unitTest.providerAccountFactory.getAuthorizeScaAccount()).thenReturn(providerAccount);
                                    when(unitTest.paymentRepository.createAuthorizeScaResource(any(), any())).thenReturn(Mono.just(PaymentsSchema.builder()
                                            .paymentId(paymentId)
                                            .build()));
                                    when(unitTest.threeCPaymentClient.initialiseAuthorizeScaIpage(any(AuthorizeScaRequest.class), eq(paymentId),
                                            eq(providerAccount))).thenReturn(Mono.just(initialiseResponse));
                                    when(unitTest.templateService.getIPageHtml(any(), any(), eq(paymentId), any()))
                                            .thenReturn("<html/>");
                                },
                                response -> {
                                    Assert.notNull(response, "Response is null");
                                    Assertions.assertEquals("123", response.getPaymentId());
                                    Assertions.assertEquals(Base64.getEncoder().encodeToString("<html/>".getBytes()),
                                            response.getProviderResponse().getThreeCResponse().getIPageHtml());
                                },
                                false,
                                null)
                ),
//        success scenario - with response from DynamoDB
                arguments(
                        new AuthorizeScaHandlerTestData(
                                AuthorizeScaRequest.builder()
                                        .requestId("123")
                                        .environment("LOCAL")
                                        .country("de")
                                        .bookingReference("GAA7360661")
                                        .build(),
                                unitTest -> {
                                    var providerAccount = new ProviderAccount();
                                    var paymentId = "123";
                                    var initialiseResponse = new InitialiseResponse();
                                    initialiseResponse.setIpgResultCode(0);
                                    when(unitTest.providerAccountFactory.getAuthorizeScaAccount()).thenReturn(providerAccount);
                                    when(unitTest.paymentRepository.createAuthorizeScaResource(any(), any())).thenReturn(Mono.just(PaymentsSchema.builder()
                                            .paymentId(paymentId)
                                            .providerResponse(ProviderResponse.builder()
                                                    .threeCResponse(ThreeCResponse.builder()
                                                            .providerResult("752")
                                                            .iPageHtml("<html/>").build()).build())
                                            .build()));
                                },
                                response -> {
                                    Assert.notNull(response, "Response is null");
                                    Assertions.assertEquals("123", response.getPaymentId());
                                },
                                false,
                                null)
                ),
//        Exception in providerAccountFactory call
                arguments(
                        new AuthorizeScaHandlerTestData(
                                null,
                                unitTest -> when(unitTest.providerAccountFactory.getAuthorizeScaAccount()).thenThrow(new RuntimeException()),
                                null,
                                true,
                                RuntimeException.class)
                ),
                arguments(
                        new AuthorizeScaHandlerTestData(
                                AuthorizeScaRequest.builder()
                                        .requestId("123")
                                        .environment("LOCAL")
                                        .country("de")
                                        .bookingReference("GAA7360661")
                                        .build(),
                                unitTest -> {
                                    var providerAccount = new ProviderAccount();
                                    var paymentId = "123";
                                    var initialiseResponse = new InitialiseResponse();
                                    initialiseResponse.setIpgResultCode(0);
                                    when(unitTest.providerAccountFactory.getAuthorizeScaAccount()).thenReturn(providerAccount);
                                    when(unitTest.paymentRepository.createAuthorizeScaResource(any(), any())).thenReturn(Mono.just(PaymentsSchema.builder()
                                            .paymentId(paymentId)
                                            .providerResponse(ProviderResponse.builder()
                                                    .threeCResponse(ThreeCResponse.builder()
                                                            .providerResult("752")
                                                            .iPageHtml("<html/>")
                                                            .template("wb_newcard_sca_v5.xml")
                                                            .providerUrl("https://web2paytest.3cint.com")
                                                            .build()).build())
                                            .build()));
                                },
                                response -> {
                                    Assertions.assertEquals("wb_newcard_sca_v5.xml",
                                            response.getProviderResponse().getThreeCResponse().getTemplate());
                                    Assertions.assertEquals("https://web2paytest.3cint.com",
                                            response.getProviderResponse().getThreeCResponse().getProviderUrl());
                                },
                                false,
                                null)
                ),
            // Failure in threeCPaymentClient.initialiseAuthorizeScaIpage()
            arguments(
                new AuthorizeScaHandlerTestData(
                    AuthorizeScaRequest.builder()
                        .requestId("123")
                        .environment("LOCAL")
                        .country("gb")
                        .bookingReference("GAA7360661")
                        .build(),
                    unitTest -> {
                      var providerAccount = new ProviderAccount();
                      when(unitTest.providerAccountFactory.getAuthorizeScaAccount()).thenReturn(
                          providerAccount);
                      when(unitTest.paymentRepository.createAuthorizeScaResource(any(), any()))
                          .thenReturn(Mono.just(PaymentsSchema.builder().paymentId("123").build()));
                      when(
                          unitTest.threeCPaymentClient.initialiseAuthorizeScaIpage(any(), eq("123"),
                              eq(providerAccount)))
                          .thenReturn(Mono.error(new RuntimeException("API Failure")));
                    },
                    null,
                    true,
                    RuntimeException.class)
            ),
            // Null response from provider
            arguments(
                new AuthorizeScaHandlerTestData(
                    AuthorizeScaRequest.builder()
                        .requestId("123")
                        .environment("LOCAL")
                        .country("gb")
                        .bookingReference("GAA7360661")
                        .build(),
                    unitTest -> {
                      var providerAccount = new ProviderAccount();
                      when(unitTest.providerAccountFactory.getAuthorizeScaAccount()).thenReturn(
                          providerAccount);
                      when(unitTest.paymentRepository.createAuthorizeScaResource(any(), any()))
                          .thenReturn(Mono.just(PaymentsSchema.builder().paymentId("123").build()));
                      when(
                          unitTest.threeCPaymentClient.initialiseAuthorizeScaIpage(any(), eq("123"),
                              eq(providerAccount)))
                          .thenReturn(Mono.empty());
                    },
                    response -> Assertions.assertNull(response, "Response should be null"),
                    false,
                    null)
            )
        );
    }

    protected record AuthorizeScaHandlerTestData(AuthorizeScaRequest authorizeScaRequest,
                                                 Consumer<DefaultPaymentSaveCardServiceTest> mocks,
                                                 Consumer<PaymentResponse> assertions,
                                                 boolean isException, Class<? extends Throwable> exceptionType) {
    }
}
