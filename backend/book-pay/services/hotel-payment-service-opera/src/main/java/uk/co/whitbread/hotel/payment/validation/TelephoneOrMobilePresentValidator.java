package uk.co.whitbread.hotel.payment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;
import uk.co.whitbread.hotel.payment.model.Booker;


public class TelephoneOrMobilePresentValidator implements
    ConstraintValidator<TelephoneOrMobilePresent, Booker> {

    @Override
    public void initialize(TelephoneOrMobilePresent telephoneOrMobilePresent) {
        // Nothing to initialize
    }

    @Override
    public boolean isValid(Booker booker, ConstraintValidatorContext constraintValidatorContext) {
        return booker == null ||
                StringUtils.hasText(booker.getTelephoneNumber()) ||
                StringUtils.hasText(booker.getMobileNumber());
    }
}
