package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out;

import java.util.List;
import lombok.Builder;

@Builder
public record WorldlineBankDetailsStatusResponseDto(
    String responseCode,
    BankDetailsStatusDataDto data,
    List<WLErrorDto> errors
) {}