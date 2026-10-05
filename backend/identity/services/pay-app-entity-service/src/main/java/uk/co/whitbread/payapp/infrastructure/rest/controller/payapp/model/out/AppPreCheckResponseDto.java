package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out;

import lombok.Builder;

@Builder
public record AppPreCheckResponseDto(
    Boolean isTetheredUser,
    String applicationGuid
) {
}
