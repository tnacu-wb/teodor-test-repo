package uk.co.whitbread.hotel.register.utils.register;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.common.validators.password.PasswordByConfigValidator;
import uk.co.whitbread.hotel.register.exceptions.PasswordPolicyException;

@ExtendWith(MockitoExtension.class)
class PasswordPolicyUtilTest {

    @Test
    void testEnforcePasswordPolicy() {
        // Arrange
        PasswordByConfigValidator passwordValidator = mock(PasswordByConfigValidator.class);
        String password = "TestPassword123!";
        boolean involveAuth0 = false;

        // Act
        PasswordPolicyUtil.enforcePasswordPolicy(passwordValidator, password, involveAuth0);

        // Assert
        verify(passwordValidator).isValid(eq(password), eq("register"), any(Consumer.class));
    }

    @Test
    void testTriggerPasswordPolicyError() {
        // Arrange
        String errorMessage = "Password does not meet policy requirements";

        // Act & Assert
        PasswordPolicyException exception = assertThrows(PasswordPolicyException.class,
                () -> PasswordPolicyUtil.triggerPasswordPolicyError(errorMessage));
        assertEquals(errorMessage, exception.getMessage());
    }

}