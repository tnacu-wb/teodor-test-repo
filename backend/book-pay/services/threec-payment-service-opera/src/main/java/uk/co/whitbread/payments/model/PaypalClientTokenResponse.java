package uk.co.whitbread.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaypalClientTokenResponse {

    @NotNull
    @Schema(required = true, description = "Created PayPal Client token")
    private String clientToken;

    @NotNull
    @Schema(required = true, description = "Created PayPal Client ID")
    private String clientId;

    @NotNull
    @Schema(required = true, description = "Generated DateTime of PayPal Client token")
    private LocalDateTime generatedAt;
}
