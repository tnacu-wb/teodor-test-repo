package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in;

import jakarta.validation.constraints.NotBlank;

public record WorldlineAppPreCheckRequestDto(@NotBlank String email) {}
