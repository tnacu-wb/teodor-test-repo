package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out;

import lombok.Builder;

@Builder
public record BankDetailsStatusDataDto(String status) {
}
