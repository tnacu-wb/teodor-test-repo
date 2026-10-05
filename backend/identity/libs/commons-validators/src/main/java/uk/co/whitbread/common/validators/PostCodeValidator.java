package uk.co.whitbread.common.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostCodeValidator implements ConstraintValidator<ValidPostcode, String> {


    private final static String AT_LEAST_ONE_ALPHA = "[a-zA-Z]+";
    private Pattern pattern;

    public void initialize(ValidPostcode constraint) {

        pattern = Pattern.compile(AT_LEAST_ONE_ALPHA);

    }

    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        Matcher matcher = pattern.matcher(value);
        boolean result = matcher.find();
        return result;
    }
}
