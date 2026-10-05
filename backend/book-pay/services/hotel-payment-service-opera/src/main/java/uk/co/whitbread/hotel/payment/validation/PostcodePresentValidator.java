package uk.co.whitbread.hotel.payment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;
import uk.co.whitbread.hotel.payment.model.Address;

public class PostcodePresentValidator implements ConstraintValidator<PostcodePresent, Address> {
    private static final String GB = "GB";

    @Override
    public void initialize(PostcodePresent postcodePresent) {
        // Nothing to initialize
    }

    @Override
    public boolean isValid(Address address, ConstraintValidatorContext constraintValidatorContext) {
        return address == null ||
                !GB.equals(address.getCountryCode()) ||
                StringUtils.hasText(address.getPostcode());
    }
}
