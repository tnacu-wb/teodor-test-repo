package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationPackagesDetailsResponseDto {

  private String packageCode;
  private String description;
  private BigDecimal unitPrice;
  private Integer totalQuantity;
  private String packageGroup;
  private BigDecimal computedPrice;
  private BigDecimal grossPrice;
  private BigDecimal vatTax;
  private String startDate;
  private String endDate;
}
