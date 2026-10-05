package uk.co.whitbread.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTokenResponse {

    @NotNull
    @Schema(required = true, description = "Created token")
    private String token;

    @Schema(required = true, description = "Card type")
    private String cardType;

}
