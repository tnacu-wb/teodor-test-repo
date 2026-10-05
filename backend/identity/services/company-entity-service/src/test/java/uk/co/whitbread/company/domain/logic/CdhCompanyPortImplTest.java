package uk.co.whitbread.company.domain.logic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.company.domain.model.in.CompaniesSearchRequest;
import uk.co.whitbread.company.domain.model.out.CompaniesProfile;
import uk.co.whitbread.company.domain.model.out.CompanyProfile;
import uk.co.whitbread.company.domain.ports.secondary.CdhCompanyOutPort;

@ExtendWith(MockitoExtension.class)
public class CdhCompanyPortImplTest {

  @InjectMocks
  private CdhCompanyPortImpl cdhCompanyPort;
  @Mock
  private CdhCompanyOutPort cdhCompanyOutPort;

  @Test
  void testGetCompaniesFromCdh() {
    //Arrange
    CompaniesSearchRequest companiesSearchRequest = CompaniesSearchRequest.builder()
        .companyName("Test").pageNumber(1).pageSize(50).build();
    var companiesProfile = CompaniesProfile.builder()
        .companies(List.of(CompanyProfile.builder()
            .name("WB")
            .corpId("1235")
            .language("en")
            .build()))
        .build();

    when(cdhCompanyOutPort.getCompaniesFromCdh(any(CompaniesSearchRequest.class)))
        .thenReturn(companiesProfile);
    //Act

    var companiesSearchResult = cdhCompanyPort.getCompaniesFromCdh(companiesSearchRequest);
    //Assert

    assertThat(companiesSearchResult).usingRecursiveComparison()
        .isEqualTo(companiesProfile);
    verifyNoMoreInteractions(cdhCompanyOutPort);
  }

}