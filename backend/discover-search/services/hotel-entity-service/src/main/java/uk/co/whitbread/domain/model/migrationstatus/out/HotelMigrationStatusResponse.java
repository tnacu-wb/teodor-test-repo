package uk.co.whitbread.domain.model.migrationstatus.out;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelMigrationStatusResponse {

  private String hotelId;
  private String pmsSource;
  private Boolean onSale;
  private LocalDateTime updatedOn;

}
