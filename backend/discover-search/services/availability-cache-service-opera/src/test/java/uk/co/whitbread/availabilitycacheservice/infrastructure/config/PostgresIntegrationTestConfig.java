package uk.co.whitbread.availabilitycacheservice.infrastructure.config;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
@Slf4j

public class PostgresIntegrationTestConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final String POSTGRES_VERSION = "postgres:11";

  public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer(POSTGRES_VERSION);

  public void initialize(final ConfigurableApplicationContext configurableApplicationContext) {
    postgreSQLContainer
        .withInitScript("schema_v1.1.sql")
        .start();
    TestPropertyValues.of(
        //reader datasource
        "spring.datasource.reader.jdbc-url=" + postgreSQLContainer.getJdbcUrl(),
        "spring.datasource.reader.username=" + postgreSQLContainer.getUsername(),
        "spring.datasource.reader.password=" + postgreSQLContainer.getPassword(),
        //writer datasource
        "spring.datasource.writer.jdbc-url=" + postgreSQLContainer.getJdbcUrl(),
        "spring.datasource.writer.username=" + postgreSQLContainer.getUsername(),
        "spring.datasource.writer.password=" + postgreSQLContainer.getPassword()
    ).applyTo(configurableApplicationContext.getEnvironment());
    log.info("url - {}, username - {}, password - {} and port - {}",
        postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword()
        , postgreSQLContainer.getFirstMappedPort());
  }

  @PreDestroy
  public void destroy() {
    postgreSQLContainer.stop();
  }
}
