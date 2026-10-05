package uk.co.whitbread.business.tether.controller;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.wiremock.spring.ConfigureWireMock;
import uk.co.whitbread.business.tether.BusinessTetherServiceApplication;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;

import java.util.Collection;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ConfigureWireMock(name = "wiremockServer", filesUnderDirectory = "src/test/resources")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BusinessTetherServiceApplication.class)
@DirtiesContext
public abstract class ContractVerifierBaseTest extends MockedJwtDecoderBase {
    private static final String VALID_SESSION_ID = "session-id";
    @Autowired
    private WebApplicationContext webApplicationContext;
    @MockitoBean
    private TokenService mockAuthTokenService;

    @BeforeEach
    public void setUp() {
        Collection<Filter> filterCollection = webApplicationContext.getBeansOfType(Filter.class).values();
        Filter[] filters = filterCollection.toArray(new Filter[filterCollection.size()]);
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(filters);
        RestAssuredMockMvc.standaloneSetup(builder);
        when(mockAuthTokenService.retrieveAndVerifyToken(anyString())).thenReturn(Optional.of(VALID_SESSION_ID));
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(anyString())).thenReturn(createEmployeeDetails());
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString())).thenReturn(createCdhEmployeeDetails());
    }

    private CdhEmployeeDetails createCdhEmployeeDetails(){
        return CdhEmployeeDetails.builder().companyAccountId("50").employeeAccountId("1").userEmail("email@test.com").build();
    }

    private EmployeeDetails createEmployeeDetails(){
        return new EmployeeDetails("2","3");
    }
    
}
