package uk.co.whitbread.piba.registration.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import uk.co.whitbread.piba.registration.validation.NullOrNotBlank;

@Data
public class RegistrationDetails {

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    protected String title;

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    protected String forename;

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    protected String surname;

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    protected String emailAddress;

    @NullOrNotBlank
    protected String mobileNumber;

    @NullOrNotBlank
    protected String landlineNumber;

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "word")
    protected String memorableWord;
}
