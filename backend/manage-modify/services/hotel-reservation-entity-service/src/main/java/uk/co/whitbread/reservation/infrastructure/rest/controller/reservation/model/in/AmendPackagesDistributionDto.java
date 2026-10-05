package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmendPackagesDistributionDto {

  private String packageCode;
  private Double price;
  private Integer totalQuantity;

  public AmendPackagesDistributionDto(String packageCode, Integer totalQuantity) {
    this.packageCode = packageCode;
    this.totalQuantity = totalQuantity;
  }
}
