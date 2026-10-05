package uk.co.whitbread.hotel.payment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ThreeDSecureVersion2Response {
    private String datacashReference;

    private String transactionId;

    @JsonIgnore
    private String result;

    private String gatewayRecommendation;

    private final String sessionId;

    private boolean success;
}
