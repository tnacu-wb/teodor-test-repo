package uk.co.whitbread.ondemandrefreshservice.infrastructure.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.OnDemandProcessResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.config.SchedulerProperties;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DateFormatValidatorTest {

    @InjectMocks
    private DateFormatValidator dateFormatValidator;
    @Mock
    private SchedulerProperties validationProperties;

    @BeforeEach
    void init(){
        ReflectionTestUtils.setField(dateFormatValidator, "validationProperties", validationProperties);
        when(validationProperties.getMaxDays()).thenReturn(90);
        when(validationProperties.getMaxRange()).thenReturn(365);
    }

    @ParameterizedTest
    @CsvSource({"2022-12-01,2023-10-25", "2022-11-10,2022-11-10", "2022-11-12,2022-11-10"})
    void testValidateInputData(String startDate, String endDate) {
        OnDemandProcessResponse onDemandProcessResponse = dateFormatValidator.validateInputDates(startDate, endDate);
        assertEquals( HttpStatus.BAD_REQUEST,onDemandProcessResponse.getStatus());
    }

    /**
     * Request without dates Success scenario
     */
    @Test
    void testValidateWhenDatesNotPassed() {
        OnDemandProcessResponse onDemandProcessResponse = dateFormatValidator.validateInputDates(null, null);
        assertNull(onDemandProcessResponse.getStatus());
    }

    /**
     * Request with start date as null and End Date as current date
     */
    @Test
    void testValidateStartDateAsNullAndEndDateAsCurrentDate() {

        String endDate = LocalDate.now().toString();
        OnDemandProcessResponse onDemandProcessResponse = dateFormatValidator.validateInputDates(null, endDate);
        assertEquals(HttpStatus.BAD_REQUEST,onDemandProcessResponse.getStatus());
    }

    /**
     * Request with start date as pass and End Date as current date
     */
    @Test
    void testValidateStartOrEndDateAsPast() {
        String startDate  = LocalDate.now().minusDays(10).toString();
        String endDate = LocalDate.now().toString();
        OnDemandProcessResponse onDemandProcessResponse = dateFormatValidator.validateInputDates(startDate, endDate);
        assertEquals(HttpStatus.BAD_REQUEST, onDemandProcessResponse.getStatus());
    }

    @Test
    void testInvalidDateFormat() {
        String startDate  = "22-11-12";
        String endDate = LocalDate.now().toString();
        OnDemandProcessResponse onDemandProcessResponse = dateFormatValidator.validateInputDates(startDate, endDate);
        assertEquals(HttpStatus.BAD_REQUEST,onDemandProcessResponse.getStatus());

    }

}