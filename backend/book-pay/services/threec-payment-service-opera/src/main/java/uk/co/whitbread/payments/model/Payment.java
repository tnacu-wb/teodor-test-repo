package uk.co.whitbread.payments.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import uk.co.whitbread.payments.validation.NotNullIfAnotherFieldHasValue;
import uk.co.whitbread.payments.validation.ValueOfEnum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@NotNullIfAnotherFieldHasValue.List({
        @NotNullIfAnotherFieldHasValue(
                dependFieldName = "environment",
                fieldName = "subType",
                fieldValue = "ECOMM")
})
@DynamoDbBean
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payment {

    @NotEmpty
    @Valid
    @ValueOfEnum(enumClass = PaymentType.class)
    @Schema(required = true, description = "Type of payment to process.", implementation = PaymentType.class, example = "CARD")
    private String type;

    @NotEmpty
    @Valid
    @ValueOfEnum(enumClass = PaymentSubType.class)
    @Schema(required = true, description = "Sub type of payment to process.", implementation = PaymentSubType.class, example = "ECOMM")
    private String subType;

    @Schema(description = "18-digit BART reference used in PAY_NOW transactions only", example = "123456789012345678")
    private String settlementReference;

    @Schema(example = "https://www.premierinn.com", description = "Browser window host name where ECOMM payment is being processed via iPage.")
    private String environment;

    @Schema(example = "true", description = "Denotes whether the cardholder does or cannot physically present the card to the merchant.")
    private boolean isCardPresent;

    @Schema(description = "Customer's saved card for payment or card for MOTO payment.")
    @Valid
    private Card card;

    @NotNull
    @Valid
    @Schema(required = true, description = "Information on amount to process payment for.")
    private Amount amount;

    @Valid
    @Schema(required = true, description = "Billing information for payment.")
    private Billing billing;

    @Valid
    @Schema(description = "Information about the mit payment")
    private Mit mit;

    @Schema(description = "paypalNonce used in PayPal transactions only", example = "GJTUG654892DFG74JDS654F967==")
    private String paypalNonce;

    @Schema(description = "paypalDeviceData used in PayPal transactions only", example = "DA34DFG5G67G547H57==")
    private String paypalDeviceData;

    public boolean equals(PaymentType paymentType, List<PaymentSubType> paymentSubTypes, String currency) {
        return this.getType().equals(paymentType.name()) && paymentSubTypes.contains(PaymentSubType.valueOf(this.getSubType())) && this.getAmount().getCurrency().equals(currency);
    }

    public boolean isEckohTransaction(List<PaymentSubType> paymentSubTypes) {
        return paymentSubTypes.contains(PaymentSubType.valueOf(this.getSubType()));
    }
}
