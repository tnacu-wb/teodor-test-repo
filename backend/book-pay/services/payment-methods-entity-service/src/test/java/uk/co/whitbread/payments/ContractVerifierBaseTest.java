package uk.co.whitbread.payments;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.StreamUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.context.WebApplicationContext;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;
import uk.co.whitbread.shared.auth.tenant.Tenant;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(classes = PaymentMethodsEntityServiceApplication.class)
public abstract class ContractVerifierBaseTest {

  private static final WireMockServer WIRE_MOCK_SERVER = new WireMockServer(wireMockConfig().dynamicPort());

  @Autowired
  private TenantRepository tenantRepository;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @DynamicPropertySource
  static void wireMockProperties(DynamicPropertyRegistry registry) throws IOException {
    if (!WIRE_MOCK_SERVER.isRunning()) {
      WIRE_MOCK_SERVER.start();
      registerStubMappings();
    }
    registry.add("wiremock.server.port", WIRE_MOCK_SERVER::port);
  }

  private static void registerStubMappings() throws IOException {
    var resolver = new PathMatchingResourcePatternResolver();
    for (var resource : resolver.getResources("classpath:/stubs/*.json")) {
      var json = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
      WIRE_MOCK_SERVER.addStubMapping(StubMapping.buildFrom(json));
    }
  }

  @BeforeEach
  public void setUp(TestInfo testInfo) {
    RestAssuredMockMvc.reset();
    if (testInfo.getDisplayName().contains("business")) {
      Tenant tenant = new Tenant();
      tenant.setIssuer("https://auth0.sandbox.whitbread.digital/");
      tenant.setName("PI BB Tenant");
      tenant.setNamespace("https://premierinn.com");
      tenantRepository.save(tenant);

      String email = "john.smith@gmail.com";
      if(testInfo.getDisplayName().equals("validate_get_paymentMethods_businessUser_success()")){
        email = "business.user@email.com";
      }

      Jwt jwt =
          Jwt.withTokenValue("tokenValidTest").issuer("https://auth0.sandbox.whitbread.digital/")
              .issuedAt(Instant.now()).expiresAt(Instant.MAX)
              .header("test", "/test")
              .claim(
                  "https://premierinn.com/customerAccountId", "5445")
              .claim(
                  "iss", "https://auth0.sandbox.whitbread.digital/")
              .claim(
                  "https://premierinn.com/employeeAccountId", "1")
              .claim(
                  "https://premierinn.com/companyAccountId", "5445")
              .claim(
                  "https://premierinn.com/email", email)
              .build();

      String finalEmail = email;
      RestAssuredMockMvc.authentication = mockMvcRequestSpecification -> {
        var authentication = new CustomJwtAuthenticationToken(jwt, Account.builder()
            .bartId("187")
            .operaCompanyId("7765828")
            .companyId("5445")
            .accessLevel("SELF")
            .email(finalEmail)
            .customerId("5445")
            .employeeId("1")
            .build());
        authentication.setAuthenticated(true);
        mockMvcRequestSpecification.auth()
            .authentication(authentication);

        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
      };
    }
    else {
      RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

  }
}