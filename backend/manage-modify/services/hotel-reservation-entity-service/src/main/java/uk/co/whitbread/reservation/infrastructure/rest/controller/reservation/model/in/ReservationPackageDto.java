package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationPackageDto {
  private BigDecimal unitPrice;
  private Integer quantity;
  private String packageCode;
  @FutureOrPresent
  private LocalDate startDate;
  @Future
  private LocalDate endDate;
}
