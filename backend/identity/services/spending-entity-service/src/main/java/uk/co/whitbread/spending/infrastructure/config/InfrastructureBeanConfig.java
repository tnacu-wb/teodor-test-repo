package uk.co.whitbread.spending.infrastructure.config;

import jakarta.validation.Validator;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.spending.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.spending.domain.ports.secondary.CdhOutPort;
import uk.co.whitbread.spending.domain.ports.secondary.EmployeeSpendOutPort;
import uk.co.whitbread.spending.domain.ports.secondary.PibaAccountServiceOutPort;
import uk.co.whitbread.spending.domain.ports.secondary.WorldlineOutPort;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.CdhClient;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.CdhOutPortImpl;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper.AccountSpendingResponseMapper;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper.CompanySpendingResponseMapper;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper.EmployeeSpendReportResponseMapper;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper.PibaTetheredGuidResponseMapper;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper.TransactionDetailsResponseMapper;
import uk.co.whitbread.spending.infrastructure.rest.client.cdhadapterservice.EmployeeSpendReportClient;
import uk.co.whitbread.spending.infrastructure.rest.client.cdhadapterservice.EmployeeSpendReportOutPortImpl;
import uk.co.whitbread.spending.infrastructure.rest.client.pibaaccountservice.PibaAccountServiceClient;
import uk.co.whitbread.spending.infrastructure.rest.client.pibaaccountservice.PibaAccountServiceOutPortImpl;
import uk.co.whitbread.spending.infrastructure.rest.client.worldline.WorldlineOutPortImpl;
import uk.co.whitbread.spending.infrastructure.rest.client.worldline.service.PropertiesLoader;
import uk.co.whitbread.spending.infrastructure.rest.client.worldline.service.WorldlineClient;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  public CdhOutPort getCdhOutPort(final CdhClient cdhClient,
      final CompanySpendingResponseMapper companySpendingResponseMapper,
      final AccountSpendingResponseMapper accountSpendingResponseMapper,
      final PibaTetheredGuidResponseMapper pibaTetheredGuidResponseMapper,
      final TransactionDetailsResponseMapper transactionDetailsResponseMapper) {
    return new CdhOutPortImpl(cdhClient, companySpendingResponseMapper,
        accountSpendingResponseMapper, pibaTetheredGuidResponseMapper,
          transactionDetailsResponseMapper);
  }

  @Bean
  public EmployeeSpendOutPort getEmployeeSpendOutPort(
      final EmployeeSpendReportClient employeeSpendReportClient,
      final EmployeeSpendReportResponseMapper employeeSpendReportResponseMapper) {
    return new EmployeeSpendReportOutPortImpl(employeeSpendReportClient,
        employeeSpendReportResponseMapper);
  }

  @Bean
  public PibaAccountServiceOutPort getPibaAccountServiceOutPort(
        PibaAccountServiceClient pibaAccountService) {
    return new PibaAccountServiceOutPortImpl(pibaAccountService);
  }

  @Bean
  public WorldlineOutPort getWorldlineOutPort(final WorldlineClient worldlineClient,
                                              final PropertiesLoader propertiesLoader) {
    return new WorldlineOutPortImpl(worldlineClient, propertiesLoader);
  }

  @Bean
  public Clock getClock() {
    return Clock.systemUTC();
  }
}
