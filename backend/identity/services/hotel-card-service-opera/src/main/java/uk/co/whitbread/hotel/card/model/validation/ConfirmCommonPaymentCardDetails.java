package uk.co.whitbread.hotel.card.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CommonPaymentCardValidator.class})
public @interface ConfirmCommonPaymentCardDetails {

    String message() default "Payment card details are invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}