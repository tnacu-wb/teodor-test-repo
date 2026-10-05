package uk.co.whitbread.infrastructure.rest.client.migrationstatus.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MigrationStatus {

  private List<MigrationStatusResponse> migrationStatusList;
}
