package uk.co.whitbread.domain.model.migrationstatus.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelsMigrationStatusResponse {

  private List<HotelMigrationStatusResponse> migrationStatusList;
}
