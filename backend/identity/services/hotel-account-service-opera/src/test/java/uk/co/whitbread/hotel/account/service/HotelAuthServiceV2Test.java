package uk.co.whitbread.hotel.account.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.hotel.account.model.ForgottenPasswordRequest;
import uk.co.whitbread.hotel.account.model.ForgottenPasswordResponse;
import uk.co.whitbread.hotel.account.properties.Auth0Properties;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.model.Auth0User;
import uk.co.whitbread.shared.auth.service.ManagementService;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HotelAuthServiceV2Test {

    public static final String USER_EMAIL = "email-test";
    public static final String USER_ID = "user-id-test";
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ManagementService authManagementServiceMock;
    @Mock
    private Auth0Properties auth0PropertiesMock;
    @Mock
    private AzureEmailService azureEmailServiceMock;
    @Mock
    private ForgottenPasswordRequest forgottenPasswordRequestMock;

    private Auth0User auth0User;

    @InjectMocks
    @Spy
    private HotelAuthServiceV2 testObj;

    @BeforeEach
    void setUp() {
        when(forgottenPasswordRequestMock.getUsername()).thenReturn(USER_EMAIL);
        auth0User = Auth0User.builder().id(USER_ID).email(USER_EMAIL).build();
        when(authManagementServiceMock.getUser(USER_EMAIL)).thenReturn(Optional.of(auth0User));
    }

    @Test
    void shouldFakeSuccessWhenUserNotFound() {
        when(authManagementServiceMock.getUser(anyString())).thenReturn(Optional.empty());

        ForgottenPasswordResponse result =
                testObj.forgotPassword(forgottenPasswordRequestMock, false);
        assertNotNull(result);
        assertTrue(result.getSuccess());
        verify(authManagementServiceMock, never()).requestPasswordChange(any());
    }

    @Test
    void shouldFakeSuccessWhenAuth0ThrowsError() {
        when(authManagementServiceMock.getUser(anyString())).thenThrow(AuthServiceException.class);

        ForgottenPasswordResponse result =
            testObj.forgotPassword(forgottenPasswordRequestMock, false);
        assertNotNull(result);
        assertTrue(result.getSuccess());
    }

    @Test
    void shouldSendForgottenPasswordEmail_PI() {
        ForgottenPasswordResponse result = testObj.forgotPassword(forgottenPasswordRequestMock, false);

        verify(testObj).sendAsyncPIResetPasswordEmail(USER_ID, forgottenPasswordRequestMock);
        assertNotNull(result);
        assertTrue(result.getSuccess());
    }

    @Test
    void shouldSendForgottenPasswordEmail_BB() {
        ForgottenPasswordResponse result = testObj.forgotPassword(forgottenPasswordRequestMock, true);

        verify(testObj).sendAsyncBBResetPasswordEmail(USER_ID, forgottenPasswordRequestMock);
        assertNotNull(result);
        assertTrue(result.getSuccess());
    }
}
