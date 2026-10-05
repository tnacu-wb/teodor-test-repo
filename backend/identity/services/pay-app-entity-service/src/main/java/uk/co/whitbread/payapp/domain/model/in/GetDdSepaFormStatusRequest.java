package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record GetDdSepaFormStatusRequest(
    @NotBlank String hostedPageGuid,
    @NotNull Scheme scheme
) {}
