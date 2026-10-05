package uk.co.whitbread.basket.infrastructure.rest.client.cdh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.model.cdh.CdhSearchCompaniesRequest;
import uk.co.whitbread.basket.domain.model.payments.out.CdhSearchCompaniesResponse;
import uk.co.whitbread.basket.generated.models.cdh.CompanySearchCriteriaDto;
import uk.co.whitbread.basket.generated.models.cdh.CompanySearchResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.client.cdh.mapper.CdhSearchCompaniesRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.cdh.mapper.CdhSearchCompaniesResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.cdh.service.CdhAdapterClient;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.basket.infrastructure.rest.client.cdh.CdhTestUtils.buildCompanyDto;
import static uk.co.whitbread.basket.infrastructure.rest.client.cdh.CdhTestUtils.createCdhSearchCompaniesResponse;

@ExtendWith(MockitoExtension.class)
class CdhSearchCompaniesOutPortImplTest {

    @Mock
    private CdhAdapterClient cdhAdapterClient;

    @Mock
    private CdhSearchCompaniesRequestMapper requestMapper;

    @Mock
    private CdhSearchCompaniesResponseMapper responseMapper;

    @InjectMocks
    private CdhSearchCompaniesOutPortImpl cdhSearchCompaniesOutPort;

    @Test
    public void shouldSearchCompaniesFromCdh_whenResultsArePresent() {
        CdhSearchCompaniesRequest request = CdhSearchCompaniesRequest.builder().globalCompanyId(12345).build();

        CompanySearchResponseDto responseDto = new CompanySearchResponseDto();
        responseDto.setResults(Collections.singletonList(buildCompanyDto()));

        CdhSearchCompaniesResponse expectedResponse = createCdhSearchCompaniesResponse();

        CompanySearchCriteriaDto companySearchCriteriaDto = new CompanySearchCriteriaDto();
        companySearchCriteriaDto.setGlobalCompanyId(12345);

        when(requestMapper.toDto(any())).thenReturn(any());
        when(cdhAdapterClient.searchCompanies(companySearchCriteriaDto)).thenReturn(responseDto);
        when(responseMapper.toModel(any(CompanySearchResponseDto.class))).thenReturn(expectedResponse);

        CdhSearchCompaniesResponse actualResponse = cdhSearchCompaniesOutPort.searchCompaniesFromCdh(request);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void shouldThrowException_whenNoResultsArePresent() {
        CdhSearchCompaniesRequest request = CdhSearchCompaniesRequest.builder().globalCompanyId(12345).build();

        CompanySearchResponseDto responseDto = new CompanySearchResponseDto();
        responseDto.setResults(Collections.emptyList());


        CompanySearchCriteriaDto companySearchCriteriaDto = new CompanySearchCriteriaDto();
        companySearchCriteriaDto.setGlobalCompanyId(12345);

        when(requestMapper.toDto(any())).thenReturn(any());
        when(cdhAdapterClient.searchCompanies(companySearchCriteriaDto)).thenReturn(responseDto);

        responseDto.setResults(Collections.emptyList());
        assertThrows(uk.co.whitbread.basket.infrastructure.rest.client.cdh.exceptions.CdhSearchCompaniesException.class,
                () -> cdhSearchCompaniesOutPort.searchCompaniesFromCdh(request));
    }

}