package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerAddressCnp;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnp;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnpRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.ProfileIdResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ProfileType;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CompanyProfileOhipMapperImpl.class)
class CompanyProfileOhipMapperTest {

  @Autowired
  private CompanyProfileOhipMapper companyProfileOhipMapper;

  @Test
  void createSaveProfileRequest__ShouldReturnOK() {

    //Arrange
    var profileType = createProfileType();
    var bookerDetailsCnpRequest = createBookerDetailsCnpRequest();

    //Act
    var profileRequest = companyProfileOhipMapper.toDto(profileType, bookerDetailsCnpRequest);

    //Assert
    assertNotNull(profileRequest);

    assertEquals("123456", profileRequest.getProfileIdList().get(0).getId());
    assertEquals("Profile", profileRequest.getProfileIdList().get(0).getType());

    assertEquals("Whitbread", profileRequest.getProfileDetails().getCompany().getCompanyName());

  }

  @Test
  void createSaveProfileResponse__ShouldReturnOK() {

    //Arrange
    var profile = createProfile();

    //Act
    var profileResponse = companyProfileOhipMapper.toModel(profile);

    //Assert
    assertNotNull(profileResponse);

    assertEquals("123456", profileResponse.getProfileId().getId());
    assertEquals("Profile", profileResponse.getProfileId().getType());

  }

  private Profile createProfile() {
    var profile = new Profile();
    var profileDetails = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();

    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setId("123456");
    uniqueIdType.setType("Profile");
    profile.setProfileIdList(List.of(uniqueIdType));

    var companyType = new CompanyType();
    companyType.setCompanyName("Whitbread");
    profileDetails.setCompany(companyType);

    profile.setProfileDetails(profileDetails);

    return profile;
  }

  private BookerDetailsCnpRequest createBookerDetailsCnpRequest() {

    return BookerDetailsCnpRequest.builder()
        .hotelId("LONEUS")
        .booker(BookerDetailsCnp.builder()
            .companyName("Whitbread")
            .emailAddress("email@whitbread.com")
            .address(BookerAddressCnp.builder()
                .addressLine1("addressLine1")
                .addressLine2("addressLine2")
                .addressLine3("addressLine3")
                .addressLine4("addressLine4")
                .postalCode("WC4 324")
                .build())
            .build())
        .build();
  }

  private ProfileType createProfileType() {
    return ProfileType.builder()
        .profileId(ProfileIdResponse.builder().id("123456").type("Profile").build())
        .build();
  }
}
