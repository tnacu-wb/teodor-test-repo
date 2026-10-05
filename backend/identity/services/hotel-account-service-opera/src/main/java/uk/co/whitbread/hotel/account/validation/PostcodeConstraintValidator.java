package uk.co.whitbread.hotel.account.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.hotel.account.model.Address;
import uk.co.whitbread.hotel.account.properties.CountryCodesWithPostcodesProperties;



import java.util.Collections;
import java.util.Optional;

@AllArgsConstructor
public class PostcodeConstraintValidator implements ConstraintValidator<PostcodeConstraint, Address> {

    private final CountryCodesWithPostcodesProperties properties;

    @Override
    public void initialize(PostcodeConstraint postcodeConstraint) {
        // No initialisation required
    }

    @Override
    public boolean isValid(Address address, ConstraintValidatorContext constraintValidatorContext) {

        return requiredFieldsAreNull(address) || countryDoesNotRequirePostcode(address) || postcodeIsPresent(address);
    }

    private boolean requiredFieldsAreNull(Address address) {
        return address == null || StringUtils.isBlank(address.getCountryCode());
    }

    private boolean postcodeIsPresent(Address address) {
        return StringUtils.isNotBlank(address.getPostCode());
    }

    private boolean countryDoesNotRequirePostcode(Address address) {
        return Optional.ofNullable(properties.getCountryCodeList())
                .orElse(Collections.emptyList())
                .stream()
                .noneMatch(countryCode -> countryCode.equals(address.getCountryCode()));
    }
}
