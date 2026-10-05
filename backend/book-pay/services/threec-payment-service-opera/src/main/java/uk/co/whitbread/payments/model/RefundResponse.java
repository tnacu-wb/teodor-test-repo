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
public class RefundResponse {

    private String refundId;
    private String requestId;
    private String paymentId;
    private boolean refunded;
    private LocalDateTime refundedOn;
    private ProviderResponse providerResponse;
    private Refund refund;
    private Booking booking;
}
