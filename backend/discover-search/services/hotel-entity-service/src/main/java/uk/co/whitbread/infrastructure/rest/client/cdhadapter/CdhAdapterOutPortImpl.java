package uk.co.whitbread.infrastructure.rest.client.cdhadapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanySearchCriteriaDto;
import uk.co.whitbread.domain.model.cdh.CdhSearchCompaniesRequest;
import uk.co.whitbread.domain.ports.secondary.CdhAdapterOutPort;
import uk.co.whitbread.infrastructure.rest.client.CdhAdapterClient;
import uk.co.whitbread.infrastructure.rest.client.cdhadapter.exceptions.CdhSearchCompaniesException;
import uk.co.whitbread.infrastructure.rest.client.cdhadapter.mapper.CdhSearchCompaniesRequestMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class CdhAdapterOutPortImpl implements CdhAdapterOutPort {

  private final CdhAdapterClient cdhAdapterClient;
  private final CdhSearchCompaniesRequestMapper requestMapper;

  @Override
  public List<String> getCompanySuppressRates(String companyId) {
    return cdhAdapterClient.getCompanySuppressRates(companyId).getSuppressRates();
  }

  @Override
  public String getCompanyAccountIdFromCdh(
      CdhSearchCompaniesRequest cdhSearchBookingsRequest) {
    CompanySearchCriteriaDto requestDto = requestMapper.toDto(cdhSearchBookingsRequest);
    var companySearchResponseDto = cdhAdapterClient.searchCompanies(requestDto);
    if (CollectionUtils.isNotEmpty(companySearchResponseDto.getResults())) {
      return companySearchResponseDto.getResults().getFirst().getCompanyAccountId();
    } else {
      throw new CdhSearchCompaniesException(String.format("No results returned while searching from"
              + " CDH for globalCompanyId = %s", cdhSearchBookingsRequest.getGlobalCompanyId()));
    }
  }
}
