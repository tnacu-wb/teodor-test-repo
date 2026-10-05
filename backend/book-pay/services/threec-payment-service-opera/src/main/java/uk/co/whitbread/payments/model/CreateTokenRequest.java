package uk.co.whitbread.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import static java.util.Optional.of;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTokenRequest extends BaseRequest {

    @NotNull
    @Schema(required = true, description = "Information on the type of Payment to process.")
    private String cardNumber;

    @Schema(required = true, description = "Information on the type of Payment to process.")
    @NotNull
    private String expiryMonth;

    @Schema(required = true, description = "Information on the type of Payment to process.")
    @NotNull
    private String expiryYear;

    @Valid
    @Schema(required = true, description = "Information regarding the booking being made.")
    private CardHolder cardHolder;

    public String getCountryCode() {
        return of(this)
                .map(CreateTokenRequest::getCardHolder)
                .map(CardHolder::getAddress)
                .map(Address::getCountryCode)
                .orElse(null);
    }
}
