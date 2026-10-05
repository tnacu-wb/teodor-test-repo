package uk.co.whitbread.hotel.account.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.co.whitbread.hotel.account.model.BaseContact;
import uk.co.whitbread.hotel.account.model.Passport;



import java.util.Optional;

public class AdditionalGuestValidator implements ConstraintValidator<AdditionalGuestConstraint, BaseContact> {


    @Override
    public void initialize(AdditionalGuestConstraint additionalGuestConstraint) {
        // No initialization required
    }

    @Override
    public boolean isValid(BaseContact contactDetail, ConstraintValidatorContext constraintValidatorContext) {
        return isCountryOfIssueValid(contactDetail);
    }

    private boolean isCountryOfIssueValid(BaseContact contactDetail) {
        Optional<String> countryOfIssue = Optional.ofNullable(contactDetail)
                .map(BaseContact::getPassport)
                .map(Passport::getCountryOfIssue);

        return !countryOfIssue.isPresent() || countryOfIssue.get().length() > 2;
    }
}
