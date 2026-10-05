package uk.co.whitbread.ondemandrefreshservice.infrastructure.config;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@Slf4j
public class PostgresIntegrationTestConfig implements
    ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final String POSTGRES_VERSION = "postgres:latest";

  @Container
  public PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(POSTGRES_VERSION);

  public void initialize(final ConfigurableApplicationContext configurableApplicationContext) {
    postgreSQLContainer.start();
    TestPropertyValues.of(
        //datasource
        "spring.datasource.reader.jdbc-url=" + postgreSQLContainer.getJdbcUrl(),
        "spring.datasource.reader.username=" + postgreSQLContainer.getUsername(),
        "spring.datasource.reader.password=" + postgreSQLContainer.getPassword(),
        //writer datasource
        "spring.datasource.writer.jdbc-url=" + postgreSQLContainer.getJdbcUrl(),
        "spring.datasource.writer.username=" + postgreSQLContainer.getUsername(),
        "spring.datasource.writer.password=" + postgreSQLContainer.getPassword()
    ).applyTo(configurableApplicationContext.getEnvironment());
    log.info("PostgreSQL Testcontainer started on port {}", postgreSQLContainer.getFirstMappedPort());
  }

  @PreDestroy
  public void destroy() {
    postgreSQLContainer.stop();
  }
}
