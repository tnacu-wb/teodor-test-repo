package uk.co.whitbread.business.tether.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import uk.co.whitbread.business.tether.validation.LinkCodeConstraint;


@Data
public class TetherLinkRequest {

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "gnj-jfz-eht")
    @LinkCodeConstraint
    private String linkCode;

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "3089500110017200017")
    private String linkId;

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "word")
    private String memorableWord;

    private boolean saveInCdh = false;
    
}
