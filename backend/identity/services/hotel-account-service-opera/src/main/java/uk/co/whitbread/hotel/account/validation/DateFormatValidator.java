package uk.co.whitbread.hotel.account.validation;



import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

class DateFormatValidator implements ConstraintValidator<DateFormat, String> {

    private String format;

    @Override
    public void initialize(DateFormat dateFormatAnnotation) {
        format = dateFormatAnnotation.value();
    }

    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
