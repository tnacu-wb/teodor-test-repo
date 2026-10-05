package uk.co.whitbread.company.infrastructure.rest.controller.company;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.company.domain.model.in.CompaniesProfileRequest;
import uk.co.whitbread.company.domain.model.in.CompaniesSearchRequest;
import uk.co.whitbread.company.domain.model.out.CompaniesProfile;
import uk.co.whitbread.company.domain.model.out.CompanyProfile;
import uk.co.whitbread.company.domain.ports.primary.CdhCompanyPort;
import uk.co.whitbread.company.domain.ports.primary.CompaniesProfilePort;
import uk.co.whitbread.company.infrastructure.rest.controller.company.mapper.CompaniesMapper;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.in.CompaniesProfileRequestDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.in.CompaniesRequestDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.out.CompaniesResponseDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.out.CompanyResponseDto;

@ExtendWith(MockitoExtension.class)
class CompaniesControllerTest {

  @InjectMocks
  private CompaniesController companiesController;
  @Mock
  private CompaniesProfilePort companiesProfileService;
  @Mock
  private CompaniesMapper companiesMapper;
  @Mock
  private CdhCompanyPort companyPort;


  @Test
  void getCompaniesProfile__HappyPath() {
    //Arrange
    var requestDto = CompaniesProfileRequestDto.builder()
        .hotelId("TEST")
        .companyName("WB")
        .arNumber("AR-13")
        .limit(5)
        .build();
    var companiesProfileRequest = CompaniesProfileRequest.builder()
        .hotelId("TEST")
        .companyName("WB")
        .arNumber("AR-13")
        .limit(5)
        .build();
    var companiesProfile = CompaniesProfile.builder().totalResults(0).build();
    var companiesResponseDto = CompaniesResponseDto.builder().totalResults(0).build();

    when(companiesMapper.toCompaniesProfileRequestModel(requestDto))
        .thenReturn(companiesProfileRequest);
    when(companiesProfileService.getCompaniesProfile(companiesProfileRequest))
        .thenReturn(companiesProfile);
    when(companiesMapper.toCompaniesProfileDto(companiesProfile))
        .thenReturn(companiesResponseDto);
    //Act
    var companiesProfileResult = companiesController.getCompaniesProfile(requestDto);
    //Assert
    assertThat(companiesProfileResult).usingRecursiveComparison()
        .isEqualTo(companiesResponseDto);
    verifyNoMoreInteractions(companiesMapper);
    verifyNoMoreInteractions(companiesProfileService);
  }


  @ParameterizedTest
  @MethodSource("argsProviderFactory")
  void getCompanyProfile__HappyPath(Boolean argument) {
    //Arrange
    var request = "123456";
    if( null != argument && Boolean.TRUE.equals(argument)) {
      when(companiesProfileService.getCompanyProfile(request, true))
          .thenReturn(CompanyProfile.builder().build());
    }else{
      when(companiesProfileService.getCompanyProfile(request, false))
          .thenReturn(CompanyProfile.builder().build());
    }
    when(companiesMapper.toCompanyResponseDto(any(CompanyProfile.class)))
        .thenReturn(CompanyResponseDto.builder().build());
    //Act
    var response = companiesController.getCompany(request, argument);
    //Assert
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(CompanyResponseDto.builder().build());
    verifyNoMoreInteractions(companiesMapper);
    verifyNoMoreInteractions(companiesProfileService);
  }

  @Test
  void getCompaniesFromCdh() {
    var request = CompaniesRequestDto.builder()
        .companyName("test")
        .build();

    var companiesSearchRequest = CompaniesSearchRequest.builder()
        .pageSize(50)
        .pageNumber(1)
        .companyName("test").build();
    var companiesResponseDto =  new CompaniesResponseDto();
    companiesResponseDto.setPageSize(50);

    when(companiesMapper.toCompaniesSearchRequestModel(request)).thenReturn(companiesSearchRequest);
    when(companyPort.getCompaniesFromCdh(companiesSearchRequest)).thenReturn(CompaniesProfile.builder()
        .build());
    when(companiesMapper.toCompaniesProfileDto(any(CompaniesProfile.class))).thenReturn(
        companiesResponseDto
    );

    var respone = companiesController.getCompaniesFromCdh(request);
    assertThat(respone).usingRecursiveComparison()
        .isEqualTo(companiesResponseDto);
    verifyNoMoreInteractions(companiesMapper);
    verifyNoMoreInteractions(companyPort);
  }

  @Test
  void getCompanyByCorpCompId__HappyPath() {
    var request = "corp-compId";
    var companyProfile = CompanyProfile.builder().build();
    var companyResponseDto =  CompanyResponseDto.builder().build();

    when(companiesProfileService.getCompanyWithNegotiatedRatesById(request)).thenReturn(companyProfile);
    when(companiesMapper.toCompanyResponseDto(any(CompanyProfile.class))).thenReturn(companyResponseDto);

    var response = companiesController.getCompanyById(request);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(companyResponseDto);
    verifyNoMoreInteractions(companiesMapper);
    verifyNoMoreInteractions(companyPort);
  }

  @Test
  void getCompanyByOperaId__HappyPath() {
    var operaId = "opera-123";
    var companyProfile = CompanyProfile.builder().build();
    var companyResponseDto = CompanyResponseDto.builder().build();

    when(companiesProfileService.getCompanyByOperaId(operaId)).thenReturn(companyProfile);
    when(companiesMapper.toCompanyResponseDto(any(CompanyProfile.class))).thenReturn(companyResponseDto);

    var response = companiesController.getCompanyByOperaId(operaId);
    assertThat(response).usingRecursiveComparison()
        .isEqualTo(companyResponseDto);
    verifyNoMoreInteractions(companiesMapper);
    verifyNoMoreInteractions(companiesProfileService);
  }

  static Stream<Boolean> argsProviderFactory() {
    return Stream.of(Boolean.FALSE, Boolean.TRUE, null);
  }
}