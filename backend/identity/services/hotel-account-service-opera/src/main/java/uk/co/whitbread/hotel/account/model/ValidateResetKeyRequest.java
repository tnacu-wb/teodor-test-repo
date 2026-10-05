package uk.co.whitbread.hotel.account.model;

import jakarta.validation.constraints.NotBlank;

public record ValidateResetKeyRequest(@NotBlank String resetKey) {
}
