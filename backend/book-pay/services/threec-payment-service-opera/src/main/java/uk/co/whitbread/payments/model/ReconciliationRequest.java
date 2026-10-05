package uk.co.whitbread.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationRequest extends BaseRequest {

    @NotNull
    @Schema(required = true, description = "Information on the business site to start reconciliation for.")
    private BusinessSite businessSite;
}
