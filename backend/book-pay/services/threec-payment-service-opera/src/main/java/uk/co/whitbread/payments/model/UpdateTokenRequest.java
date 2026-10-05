package uk.co.whitbread.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTokenRequest extends BaseRequest {

    @NotNull
    @Schema(required = true, description = "Token number")
    private String token;

    @NotNull
    @Schema(required = true, description = "Card holder first name")
    private String cardHolderFirstName;

    @NotNull
    @Schema(required = true, description = "Card holder last name")
    private String cardHolderLastName;

    @NotNull
    @Schema(required = true, description = "Card holder address")
    private Address cardHolderAddress;

}