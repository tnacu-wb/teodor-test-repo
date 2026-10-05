package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RemoveParticipantRequestDto(
    @NotBlank String applicationId,
    @NotBlank String applicationGuid,
    @NotNull int employeeId
) {}
