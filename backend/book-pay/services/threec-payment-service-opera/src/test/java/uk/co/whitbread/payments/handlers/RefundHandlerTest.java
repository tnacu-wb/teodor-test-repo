package uk.co.whitbread.payments.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.co.whitbread.payments.model.RefundRequest;
import uk.co.whitbread.payments.model.RefundResponse;
import uk.co.whitbread.payments.service.impl.DefaultRefundService;
import uk.co.whitbread.payments.service.impl.DefaultValidationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundHandlerTest {

    @Mock
    private DefaultRefundService defaultRefundService;

    @Mock
    private DefaultValidationService defaultValidationService;

    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private RefundHandler refundHandler;

    private RefundRequest refundRequest;
    private RefundResponse refundResponse;


    @BeforeEach
    void setUp() {
        refundRequest = new RefundRequest();
        refundResponse = new RefundResponse();
    }

    @Test
    void testRefund_Success() {
        doNothing().when(defaultValidationService).validate(any(RefundRequest.class));
        when(defaultRefundService.refund(any(RefundRequest.class))).thenReturn(Mono.just(refundResponse));
        when(serverRequest.bodyToMono(RefundRequest.class)).thenReturn(Mono.just(refundRequest));

        Mono<ServerResponse> responseMono = refundHandler.refund(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(defaultValidationService, times(1)).validate(refundRequest);
        verify(defaultRefundService, times(1)).refund(refundRequest);
    }

    @Test
    void testRefund_ValidationFailure() {
        doThrow(new RuntimeException("Validation failed")).when(defaultValidationService).validate(any(RefundRequest.class));
        when(serverRequest.bodyToMono(RefundRequest.class)).thenReturn(Mono.just(refundRequest));

        Mono<ServerResponse> responseMono = refundHandler.refund(serverRequest);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Validation failed"))
                .verify();

        verify(defaultValidationService, times(1)).validate(refundRequest);
        verify(defaultRefundService, times(0)).refund(any());
    }

    @Test
    void testRefund_RefundServiceFailure() {
        doNothing().when(defaultValidationService).validate(any(RefundRequest.class));
        when(defaultRefundService.refund(any(RefundRequest.class))).thenReturn(Mono.error(new RuntimeException("Refund failed")));
        when(serverRequest.bodyToMono(RefundRequest.class)).thenReturn(Mono.just(refundRequest));

        Mono<ServerResponse> responseMono = refundHandler.refund(serverRequest);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Refund failed"))
                .verify();

        verify(defaultValidationService, times(1)).validate(refundRequest);
        verify(defaultRefundService, times(1)).refund(refundRequest);
    }
}