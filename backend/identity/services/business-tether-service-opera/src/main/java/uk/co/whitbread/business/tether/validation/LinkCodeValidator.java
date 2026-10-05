package uk.co.whitbread.business.tether.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import uk.co.whitbread.business.tether.properties.ValidatorProperties;


@AllArgsConstructor
public class LinkCodeValidator implements ConstraintValidator<LinkCodeConstraint, String> {

    private final ValidatorProperties validatorProperties;

    @Override
    public void initialize(LinkCodeConstraint linkCodeConstraint) {
        // No initialization required
    }

    @Override
    public boolean isValid(String linkCode, ConstraintValidatorContext constraintValidatorContext) {
        return isLinkCodeLengthValid(linkCode);
    }

    private boolean isLinkCodeLengthValid(String linkCode) {
        return linkCode.length() == validatorProperties.getLength();
    }
}
