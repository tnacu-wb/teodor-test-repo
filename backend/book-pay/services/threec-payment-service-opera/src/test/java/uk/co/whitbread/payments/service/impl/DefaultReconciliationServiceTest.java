package uk.co.whitbread.payments.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.model.BusinessSite;
import uk.co.whitbread.payments.model.ReconciliationRequest;
import uk.co.whitbread.payments.model.threec.StartReconciliationResponse;
import uk.co.whitbread.payments.client.ThreeCPaymentClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultReconciliationServiceTest {
    private static final String HOTEL_CODE = "LONHOL";
    @Mock
    ThreeCPaymentClient threeCPaymentClientMock;

    private DefaultReconciliationService defaultReconciliationService;

    @BeforeEach
    void setUp() {
        defaultReconciliationService = new DefaultReconciliationService(threeCPaymentClientMock);
    }

    @Test
    void testSuccessfulStartReconciliation() {
        var startReconciliationResponse = new StartReconciliationResponse(0, "Success");
        when(threeCPaymentClientMock.startReconciliation(any(), any())).thenReturn(Mono.just(startReconciliationResponse));
        ReconciliationRequest reconciliationRequest = ReconciliationRequest.builder().requestId(UUID.randomUUID().toString()).businessSite(BusinessSite.builder().identifier(HOTEL_CODE).build()).build();
        var result = defaultReconciliationService.reconcile(reconciliationRequest).block();
        assertTrue(result);
    }

    @Test
    void verifyPaymentExceptionWhenReconciliationNonZeroResultCode() {
        var startReconciliationResponse = new StartReconciliationResponse(666, "Failure");
        when(threeCPaymentClientMock.startReconciliation(any(), any())).thenReturn(Mono.just(startReconciliationResponse));
        ReconciliationRequest reconciliationRequest = ReconciliationRequest.builder().requestId(UUID.randomUUID().toString()).businessSite(BusinessSite.builder().identifier(HOTEL_CODE).build()).build();
        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> defaultReconciliationService.reconcile(reconciliationRequest).block());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    }
}