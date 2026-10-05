package uk.co.whitbread.business.tether.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginCriteria {

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "92562846-62b5-42d8-b393-132a1d78fd1a")
    private String guid;
    @Schema(example = "12345678")
    private String worldlineSessionId;
    @Schema(example = "secret")
    private String worldlineSharedSecret;
    @Schema(example = "DE")
    private String scheme;
}
