package uk.co.whitbread.piba.account.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import jakarta.servlet.Filter;
import java.util.Collection;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.cache.CacheManager;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.co.whitbread.piba.account.PibaAccountServiceApplication;
import uk.co.whitbread.piba.account.helper.AuthTestHelper;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PibaAccountServiceApplication.class)
public abstract class ContractVerifierBaseTest {
    private static final String VALID_SESSION_ID="session-id";

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig()
                    .dynamicPort()
                    .withRootDirectory("src/test/resources"))
            .build();

    @DynamicPropertySource
    static void wireMockProperties(DynamicPropertyRegistry registry) {
        registry.add("wiremock.server.port", () -> wireMock.getPort());
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private TokenService mockAuthTokenService;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private TenantRepository tenantRepository;

    @BeforeEach
    public void setUp() {
        AuthTestHelper.configureAuth0Context(tenantRepository, jwtDecoder);
        Collection<Filter> filterCollection = webApplicationContext.getBeansOfType(Filter.class).values();
        Filter[] filters = filterCollection.toArray(new Filter[filterCollection.size()]);
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(filters);
        RestAssuredMockMvc.standaloneSetup(builder);
        when(mockAuthTokenService.retrieveAndVerifyToken(anyString())).thenReturn(Optional.of(VALID_SESSION_ID));
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(anyString()))
                .thenAnswer(token -> createEmployeeDetails(token.getArgument(0)));
    }
    
    
    private EmployeeDetails createEmployeeDetails(String token) {
        if ("Bearer dummy_value4".equals(token)) {
            return new EmployeeDetails("123", "10");
        } else if ("Bearer dummy_value5".equals(token)) {
            return new EmployeeDetails("123", "33");
        } else if ("Bearer dummy_value6".equals(token)) {
            return new EmployeeDetails("123", "42");
        } else if ("Bearer dummy_value7".equals(token)) {
            return new EmployeeDetails("123", "55");
        } else if ("Bearer dummy_value2".equals(token)) {
            return new EmployeeDetails("123", "40");
        } else if ("Bearer dummy_value1".equals(token)) {
            return new EmployeeDetails("123", "41");
        } else if ("Bearer dummy_value8".equals(token)) {
            return new EmployeeDetails("123", "43");
        } else if ("Bearer dummy_value3".equals(token)) {
            return new EmployeeDetails("123", "15");
        } else if ("Bearer dummy_value9".equals(token)) {
            return new EmployeeDetails("987", "2");
        } else if ("Bearer dummy_value10".equals(token)) {
            return new EmployeeDetails("123", "99");
        }
        return null;
    }

}