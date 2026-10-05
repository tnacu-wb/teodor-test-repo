package uk.co.whitbread.ohip.infrastructure.rest.controller.udfs.model.in;

import jakarta.validation.constraints.NotEmpty;

public record CharacterUdfDto(@NotEmpty String name, @NotEmpty String value) {

}
