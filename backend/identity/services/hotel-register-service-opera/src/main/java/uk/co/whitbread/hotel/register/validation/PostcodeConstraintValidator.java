package uk.co.whitbread.hotel.register.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.hotel.register.model.Address;


public class PostcodeConstraintValidator implements ConstraintValidator<PostcodeConstraint, Address> {

    private static final String COUNTRY_CODE_GB = "GB";
    private static final String COUNTRY_CODE_DE = "DE";

    @Override
    public void initialize(PostcodeConstraint postcodeConstraint) {
        // No initialisation required
    }

    @Override
    public boolean isValid(Address address, ConstraintValidatorContext constraintValidatorContext) {

        return requiredFieldsAreNull(address) || countryIsNotUkOrGermany(address) || postcodeIsPresent(address);
    }

    private boolean requiredFieldsAreNull(Address address) {
        return address == null || StringUtils.isBlank(address.getCountryCode());
    }

    private boolean postcodeIsPresent(Address address) {
        return StringUtils.isNotBlank(address.getPostCode());
    }

    private boolean countryIsNotUkOrGermany(Address address) {
        return !(COUNTRY_CODE_GB.equals(address.getCountryCode()) || COUNTRY_CODE_DE.equals(address.getCountryCode()));
    }
}
