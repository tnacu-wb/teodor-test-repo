package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import jakarta.validation.constraints.NotBlank;

public record DeleteCardRequestDto(
    @NotBlank String applicationGuid,
    @NotBlank String applicationId,
    @NotBlank String cardGuid
) {}