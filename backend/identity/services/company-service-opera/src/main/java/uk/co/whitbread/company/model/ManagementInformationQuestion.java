package uk.co.whitbread.company.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagementInformationQuestion {
    private String questionId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    private String label;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Boolean mandatory;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    private String managementHeader;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Boolean active;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private QuestionLocation location;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private ManagementInformationAnswer managementInformationAnswer;
    private String type;

    private Integer positionId;
}
