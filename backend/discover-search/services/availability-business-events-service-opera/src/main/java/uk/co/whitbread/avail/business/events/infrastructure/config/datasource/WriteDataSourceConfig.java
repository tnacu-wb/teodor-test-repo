package uk.co.whitbread.avail.business.events.infrastructure.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
    basePackages = "uk.co.whitbread.avail.business.events.infrastructure.repository",
    entityManagerFactoryRef = "writerEntityManager",
    transactionManagerRef = "writerTransactionManager")
@EnableTransactionManagement
@Slf4j
public class WriteDataSourceConfig {

  private static final String ENTITY_PAKAGE_TO_SCAN =
      "uk.co.whitbread.avail.business.events.infrastructure.entity";

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.writer")
  @Primary
  public DataSource writerDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  @Primary
  public LocalContainerEntityManagerFactoryBean writerEntityManager(final Environment env) {
    final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    final DataSource writeDataSource = writerDataSource();
    if (writeDataSource instanceof HikariDataSource) {
      final HikariDataSource hikariDataSource = (HikariDataSource) writeDataSource;
      final String jdbcUrl = hikariDataSource.getJdbcUrl();
      log.info("Availability business events service write datasource URL : {}", jdbcUrl);
    }
    em.setDataSource(writeDataSource);
    em.setPackagesToScan(ENTITY_PAKAGE_TO_SCAN);
    final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    final HashMap<String, Object> properties = new HashMap<>();
    properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
    properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
    em.setJpaPropertyMap(properties);
    return em;
  }

  @Bean
  @Primary
  public PlatformTransactionManager writerTransactionManager(final Environment env) {
    final JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(writerEntityManager(env).getObject());
    return transactionManager;
  }

}
