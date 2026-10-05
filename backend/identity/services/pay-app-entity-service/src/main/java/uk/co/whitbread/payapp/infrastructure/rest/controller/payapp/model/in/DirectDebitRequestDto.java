package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import uk.co.whitbread.payapp.domain.model.in.DirectDebitOption;

public record DirectDebitRequestDto(
    @NotBlank String applicationId,
    @NotBlank String applicationGuid,
    @NotBlank String resumeUrl,
    @NotNull DirectDebitOption directDebitOption
) {}
