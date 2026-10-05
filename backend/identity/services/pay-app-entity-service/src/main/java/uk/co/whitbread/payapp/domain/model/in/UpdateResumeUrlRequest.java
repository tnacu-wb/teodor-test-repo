package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;

public record UpdateResumeUrlRequest(
    @NotBlank String applicationId,
    @NotBlank String applicationGuid,
    @NotBlank String resumeUrl
) {}
