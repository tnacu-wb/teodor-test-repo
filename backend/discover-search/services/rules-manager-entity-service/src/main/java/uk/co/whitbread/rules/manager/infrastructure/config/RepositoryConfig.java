package uk.co.whitbread.rules.manager.infrastructure.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import javax.sql.DataSource;
import liquibase.change.DatabaseChange;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jpa.autoconfigure.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.rules.manager.infrastructure.exceptions.ErrorCode;
import uk.co.whitbread.rules.manager.infrastructure.exceptions.RuleEngineException;

@Slf4j
@Configuration
@ConditionalOnClass({SpringLiquibase.class, DatabaseChange.class})
@ConditionalOnProperty(prefix = "spring.liquibase", name = "enabled", matchIfMissing = false)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@Import({RepositoryConfig.SpringLiquibaseDependsOnPostProcessor.class,
    RepositoryConfig.LiquibaseEntityManagerFactoryDependsOnPostProcessor.class})
@SuppressWarnings("java:S1118")
public class RepositoryConfig {

  @Component
  @ConditionalOnProperty(prefix = "spring.liquibase", name = "enabled", matchIfMissing = false)
  public static class SchemaInitBean implements InitializingBean {

    private final DataSource dataSource;
    private final String schemaName;

    @Autowired
    public SchemaInitBean(DataSource dataSource,
        @Value("${spring.liquibase.default-schema}") String schemaName) {
      this.dataSource = dataSource;
      this.schemaName = schemaName;
    }

    @Override
    public void afterPropertiesSet() {
      try (Connection conn = dataSource.getConnection();
          Statement statement = conn.createStatement()) {
        log.info("Creating DB schema '{}' if not exists.", schemaName);
        statement.execute(MessageFormat.format("create schema if not exists {0}", schemaName));
      } catch (SQLException e) {
        var message = "Failed to create schema '" + schemaName + "'";
        var exception = new RuleEngineException(ErrorCode.DIGITAL_CREATE_DB_EXCEPTION, message);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
  }

  @ConditionalOnBean(SchemaInitBean.class)
  public static class SpringLiquibaseDependsOnPostProcessor
      extends AbstractDependsOnBeanFactoryPostProcessor {

    SpringLiquibaseDependsOnPostProcessor() {
      // SpringLiquibase bean to depend on our SchemaInitBean
      super(SpringLiquibase.class, SchemaInitBean.class);
    }
  }

  @ConditionalOnBean(SchemaInitBean.class)
  public static class LiquibaseEntityManagerFactoryDependsOnPostProcessor
      extends EntityManagerFactoryDependsOnPostProcessor {

    LiquibaseEntityManagerFactoryDependsOnPostProcessor() {
      super(SpringLiquibase.class, SchemaInitBean.class);
    }
  }
}
