package uk.co.whitbread.common.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateFormatValidator implements ConstraintValidator<DateFormat, String> {

    private String format;
    private boolean nullable;

    @Override
    public void initialize(DateFormat dateFormatAnnotation) {
        format = dateFormatAnnotation.value();
        nullable = dateFormatAnnotation.nullable();
    }

    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (date == null) {
            return validateNullableDate(context);
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }


    private boolean validateNullableDate(ConstraintValidatorContext context) {
        if (nullable) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("may not be empty").addConstraintViolation();
        return false;
    }
}
