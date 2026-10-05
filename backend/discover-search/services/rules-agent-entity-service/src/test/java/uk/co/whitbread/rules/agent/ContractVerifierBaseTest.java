package uk.co.whitbread.rules.agent;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.context.WebApplicationContext;
import uk.co.whitbread.rules.agent.domain.ports.primary.RuleEngineCacheManagerInPort;

@SpringBootTest(classes = RulesAgentServiceApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ContractVerifierBaseTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private RuleEngineCacheManagerInPort ruleManagerInPort;

  private static final WireMockServer WIRE_MOCK_SERVER;

  static {
    WIRE_MOCK_SERVER = new WireMockServer(
        wireMockConfig()
            .dynamicPort()
            .usingFilesUnderClasspath("."));
    WIRE_MOCK_SERVER.start();
    loadStubs();
    Runtime.getRuntime().addShutdownHook(new Thread(WIRE_MOCK_SERVER::stop));
  }

  @BeforeAll
  public void init() {
    Resource initData = new ClassPathResource("stubs/data.sql");
    DatabasePopulator dbPopulator = new ResourceDatabasePopulator(initData);
    DatabasePopulatorUtils.execute(dbPopulator, dataSource);

    ruleManagerInPort.loadRulesInMemory();
  }

  @BeforeEach
  public void setUp() {
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
  }

  @DynamicPropertySource
  static void wireMockProperties(DynamicPropertyRegistry registry) {
    registry.add("wiremock.server.port", WIRE_MOCK_SERVER::port);
    registry.add("wiremock.server.https-port", () -> -1);
  }

  private static void loadStubs() {
    try {
      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      Resource[] resources = resolver.getResources("classpath:/stubs/**/*.json");
      for (Resource resource : resources) {
        try (var inputStream = resource.getInputStream()) {
          String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
          StubMapping mapping = StubMapping.buildFrom(json);
          WIRE_MOCK_SERVER.addStubMapping(mapping);
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load stub mappings from classpath:/stubs/", e);
    }
  }

}