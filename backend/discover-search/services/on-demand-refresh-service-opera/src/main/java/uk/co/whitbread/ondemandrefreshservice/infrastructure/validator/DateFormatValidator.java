package uk.co.whitbread.ondemandrefreshservice.infrastructure.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.OnDemandProcessResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.config.SchedulerProperties;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static java.time.temporal.ChronoUnit.DAYS;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.util.SanitizingUtils.sanitize;

@Slf4j
@RequiredArgsConstructor
@Component
public class DateFormatValidator {

    private final SchedulerProperties validationProperties;
    private static final String ERROR_MSG_INVALID_DATE_FORMAT = "Invalid Date Format !";
    private static final String ERROR_MSG_DATE_RANGE_MAX = "The Given date ranges can't be greater than 365 days !";
    private static final String ERROR_MSG_START_DATE_GREATER_THAN_END_DATE = "Given start date greater than end date !";
    private static final String DATES_NOT_PASSED = "Accepted scenario, dates are optional!";
    private static final String ERROR_MSG_START_DATE_IS_BLANK = "Start date passed as empty or null !";
    private static final String ERROR_MSG_END_DATE_IS_BLANK = "End date passed as empty or null !";
    private static final String ERROR_MSG_START_DATE_AND_END_DATE_EQUAL = "Start date and End date can't be equal !";
    private static final String ERROR_MSG_START_DATE_OR_END_DATE_PAST = "Start date or End date can't be past !";

    public OnDemandProcessResponse validateInputDates(String startDate, String endDate) {

        // Start and end date to be optional fields
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            return isDatesMetConstrains(validateAndSetDefaultDates(startDate, endDate));
        }
        return isDatesMetConstrains(isValidDateFormat(startDate, endDate));
    }

    private OnDemandProcessResponse validateAndSetDefaultDates(String inputStartDate, String inputEndDate) {
        OnDemandProcessResponse onDemandProcessResponse = new OnDemandProcessResponse();
        if (StringUtils.isBlank(inputStartDate) && StringUtils.isBlank(inputEndDate)) {
            // If start and end dates are not passed, then consider current date as start date
            // and end date as per the configured value in the property file
            onDemandProcessResponse.setStartDate(LocalDate.now());
            onDemandProcessResponse.setEndDate(LocalDate.now().plusDays(validationProperties.getMaxDays()));
            onDemandProcessResponse.setMessage(DATES_NOT_PASSED);
        } else if (StringUtils.isBlank(inputStartDate) && StringUtils.isNotBlank(inputEndDate)) {
            // If start date is not passed and end date passed, then consider current date as start date
            onDemandProcessResponse.setStartDate(LocalDate.now());
            onDemandProcessResponse.setEndDate(LocalDate.parse(inputEndDate, DateTimeFormatter.ISO_LOCAL_DATE));
            onDemandProcessResponse.setMessage(ERROR_MSG_START_DATE_IS_BLANK);
        } else if (StringUtils.isNotBlank(inputStartDate) && StringUtils.isBlank(inputEndDate)) {
            onDemandProcessResponse.setStartDate(LocalDate.parse(inputStartDate, DateTimeFormatter.ISO_LOCAL_DATE));
            // If start date passed and end date is not passed, then end date as per the configured value in the property file
            onDemandProcessResponse.setEndDate(onDemandProcessResponse.getStartDate().plusDays(validationProperties.getMaxDays()));
            onDemandProcessResponse.setMessage(ERROR_MSG_END_DATE_IS_BLANK);
        }
        return onDemandProcessResponse;

    }

    private OnDemandProcessResponse isValidDateFormat(String startDate, String endDate) {
        OnDemandProcessResponse onDemandProcessResponse = new OnDemandProcessResponse();
        try {
            onDemandProcessResponse.setStartDate(LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE));
            onDemandProcessResponse.setEndDate(LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE));
        } catch (DateTimeParseException e) {
            log.error("Error while trying to validate date format for startDate: {} endDate: {}", sanitize(startDate),
                    sanitize(endDate));
            onDemandProcessResponse.setMessage(ERROR_MSG_INVALID_DATE_FORMAT);
            onDemandProcessResponse.setStatus(HttpStatus.BAD_REQUEST);
        }
        return onDemandProcessResponse;
    }

    private OnDemandProcessResponse isDatesMetConstrains(OnDemandProcessResponse onDemandProcessResponse) {
        if (onDemandProcessResponse.getStartDate() != null && onDemandProcessResponse.getEndDate() != null) {
            if (onDemandProcessResponse.getStartDate().isAfter(onDemandProcessResponse.getEndDate())) {
                // Start date cannot be greater than end date
                onDemandProcessResponse.setMessage(ERROR_MSG_START_DATE_GREATER_THAN_END_DATE);
                onDemandProcessResponse.setStatus(HttpStatus.BAD_REQUEST);
            } else if (DAYS.between(onDemandProcessResponse.getStartDate(), onDemandProcessResponse.getEndDate()) >
                    validationProperties.getMaxRange()) {
                // End date cannot be greater than the date value parameter specified
                onDemandProcessResponse.setMessage(ERROR_MSG_DATE_RANGE_MAX);
                onDemandProcessResponse.setStatus(HttpStatus.BAD_REQUEST);
            } else if (onDemandProcessResponse.getStartDate().isBefore(LocalDate.now()) ||
                    onDemandProcessResponse.getEndDate().isBefore(LocalDate.now())) {
                // Start date and End date cannot be past
                onDemandProcessResponse.setMessage(ERROR_MSG_START_DATE_OR_END_DATE_PAST);
                onDemandProcessResponse.setStatus(HttpStatus.BAD_REQUEST);
            } else if (onDemandProcessResponse.getStartDate().equals(onDemandProcessResponse.getEndDate())) {
                // Start date and End date cannot be equal
                onDemandProcessResponse.setMessage(ERROR_MSG_START_DATE_AND_END_DATE_EQUAL);
                onDemandProcessResponse.setStatus(HttpStatus.BAD_REQUEST);
            }
        }
        return onDemandProcessResponse;
    }


}