package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PermissionEvaluatorTest {

    private static final String PI_BOOKING_CHANNEL = "PI";
    private static final String DISTR_BOOKING_CHANNEL = "DISTR";

    @Mock
    AuthenticatedUserService authenticatedUserService;
    @InjectMocks
    PermissionEvaluator permissionEvaluator;

    @Test
    void hasAccess_PI_BOOKING_CHANNEL() {
        // When
        boolean hasAccess = permissionEvaluator.hasAccess(PI_BOOKING_CHANNEL);
        // Then
        assertTrue(hasAccess);
    }

    @Test
    void hasAccess_DISTR_BOOKING_CHANNEL() {
        // When
        boolean hasAccess = permissionEvaluator.hasAccess(DISTR_BOOKING_CHANNEL);
        // Then
        assertTrue(hasAccess);
    }

    @Test
    void hasAccess_forAuthenticatedUser() {
        // Given
        when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
        // When
        boolean hasAccess = permissionEvaluator.hasAccess("some_channel");
        // Then
        assertTrue(hasAccess);
    }

    @Test
    void hasAccess_forUnauthenticatedUser() {
        // Given
        when(authenticatedUserService.isUserAuthenticated()).thenReturn(false);
        // When
        boolean hasAccess = permissionEvaluator.hasAccess("some_channel");
        // Then
        assertFalse(hasAccess);
    }
}
