package uk.co.whitbread.ohip.domain.logic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.notNullValue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.profile.ProfileTestUtils;
import uk.co.whitbread.ohip.domain.model.profile.in.CompaniesProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.UpdateProfileRequest;
import uk.co.whitbread.ohip.domain.ports.secondary.ProfileOutPort;

@ExtendWith(MockitoExtension.class)
class ProfileInPortImplTest {

  @InjectMocks
  private ProfileInPortImpl profileInPort;

  @Mock
  private ProfileOutPort profileOutPort;

  @Test
  void createProfile__Success() {
    //Arrange
    var request = ProfileTestUtils.mockStayingGuestDetails();
    when(profileOutPort.createProfile(any(), anyString(), anyString())).thenReturn(
        Arrays.asList("12345", "46382"));

    //Act
    var response = profileInPort.createProfile(request, "HOTEL_ID", "123456");

    //Assert
    assertThat(response, notNullValue());

    verifyNoMoreInteractions(profileOutPort);

  }

  @Test
  void addProfile__Success() {
    //Arrange
    var request = ProfileTestUtils.mockAddProfileRequest();

    //Act
    profileInPort.addProfile(request, "HOTEL_ID");

    //Assert
    verify(profileOutPort, times(1)).addProfile(any(), anyString());
  }

  @Test
  void updateProfile__Success() {
    //Arrange
    var request = UpdateProfileRequest.builder().build();

    //Act
    profileInPort.updateProfile("HOTEL_ID", "1234", request);

    //Assert
    verify(profileOutPort, times(1)).updateProfile(anyString(), anyString(), any());
  }

  @Test
  void getCompaniesProfile__Success() {
    //Arrange
    var request = CompaniesProfileRequest.builder().hotelId("TEST").limit(1).build();

    //Act
    profileInPort.getCompaniesProfile(request);

    //Assert
    verify(profileOutPort, times(1)).getCompaniesProfile(any());
  }

  @Test
  void getCompanyProfileByCorporateId__Success() {
    //Arrange
    var corporateId = "corporateId";

    //Act
    profileInPort.getCompanyProfileByCorporateId(corporateId);

    //Assert
    verify(profileOutPort, times(1)).getCompanyProfileByCorporateId(any());
  }
}
