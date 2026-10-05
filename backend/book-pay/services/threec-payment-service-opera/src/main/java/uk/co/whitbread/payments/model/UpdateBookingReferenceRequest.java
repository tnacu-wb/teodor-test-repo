package uk.co.whitbread.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.Valid;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingReferenceRequest {

    @NotBlank
    @Valid
    @Schema(required = true, description = "Payment Id", example = "a4a63ec9-1065-4f91-8625-5fe7a7c5432c")
    private String paymentId;

    @NotBlank
    @Valid
    @Schema(required = true, description = "Booking Confirmation Number", example = "BR260692A")
    private String bookingReference;
}
