package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationTaxTypeInfo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResCashieringTypeDto {
  private ReservationTaxTypeInfoDto taxType;
}
