package uk.co.whitbread.ondemandrefreshservice.infrastructure.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public final class LocalDateTimeToDateConverter {


    private LocalDateTimeToDateConverter(){
    }
    public static Date convertToDate(LocalDateTime localTime) {
        ZonedDateTime zdt = localTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

}
