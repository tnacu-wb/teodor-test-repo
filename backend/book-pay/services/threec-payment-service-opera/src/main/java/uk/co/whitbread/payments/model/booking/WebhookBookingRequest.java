package uk.co.whitbread.payments.model.booking;

import lombok.*;
import uk.co.whitbread.payments.model.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WebhookBookingRequest {
    private String paymentId;
    private ProviderResponse providerResponse;
    private Booking booking;
    private Payment payment;
}
