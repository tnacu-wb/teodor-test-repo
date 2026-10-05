package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out;

import java.util.List;
import lombok.Builder;

@Builder
public record WorldlineAppPreCheckResponseDto(
    String responseCode,
    AppPreCheckDataDto data,
    List<WLErrorDto> errors
) {}