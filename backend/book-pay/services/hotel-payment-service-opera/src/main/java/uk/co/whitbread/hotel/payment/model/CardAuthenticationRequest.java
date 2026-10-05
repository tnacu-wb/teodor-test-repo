package uk.co.whitbread.hotel.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardAuthenticationRequest {

    @Valid
    @NotNull
    @Schema(required = true)
    private PaymentCard paymentCard;

    @Valid
    @Schema
    private Booker booker;

    @NotNull
    @NotEmpty
    @Schema(example = "https://www.myurl.com", required = true)
    private String redirectUrl;

    @Schema(example = "https://www.beta.premierinn.com")
    private String environment;

    @Schema
    private String cbtSessionId;

    @Valid
    @Schema
    private Device device;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Device {
        @Schema(example = "FULL_SCREEN")
        private String windowSize;

        @Schema(example = "STRING")
        private String colourDepth;

        @Schema(example = "true")
        private boolean javaEnabled;

        @Schema(example = "en-GB")
        private String language;

        @Schema(example = "900")
        private int screenHeight;

        @Schema(example = "1440")
        private int screenWidth;

        @Schema(example = "UTC")
        private String timeZone;
    }
}
