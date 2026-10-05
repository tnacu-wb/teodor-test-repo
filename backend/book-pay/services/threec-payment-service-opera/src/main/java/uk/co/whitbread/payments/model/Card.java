package uk.co.whitbread.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Card {

    @Schema(description = "3C Payment token.", example = "4943056398164344242")
    private String token;

    @Schema(description = "Expiry month for the payment card/token.", example = "01", format = "MM")
    private String expiryMonth;

    @Schema(description = "Expiry year for the payment card/token.", example = "21", format = "YY")
    private String expiryYear;

    /**
     * Exclusive to MOTO payments
     */
    @Schema(description = "CVV for the payment card/token.", example = "222")
    private String cvv;

    @DynamoDbIgnore
    public String getCvv() {
        return cvv;
    }
}
