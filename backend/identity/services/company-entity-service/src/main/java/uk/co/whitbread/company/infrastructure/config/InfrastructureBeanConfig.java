package uk.co.whitbread.company.infrastructure.config;

import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.company.domain.logic.CdhCompanyPortImpl;
import uk.co.whitbread.company.domain.logic.CompaniesProfilePortBusinessCase;
import uk.co.whitbread.company.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.company.domain.ports.primary.CdhCompanyPort;
import uk.co.whitbread.company.domain.ports.primary.CompaniesProfilePort;
import uk.co.whitbread.company.domain.ports.secondary.CdhCompanyOutPort;
import uk.co.whitbread.company.domain.ports.secondary.ProfileOutPort;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.account.CdhClient;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.account.CdhCompanyOutPortImpl;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.mapper.CdhCompanyMapper;
import uk.co.whitbread.company.infrastructure.rest.client.ohip.mapper.ProfileOhipMapper;
import uk.co.whitbread.company.infrastructure.rest.client.ohip.profile.OhipProfileClient;
import uk.co.whitbread.company.infrastructure.rest.client.ohip.profile.ProfileOutPortImpl;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  public CompaniesProfilePort companiesProfileService(ProfileOutPort profileOutPort) {
    return new CompaniesProfilePortBusinessCase(profileOutPort);
  }

  @Bean
  public CdhCompanyPort cdhCompanyPort(CdhCompanyOutPort cdhCompanyOutPort) {
    return new CdhCompanyPortImpl(cdhCompanyOutPort);
  }

  @Bean
  public ProfileOutPort profileOutPort(OhipProfileClient ohipProfileClient, ProfileOhipMapper profileOhipMapper) {
    return new ProfileOutPortImpl(ohipProfileClient, profileOhipMapper);
  }

  @Bean
  public CdhCompanyOutPort cdhCompanyOutPort(CdhClient cdhClient,
      OhipProfileClient ohipProfileClient, CdhCompanyMapper cdhCompanyMapper) {
    return new CdhCompanyOutPortImpl(cdhClient, ohipProfileClient, cdhCompanyMapper);
  }
}
