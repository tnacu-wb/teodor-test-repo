package uk.co.whitbread.token;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;
import uk.co.whitbread.shared.auth.security.PermissionEvaluator;
import uk.co.whitbread.token.domain.model.out.AuthToken;
import uk.co.whitbread.token.domain.ports.primary.TokenInPort;

@SpringBootTest(classes = OperaTokenServiceApplication.class)
public abstract class ContractVerifierBaseTest {

  @Autowired
  private WebApplicationContext webApplicationContext;
  @MockitoBean(name = "permissionEvaluator")
  private PermissionEvaluator permissionEvaluator;
  @MockitoBean
  private TokenInPort tokenInPort;

  @BeforeEach
  public void setUp() {
    when(permissionEvaluator.hasAccess(anyString())).thenReturn(true);

    // Mock successful token response for "ohip" provider
    AuthToken successToken = new AuthToken(
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.uXrIhrveuvbR4tD1ULholQObboLVC-wIfJOEVElEzcs",
        "Bearer",
        3600L,
        "2025-08-06T11:32:00Z"
    );
    when(tokenInPort.getToken("ohip")).thenReturn(successToken);

    // Mock error for "invalid-provider" - this will be handled by global exception handler
    when(tokenInPort.getToken("invalid-provider"))
        .thenThrow(new uk.co.whitbread.token.infrastructure.rest.client.token.exception.TokenServiceException(
            uk.co.whitbread.token.ErrorCode.TOKEN_UNABLE_TO_ACQUIRE_TOKEN_EXCEPTION,
            "Unable to acquire access token for invalid-provider"));

    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    RestAssuredMockMvc.authentication = RestAssuredMockMvc.principal(
        new User("authorized_user", "password", Collections.emptyList()));
  }
}