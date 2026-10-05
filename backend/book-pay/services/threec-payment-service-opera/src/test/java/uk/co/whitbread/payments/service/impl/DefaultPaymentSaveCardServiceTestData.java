package uk.co.whitbread.payments.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.util.Assert;
import java.util.Base64;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.threec.InitialiseResponse;
import uk.co.whitbread.payments.properties.ProviderAccount;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class DefaultPaymentSaveCardServiceTestData {

  protected static Stream<Arguments> saveCard() {
    return Stream.of(
//        success scenario - with response from calling Planet
        arguments(
            new SaveCardHandlerTestData(
                SaveCardRequest.builder()
                    .requestId("123")
                    .cardType("PIBA")
                    .billingAddress(Address.builder()
                        .build())
                    .environment("LOCAL")
                    .build(),
                unitTest -> {
                  var providerAccount = new ProviderAccount();
                  var paymentId = "123";
                  var initialiseResponse = new InitialiseResponse();
                  initialiseResponse.setIpgResultCode(0);
                  when(unitTest.providerAccountFactory.getSaveCardAccount(any())).thenReturn(providerAccount);
                  when(unitTest.paymentRepository.createPaymentSaveCardResource(any(), any())).thenReturn(Mono.just(PaymentsSchema.builder()
                      .paymentId(paymentId)
                      .saveCardDetails(SaveCardDetails.builder().build())
                      .build()));
                  when(unitTest.threeCPaymentClient.initialiseIpage(any(SaveCardRequest.class), eq(paymentId),
                      any(), eq(providerAccount))).thenReturn(Mono.just(initialiseResponse));
                  when(unitTest.templateService.getIPageHtml(any(), any(), eq(paymentId), any()))
                      .thenReturn("<html/>");
                },
                response -> {
                  Assert.notNull(response, "Response is null");
                  Assertions.assertEquals("123", response.getPaymentId());
                  Assertions.assertEquals(Base64.getEncoder().encodeToString("<html/>".getBytes(StandardCharsets.UTF_8)),
                      response.getProviderResponse().getThreeCResponse().getIPageHtml());
                },
                false,
                null)
        ),
//        success scenario - with response from DynamoDB
        arguments(
            new SaveCardHandlerTestData(
                SaveCardRequest.builder()
                    .requestId("123")
                    .cardType("PIBA")
                    .billingAddress(Address.builder()
                        .build())
                    .environment("LOCAL")
                    .build(),
                unitTest -> {
                  var providerAccount = new ProviderAccount();
                  var paymentId = "123";
                  var initialiseResponse = new InitialiseResponse();
                  initialiseResponse.setIpgResultCode(0);
                  when(unitTest.providerAccountFactory.getSaveCardAccount(any())).thenReturn(providerAccount);
                  when(unitTest.paymentRepository.createPaymentSaveCardResource(any(), any())).thenReturn(Mono.just(PaymentsSchema.builder()
                      .paymentId(paymentId)
                      .saveCardDetails(SaveCardDetails.builder().build())
                      .providerResponse(ProviderResponse.builder()
                          .threeCResponse(ThreeCResponse.builder()
                              .providerResult("752")
                              .iPageHtml("<html/>").build()).build())
                      .build()));
                },
                response -> {
                  Assert.notNull(response, "Response is null");
                  Assertions.assertEquals("123", response.getPaymentId());
                  Assertions.assertEquals("<html/>",
                      response.getProviderResponse().getThreeCResponse().getIPageHtml());
                },
                false,
                null)
        ),
//        Exception in providerAccountFactory call
        arguments(
            new SaveCardHandlerTestData(
                null,
                unitTest -> when(unitTest.providerAccountFactory.getSaveCardAccount(any())).thenThrow(new RuntimeException()),
                null,
                true,
                RuntimeException.class)
        ),
//        Exception in providerAccountFactory call
        arguments(
            new SaveCardHandlerTestData(
                null,
                unitTest -> when(unitTest.providerAccountFactory.getSaveCardAccount(any())).thenThrow(new RuntimeException()),
                null,
                true,
                RuntimeException.class)
        )
    );
  }

  protected record SaveCardHandlerTestData(SaveCardRequest saveCardRequest,
                                           Consumer<DefaultPaymentSaveCardServiceTest> mocks,
                                           Consumer<PaymentResponse> assertions,
                                           boolean isException, Class<? extends Throwable> exceptionType) {
  }
}
