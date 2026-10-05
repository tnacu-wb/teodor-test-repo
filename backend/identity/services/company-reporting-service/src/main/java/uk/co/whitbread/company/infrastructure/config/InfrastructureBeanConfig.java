package uk.co.whitbread.company.infrastructure.config;

import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.company.domain.logic.ReportingPortBusinessCase;
import uk.co.whitbread.company.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.company.domain.ports.primary.ReportInPort;
import uk.co.whitbread.company.domain.ports.secondary.ReportOutPort;
import uk.co.whitbread.company.infrastructure.rest.client.aws.AmazonClient;
import uk.co.whitbread.company.infrastructure.rest.client.aws.properties.S3Properties;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.mapper.CdhMiReportRequestMapper;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.mapper.CdhMiReportResponseMapper;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.service.CdhAdapterClient;
import uk.co.whitbread.company.infrastructure.rest.client.company.ReportOutPortImpl;
import uk.co.whitbread.company.infrastructure.rest.client.company.utils.CdhReportClient;
import uk.co.whitbread.company.infrastructure.rest.client.properties.CompanyReportProperties;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  public ReportInPort reportInPort(ReportOutPort reportOutPort) {
    return new ReportingPortBusinessCase(reportOutPort);
  }

  @Bean
  public ReportOutPort reportOutPort(CdhReportClient cdhReportClient,
      CdhAdapterClient cdhAdapterClient,
      CdhMiReportRequestMapper cdhMiReportRequestMapper,
      CdhMiReportResponseMapper cdhMiReportResponseMapper,
      CompanyReportProperties companyReportProperties,
      AmazonClient amazonClient,
      S3Properties s3Properties) {
    return new ReportOutPortImpl(cdhReportClient, cdhAdapterClient,
        cdhMiReportRequestMapper, cdhMiReportResponseMapper,
        companyReportProperties,
        amazonClient, s3Properties);
  }

}
