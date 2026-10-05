package uk.co.whitbread.company.employee;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = CompanyEmployeeServiceApplication.class)
@DirtiesContext
public abstract class ContractVerifierBaseTest {

    private static final WireMockServer WIRE_MOCK_SERVER = createWireMockServer();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @DynamicPropertySource
    static void registerWireMockProperties(DynamicPropertyRegistry registry) {
        registry.add("wiremock.server.port", WIRE_MOCK_SERVER::port);
    }

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

    @AfterAll
    static void tearDownWireMock() {
        WIRE_MOCK_SERVER.stop();
    }

    private static WireMockServer createWireMockServer() {
        WireMockServer wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        ClasspathFileSource stubSource = new ClasspathFileSource("stubs");
        stubSource.listFilesRecursively()
            .forEach(textFile -> wireMockServer.addStubMapping(
                StubMapping.buildFrom(textFile.readContentsAsString())));
        return wireMockServer;
    }
}
