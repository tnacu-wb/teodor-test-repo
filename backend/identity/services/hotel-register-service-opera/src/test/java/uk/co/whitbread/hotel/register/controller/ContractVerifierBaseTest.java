package uk.co.whitbread.hotel.register.controller;


import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.co.whitbread.hotel.register.HotelRegisterMicroserviceApplication;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HotelRegisterMicroserviceApplication.class)
@DirtiesContext
public abstract class ContractVerifierBaseTest {

    private static final WireMockServer wiremock = ContractVerifierWireMockSupport.createServer();

    @DynamicPropertySource
    static void wireMockProperties(DynamicPropertyRegistry registry) {
        ContractVerifierWireMockSupport.ensureRunning(wiremock);
        registry.add("wiremock.server.port", wiremock::port);
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @AfterEach
    public void tearDown() {
        ContractVerifierWireMockSupport.resetRequests(wiremock);
    }

    @AfterAll
    static void stopWireMock() {
        ContractVerifierWireMockSupport.stop(wiremock);
    }
}
