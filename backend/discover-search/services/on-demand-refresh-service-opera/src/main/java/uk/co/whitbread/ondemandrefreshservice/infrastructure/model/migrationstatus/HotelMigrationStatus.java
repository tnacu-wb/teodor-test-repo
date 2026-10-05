package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.migrationstatus;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class HotelMigrationStatus {
    private String hotelId;
    private String pmsSource;
    private boolean onSale;
    private LocalDateTime updatedOn;
}
