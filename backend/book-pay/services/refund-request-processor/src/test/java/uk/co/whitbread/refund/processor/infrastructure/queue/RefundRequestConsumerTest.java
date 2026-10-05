package uk.co.whitbread.refund.processor.infrastructure.queue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.refund.processor.domain.model.in.RefundRequest;
import uk.co.whitbread.refund.processor.domain.ports.primary.RefundRequestProcessInPort;
import uk.co.whitbread.refund.processor.infrastructure.queue.mapper.RefundRequestMapper;
import uk.co.whitbread.refund.processor.infrastructure.queue.model.refund.Amount;
import uk.co.whitbread.refund.processor.infrastructure.queue.model.refund.Booking;
import uk.co.whitbread.refund.processor.infrastructure.queue.model.refund.BusinessSite;
import uk.co.whitbread.refund.processor.infrastructure.queue.model.refund.Card;
import uk.co.whitbread.refund.processor.infrastructure.queue.model.refund.PaymentType;
import uk.co.whitbread.refund.processor.infrastructure.queue.model.refund.Refund;
import uk.co.whitbread.refund.processor.infrastructure.queue.model.refund.RefundReason;
import uk.co.whitbread.refund.processor.infrastructure.queue.model.refund.RefundRequestEvent;

@ExtendWith(MockitoExtension.class)
class RefundRequestConsumerTest {

    @Mock
    private RefundRequestProcessInPort refundRequestProcessInPort;

    @Mock
    private RefundRequestMapper refundRequestMapper;

    @InjectMocks
    private RefundRequestConsumer refundRequestConsumer;

    @Test
    void testConsumeRefundRequest__success() {
        var refundRequestEvent = getRefundRequestEvent();

        ConsumerRecord<String, RefundRequestEvent>
                consumerRecord = new ConsumerRecord<>("refunds", 1, 0,
                refundRequestEvent.getItemId(), refundRequestEvent);

        when(refundRequestMapper.toDomain(consumerRecord.value())).thenReturn(getRefundRequest());
        doNothing().when(refundRequestProcessInPort).processRefund(any());

        refundRequestConsumer.onRefund(consumerRecord);

        verify(refundRequestMapper, times(1)).toDomain(any(RefundRequestEvent.class));
        verify(refundRequestProcessInPort, times(1)).processRefund(any(RefundRequest.class));
    }

    @Test
    void testConsumeRefundRequest__dlt() {
        var refundRequestEvent = getRefundRequestEvent();

        when(refundRequestMapper.toDomain(refundRequestEvent)).thenReturn(getRefundRequest());
        doNothing().when(refundRequestProcessInPort).handleFailedRefund(any(RefundRequest.class));

        refundRequestConsumer.dltHandler(refundRequestEvent);

        verify(refundRequestMapper, times(1)).toDomain(any(RefundRequestEvent.class));
        verify(refundRequestProcessInPort, times(1)).handleFailedRefund(any(RefundRequest.class));
    }

    private RefundRequestEvent getRefundRequestEvent() {
        return RefundRequestEvent
                .builder()
                .itemId("ABCD#12345")
                .paymentId("123456789")
                .hotelCode("LONEUS")
                .basketReference("AKV123466")
                .refund(Refund
                        .builder()
                        .type(PaymentType.CARD)
                        .amount(Amount
                                .builder()
                                .currency("GBP")
                                .minorUnits(BigDecimal.TEN)
                                .build())
                        .card(Card
                                .builder()
                                .token("4216333880397891103")
                                .expiryYear("26")
                                .expiryMonth("03")
                                .build())
                        .reason(RefundReason.AMEND)
                        .build())
                .booking(Booking
                        .builder()
                        .channel("WEB")
                        .type("PAY_NOW")
                        .journey("REFUND")
                        .businessSite(BusinessSite
                                .builder()
                                .identifier("LONEUS")
                                .type("HOTEL")
                                .build())
                        .build())
                .build();
    }


    private RefundRequest getRefundRequest() {
        return RefundRequest
                .builder()
                .itemId("ABCD#12345")
                .paymentId("123456789")
                .hotelCode("LONEUS")
                .basketReference("AKV123466")
                .refund(uk.co.whitbread.refund.processor.domain.model.in.Refund
                        .builder()
                        .type(uk.co.whitbread.refund.processor.domain.model.in.PaymentType.CARD)
                        .amount(uk.co.whitbread.refund.processor.domain.model.in.Amount
                                .builder()
                                .currency("GBP")
                                .minorUnits(BigDecimal.TEN)
                                .build())
                        .card(uk.co.whitbread.refund.processor.domain.model.in.Card
                                .builder()
                                .token("4216333880397891103")
                                .expiryYear("26")
                                .expiryMonth("03")
                                .build())
                        .reason(uk.co.whitbread.refund.processor.domain.model.in.RefundReason.AMEND)
                        .build())
                .booking(uk.co.whitbread.refund.processor.domain.model.in.Booking
                        .builder()
                        .channel("WEB")
                        .type("PAY_NOW")
                        .journey("REFUND")
                        .businessSite(uk.co.whitbread.refund.processor.domain.model.in.BusinessSite
                                .builder()
                                .identifier("LONEUS")
                                .type("HOTEL")
                                .build())
                        .build())
                .build();
    }
}
