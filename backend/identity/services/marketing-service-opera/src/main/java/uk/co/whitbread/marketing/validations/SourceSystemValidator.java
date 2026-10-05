package uk.co.whitbread.marketing.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourceSystemValidator implements ConstraintValidator<ValidateSourceSystem, String> {

    private List<String> acceptedValues;

    @Override
    public void initialize(ValidateSourceSystem annotation) {
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        context.buildConstraintViolationWithTemplate(String.format("Supplied value is not one of %s.", acceptedValues)).addConstraintViolation();

        if (value == null) {
            return true;
        }

        return acceptedValues.contains(value);
    }
}
