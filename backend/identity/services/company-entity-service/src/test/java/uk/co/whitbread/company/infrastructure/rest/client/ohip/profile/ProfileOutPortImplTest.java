package uk.co.whitbread.company.infrastructure.rest.client.ohip.profile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.company.domain.model.in.CompaniesProfileRequest;
import uk.co.whitbread.company.domain.model.out.CompaniesProfile;
import uk.co.whitbread.company.domain.model.out.CompanyProfile;
import uk.co.whitbread.company.infrastructure.rest.client.ohip.mapper.ProfileOhipMapper;
import uk.co.whitbread.ohip.generated.models.CompaniesProfileDto;
import uk.co.whitbread.ohip.generated.models.CompanyProfileDto;
import uk.co.whitbread.ohip.generated.models.NegotiatedRateDto;
import uk.co.whitbread.ohip.generated.models.NegotiatedRatesResponseDto;

@ExtendWith(MockitoExtension.class)
class ProfileOutPortImplTest {

  @InjectMocks
  private ProfileOutPortImpl profileOutPort;
  @Mock
  private OhipProfileClient ohipProfileClient;
  @Mock
  private ProfileOhipMapper profileOhipMapper;

  @Test
  void getCompaniesProfile__HappyPath() {
    //Arrange
    var companiesProfileRequest = CompaniesProfileRequest.builder()
        .hotelId("TEst").limit(50).build();
    var companiesProfileDto = new CompaniesProfileDto();
    var companyProfileDto = new CompanyProfileDto();
    companyProfileDto.setName("WB");
    companyProfileDto.corpId("1235");
    companyProfileDto.language("en");
    companiesProfileDto.companies(List.of(companyProfileDto));
    var companiesProfile = CompaniesProfile.builder()
        .companies(List.of(CompanyProfile.builder()
            .name("WB")
            .corpId("1235")
            .language("en")
            .build()))
        .build();

    when(ohipProfileClient.getCompaniesProfile(companiesProfileRequest.getHotelId(),
        companiesProfileRequest.getArNumber(), companiesProfileRequest.getCompanyName(),
        companiesProfileRequest.getLimit()))
        .thenReturn(companiesProfileDto);
    when(profileOhipMapper.toCompaniesProfileRequestDto(
        companiesProfileDto))
        .thenReturn(companiesProfile);
    //Act
    var companiesProfileResult = profileOutPort.getCompaniesProfile(companiesProfileRequest);
    //Assert
    assertThat(companiesProfileResult).usingRecursiveComparison()
        .isEqualTo(companiesProfile);
    verifyNoMoreInteractions(ohipProfileClient);
    verifyNoMoreInteractions(profileOhipMapper);
  }


  @ParameterizedTest
  @MethodSource("argsProviderFactory")
  void getCompanyProfile__HappyPath(Boolean input) {
    //Arrange
    var request = "123456";

    var negotiatedRatesResponseDto = new NegotiatedRatesResponseDto();
    NegotiatedRateDto negotiatedRateDto = new NegotiatedRateDto();
    negotiatedRatesResponseDto.setNegotiatedRates(List.of(negotiatedRateDto));

    when(ohipProfileClient.getCompanyProfileByCorporateId(request))
        .thenReturn(new CompanyProfileDto().companyId("6789"));
    when(profileOhipMapper.toCompanyProfileDto(any(CompanyProfileDto.class)))
        .thenReturn(CompanyProfile.builder().companyId("6789").build());
    if (!Boolean.TRUE.equals(input)) {
      when(ohipProfileClient.getNegotiatedRatesForCompanyProfile("6789")).thenReturn(
          negotiatedRatesResponseDto);
    }
    //Act
    var response = profileOutPort.getCompanyProfileByCorporateId(request, input.booleanValue());
    //Assert
    if (!Boolean.TRUE.equals(input)) {
      assertThat(response).usingRecursiveComparison()
          .isEqualTo(
              CompanyProfile.builder().companyId("6789").negotiatedRateEnabled(true).build());
    } else {
      assertThat(response).usingRecursiveComparison()
          .isEqualTo(
              CompanyProfile.builder().companyId("6789").build());
    }
    verifyNoMoreInteractions(ohipProfileClient);
    verifyNoMoreInteractions(profileOhipMapper);
  }

  @ParameterizedTest
  @MethodSource("argsProviderFactory")
  void getCompanyProfileById__HappyPath(Boolean input) {
    //Arrange
    var request = "123456";

    var negotiatedRatesResponseDto = new NegotiatedRatesResponseDto();
    NegotiatedRateDto negotiatedRateDto = new NegotiatedRateDto();
    negotiatedRatesResponseDto.setNegotiatedRates(List.of(negotiatedRateDto));

    when(ohipProfileClient.getCompanyProfileByCompanyId(request))
        .thenReturn(new CompanyProfileDto().companyId("6789"));
    when(profileOhipMapper.toCompanyProfileDto(any(CompanyProfileDto.class)))
        .thenReturn(CompanyProfile.builder().companyId("6789").build());
    if (!Boolean.TRUE.equals(input)) {
      when(ohipProfileClient.getNegotiatedRatesForCompanyProfile("6789")).thenReturn(
          negotiatedRatesResponseDto);
    }
    //Act
    var response = profileOutPort.getCompanyProfileByCompanyId(request, input.booleanValue());
    //Assert
    if (!Boolean.TRUE.equals(input)) {
      assertThat(response).usingRecursiveComparison()
          .isEqualTo(
              CompanyProfile.builder().companyId("6789").negotiatedRateEnabled(true).build());
    } else {
      assertThat(response).usingRecursiveComparison()
          .isEqualTo(
              CompanyProfile.builder().companyId("6789").build());
    }
    verifyNoMoreInteractions(ohipProfileClient);
    verifyNoMoreInteractions(profileOhipMapper);
  }

  static Stream<Boolean> argsProviderFactory() {
    return Stream.of(false, true);
  }
}