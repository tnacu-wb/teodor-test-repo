package uk.co.whitbread.business.tether.model;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class TetherLinkResponse {
    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "92562846-62b5-42d8-b393-132a1d78fd1a")
    private String guid;
}
