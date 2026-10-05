package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DirectDebitRequest(
    @NotBlank String applicationId,
    @NotBlank String applicationGuid,
    @NotBlank String resumeUrl,
    @NotNull DirectDebitOption directDebitOption
) {}
