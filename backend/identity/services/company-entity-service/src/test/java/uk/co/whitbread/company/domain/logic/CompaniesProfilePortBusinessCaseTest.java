package uk.co.whitbread.company.domain.logic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import uk.co.whitbread.company.domain.ports.secondary.ProfileOutPort;

@ExtendWith(MockitoExtension.class)
class CompaniesProfilePortBusinessCaseTest {

  @InjectMocks
  private CompaniesProfilePortBusinessCase companiesProfilePort;
  @Mock
  private ProfileOutPort profileOutPort;

  @Test
  void getCompaniesProfile__HappyPath() {
    //Arrange
    var companiesProfileRequest = CompaniesProfileRequest.builder()
        .hotelId("TEst").limit(50).build();
    var companiesProfile = CompaniesProfile.builder()
        .companies(List.of(CompanyProfile.builder()
            .name("WB")
            .corpId("1235")
            .language("en")
            .build()))
        .build();

    when(profileOutPort.getCompaniesProfile(companiesProfileRequest))
        .thenReturn(companiesProfile);
    //Act

    var companiesProfileResult = companiesProfilePort.getCompaniesProfile(companiesProfileRequest);
    //Assert

    assertThat(companiesProfileResult).usingRecursiveComparison()
        .isEqualTo(companiesProfile);
    verifyNoMoreInteractions(profileOutPort);
  }

  @Test
  void getCompanyProfileByCorporateId__HappyPath() {
    //Arrange
    var companiesProfileRequest = "compId";
    var companyProfile = createCompanyProfile(true);
    when(profileOutPort.getCompanyProfileByCorporateId(companiesProfileRequest, false))
        .thenReturn(companyProfile);

    //Act
    var companiesProfileResult = companiesProfilePort.getCompanyWithNegotiatedRatesById(companiesProfileRequest);

    //Assert
    assertThat(companiesProfileResult).usingRecursiveComparison()
        .isEqualTo(companyProfile);
    verify(profileOutPort).getCompanyProfileByCorporateId(companiesProfileRequest, false);
    verify(profileOutPort, times(0)).getCompanyProfileByCompanyId(companiesProfileRequest, false);
  }

  @Test
  void getCompanyProfileByCompanyId__HappyPath() {
    //Arrange
    var companiesProfileRequest = "compId";
    var companyProfile = createCompanyProfile(true);
    when(profileOutPort.getCompanyProfileByCorporateId(companiesProfileRequest, false))
        .thenReturn(CompanyProfile.builder().build());
    when(profileOutPort.getCompanyProfileByCompanyId(companiesProfileRequest, false))
        .thenReturn(companyProfile);
    //Act
    var companiesProfileResult = companiesProfilePort.getCompanyWithNegotiatedRatesById(companiesProfileRequest);

    //Assert
    assertThat(companiesProfileResult).usingRecursiveComparison()
        .isEqualTo(companyProfile);
    verify(profileOutPort, times(1)).getCompanyProfileByCorporateId(companiesProfileRequest, false);
    verify(profileOutPort, times(1)).getCompanyProfileByCompanyId(companiesProfileRequest, false);

  }

  @Test
  void getCompanyProfileById__NegotiatedRatesNotSet() {
    //Arrange
    var companiesProfileRequest = "compId";
    var companyProfile = createCompanyProfile(false);
    when(profileOutPort.getCompanyProfileByCorporateId(companiesProfileRequest, false))
        .thenReturn(companyProfile);
    //Act
    var companiesProfileResult = companiesProfilePort.getCompanyWithNegotiatedRatesById(companiesProfileRequest);

    //Assert
    assertThat(companiesProfileResult).usingRecursiveComparison()
        .isEqualTo(null);
    verify(profileOutPort, times(1)).getCompanyProfileByCorporateId(companiesProfileRequest, false);
    verify(profileOutPort, times(0)).getCompanyProfileByCompanyId(companiesProfileRequest, false);
  }

  @Test
  void getCompanyProfileById__NotFound() {
    //Arrange
    var companiesProfileRequest = "compId";
    when(profileOutPort.getCompanyProfileByCorporateId(companiesProfileRequest, false))
        .thenReturn(null);
    when(profileOutPort.getCompanyProfileByCorporateId(companiesProfileRequest, false))
        .thenReturn(null);
    //Act
    var companiesProfileResult = companiesProfilePort.getCompanyWithNegotiatedRatesById(companiesProfileRequest);

    //Assert
    assertThat(companiesProfileResult).usingRecursiveComparison()
        .isEqualTo(null);
    verify(profileOutPort, times(1)).getCompanyProfileByCorporateId(companiesProfileRequest, false);
  }

  @ParameterizedTest
  @MethodSource("argsProviderFactory")
  void getCompanyProfile__HappyPath(boolean argument) {
    //Arrange
    var request = "corporateId";

    when(profileOutPort.getCompanyProfileByCorporateId(request, argument))
        .thenReturn(CompanyProfile.builder().build());
    //Act

    var response = companiesProfilePort.getCompanyProfile(request, argument);
    //Assert

    assertThat(response).usingRecursiveComparison()
        .isEqualTo(CompanyProfile.builder().build());
    verifyNoMoreInteractions(profileOutPort);
  }

  @Test
  void getCompanyByOperaId__HappyPath() {
    //Arrange
    var operaId = "opera-123";
    var companyProfile = createCompanyProfile(true);

    when(profileOutPort.getCompanyProfileByCompanyId(operaId, true))
        .thenReturn(companyProfile);

    //Act
    var result = companiesProfilePort.getCompanyByOperaId(operaId);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .isEqualTo(companyProfile);
    verify(profileOutPort, times(1)).getCompanyProfileByCompanyId(operaId, true);
    verifyNoMoreInteractions(profileOutPort);
  }

  static Stream<Boolean> argsProviderFactory() {
    return Stream.of(false, true);
  }

  private CompanyProfile createCompanyProfile(boolean negotiatedRateEnabled) {
    return CompanyProfile.builder()
        .name("WB")
        .corpId("1235")
        .companyId("1235")
        .language("en")
        .negotiatedRateEnabled(negotiatedRateEnabled)
        .build();
  }
}