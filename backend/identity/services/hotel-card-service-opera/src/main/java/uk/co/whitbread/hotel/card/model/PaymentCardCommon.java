package uk.co.whitbread.hotel.card.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.hotel.card.model.validation.ConfirmCommonPaymentCardDetails;

@Data
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ConfirmCommonPaymentCardDetails
public class PaymentCardCommon {
    private String userEmail;
    private String cardToken;
    private String cardNumber;
    private String expiryDate;
    @NotEmpty
    private String cardType;
    private String cardHolderName;
    @Valid
    private AddressDTO billingAddress;
    @NotNull
    private Boolean cnpRequired;
    private String cnpBusinessAccountUsername;
    private String cnpBusinessAccountPassword;
}
