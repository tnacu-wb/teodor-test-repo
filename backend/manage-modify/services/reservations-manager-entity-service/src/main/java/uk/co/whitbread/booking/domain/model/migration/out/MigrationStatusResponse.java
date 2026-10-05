package uk.co.whitbread.booking.domain.model.migration.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MigrationStatusResponse {

  private String hotelId;
  private String onSale;
  private PmsSource pmsSource;
}
