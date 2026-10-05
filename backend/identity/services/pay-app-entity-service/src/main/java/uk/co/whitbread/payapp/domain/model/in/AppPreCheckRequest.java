package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppPreCheckRequest(
    @NotBlank String email,
    @NotNull Scheme scheme
) {
}
