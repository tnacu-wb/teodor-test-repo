package uk.co.whitbread.infrastructure.rest.client.cdhadapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanyDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanySearchCriteriaDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanySearchResponseDto;
import uk.co.whitbread.domain.model.cdh.CdhSearchCompaniesRequest;
import uk.co.whitbread.infrastructure.rest.client.CdhAdapterClient;
import uk.co.whitbread.infrastructure.rest.client.cdhadapter.exceptions.CdhSearchCompaniesException;
import uk.co.whitbread.infrastructure.rest.client.cdhadapter.mapper.CdhSearchCompaniesRequestMapper;

@ExtendWith(MockitoExtension.class)
class CdhAdapterOutPortImplTest {

  private static final Integer GLOBAL_COMPANY_ID = 1001;
  private static final String ACCOUNT_ID = "ACC_123";

  @Mock
  private CdhAdapterClient cdhAdapterClient;

  @Mock
  private CdhSearchCompaniesRequestMapper requestMapper;

  @InjectMocks
  private CdhAdapterOutPortImpl cdhAdapterOutPortImpl;

  @Test
  void getCompanyAccountIdFromCdh_ShouldReturnAccountId_WhenResultsArePresent() {
    // Arrange
    var request = buildCdhSearchCompaniesRequest();
    var requestDto = mock(CompanySearchCriteriaDto.class);
    var companyDto = mock(CompanyDto.class);
    var responseDto = mock(CompanySearchResponseDto.class);

    when(requestMapper.toDto(request)).thenReturn(requestDto);
    when(cdhAdapterClient.searchCompanies(requestDto)).thenReturn(responseDto);
    when(responseDto.getResults()).thenReturn(List.of(companyDto));
    when(companyDto.getCompanyAccountId()).thenReturn(ACCOUNT_ID);

    // Act
    var result = cdhAdapterOutPortImpl.getCompanyAccountIdFromCdh(request);

    // Assert
    assertEquals(ACCOUNT_ID, result);
    verifyNoMoreInteractions(cdhAdapterClient, requestMapper);
  }

  @Test
  void getCompanyAccountIdFromCdh_ShouldThrowCdhSearchCompaniesException_WhenResultsAreEmpty() {
    // Arrange
    var request = buildCdhSearchCompaniesRequest();
    var requestDto = mock(CompanySearchCriteriaDto.class);
    var responseDto = mock(CompanySearchResponseDto.class);

    when(requestMapper.toDto(request)).thenReturn(requestDto);
    when(cdhAdapterClient.searchCompanies(requestDto)).thenReturn(responseDto);
    when(responseDto.getResults()).thenReturn(Collections.emptyList());

    // Act & Assert
    var thrownException = assertThrowsExactly(CdhSearchCompaniesException.class,
        () -> cdhAdapterOutPortImpl.getCompanyAccountIdFromCdh(request));

    assertEquals(
        "No results returned while searching from CDH for globalCompanyId = " + GLOBAL_COMPANY_ID,
        thrownException.getMessage()
    );
    verifyNoMoreInteractions(cdhAdapterClient, requestMapper);
  }

  @Test
  void getCompanyAccountIdFromCdh_ShouldThrowCdhSearchCompaniesException_WhenResultsAreNull() {
    // Arrange
    var request = buildCdhSearchCompaniesRequest();
    var requestDto = mock(CompanySearchCriteriaDto.class);
    var responseDto = mock(CompanySearchResponseDto.class);

    when(requestMapper.toDto(request)).thenReturn(requestDto);
    when(cdhAdapterClient.searchCompanies(requestDto)).thenReturn(responseDto);
    when(responseDto.getResults()).thenReturn(null);

    // Act & Assert
    var thrownException = assertThrowsExactly(CdhSearchCompaniesException.class,
        () -> cdhAdapterOutPortImpl.getCompanyAccountIdFromCdh(request));

    assertEquals(
        "No results returned while searching from CDH for globalCompanyId = " + GLOBAL_COMPANY_ID,
        thrownException.getMessage()
    );
    verifyNoMoreInteractions(cdhAdapterClient, requestMapper);
  }

  private CdhSearchCompaniesRequest buildCdhSearchCompaniesRequest() {
    return CdhSearchCompaniesRequest.builder()
        .globalCompanyId(GLOBAL_COMPANY_ID)
        .accessContext("BB_CCUI")
        .accessedBy("hotel-entity-service")
        .build();
  }
}

