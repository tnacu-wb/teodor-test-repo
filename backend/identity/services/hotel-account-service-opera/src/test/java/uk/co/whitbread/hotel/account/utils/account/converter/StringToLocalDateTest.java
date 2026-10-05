package uk.co.whitbread.hotel.account.utils.account.converter;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringToLocalDateTest {

    @Test
    void convertFormat1() {
        LocalDate localDate = StringToLocalDate.convert(null, "15 Jan 2017");
        assertThat(localDate, is(LocalDate.of(2017, 1, 15)));
    }

    @Test
    void convertFormat2() {
        LocalDate localDate = StringToLocalDate.convert(null, "15 January 2017");
        assertThat(localDate, is(LocalDate.of(2017, 1, 15)));
    }

    @Test
    void invalidNotDateFormat() {
        assertThrows(IllegalArgumentException.class,
                () -> StringToLocalDate.convert(null, "random"),
                "Converter StringToLocalDate used incorrectly. Arguments passed in were: null and random ");
    }

    @Test
    void invalidFormat() {
        assertThrows(IllegalArgumentException.class,
                () -> StringToLocalDate.convert(null, 1),
                "Converter StringToLocalDate used incorrectly. Arguments passed in were: null and 1 ");
    }

    @Test
    void nullSource() {
        assertThat(StringToLocalDate.convert(null, null), is(IsNull.nullValue()));
    }
}
