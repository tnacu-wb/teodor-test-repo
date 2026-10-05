package uk.co.whitbread.piba.account.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.piba.account.model.Currency;
import worldline.mst.bsm.api.b2b.pi.data.CurrencyType;

public class ConvertersTest {
    public static final String TETHERED_USER_GUID="327f7a0c-9a33-41c2-808d-74f15f24797c";


    @Test
    void testConvertToTetheredGuidWl(){
        assertEquals(TETHERED_USER_GUID, Converters.convertToTetheredGuidWl(TETHERED_USER_GUID));
    }

    @Test
    void testConvertFromTetheredGuid(){
        assertEquals("{"+TETHERED_USER_GUID+"}", Converters.convertFromTetheredGuid(TETHERED_USER_GUID));
    }

    @Test
    void testConvertToCurrency() {
        CurrencyType currencyType = new CurrencyType();
        currencyType.setAmount(BigDecimal.valueOf(45.60));
        currencyType.setCurrencyCode("826");
        Currency expectedCurrency = new Currency(BigDecimal.valueOf(45.60), "GBP", "£");
        assertEquals(expectedCurrency, Converters.convertToCurrency(currencyType));
    }

    @Test
    void testConvertToCurrencyWithNull() {
        assertNull(Converters.convertToCurrency(null));
    }

    @Test
    void testConvertToCurrencyWithNonNumericCode() {
        CurrencyType currencyType = new CurrencyType();
        currencyType.setAmount(new java.math.BigDecimal("100.00"));
        currencyType.setCurrencyCode("ABC");
        Currency expectedCurrency = new Currency(new java.math.BigDecimal("100.00"), null, null);
        assertEquals(expectedCurrency, Converters.convertToCurrency(currencyType));
    }

    @Test
    void testConvertToLocalDate() throws DatatypeConfigurationException {
        LocalDate localDate = LocalDate.of(2021, 4, 26);
        XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
        LocalDate returnDate = Converters.convertToLocalDate(xmlDate);
        assertEquals(localDate, returnDate);
    }

    @Test
    void testConvertNullToLocalDate() {
        assertNull(Converters.convertToLocalDate(null));
    }

    @Test
    void testLocalDateToXmlGregorianCalendar() throws DatatypeConfigurationException {
        LocalDate localDate = LocalDate.of(2021, 4, 26);
        XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
        assertEquals(xmlDate, Converters.localDateToXmlGregorianCalendar(localDate));
    }

    @Test
    void testConvertNullToXmlGregorianCalendar() {
        assertNull(Converters.localDateToXmlGregorianCalendar(null));
    }

    @Test
    void testConvertToLocalDateTime() throws DatatypeConfigurationException {
        LocalDateTime localDate=LocalDateTime.of(2021,4,26,4,56,12);
        localDate.atZone(ZoneId.systemDefault());
        XMLGregorianCalendar xmlDate =DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
        LocalDateTime returnDate=Converters.convertToLocalDateTime(xmlDate);
        assertEquals(localDate, returnDate);
    }

    @Test
    void testConvertFrom() throws DatatypeConfigurationException {
        LocalDateTime localDateTime=LocalDateTime.of(2021,4,26,4,56,12);
        XMLGregorianCalendar xmlDate =DatatypeFactory.newInstance().newXMLGregorianCalendar(localDateTime.toString());
        assertEquals(xmlDate, Converters.localDateTimeToXmlGregorianCalendar(localDateTime));
    }
}
