package uk.co.whitbread.hotel.info.errors;

import org.springframework.beans.PropertyAccessException;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultBindingErrorProcessor;
import org.springframework.validation.FieldError;
import uk.co.whitbread.hotel.info.converters.CaseInsensitiveEnumPropertyEditor;

import java.util.Optional;

public class SmartBindingErrorProcessor extends DefaultBindingErrorProcessor {

    /**
     * Exact copy of super method, with one small difference:
     * It attempts to derive the defaultMessage for the produced FieldError from the cause Exception.
     * Allows {@link CaseInsensitiveEnumPropertyEditor#setAsText} error messages to bubble up and be displayed in the default error responses.
     * @see DefaultBindingErrorProcessor#processPropertyAccessException(PropertyAccessException, BindingResult)
     */
    @Override
    public void processPropertyAccessException(PropertyAccessException ex, BindingResult bindingResult) {
        String field = ex.getPropertyName();
        String[] codes = bindingResult.resolveMessageCodes(ex.getErrorCode(), field);
        Object[] arguments = getArgumentsForBindError(bindingResult.getObjectName(), field);
        Object rejectedValue = ex.getValue();
        if (rejectedValue != null && rejectedValue.getClass().isArray()) {
            rejectedValue = StringUtils.arrayToCommaDelimitedString(ObjectUtils.toObjectArray(rejectedValue));
        }
        bindingResult.addError(new FieldError(
                bindingResult.getObjectName(), field, rejectedValue, true,
                codes, arguments, Optional.ofNullable(ex.getCause()).orElse(ex).getLocalizedMessage()));
    }
}