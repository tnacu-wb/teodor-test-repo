package uk.co.whitbread.payments.util.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payments.model.PaymentResponse;
import uk.co.whitbread.payments.model.booking.WebhookBookingRequest;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;

@Component
@Slf4j
public class HotelBookingRequestBuilder {

    public WebhookBookingRequest buildHotelBookingRequst(PaymentResponse response) {

        return WebhookBookingRequest.builder()
                .paymentId(response.getPaymentId())
                .providerResponse(response.getProviderResponse())
                .booking(response.getBooking())
                .payment(response.getPayment())
                .build();
    }
}
