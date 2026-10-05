package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.whitbread.ondemandrefreshservice.domain.logic.ProcessRefreshHotelAvailability;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.OnDemandProcessResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.config.SchedulerProperties;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.validator.DateFormatValidator;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.validator.HotelIdValidator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

class HotelAvailabilityOnDemandRefreshControllerTest {

    @InjectMocks
    private HotelAvailabilityOnDemandRefreshController hotelAvailabilityOnDemandRefreshController;

    @Mock
    private ProcessRefreshHotelAvailability processRefreshHotelAvailability;

    @Mock
    private DateFormatValidator dateFormatValidator;

    @Mock
    private HotelIdValidator hotelIdValidator;

    @Mock
    private SchedulerProperties validationProperties;

    @BeforeEach
    void init(){
        ReflectionTestUtils.setField(dateFormatValidator, "validationProperties", validationProperties);
        ReflectionTestUtils.setField(validationProperties, "maxDays", 90);
        ReflectionTestUtils.setField(validationProperties, "maxRange", 365);
    }

    /**
     * Request with dates Success scenario with 202 accepted
     */
    @Test
    void testRequestWithDates202Response() {
        OnDemandProcessResponse onDemandProcessResponse = new OnDemandProcessResponse();
        onDemandProcessResponse.setStatus(HttpStatus.ACCEPTED);
        onDemandProcessResponse.setMessage("Success");
        Set<String> hotelIds = new HashSet<>();
        hotelIds.add("TKINPT");
        hotelIds.add("DUBAI");
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(90).toString();
        doCallRealMethod().when(dateFormatValidator).validateInputDates(any(), any());
        doReturn("").when(hotelIdValidator).validateHotelIds(anySet());
        when(processRefreshHotelAvailability.refreshOnDemandWithDates(hotelIds, startDate, endDate)).thenReturn(onDemandProcessResponse);
        ReflectionTestUtils.setField(validationProperties, "maxDays", 90);
        doReturn(90).when(validationProperties).getMaxDays();
        when(validationProperties.getMaxRange()).thenReturn(365);
        ResponseEntity<String> result = hotelAvailabilityOnDemandRefreshController.refreshHotelAvailabilityOnDemand(hotelIds, startDate, endDate);
        assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
    }

    /**
     * Hotel Ids validation fail scenario
     */
    @Test
    void tesHotelIdsValidation400Response() {
        Set<String> hotelIds = new HashSet<>();
        hotelIds.add("TKINPT");
        hotelIds.add("DUBAI");
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(90).toString();
        OnDemandProcessResponse onDemandProcessResponse = OnDemandProcessResponse.builder()
                .startDate(LocalDate.parse("2022-12-01"))
                .endDate(LocalDate.parse("2023-10-25"))
                .message("Failed")
                .status(HttpStatus.BAD_REQUEST)
                .build();

        doReturn(onDemandProcessResponse).when(dateFormatValidator).validateInputDates(any(), any());
        // If HotelIds validation response with error message then return bad request
        String error = "Failed with error";
        doReturn(error).when(hotelIdValidator).validateHotelIds(anySet());
        doReturn(onDemandProcessResponse).when(processRefreshHotelAvailability).refreshOnDemandWithDates(anySet(), any(), any());
        ResponseEntity<String> result = hotelAvailabilityOnDemandRefreshController.refreshHotelAvailabilityOnDemand(hotelIds, startDate, endDate);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    /**
     * Request with invalid dates failed scenario with 400 accepted
     */
    @Test
    void testRequestWithInvalidDates400Response() {
        Set<String> hotelIds = new HashSet<>();
        hotelIds.add("TKINPT");
        hotelIds.add("DUBAI");
        String startDate = LocalDate.now().toString();
        String endDate = "2023/10/01";
        OnDemandProcessResponse onDemandProcessResponse = new OnDemandProcessResponse();
        onDemandProcessResponse.setStatus(HttpStatus.BAD_REQUEST);
        onDemandProcessResponse.setMessage("Failed");
        doCallRealMethod().when(dateFormatValidator).validateInputDates(any(), any());
        doReturn("").when(hotelIdValidator).validateHotelIds(anySet());
        doReturn(onDemandProcessResponse).when(processRefreshHotelAvailability).refreshOnDemandWithDates(
                anySet(), any(), any());
        ResponseEntity<String> result = hotelAvailabilityOnDemandRefreshController.refreshHotelAvailabilityOnDemand(hotelIds, startDate, endDate);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    /**
     * Request with start date greater than end date, failed scenario with 400 accepted
     */
    @Test
    void testRequestStartDateGreaterEndDate400Response() {
        Set<String> hotelIds = new HashSet<>();
        hotelIds.add("TKINPT");
        hotelIds.add("DUBAI");
        String startDate = LocalDate.now().plusDays(90).toString();
        String endDate = LocalDate.now().toString();
        OnDemandProcessResponse onDemandProcessResponse = new OnDemandProcessResponse();
        onDemandProcessResponse.setStatus(HttpStatus.BAD_REQUEST);
        onDemandProcessResponse.setMessage("Failed");
        doCallRealMethod().when(dateFormatValidator).validateInputDates(any(), any());
        doReturn("").when(hotelIdValidator).validateHotelIds(anySet());
        doReturn(onDemandProcessResponse).when(processRefreshHotelAvailability).refreshOnDemandWithDates(
                anySet(), any(), any());
        ResponseEntity<String> result = hotelAvailabilityOnDemandRefreshController.refreshHotelAvailabilityOnDemand(hotelIds, startDate, endDate);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    /**
     * Request with start date and end date greater than configured max range, failed scenario with 400 accepted
     */
    @Test
    void testRequestStartDateEndDateRangeGreaterMaxRange400Response() {
        Set<String> hotelIds = new HashSet<>();
        hotelIds.add("TKINPT");
        hotelIds.add("DUBAI");
        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(150).toString();
        OnDemandProcessResponse onDemandProcessResponse = new OnDemandProcessResponse();
        onDemandProcessResponse.setStatus(HttpStatus.BAD_REQUEST);
        onDemandProcessResponse.setMessage("Failed");
        doCallRealMethod().when(dateFormatValidator).validateInputDates(any(), any());
        doReturn("").when(hotelIdValidator).validateHotelIds(anySet());
        doReturn(onDemandProcessResponse).when(processRefreshHotelAvailability).refreshOnDemandWithDates(anySet(), any(), any());
        when(validationProperties.getMaxDays()).thenReturn(90);
        ResponseEntity<String> result = hotelAvailabilityOnDemandRefreshController.refreshHotelAvailabilityOnDemand(hotelIds, startDate, endDate);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}