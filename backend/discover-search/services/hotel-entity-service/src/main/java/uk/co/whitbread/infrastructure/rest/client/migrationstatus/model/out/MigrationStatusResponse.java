package uk.co.whitbread.infrastructure.rest.client.migrationstatus.model.out;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MigrationStatusResponse {

  private String hotelId;
  private String pmsSource;
  private Boolean onSale;
  private LocalDateTime updatedOn;

}
