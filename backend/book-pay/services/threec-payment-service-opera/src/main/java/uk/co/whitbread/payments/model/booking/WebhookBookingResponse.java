package uk.co.whitbread.payments.model.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WebhookBookingResponse {

    private String sessionId;

    private String confirmationNumber;

    private BookingStatus bookingStatus;

    private String code;
}
