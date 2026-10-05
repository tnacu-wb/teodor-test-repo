package uk.co.whitbread.hotel.account.client.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTokenRequest {
    private String requestId;
    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private CardHolder cardHolder;
}
