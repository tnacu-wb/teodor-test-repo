package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import jakarta.validation.constraints.NotEmpty;

public record ChangeBasketIdContextDto(
    @NotEmpty
    String idContext) {
}