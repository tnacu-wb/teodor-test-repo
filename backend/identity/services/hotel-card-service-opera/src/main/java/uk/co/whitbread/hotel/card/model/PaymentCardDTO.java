package uk.co.whitbread.hotel.card.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentCardDTO {
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean business;
    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean personalCard;

    private String customerAccountId;
    private String companyAccountId;
    private String employeeAccountId;
    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String userEmail;

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String cardToken;
    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String cardNumberLast4Digits;
    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String expiryDate;
    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String cardType;
    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String cardHolderName;
    
    @Valid
    private AddressDTO billingAddress;

    private Boolean cnpRequired;
    private String cnpBusinessAccountUsername;
    private String cnpBusinessAccountPassword;
    private String cardId;
    private String cardLabel;
}
