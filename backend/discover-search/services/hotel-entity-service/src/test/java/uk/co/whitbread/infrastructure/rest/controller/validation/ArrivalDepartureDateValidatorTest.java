package uk.co.whitbread.infrastructure.rest.controller.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilitiesByIdsRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilityRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelInventoryRequestDto;

class ArrivalDepartureDateValidatorTest {
    private ArrivalDepartureDateValidator arrivalDepartureDateValidator;

    private HotelInventoryRequestDto hotelInventoryReq;
    private LocalDate today;
    private  DateTimeFormatter dtf;

    @BeforeEach
    public void setUp() {
        arrivalDepartureDateValidator = new ArrivalDepartureDateValidator();
        hotelInventoryReq = new HotelInventoryRequestDto();
        dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        today = LocalDate.now();
    }

    @Test
    void successfulTestForArrivalDepartureDate(){
        hotelInventoryReq.setDateRangeStart(today.format(dtf));
        hotelInventoryReq.setDateRangeEnd(today.plusDays(1).format(dtf));
        assertThat(arrivalDepartureDateValidator.isValid(hotelInventoryReq,null)).isTrue();
    }

    @Test
    void failureTestForNullSearchCriteria(){
        hotelInventoryReq=null;
        assertThat(arrivalDepartureDateValidator.isValid(hotelInventoryReq,null)).isFalse();
    }

    @Test
    void failureTestForNullArrivalDate(){
        hotelInventoryReq.setDateRangeStart(null);
        hotelInventoryReq.setDateRangeEnd(today.format(dtf));
        assertThat(arrivalDepartureDateValidator.isValid(hotelInventoryReq,null)).isFalse();

    }

    @Test
    void failureTestForNullDepartureDate(){
        hotelInventoryReq.setDateRangeStart(today.format(dtf));
        hotelInventoryReq.setDateRangeEnd(null);
        assertThat(arrivalDepartureDateValidator.isValid(hotelInventoryReq,null)).isFalse();
    }

    @Test
    void failureTestForNullArrivalAndDepartureDateSearchCriteria(){
        hotelInventoryReq.setDateRangeStart(null);
        hotelInventoryReq.setDateRangeEnd(null);
        assertThat(arrivalDepartureDateValidator.isValid(hotelInventoryReq,null)).isFalse();
    }

    @Test
    void failureTestForArrivalBeforeTodaySearchCriteria(){
        hotelInventoryReq.setDateRangeStart(today.minusDays(1).format(dtf));
        hotelInventoryReq.setDateRangeEnd(null);
        assertThat(arrivalDepartureDateValidator.isValid(hotelInventoryReq,null)).isFalse();
    }

    @Test
    void failureTestForBothStartNEndInPast(){
        hotelInventoryReq.setDateRangeStart(today.minusDays(2).format(dtf));
        hotelInventoryReq.setDateRangeEnd(today.minusDays(1).format(dtf));
        assertThat(arrivalDepartureDateValidator.isValid(hotelInventoryReq,null)).isFalse();
    }

    @Test
    void failureTestForArrivalAndDepartureEqualsSearchCriteria(){
        hotelInventoryReq.setDateRangeStart(today.format(dtf));
        hotelInventoryReq.setDateRangeEnd(today.format(dtf));
        assertThat(arrivalDepartureDateValidator.isValid(hotelInventoryReq,null)).isFalse();
    }

    @Test
    void failureTestForArrivalAfterDepartureSearchCriteria(){
        hotelInventoryReq.setDateRangeStart(today.plusDays(3).format(dtf));
        hotelInventoryReq.setDateRangeEnd(today.plusDays(1).format(dtf));
        assertThat(arrivalDepartureDateValidator.isValid(hotelInventoryReq,null)).isFalse();
    }

    @Test
    void failureTestForArrivalWithInvalidDateSearchCriteria(){
        hotelInventoryReq.setDateRangeStart("invalid");
        hotelInventoryReq.setDateRangeEnd(today.plusDays(1).format(dtf));
        assertThat(arrivalDepartureDateValidator.isValid(hotelInventoryReq,null)).isFalse();
    }

