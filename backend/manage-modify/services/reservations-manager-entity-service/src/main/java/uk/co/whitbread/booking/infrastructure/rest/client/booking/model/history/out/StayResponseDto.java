package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StayResponseDto {

  private List<StayDto> stays;
  private Integer pageIndex;
  private int pageSize;
  private int totalSize;
  private TypesTotalsStaysDto totals;
  private String continuationToken;
}
