package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
public class StayingGuestAdditionalDetailsDto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dob;
  private String nationality;
  private String passportNumber;
}
