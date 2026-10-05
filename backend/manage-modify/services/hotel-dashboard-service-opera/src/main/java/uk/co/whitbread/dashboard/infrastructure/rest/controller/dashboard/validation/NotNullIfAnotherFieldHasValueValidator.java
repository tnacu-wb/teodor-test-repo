package uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Implementation of {@link NotNullIfAnotherFieldHasValue} validator.
 **/
public class NotNullIfAnotherFieldHasValueValidator
    implements ConstraintValidator<NotNullIfAnotherFieldHasValue, Object> {

  private String[] fieldNames;

  @Override
  public void initialize(final NotNullIfAnotherFieldHasValue annotation) {
    fieldNames = annotation.values();
  }

  @Override
  public boolean isValid(final Object value, final ConstraintValidatorContext ctx) {

    final long numNotBlankFields = Stream.of(fieldNames)
        .map(fieldName -> getProperty(value, fieldName))
        .filter(StringUtils::isNotBlank).count();

    if (numNotBlankFields == 0) {
      return true;
    }

    if (numNotBlankFields != fieldNames.length) {
      final String errorMessage = String.format("%s: %s", ctx.getDefaultConstraintMessageTemplate(),
          Arrays.asList(fieldNames));

      ctx.disableDefaultConstraintViolation();
      ctx.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();

      return false;

    }

    return true;
  }

  private String getProperty(final Object value, final String fieldName) {
    try {
      final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
      final Object propertyValue = beanWrapper.getPropertyValue(fieldName);
      return propertyValue == null ? null : propertyValue.toString();
    } catch (final Exception ex) {
      // In this case we can't get the property value
      return null;
    }
  }

}
