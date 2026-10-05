package uk.co.whitbread.hotel.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uk.co.whitbread.hotel.payment.validation.NotNullIfAnotherFieldHasValue;

@NotNullIfAnotherFieldHasValue.List({
        @NotNullIfAnotherFieldHasValue(
                dependFieldName = "cardType",
                fieldName = "useExistingCard",
                fieldValue = "false"),
        @NotNullIfAnotherFieldHasValue(
                dependFieldName = "cardNumber",
                fieldName = "useExistingCard",
                fieldValue = "false"),
        @NotNullIfAnotherFieldHasValue(
                dependFieldName = "expiryDate",
                fieldName = "useExistingCard",
                fieldValue = "false"),
        @NotNullIfAnotherFieldHasValue(
                dependFieldName = "cardholderName",
                fieldName = "useExistingCard",
                fieldValue = "false")
})
@Data
public class PaymentCard {

    @Schema(example = "AM")
    private String cardType;
    @Schema(example = "4444333322221111")
    private String cardNumber;
    @Schema(example = "01/19")
    private String expiryDate;
    @Schema(example = "John Smith")
    private String cardholderName;
    @Schema(example = "123")
    private String cardSecurityCode;

    @Schema(example = "01/15")
    private String startDate;
    @Schema(example = "STRING")
    private String issueNumber;

    @Schema(example = "false")
    private boolean prepaymentRequired;
    @Schema(example = "STRING")
    private String paymentAuthenticationResponse;

    @NotNull
    @Schema(required = true, example = "false",
            description = "If useExistingCard=false then cardNumber, cardType, cardholderName and expiryDate are mandatory.")
    private boolean useExistingCard;
    @Valid
    private Address billingAddress;
    @Valid
    private BusinessAccount businessAccount;

    @Schema(example = "STRING")
    private String cbtCentralCardId;

    @Schema(example = "STRING")
    private String cbtEmployeeCardId;


}
