package uk.co.whitbread.hotel.account.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;



@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = PaymentDetailsValidator.class)
@Documented
public @interface PaymentDetails {

    String message() default "paymentPreference.paymentCard.cardNumber, paymentPreference.paymentCard.cardHolderName, "
        + "paymentPreference.paymentCard.expiryDate and paymentPreference.paymentCard.cardType must be present";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
