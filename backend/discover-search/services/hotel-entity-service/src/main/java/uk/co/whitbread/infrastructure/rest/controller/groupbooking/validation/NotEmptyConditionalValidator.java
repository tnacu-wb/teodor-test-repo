package uk.co.whitbread.infrastructure.rest.controller.groupbooking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapperImpl;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.model.in.GroupBookingRequestDto;


@Slf4j
public class NotEmptyConditionalValidator implements ConstraintValidator<NotEmptyConditional, GroupBookingRequestDto> {

  private String checkedField;
  private String condition;

  @Override
  public void initialize(NotEmptyConditional constraint) {
    this.checkedField = constraint.checkedField();
    this.condition = constraint.condition();
  }

  @Override
  public boolean isValid(GroupBookingRequestDto request, ConstraintValidatorContext context) {
    String checkedFieldValue = (String) new BeanWrapperImpl(request).getPropertyValue(checkedField);
    boolean conditionFieldValue = (boolean) new BeanWrapperImpl(request).getPropertyValue(condition);
    boolean isValid = !conditionFieldValue || StringUtils.isNotEmpty(checkedFieldValue);

    if (!isValid) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(
              String.format("Field %s must not be empty as %s is 'true'", checkedField, condition))
          .addConstraintViolation();
    }

    return isValid;
  }
}