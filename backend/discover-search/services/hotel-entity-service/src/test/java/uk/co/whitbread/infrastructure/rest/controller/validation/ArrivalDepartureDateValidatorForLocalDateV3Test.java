package uk.co.whitbread.infrastructure.rest.controller.validation;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out.PriceDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out.RoomDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilitiesByIdsRequestV3Dto;

class ArrivalDepartureDateValidatorForLocalDateV3Test {
    private ArrivalDepartureDateValidatorForLocalDateV3 arrivalDepartureDateValidatorForLocalDate;

    private HotelAvailabilitiesByIdsRequestV3Dto hotelAvailabilitiesByIdsRequestV3Dto;
    private LocalDate today;

    @BeforeEach
    public void setUp() {
        arrivalDepartureDateValidatorForLocalDate = new ArrivalDepartureDateValidatorForLocalDateV3();
        hotelAvailabilitiesByIdsRequestV3Dto = new HotelAvailabilitiesByIdsRequestV3Dto();
        today = LocalDate.now();
    }

    @Test
    void successfulTestForArrivalDepartureDate(){
        hotelAvailabilitiesByIdsRequestV3Dto.setArrivalDate(LocalDate.parse(today.format(ISO_LOCAL_DATE)));
        hotelAvailabilitiesByIdsRequestV3Dto.setDepartureDate(LocalDate.parse(today.plusDays(1).
                format(ISO_LOCAL_DATE)));
        assertThat(arrivalDepartureDateValidatorForLocalDate.isValid(hotelAvailabilitiesByIdsRequestV3Dto,
                null)).isTrue();
    }

    @Test
    void failureTestForNullSearchCriteria(){
        hotelAvailabilitiesByIdsRequestV3Dto =null;
        assertThat(arrivalDepartureDateValidatorForLocalDate.isValid(hotelAvailabilitiesByIdsRequestV3Dto,
                null)).isFalse();
    }

    @Test
    void failureTestForNullArrivalDate(){
        hotelAvailabilitiesByIdsRequestV3Dto.setArrivalDate(null);
        hotelAvailabilitiesByIdsRequestV3Dto.setDepartureDate(LocalDate.parse(today.format(ISO_LOCAL_DATE)));
        assertThat(arrivalDepartureDateValidatorForLocalDate.isValid(hotelAvailabilitiesByIdsRequestV3Dto,
                null)).isFalse();
    }

    @Test
    void failureTestForNullDepartureDate(){
        hotelAvailabilitiesByIdsRequestV3Dto.setArrivalDate(LocalDate.parse(today.format(ISO_LOCAL_DATE)));
        hotelAvailabilitiesByIdsRequestV3Dto.setDepartureDate(null);
        assertThat(arrivalDepartureDateValidatorForLocalDate.isValid(hotelAvailabilitiesByIdsRequestV3Dto,
                null)).isFalse();
    }

    @Test
    void failureTestForNullArrivalAndDepartureDateSearchCriteria(){
        hotelAvailabilitiesByIdsRequestV3Dto.setArrivalDate(null);
        hotelAvailabilitiesByIdsRequestV3Dto.setDepartureDate(null);
        assertThat(arrivalDepartureDateValidatorForLocalDate.isValid(hotelAvailabilitiesByIdsRequestV3Dto,
                null)).isFalse();
    }

    @Test
    void failureTestForArrivalBeforeTodaySearchCriteria(){
        hotelAvailabilitiesByIdsRequestV3Dto.setArrivalDate(LocalDate.parse(today.minusDays(1).
                format(ISO_LOCAL_DATE)));
        hotelAvailabilitiesByIdsRequestV3Dto.setDepartureDate(null);
        assertThat(arrivalDepartureDateValidatorForLocalDate.isValid(hotelAvailabilitiesByIdsRequestV3Dto,
                null)).isFalse();
    }

    @Test
    void failureTestForBothStartNEndInPast(){
        hotelAvailabilitiesByIdsRequestV3Dto.setArrivalDate(LocalDate.parse(today.minusDays(2).
                format(ISO_LOCAL_DATE)));
        hotelAvailabilitiesByIdsRequestV3Dto.setDepartureDate(LocalDate.parse(today.minusDays(1).
                format(ISO_LOCAL_DATE)));
        assertThat(arrivalDepartureDateValidatorForLocalDate.isValid(hotelAvailabilitiesByIdsRequestV3Dto,
                null)).isFalse();
    }

    @Test
    void failureTestForArrivalAndDepartureEqualsSearchCriteria(){
        hotelAvailabilitiesByIdsRequestV3Dto.setArrivalDate(LocalDate.parse(today.format(ISO_LOCAL_DATE)));
        hotelAvailabilitiesByIdsRequestV3Dto.setDepartureDate(LocalDate.parse(today.format(ISO_LOCAL_DATE)));
        assertThat(arrivalDepartureDateValidatorForLocalDate.isValid(hotelAvailabilitiesByIdsRequestV3Dto,
                null)).isFalse();
    }

