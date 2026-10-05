package uk.co.whitbread.hotel.card.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;
import uk.co.whitbread.hotel.card.model.CardType;
import uk.co.whitbread.hotel.card.model.PaymentCardCommon;

public class CommonPaymentCardValidator implements ConstraintValidator<ConfirmCommonPaymentCardDetails, PaymentCardCommon> {

    @Override
    public boolean isValid(PaymentCardCommon paymentCardCommon, ConstraintValidatorContext constraintValidatorContext) {
        if (CardType.PIBA.name().equalsIgnoreCase(paymentCardCommon.getCardType()) && Boolean.TRUE.equals(paymentCardCommon.getCnpRequired())) {
            return StringUtils.hasText(paymentCardCommon.getCnpBusinessAccountPassword());
        }
        return true;
    }
}
