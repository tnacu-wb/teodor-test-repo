package uk.co.whitbread.company.infrastructure.rest.client.cdh.account;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.generated.models.CompaniesDto;
import uk.co.whitbread.cdh.generated.models.CompanySearchResponseDto;
import uk.co.whitbread.company.domain.model.in.CompaniesSearchRequest;
import uk.co.whitbread.company.domain.model.out.CompaniesProfile;
import uk.co.whitbread.company.domain.model.out.CompanyProfile;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.mapper.CdhCompanyMapper;
import uk.co.whitbread.company.infrastructure.rest.client.ohip.profile.OhipProfileClient;
import uk.co.whitbread.ohip.generated.models.CompanyProfileDto;
import uk.co.whitbread.ohip.generated.models.NegotiatedRateDto;
import uk.co.whitbread.ohip.generated.models.NegotiatedRatesResponseDto;

@ExtendWith(MockitoExtension.class)
public class CdhCompanyOutPortImplTest {

  @InjectMocks
  private CdhCompanyOutPortImpl cdhCompanyOutPort;
  @Mock
  private CdhClient cdhClient;
  @Mock
  private OhipProfileClient ohipProfileClient;
  @Mock
  private CdhCompanyMapper cdhCompanyMapper;


  @Test
  void getCompaniesFromCdh() {

    var companySearchResponseDto = new CompanySearchResponseDto();

    var companyProfile = createCompanyProfile("12345","6789");
    var companyProfile1 = createCompanyProfile("54321","9876");

    var companyProfileDto = createCompanyProfileDto("12345","6789");
    var companyProfileDto1 = createCompanyProfileDto("54321","9876");

    var companiesProfile = CompaniesProfile.builder()
        .companies(List.of(companyProfile, companyProfile1)).build();

    companySearchResponseDto.setResults(List
        .of(createCompaniesDto(12345), createCompaniesDto(54321)));

    var negotiatedRatesResponseDto = new NegotiatedRatesResponseDto();
    NegotiatedRateDto negotiatedRateDto = new NegotiatedRateDto();
    negotiatedRatesResponseDto.setNegotiatedRates(List.of(negotiatedRateDto));

    var expectedCompanyProfile1 = createCompanyProfile("12345", "6789")
        .toBuilder()
        .negotiatedRateEnabled(true)
        .build();

    var expectedCompanyProfile2 = createCompanyProfile("54321", "9876")
        .toBuilder()
        .negotiatedRateEnabled(false)
        .build();

    var expectedCompaniesProfile = CompaniesProfile.builder()
        .limit(50)
        .offset(1)
        .companies(List.of(expectedCompanyProfile1, expectedCompanyProfile2)).totalResults(2).
        build();

    //Arrange
    var request = CompaniesSearchRequest.builder().companyName("test")
        .pageSize(50)
        .pageNumber(1)
        .negotiatedRateCompanies(false)
        .build();
    when(cdhClient.getCompanies(request))
        .thenReturn(companySearchResponseDto);
    when(cdhCompanyMapper.toCompaniesResponseDto(companySearchResponseDto)).thenReturn(companiesProfile);
    when(ohipProfileClient.getCompanyProfileByCorporateId("12345")).thenReturn(companyProfileDto);
    when(cdhCompanyMapper.toCompanyProfileDto(companyProfileDto)).thenReturn(companyProfile);
    when(ohipProfileClient.getCompanyProfileByCorporateId("54321")).thenReturn(companyProfileDto1);
    when(cdhCompanyMapper.toCompanyProfileDto(companyProfileDto1)).thenReturn(companyProfile1);
    when(ohipProfileClient.getNegotiatedRatesForCompanyProfile("6789")).thenReturn(negotiatedRatesResponseDto);

    //Act
    var response = cdhCompanyOutPort.getCompaniesFromCdh(request);

    //Assert
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectedCompaniesProfile);
  }

  @Test
  void getOnlyNegotiatedRatesCompaniesFromCdh() {

    var companySearchResponseDto = new CompanySearchResponseDto();

    var companyProfile = createCompanyProfile("12345","6789");
    var companyProfile1 = createCompanyProfile("54321","9876");

    var companyProfileDto = createCompanyProfileDto("12345","6789");
    var companyProfileDto1 = createCompanyProfileDto("54321","9876");

    var companiesProfile = CompaniesProfile.builder()
        .companies(List.of(companyProfile, companyProfile1)).build();

    companySearchResponseDto.setResults(List
        .of(createCompaniesDto(12345), createCompaniesDto(54321)));

    var negotiatedRatesResponseDto = new NegotiatedRatesResponseDto();
    NegotiatedRateDto negotiatedRateDto = new NegotiatedRateDto();
    negotiatedRatesResponseDto.setNegotiatedRates(List.of(negotiatedRateDto));

    var expectedCompanyProfile = createCompanyProfile("12345", "6789")
        .toBuilder()
        .negotiatedRateEnabled(true)
        .build();

    var expectedCompaniesProfile = CompaniesProfile.builder()
        .limit(50)
        .offset(1)
        .companies(List.of(expectedCompanyProfile)).totalResults(1).
        build();

    //Arrange
    var request = CompaniesSearchRequest.builder().companyName("test")
        .pageSize(50)
        .pageNumber(1)
        .negotiatedRateCompanies(true)
        .build();
    when(cdhClient.getCompanies(request))
        .thenReturn(companySearchResponseDto);
    when(cdhCompanyMapper.toCompaniesResponseDto(companySearchResponseDto)).thenReturn(companiesProfile);
    when(ohipProfileClient.getCompanyProfileByCorporateId("12345")).thenReturn(companyProfileDto);
    when(cdhCompanyMapper.toCompanyProfileDto(companyProfileDto)).thenReturn(companyProfile);
    when(ohipProfileClient.getCompanyProfileByCorporateId("54321")).thenReturn(companyProfileDto1);
    when(cdhCompanyMapper.toCompanyProfileDto(companyProfileDto1)).thenReturn(companyProfile1);
    when(ohipProfileClient.getNegotiatedRatesForCompanyProfile("6789")).thenReturn(negotiatedRatesResponseDto);

    //Act
    var response = cdhCompanyOutPort.getCompaniesFromCdh(request);

    //Assert
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(expectedCompaniesProfile);
  }

  private CompaniesDto createCompaniesDto(Integer globalCompanyId) {
    var companiesDto = new CompaniesDto();
    companiesDto.setGlobalCompanyId(globalCompanyId);
    return companiesDto;
  }

  private CompanyProfile createCompanyProfile(String corpId, String companyId) {
    return CompanyProfile.builder()
        .companyId(companyId)
        .corpId(corpId)
        .build();

  }

  private CompanyProfileDto createCompanyProfileDto(String corpId, String companyId) {
    var companyProfileDto = new CompanyProfileDto();
    companyProfileDto.setCompanyId(companyId);
    companyProfileDto.setCorpId(corpId);
    return  companyProfileDto;
  }

}
