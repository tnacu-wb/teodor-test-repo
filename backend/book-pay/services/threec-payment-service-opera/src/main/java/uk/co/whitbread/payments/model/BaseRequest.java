package uk.co.whitbread.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseRequest {

    @NotBlank
    @Schema(description = "Unique reference for transaction provided by consumer.", example = "a0a9f782-98ee-468c-9839-30c487c832a3")
    private String requestId;
}
