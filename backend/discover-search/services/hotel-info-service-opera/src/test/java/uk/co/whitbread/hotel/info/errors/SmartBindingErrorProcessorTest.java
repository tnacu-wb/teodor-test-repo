package uk.co.whitbread.hotel.info.errors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.PropertyAccessException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SmartBindingErrorProcessorTest {

    private SmartBindingErrorProcessor smartBindingErrorProcessor;

    @BeforeEach
    public void setUp() {
        smartBindingErrorProcessor = new SmartBindingErrorProcessor();
    }

    @Test
    public void gets_message_from_cause_exception() {

        PropertyAccessException exception = mock(PropertyAccessException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getPropertyName()).thenReturn("name");
        when(exception.getErrorCode()).thenReturn("007");

        when(bindingResult.resolveMessageCodes("007", "name")).thenReturn(new String[]{"007", "1337"});

        when(bindingResult.getObjectName()).thenReturn("Person");
        when(exception.getValue()).thenReturn(new String[]{"bad name", "is bad"});

        Exception causeException = mock(Exception.class);
        when(exception.getCause()).thenReturn(causeException);
        when(causeException.getLocalizedMessage()).thenReturn("HELP");

        smartBindingErrorProcessor.processPropertyAccessException(exception, bindingResult);

        ArgumentCaptor<FieldError> argument = ArgumentCaptor.forClass(FieldError.class);

        verify(bindingResult).addError(argument.capture());

        assertEquals("HELP", argument.getValue().getDefaultMessage());
    }
}