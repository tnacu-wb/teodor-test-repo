package uk.co.whitbread.infrastructure.rest.client.companyentity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.domain.model.company.out.Company;
import uk.co.whitbread.domain.ports.secondary.CompanyEntityServiceOutPort;
import uk.co.whitbread.infrastructure.rest.client.CompanyEntityServiceClient;
import uk.co.whitbread.infrastructure.rest.client.companyentity.mapper.CompanyEntityServiceMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class CompanyEntityServiceOutPortImpl implements CompanyEntityServiceOutPort {

  private final CompanyEntityServiceClient companyEntityServiceClient;
  private final CompanyEntityServiceMapper companyEntityServiceMapper;

  @Override
  public Company getCompanyById(String id) {
    var companyResponseDto = companyEntityServiceClient.getCompanyById(id);
    return companyEntityServiceMapper.toModel(companyResponseDto);
  }
}
