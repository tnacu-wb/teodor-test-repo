package uk.co.whitbread.payments.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Used to validate a list of Strings against a given enum.
 */
public class ValueOfEnumsValidator implements ConstraintValidator<ValueOfEnums, List<String>> {

    private List<String> acceptedValues;

    @Override
    public void initialize(ValueOfEnums annotation) {
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {

        context.buildConstraintViolationWithTemplate(String.format("One or more values %s are not one of %s.",value, acceptedValues)).addConstraintViolation();

        if (value == null) {
            return true;
        }

        return acceptedValues.containsAll(value);
    }
}
