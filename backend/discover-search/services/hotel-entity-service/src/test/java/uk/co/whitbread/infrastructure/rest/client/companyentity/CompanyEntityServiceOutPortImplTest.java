package uk.co.whitbread.infrastructure.rest.client.companyentity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.booking.infrastructure.rest.client.companyentity.generated.models.CompanyResponseDto;
import uk.co.whitbread.domain.model.company.out.Company;
import uk.co.whitbread.infrastructure.rest.client.CompanyEntityServiceClient;
import uk.co.whitbread.infrastructure.rest.client.companyentity.mapper.CompanyEntityServiceMapper;

@ExtendWith(MockitoExtension.class)
class CompanyEntityServiceOutPortImplTest {

  private static final String COMPANY_ID = "COMPANY_123";

  @Mock
  private CompanyEntityServiceClient companyEntityServiceClient;

  @Mock
  private CompanyEntityServiceMapper companyEntityServiceMapper;

  @InjectMocks
  private CompanyEntityServiceOutPortImpl companyEntityServiceOutPortImpl;

  @Test
  void getCompanyById_shouldReturnMappedCompany() {
    // Arrange
    var companyResponseDto = new CompanyResponseDto();
    var expectedCompany = Company.builder().companyId(COMPANY_ID).name("Test Company").build();

    when(companyEntityServiceClient.getCompanyById(COMPANY_ID)).thenReturn(companyResponseDto);
    when(companyEntityServiceMapper.toModel(companyResponseDto)).thenReturn(expectedCompany);

    // Act
    var result = companyEntityServiceOutPortImpl.getCompanyById(COMPANY_ID);

    // Assert
    assertNotNull(result);
    assertEquals(expectedCompany, result);
    verify(companyEntityServiceClient).getCompanyById(COMPANY_ID);
    verify(companyEntityServiceMapper).toModel(companyResponseDto);
  }

  @Test
  void getCompanyById_shouldThrowExceptionWhenClientFails() {
    // Arrange
    when(companyEntityServiceClient.getCompanyById(COMPANY_ID))
        .thenThrow(new RuntimeException("Service failure"));

    // Act & Assert
    var exception = assertThrows(RuntimeException.class,
        () -> companyEntityServiceOutPortImpl.getCompanyById(COMPANY_ID));

    assertEquals("Service failure", exception.getMessage());
    verify(companyEntityServiceClient).getCompanyById(COMPANY_ID);
    verifyNoInteractions(companyEntityServiceMapper);
  }
}
