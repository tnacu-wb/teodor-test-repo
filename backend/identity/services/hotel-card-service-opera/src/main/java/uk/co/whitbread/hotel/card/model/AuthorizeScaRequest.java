package uk.co.whitbread.hotel.card.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizeScaRequest {

    @NotEmpty
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String requestId;
    @Pattern(regexp = "^(https?://)([\\w.-]+)(:\\d+)?$")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String environment;
    @Pattern(regexp = "^[a-zA-Z]{2,3}$")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String language;
    private String bookingReference;

    @Builder.Default
    @JsonSetter(nulls = Nulls.SKIP)
    private String country = "gb";
}
