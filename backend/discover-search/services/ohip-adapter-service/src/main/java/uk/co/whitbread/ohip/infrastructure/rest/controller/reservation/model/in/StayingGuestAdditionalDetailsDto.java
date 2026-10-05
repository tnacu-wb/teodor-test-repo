package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StayingGuestAdditionalDetailsDto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dob;
  @Size(max = 20, message = "Nationality must be at most 20 characters long")
  private String nationality;
  @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Passport number must contain only alphanumeric characters")
  private String passportNumber;
}
