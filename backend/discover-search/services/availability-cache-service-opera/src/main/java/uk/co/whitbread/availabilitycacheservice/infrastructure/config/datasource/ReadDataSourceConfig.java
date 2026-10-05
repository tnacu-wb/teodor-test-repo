package uk.co.whitbread.availabilitycacheservice.infrastructure.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read",
    entityManagerFactoryRef = "readerEntityManager",
    transactionManagerRef = "readerTransactionManager")
@EnableTransactionManagement
@Slf4j
public class ReadDataSourceConfig extends DataSourceConfig {

  private static final String ENTITY_PAKAGE_TO_SCAN = "uk.co.whitbread.availabilitycacheservice.infrastructure.entity";
  private static final String READER_DT_SRC_TYP_NM = "Reader";
  @Autowired
  private Environment env;

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.reader")
  public DataSource readerDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean readerEntityManager() {
    final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    final DataSource readDataSource = readerDataSource();
    if (readDataSource instanceof HikariDataSource) {
      final HikariDataSource hikariDataSource = (HikariDataSource) readDataSource;
      final String jdbcUrl = hikariDataSource.getJdbcUrl();
      log.info("Availability cache read datasource URL : {}", jdbcUrl);
      updateDataSourceConfiguration(hikariDataSource, READER_DT_SRC_TYP_NM);
    }
    em.setDataSource(readDataSource);
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
  public PlatformTransactionManager readerTransactionManager() {
    final JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(readerEntityManager().getObject());
    return transactionManager;
  }

}
