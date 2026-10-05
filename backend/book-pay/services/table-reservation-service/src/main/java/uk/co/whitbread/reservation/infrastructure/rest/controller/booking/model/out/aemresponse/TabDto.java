package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TabDto {

  private String name;
  private List<ColumnDto> columns;

}
