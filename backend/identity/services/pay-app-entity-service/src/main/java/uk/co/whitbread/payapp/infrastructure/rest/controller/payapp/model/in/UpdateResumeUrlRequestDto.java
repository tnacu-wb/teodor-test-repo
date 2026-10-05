package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import jakarta.validation.constraints.NotBlank;

public record UpdateResumeUrlRequestDto(
    @NotBlank String applicationId,
    @NotBlank String applicationGuid,
    @NotBlank String resumeUrl
) {}
