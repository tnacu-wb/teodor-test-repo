package uk.co.whitbread.hotel.info.model.aem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.info.model.domain.HotelKeyData;

/**
 * Models the key data for a hotel, meaning its code, brand and name. Different from {@link HotelKeyData},
 * because this uses the structure from AEM.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AEMHotelKeyData {
    private String code;
    private String name;
    private HotelBrandWithLegend hotelBrand;
}
