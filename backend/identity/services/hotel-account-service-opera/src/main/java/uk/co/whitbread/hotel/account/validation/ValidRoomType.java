package uk.co.whitbread.hotel.account.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RoomTypeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRoomType {
  String message() default "Invalid room type for the given number of adults and children";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}