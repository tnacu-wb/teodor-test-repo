package uk.co.whitbread.payments.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {
    private String paymentId;
    private String requestId;
    private ProviderResponse providerResponse;
    private Booking booking;
    private Payment payment;
    private LocalDateTime createdOn;
    private boolean refunded;
    private LocalDateTime refundedOn;
    private String bookingReference;
    private String paymentStatus;
    private boolean revisedSolution;
    private SaveCardDetails saveCardDetails;
}