    @Test
    void failureTestForStartnEndDateInInvalidFormats(){
        hotelInventoryReq.setDateRangeStart("22-09-2032");
        hotelInventoryReq.setDateRangeEnd("23-09-2032");
        assertThat(arrivalDepartureDateValidator.isValid(hotelInventoryReq,null)).isFalse();
    }

    @Test
    void failureTestForDepartureWithInvalidDateSearchCriteria(){
        hotelInventoryReq.setDateRangeStart(today.plusDays(1).format(dtf));
        hotelInventoryReq.setDateRangeEnd("invalid");
        assertThat(arrivalDepartureDateValidator.isValid(hotelInventoryReq,null)).isFalse();
    }

    @Test
    void testIsValid_HotelAvailabilitiesByIdsRequestDto_Success() {
        final LocalDate now = LocalDate.now();
        final HotelAvailabilitiesByIdsRequestDto
            hotelAvailabilitiesByIds = mockHotelAvailabilitiesByIdsReq(now, now.plusDays(1));
        assertThat(arrivalDepartureDateValidator
            .isValid(hotelAvailabilitiesByIds, null)).isTrue();
    }

    @Test
    void testIsValid_HotelAvailabilitiesByIdsRequestDto_Faliure() {
        final LocalDate now = LocalDate.now();
        final HotelAvailabilitiesByIdsRequestDto
            hotelAvailabilitiesByIds = mockHotelAvailabilitiesByIdsReq(now, now.minusDays(2));
        assertThat(arrivalDepartureDateValidator
            .isValid(hotelAvailabilitiesByIds, null)).isFalse();
    }

    private HotelAvailabilitiesByIdsRequestDto mockHotelAvailabilitiesByIdsReq(final LocalDate arrivalDate, final LocalDate departureDate){
        return HotelAvailabilitiesByIdsRequestDto.builder()
            .hotelIds(List.of("LONKIN", "LONEUS"))
            .arrivalDate(arrivalDate.format(dtf))
            .departureDate(departureDate.format(dtf))
            .roomTypes(List.of("DB"))
            .adultsNumber(List.of(1))
            .childrenNumber(List.of(0))
            .cotsRequired(List.of(false))
            .ratePlanCodes(List.of("FLEXRATE"))
            .channel("PI")
            .subchannel("WEB")
            .language("EN")
            .country("GB").build();
    }

    @Test
    void testIsValid_HotelAvailabilityRequestDto_Success() {
        final LocalDate now = LocalDate.now();
        final HotelAvailabilityRequestDto
            hotelAvailabilityRequest = mockHotelAvailabilityReq(now, now.plusDays(1));
        assertThat(arrivalDepartureDateValidator
            .isValid(hotelAvailabilityRequest, null)).isTrue();
    }

    @Test
    void testIsValid_HotelAvailabilityRequestDto_Faliure() {
        final LocalDate now = LocalDate.now();
        final HotelAvailabilityRequestDto
            hotelAvailabilityRequest = mockHotelAvailabilityReq(now, now.minusDays(2));
        assertThat(arrivalDepartureDateValidator
            .isValid(hotelAvailabilityRequest, null)).isFalse();
    }

    private HotelAvailabilityRequestDto mockHotelAvailabilityReq(final LocalDate arrivalDate, final LocalDate departureDate){
        return HotelAvailabilityRequestDto.builder()
            .hotelId("LONKIN")
            .arrivalDate(arrivalDate.format(dtf))
            .departureDate(departureDate.format(dtf))
            .roomTypes(List.of("DB"))
            .adultsNumber(List.of(1))
            .childrenNumber(List.of(0))
            .cotsRequired(List.of(false))
            .ratePlanCodes(List.of("FLEXRATE"))
            .channel("PI")
            .subchannel("WEB")
            .language("EN").build();
    }
}