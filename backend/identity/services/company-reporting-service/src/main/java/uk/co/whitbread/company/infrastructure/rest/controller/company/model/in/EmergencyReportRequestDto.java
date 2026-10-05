package uk.co.whitbread.company.infrastructure.rest.controller.company.model.in;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record EmergencyReportRequestDto(
    @NotNull
    @Pattern(regexp = "EN|DE", flags = Pattern.Flag.CASE_INSENSITIVE)
    String language) {

}
