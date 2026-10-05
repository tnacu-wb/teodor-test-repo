package uk.co.whitbread.payapp;

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

@SpringBootTest(classes = PayAppEntityServiceApplication.class)
public abstract class ContractVerifierBaseTest {

  @Autowired
  private WebApplicationContext webApplicationContext;
  @MockitoBean(name = "permissionEvaluator")
  private PermissionEvaluator permissionEvaluator;

  @BeforeEach
  public void setUp() {
    when(permissionEvaluator.hasAccess(anyString())).thenReturn(true);
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    RestAssuredMockMvc.authentication = RestAssuredMockMvc.principal(
        new User("authorized_user", "password", Collections.emptyList()));
  }
}