package uk.co.whitbread.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizeScaRequest extends BaseRequest {

    @NotEmpty
    @Schema(description = "Environment hostname")
    private String environment;
    @Schema(description = "Language specified by the user")
    private String language;
    @Schema(description = "Booking reference")
    private String bookingReference;
    @Schema(description = "Country as per User site", defaultValue = "gb")
    private String country = "gb";
}