    @Test
    void failureTestForArrivalAfterDepartureSearchCriteria(){
        hotelAvailabilitiesByIdsRequestV3Dto.setArrivalDate(LocalDate.parse(today.plusDays(3).
                format(ISO_LOCAL_DATE)));
        hotelAvailabilitiesByIdsRequestV3Dto.setDepartureDate(LocalDate.parse(today.plusDays(1).
                format(ISO_LOCAL_DATE)));
        assertThat(arrivalDepartureDateValidatorForLocalDate.isValid(hotelAvailabilitiesByIdsRequestV3Dto,
                null)).isFalse();
    }

    @Test
    void failureTestForArrivalWithInvalidDateSearchCriteria(){
        int year = 2012;
        int month = 9;
        int dayOfMonth = 22;
        LocalDate arrivalParseDate = LocalDate.of(year, month, dayOfMonth);
        hotelAvailabilitiesByIdsRequestV3Dto.setArrivalDate(LocalDate.parse(arrivalParseDate.format(ISO_LOCAL_DATE)));
        hotelAvailabilitiesByIdsRequestV3Dto.setDepartureDate(LocalDate.parse(today.plusDays(1).
                format(ISO_LOCAL_DATE)));
        assertThat(arrivalDepartureDateValidatorForLocalDate.isValid(hotelAvailabilitiesByIdsRequestV3Dto,
                null)).isFalse();
    }

    @Test
    void failureTestForStartnEndDateInInvalidFormats(){
        int arrivalYear = 20432;
        int arrivalMonth = 9;
        int arrivalDay = 22;
        LocalDate arrivalParseDate = LocalDate.of(arrivalYear, arrivalMonth, arrivalDay);

        int depYear = 20322;
        int depMonth = 9;
        int depDay = 23;
        LocalDate departureParseDate = LocalDate.of(depYear, depMonth, depDay);
        hotelAvailabilitiesByIdsRequestV3Dto.setArrivalDate(LocalDate.parse(arrivalParseDate.format(ISO_LOCAL_DATE)));
        hotelAvailabilitiesByIdsRequestV3Dto.setDepartureDate(LocalDate.parse(departureParseDate.
                format(ISO_LOCAL_DATE)));
        assertThat(arrivalDepartureDateValidatorForLocalDate.isValid(hotelAvailabilitiesByIdsRequestV3Dto,
                null)).isFalse();
    }

    @Test
    void failureTestForDepartureWithInvalidDateSearchCriteria(){
        int year = 20412;
        int month = 9;
        int dayOfMonth = 22;
        LocalDate departureParseDate = LocalDate.of(year, month, dayOfMonth);
        hotelAvailabilitiesByIdsRequestV3Dto.setArrivalDate(LocalDate.parse(today.plusDays(1).
                format(ISO_LOCAL_DATE)));
        hotelAvailabilitiesByIdsRequestV3Dto.setDepartureDate(LocalDate.parse(departureParseDate.
                format(ISO_LOCAL_DATE)));
        assertThat(arrivalDepartureDateValidatorForLocalDate.isValid(hotelAvailabilitiesByIdsRequestV3Dto,
                null)).isTrue();
    }

    @Test
    void testIsValid_HotelAvailabilitiesByIdsRequestV2Dto_Success() {
        final LocalDate now = LocalDate.now();
        final HotelAvailabilitiesByIdsRequestV3Dto dto
                 = mockHotelAvailabilitiesByIdsRequestV3Dto(now,
                now.plusDays(1));
        assertThat(arrivalDepartureDateValidatorForLocalDate
            .isValid(dto, null)).isTrue();
    }

    @Test
    void testIsValid_HotelAvailabilitiesByIdsRequestDto_Faliure() {
        final LocalDate now = LocalDate.now();
        final HotelAvailabilitiesByIdsRequestV3Dto
                dto = mockHotelAvailabilitiesByIdsRequestV3Dto(now,
                now.minusDays(2));
        assertThat(arrivalDepartureDateValidatorForLocalDate
            .isValid(dto, null)).isFalse();
    }

    private HotelAvailabilitiesByIdsRequestV3Dto mockHotelAvailabilitiesByIdsRequestV3Dto(final LocalDate arrivalDate,
                            final LocalDate departureDate){
        new RoomDto("DB", 1, 0, new PriceDto(new BigDecimal("60.00"), "EUR"));
        return HotelAvailabilitiesByIdsRequestV3Dto.builder()
            .hotelIds(List.of("LONKIN", "LONEUS"))
            .arrivalDate(LocalDate.parse(arrivalDate.format(ISO_LOCAL_DATE)))
            .departureDate(LocalDate.parse(departureDate.format(ISO_LOCAL_DATE)))
            .build();
    }

}