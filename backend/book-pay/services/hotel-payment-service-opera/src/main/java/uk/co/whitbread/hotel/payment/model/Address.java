package uk.co.whitbread.hotel.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import uk.co.whitbread.hotel.payment.validation.PostcodePresent;

@Data
@PostcodePresent
public class Address{

    @NotEmpty
    @Schema(required = true, example = "120 Holborn")
    private String line1;
    @Schema(example = "London")
    private String line2;
    @Schema(example = "STRING")
    private String line3;
    @Schema(example = "STRING")
    private String line4;
    @Schema(example = "STRING")
    private String line5;

    @NotEmpty
    @Schema(required = true, example = "UK")
    private String countryCode;
    @Schema(example = "EC1N 2TD")
    private String postcode;
    @Schema(example = "STRING")
    private String companyName;

}
