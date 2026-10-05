package uk.co.whitbread.contentservice.roomtypes.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;
import uk.co.whitbread.contentservice.roomtypes.Application;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles({"disable-caching", "local"})
public abstract class ContractVerifierBaseTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort()
                    .usingFilesUnderDirectory("src/test/resources"))
            .build();

    @MockitoBean
    private RedisTemplate redisTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @DynamicPropertySource
    static void wireMockProperties(DynamicPropertyRegistry registry) {
        registry.add("wiremock.server.port", () -> wireMock.getPort());
        registry.add("feign-clients.aem.url", () -> "http://localhost:" + wireMock.getPort());
    }

    @BeforeEach
    public void setUp() throws IOException {
        wireMock.resetAll();
        loadStubs();
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

    private void loadStubs() throws IOException {
        var resolver = new PathMatchingResourcePatternResolver();
        var resources = resolver.getResources("classpath:/stubs/*.json");
        for (var resource : resources) {
            String json = resource.getContentAsString(StandardCharsets.UTF_8);
            StubMapping stubMapping = StubMapping.buildFrom(json);
            wireMock.addStubMapping(stubMapping);
        }
    }
}
