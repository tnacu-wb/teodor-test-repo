package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import uk.co.whitbread.payapp.domain.model.in.Scheme;

public record GetDdSepaFormStatusRequestDto(
    @NotBlank String hostedPageGuid,
    @NotNull Scheme scheme
) {}
