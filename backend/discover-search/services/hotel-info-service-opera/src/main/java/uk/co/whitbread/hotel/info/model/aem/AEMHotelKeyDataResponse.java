package uk.co.whitbread.hotel.info.model.aem;

import lombok.Data;
import uk.co.whitbread.hotel.info.model.aem.AEMHotelKeyData;

import java.util.ArrayList;
import java.util.List;

/**
 * Response from an AEM call to /en/directoryService/hotel/{hotelCode}. Note that data appears to only be available in english.
 * Switching 'en' to 'de' and trying FRAMTI returns nothing. Other hotel codes don't work, either.
 */

@Data
public class AEMHotelKeyDataResponse {
    private List<AEMHotelKeyData> hotels;

    public List<AEMHotelKeyData> getHotels() {
        if (hotels == null) {
            hotels = new ArrayList<>();
        }
        return hotels;
    }
}
