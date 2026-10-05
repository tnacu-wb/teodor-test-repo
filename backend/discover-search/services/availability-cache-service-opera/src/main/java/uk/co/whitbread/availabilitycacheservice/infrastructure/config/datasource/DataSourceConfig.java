package uk.co.whitbread.availabilitycacheservice.infrastructure.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public abstract class DataSourceConfig {

  @Value("${spring.datasource.hikari.connectionTimeout:15000}")
  private String connectionTimeout;

  @Value("${spring.datasource.hikari.idleTimeout:300000}")
  private String connIdleTimeout;

  @Value("${spring.datasource.hikari.maxLifetime:600000}")
  private String connMaxLifetime;

  @Value("${spring.datasource.hikari.maxPoolSize:15}")
  private String connMaxPoolSize;

  protected void updateDataSourceConfiguration(final HikariDataSource hikariDataSource, final String dataSourceType) {
    hikariDataSource.setConnectionTimeout(Long.valueOf(connectionTimeout));
    hikariDataSource.setIdleTimeout(Long.valueOf(connIdleTimeout));
    hikariDataSource.setMaxLifetime(Long.valueOf(connMaxLifetime));
    hikariDataSource.setMaximumPoolSize(Integer.valueOf(connMaxPoolSize));
    log.info(
        "DataSource Type:{}, Configuration - AutoCommit:{}, Conn-Timeout:{}, Idle-Timeout:{}, Max-Lifetime:{}, "
            + "Max-Pool-Size:{}, Min-Idle:{}",
        dataSourceType,
        hikariDataSource.isAutoCommit(),
        hikariDataSource.getConnectionTimeout(),
        hikariDataSource.getIdleTimeout(),
        hikariDataSource.getMaxLifetime(),
        hikariDataSource.getMaximumPoolSize(),
        hikariDataSource.getMinimumIdle());
  }

}
