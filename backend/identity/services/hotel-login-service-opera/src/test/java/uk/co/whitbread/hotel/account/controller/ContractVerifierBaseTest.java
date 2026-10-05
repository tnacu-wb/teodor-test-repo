package uk.co.whitbread.hotel.account.controller;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;
import uk.co.whitbread.hotel.account.HotelLoginApplication;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HotelLoginApplication.class)
public abstract class ContractVerifierBaseTest {

    private static final WireMockServer wireMockServer;

    static {
        wireMockServer = new WireMockServer(
                wireMockConfig()
                        .dynamicPort()
                        .usingFilesUnderClasspath(".")
        );
        wireMockServer.start();
        loadStubs();
        Runtime.getRuntime().addShutdownHook(new Thread(wireMockServer::stop));
    }

    @DynamicPropertySource
    static void wireMockProperties(DynamicPropertyRegistry registry) {
        registry.add("wiremock.server.port", wireMockServer::port);
        registry.add("wiremock.server.https-port", () -> -1);
    }

    private static void loadStubs() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:/stubs/*.json");
            for (Resource resource : resources) {
                try (var inputStream = resource.getInputStream()) {
                    String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    StubMapping mapping = StubMapping.buildFrom(json);
                    wireMockServer.addStubMapping(mapping);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load stub mappings from classpath:/stubs/", e);
        }
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }
}
