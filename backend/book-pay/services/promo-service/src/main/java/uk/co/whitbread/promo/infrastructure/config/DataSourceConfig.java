package uk.co.whitbread.promo.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.promo.infrastructure.rest.client.properties.CopyDataSourceProperties;

@Configuration
@Slf4j
public class DataSourceConfig {

  private final CopyDataSourceProperties copyProps;

  public DataSourceConfig(CopyDataSourceProperties copyProps) {
    this.copyProps = copyProps;
  }

  @Bean
  @ConditionalOnProperty(
      name = "copy-datasource.enabled",
      havingValue = "true"
  )
  public DataSource copyDataSource() {
    HikariConfig cfg = new HikariConfig();
    cfg.setJdbcUrl(copyProps.getUrl());
    cfg.setUsername(copyProps.getUsername());
    cfg.setPassword(copyProps.getPassword());

    if (copyProps.getDriverClassName() != null) {
      cfg.setDriverClassName(copyProps.getDriverClassName());
    }

    CopyDataSourceProperties.Hikari h = copyProps.getHikari();
    cfg.setMaximumPoolSize(h.getMaximumPoolSize());
    cfg.setMinimumIdle(h.getMinimumIdle());
    cfg.setConnectionTimeout(h.getConnectionTimeout());
    cfg.setValidationTimeout(h.getValidationTimeout());
    cfg.setIdleTimeout(h.getIdleTimeout());
    cfg.setMaxLifetime(h.getMaxLifetime());
    cfg.setLeakDetectionThreshold(h.getLeakDetectionThreshold());
    cfg.setConnectionTestQuery(h.getConnectionTestQuery());
    cfg.setPoolName("promo-copy-pool");

    return new HikariDataSource(cfg);
  }
}