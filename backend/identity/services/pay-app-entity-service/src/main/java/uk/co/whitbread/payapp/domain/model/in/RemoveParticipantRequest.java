package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RemoveParticipantRequest(
    @NotBlank String applicationId,
    @NotBlank String applicationGuid,
    @NotNull int employeeId
) {}
