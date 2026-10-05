package uk.co.whitbread.hotel.info.converters;

import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.info.model.aem.AEMHotelKeyData;
import uk.co.whitbread.hotel.info.model.domain.HotelKeyData;

@Component
public class HotelKeyDataMapper {

    public HotelKeyData map(AEMHotelKeyData aemData) {

        HotelKeyData hotelKeyData = new HotelKeyData();

        if (aemData != null) {
            hotelKeyData.setCode(aemData.getCode());
            hotelKeyData.setName(aemData.getName());

            if (aemData.getHotelBrand() != null) {
                hotelKeyData.setBrand(aemData.getHotelBrand().getBrandCode());
            }
        }

        return hotelKeyData;
    }
}
