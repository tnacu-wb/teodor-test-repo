package uk.co.whitbread.promo.config;

import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class LiquibaseConfig {

  private final String changeLog;
  private final String defaultSchema;
  private final String contexts;
  private final boolean enabled;

  public LiquibaseConfig(
          @Value("${spring.liquibase.change-log}") String changeLog,
          @Value("${spring.liquibase.default-schema}") String defaultSchema,
          @Value("${spring.liquibase.contexts}") String contexts,
          @Value("${spring.liquibase.enabled:true}") boolean enabled
  ) {
    this.changeLog = changeLog;
    this.defaultSchema = defaultSchema;
    this.contexts = contexts;
    this.enabled = enabled;
  }

  @Bean
  public SpringLiquibase liquibase(DataSource dataSource) {

    SpringLiquibase liquibase = new SpringLiquibase();

    liquibase.setDataSource(dataSource);

    liquibase.setChangeLog(changeLog);

    liquibase.setDefaultSchema(defaultSchema);

    liquibase.setContexts(contexts);

    liquibase.setShouldRun(enabled);

    return liquibase;
  }
}