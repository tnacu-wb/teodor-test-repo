package uk.co.whitbread.hotel.card.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDTO {

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "120 Holborn")
    private String line1;

    @Schema
    private String line2;

    @Schema
    private String line3;

    @Schema
    private String line4;

    @Schema
    private String line5;

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "EC1N 2TD")
    private String postCode;

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "GB")
    private String countryCode;

    @Schema
    private String country;

    @Schema(example = "BUSINESS", allowableValues = "HOME, BUSINESS")
    private String type;

    @Schema(example = "Whitbread")
    private String companyName;

}
