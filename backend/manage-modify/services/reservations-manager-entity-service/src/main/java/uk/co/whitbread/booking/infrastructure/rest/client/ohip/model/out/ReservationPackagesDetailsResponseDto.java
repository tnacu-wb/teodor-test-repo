package uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
