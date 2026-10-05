package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;

public record DeleteCardRequest(
    @NotBlank String applicationGuid,
    @NotBlank String applicationId,
    @NotBlank String cardGuid
) {}
