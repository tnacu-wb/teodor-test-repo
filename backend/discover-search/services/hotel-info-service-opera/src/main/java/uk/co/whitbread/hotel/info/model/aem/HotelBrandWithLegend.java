package uk.co.whitbread.hotel.info.model.aem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.info.model.domain.BartHotelBrandCode;

/**
 * Represents a brand and legend pairing, like: {"brandCode": "PID", "brandLegend": "Premier Inn Germany"}
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelBrandWithLegend {
    private BartHotelBrandCode brandCode;
    private String brandLegend;
}
