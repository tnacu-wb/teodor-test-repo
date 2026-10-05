package uk.co.whitbread.basket.infrastructure.rest.client.cdh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.domain.model.cdh.CdhSearchCompaniesRequest;
import uk.co.whitbread.basket.domain.model.payments.out.CdhSearchCompaniesResponse;
import uk.co.whitbread.basket.domain.ports.secondary.CdhSearchCompaniesOutPort;
import uk.co.whitbread.basket.generated.models.cdh.CompanySearchCriteriaDto;
import uk.co.whitbread.basket.infrastructure.rest.client.cdh.exceptions.CdhSearchCompaniesException;
import uk.co.whitbread.basket.infrastructure.rest.client.cdh.mapper.CdhSearchCompaniesRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.cdh.mapper.CdhSearchCompaniesResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.cdh.service.CdhAdapterClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class CdhSearchCompaniesOutPortImpl implements CdhSearchCompaniesOutPort {

  private final CdhAdapterClient cdhAdapterClient;
  private final CdhSearchCompaniesRequestMapper requestMapper;
  private final CdhSearchCompaniesResponseMapper responseMapper;

  @Override
  public CdhSearchCompaniesResponse searchCompaniesFromCdh(
          CdhSearchCompaniesRequest cdhSearchBookingsRequest) {
    CompanySearchCriteriaDto requestDto = requestMapper.toDto(cdhSearchBookingsRequest);
    var companySearchResponseDto = cdhAdapterClient.searchCompanies(requestDto);

    if (CollectionUtils.isNotEmpty(companySearchResponseDto.getResults())) {
      log.info("Successfully retrieved companies from CDH service. Results size: {}",
              companySearchResponseDto.getResults().size());
      return responseMapper.toModel(companySearchResponseDto);
    } else {
      throw new CdhSearchCompaniesException(String.format("No results returned while searching from"
              + " CDH for globalCompanyId = %s", cdhSearchBookingsRequest.getGlobalCompanyId()));
    }
  }

}
