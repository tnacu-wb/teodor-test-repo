package uk.co.whitbread.payments.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.RefundRequest;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionResponse;
import uk.co.whitbread.payments.repository.PaymentRepository;
import uk.co.whitbread.payments.service.ProviderAccountFactory;
import uk.co.whitbread.payments.client.ThreeCPaymentClient;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DefaultRefundServiceTest {

    private static final String ANY_PAYMENT_ID = "123456789D";
    @Mock
    ThreeCPaymentClient threeCPaymentClient;
    @Mock
    PaymentRepository paymentRepository;
    @Mock
    ProviderAccountFactory providerAccountFactory;
    private DefaultRefundService defaultRefundService;
    private static ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        defaultRefundService = new DefaultRefundService(threeCPaymentClient, paymentRepository, providerAccountFactory);
    }

    @ParameterizedTest
    @MethodSource("createRefundRequests")
    void testRefundPayment(RefundRequest refundRequest) throws IOException {
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        lenient().when(paymentRepository.getByRequestId(any())).thenReturn(Mono.just(Optional.of(paymentsSchema)));
        lenient().when(threeCPaymentClient.noCardReadRequest(any(RefundRequest.class), any(), any())).thenReturn(Mono.just(objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/payRequestNoCardReadRefundResponse.json"), NoCardReadTransactionResponse.class)));
        lenient().when(paymentRepository.updateRefundResource(any()))
                .thenReturn(Mono.just(PaymentsSchema
                        .builder()
                        .paymentId(ANY_PAYMENT_ID)
                        .refunded(true)
                        .refundedOn(LocalDateTime.now())
                        .build()));
        var refundResponse = defaultRefundService.refund(refundRequest).block();
        assertNotNull(refundResponse);
        assertNotNull(refundResponse.getRefundId());
        assertTrue(refundResponse.isRefunded());
    }

    @ParameterizedTest
    @MethodSource("createRefundRequests")
    void testWhenRefundResourceNotFound(RefundRequest refundRequest) throws IOException {
        //setting refund resource empty
        lenient().when(paymentRepository.getByRequestId(any())).thenReturn(Mono.just(Optional.empty()));
        lenient().when(paymentRepository.createRefundResource(any())).thenReturn(Mono.just(mock(PaymentsSchema.class)));
        lenient().when(threeCPaymentClient.noCardReadRequest(any(RefundRequest.class), any(), any())).thenReturn(Mono.just(objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/payRequestNoCardReadRefundResponse.json"), NoCardReadTransactionResponse.class)));
        lenient().when(paymentRepository.updateRefundResource(any()))
                .thenReturn(Mono.just(PaymentsSchema
                        .builder()
                        .paymentId(ANY_PAYMENT_ID)
                        .refunded(true)
                        .refundedOn(LocalDateTime.now())
                        .build()));

        //then
        var refundResponse = defaultRefundService.refund(refundRequest).block();
        assertNotNull(refundResponse);
        assertNotNull(refundResponse.getRefundId());
        assertTrue(refundResponse.isRefunded());
    }

    static Stream<Arguments> createRefundRequests() throws IOException {
        objectMapper = new ObjectMapper().findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return Stream.of(
                Arguments.of(
                        objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createRefundRequest.json"), RefundRequest.class)
                )
        );
    }
}
