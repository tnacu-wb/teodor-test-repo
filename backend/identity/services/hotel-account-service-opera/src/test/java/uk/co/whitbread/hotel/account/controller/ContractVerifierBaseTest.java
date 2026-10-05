package uk.co.whitbread.hotel.account.controller;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;
import uk.co.whitbread.hotel.account.HotelAccountMicroserviceApplication;
import uk.co.whitbread.hotel.account.config.TestWireMockServer;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;
import uk.co.whitbread.shared.auth.service.TokenService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HotelAccountMicroserviceApplication.class)
@DirtiesContext
public abstract class ContractVerifierBaseTest {

    static {
        TestWireMockServer.start();
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoSpyBean
    private TokenService mockAuthTokenService;

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);

        // Mock valid authorization token
        doReturn(Optional.of("customerAccountId1"))
            .when(mockAuthTokenService)
            .retrieveCustomerAccountIdAndVerifyToken("Bearer mockAuthorization");

        // Mock for different authorization token that should return no stays
        doReturn(Optional.of("customerAccountId2"))
            .when(mockAuthTokenService)
            .retrieveCustomerAccountIdAndVerifyToken("Bearer mockAuthorization2");

        // Handle null token case (when Authorization header is missing completely)
        // For successful contract tests that use session-id headers, we need to return
        // a valid session ID when Authorization is null, so the controller can use the session-id fallback
        when(mockAuthTokenService.retrieveAndVerifyToken(null))
            .thenReturn(Optional.empty()); // This allows fallback to session-id in some controllers

        // Handle invalid/expired tokens - these should throw TokenVerificationException to trigger 401 Unauthorized
        // Specific tokens from the contract tests that should return 401:
        doThrow(new TokenVerificationException("Provided token was invalid or expired"))
            .when(mockAuthTokenService).retrieveAndVerifyToken(
                "Bearer mockAuthorization3");

        doThrow(new TokenVerificationException("Provided token was invalid or expired"))
            .when(mockAuthTokenService).retrieveAndVerifyToken("Bearer invalid_token");

        doThrow(new TokenVerificationException("Provided token was invalid or expired"))
            .when(mockAuthTokenService).retrieveAndVerifyToken(
                "Bearer mockAuthorization4");

        doThrow(new TokenVerificationException("Provided token was invalid or expired"))
            .when(mockAuthTokenService).retrieveAndVerifyToken(
                "Bearer mockAuthorization5");

        // For any other invalid tokens, also throw TokenVerificationException
        when(mockAuthTokenService.retrieveAndVerifyToken(not(eq("Bearer mockAuthorization"))))
            .thenAnswer(invocation -> {
                String token = invocation.getArgument(0);
                if (token == null) {
                    return Optional.empty(); // Already handled above, but just in case
                }
                // For any other non-null, non-valid tokens, throw exception for 401
                throw new TokenVerificationException("Provided token was invalid or expired");
            });
        doAnswer(token -> createEmployeeDetails(token.getArgument(0))).when(mockAuthTokenService)
                .retrieveEmployeeDetailsAndVerifyToken(anyString());
    }

    private EmployeeDetails createEmployeeDetails(String token) {
        if ("Bearer mockAuthorization".equals(token)) {
            return new EmployeeDetails("28", "1");
        }
        return null;
    }
}
