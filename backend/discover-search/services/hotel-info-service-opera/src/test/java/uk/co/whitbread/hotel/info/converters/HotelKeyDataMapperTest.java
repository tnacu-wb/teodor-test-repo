package uk.co.whitbread.hotel.info.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.info.model.aem.AEMHotelKeyData;
import uk.co.whitbread.hotel.info.model.aem.HotelBrandWithLegend;
import uk.co.whitbread.hotel.info.model.domain.BartHotelBrandCode;
import uk.co.whitbread.hotel.info.model.domain.HotelKeyData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HotelKeyDataMapperTest {

    private HotelKeyDataMapper hotelKeyDataMapper;

    @BeforeEach
    public void setUp() {

        hotelKeyDataMapper = new HotelKeyDataMapper();
    }

    @Test
    public void successful() {

        AEMHotelKeyData aemHotelKeyData = new AEMHotelKeyData();
        aemHotelKeyData.setCode("007");
        aemHotelKeyData.setName("Bond");
        aemHotelKeyData.setHotelBrand(new HotelBrandWithLegend(BartHotelBrandCode.PI, "PI"));

        HotelKeyData actual = hotelKeyDataMapper.map(aemHotelKeyData);

        assertEquals("007", actual.getCode());
        assertEquals("Bond", actual.getName());
        assertEquals(BartHotelBrandCode.PI, actual.getBrand());
    }

    @Test
    public void default_key_data_when_given_null() {

        HotelKeyData actual = hotelKeyDataMapper.map(null);

        assertNull(actual.getCode());
        assertNull(actual.getName());
        assertNull(actual.getBrand());
    }

    @Test
    public void no_nullpointer_when_null_brand() {

        AEMHotelKeyData aemHotelKeyData = new AEMHotelKeyData();
        aemHotelKeyData.setCode("007");
        aemHotelKeyData.setName("Bond");

        HotelKeyData actual = hotelKeyDataMapper.map(aemHotelKeyData);

        assertEquals("007", actual.getCode());
        assertEquals("Bond", actual.getName());
        assertNull(actual.getBrand());
    }
}