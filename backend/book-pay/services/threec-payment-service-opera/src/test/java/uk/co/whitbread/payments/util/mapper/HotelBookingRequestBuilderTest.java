package uk.co.whitbread.payments.util.mapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.model.booking.WebhookBookingRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HotelBookingRequestBuilderTest {

    @Test
    public void verifyHotelBookingRequestMapping() {

       var hotelBookingRequestBuilder = new HotelBookingRequestBuilder();
       var request =  WebhookBookingRequest.builder()
                .paymentId("123456789D")
                .providerResponse(ProviderResponse.builder()
                        .threeCResponse(ThreeCResponse.builder()
                                .providerStatus("CQ")
                                .providerResult("0000")
                                .scaReference("VA123456789")
                                .token("1234567890")
                                .build())
                        .build())
                .booking(Booking.builder()
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .payment(Payment.builder()
                        .amount(Amount.builder()
                                .minorUnits(123)
                                .currency("GBP")
                                .build())
                        .build())
                .build();

        var paymentResponse = PaymentResponse.builder()
                .paymentId("123456789D")
                .providerResponse(ProviderResponse.builder()
                        .threeCResponse(ThreeCResponse.builder()
                                .providerStatus("CQ")
                                .providerResult("0000")
                                .scaReference("VA123456789")
                                .token("1234567890")
                                .build())
                        .build())
                .booking(Booking.builder()
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .payment(Payment.builder()
                        .amount(Amount.builder()
                                .minorUnits(123)
                                .currency("GBP")
                                .build())
                        .build())
                .build();

        var actual = hotelBookingRequestBuilder.buildHotelBookingRequst(paymentResponse);

        assertEquals(request.getPaymentId(), actual.getPaymentId());
        assertEquals(request.getProviderResponse(), actual.getProviderResponse());
        assertEquals(request.getBooking(), actual.getBooking());
        assertEquals(request.getPayment(), actual.getPayment());
    }
}
