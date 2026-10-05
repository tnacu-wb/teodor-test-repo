package uk.co.whitbread.employee.bulk;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = BulkEmployeeUploadServiceApplication.class)
public abstract class ContractVerifierBaseTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(
            WireMockConfiguration.options()
                .dynamicPort()
                .usingFilesUnderClasspath("stubs")
        );
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("wiremock.server.port", wireMockServer::port);
    }

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }
}