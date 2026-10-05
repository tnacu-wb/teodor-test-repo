package uk.co.whitbread.piba.account.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Optional;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import worldline.mst.bsm.api.b2b.pi.data.CurrencyType;

@Slf4j
@UtilityClass
public class Converters {

    public static uk.co.whitbread.piba.account.model.Currency convertToCurrency(
        CurrencyType currencyType) {

        if (currencyType == null || currencyType.getCurrencyCode() == null || currencyType.getCurrencyCode().isEmpty()) {
            return null;
        }

        Optional<Currency> currencyCode = Currency.getAvailableCurrencies().stream()
            .filter(c -> c.getNumericCode() == parseInt(currencyType.getCurrencyCode()))
            .findAny();

        return new uk.co.whitbread.piba.account.model.Currency(currencyType.getAmount(),
            currencyCode.map(Currency::getCurrencyCode).orElse(null),
            currencyCode.map(Currency::getSymbol).orElse(null));
    }

    public static LocalDate convertToLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
        return xmlGregorianCalendar == null ? null : LocalDate.of(xmlGregorianCalendar.getYear(), xmlGregorianCalendar.getMonth(), xmlGregorianCalendar.getDay());
    }

    @SneakyThrows
    public static XMLGregorianCalendar localDateToXmlGregorianCalendar(LocalDate localDate) {
        return localDate == null ? null : DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
    }

    public static LocalDateTime convertToLocalDateTime(XMLGregorianCalendar xmlGregorianCalendar) {
        ZonedDateTime zonedDateTime = xmlGregorianCalendar.toGregorianCalendar()
                .toZonedDateTime();
        return ZonedDateTime.ofInstant(zonedDateTime.toInstant(),
                ZoneId.systemDefault()).toLocalDateTime();
    }

    @SneakyThrows
    public static XMLGregorianCalendar localDateTimeToXmlGregorianCalendar(LocalDateTime localDateTime) {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDateTime.toString());
    }

    public String convertToTetheredGuidWl(String tetheredGuidWl) {
        return tetheredGuidWl;
    }

    public String convertFromTetheredGuid(String tetheredGuid) {
        return "{"+tetheredGuid+"}";
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // Return -1 if parsing fails, because no ISO 4217 currency uses -1 as a numeric code.
            // This ensures that an invalid currency code will not match any real currency.
            log.warn("Currency code '{}' is not a valid integer and will be ignored.", value);
            return -1;
        }
    }

}
