package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeEmails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeAddresses;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.in.BillingAddressRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerAddress;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerAddressCnp;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnp;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnpRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.AddressTypeResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.EmailTypeResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ProfileIdResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ProfileType;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BookerProfileOhipMapperImpl.class)
class BookerProfileOhipMapperTest {

  @Autowired
  private BookerProfileOhipMapper bookerProfileOhipMapper;

  @Test
  void createSaveProfileRequest__ShouldReturnOK() {

    //Arrange
    var profileType = createProfileType();
    var bookerDetailsCnpRequest = createBookerDetailsCnpRequest();

    //Act
    var profileRequest = bookerProfileOhipMapper.toDto(profileType, bookerDetailsCnpRequest);

    //Assert
    assertNotNull(profileRequest);

    assertEquals("123456", profileRequest.getProfileIdList().get(0).getId());
    assertEquals("Profile", profileRequest.getProfileIdList().get(0).getType());
    assertEquals("123456", profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getId());
    assertEquals("HOME", profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getType());
    assertEquals("London",
        profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getCityName());
    assertEquals("WC4 324",
        profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getPostalCode());
    assertEquals("addressLine1",
        profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(0));
    assertEquals("addressLine2",
        profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(1));
    assertEquals("addressLine3",
        profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(2));
    assertEquals("addressLine4",
        profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(3));

    assertEquals("123456", profileRequest.getProfileDetails().getEmails().getEmailInfo().get(0).getId());
    assertEquals("HOME", profileRequest.getProfileDetails().getEmails().getEmailInfo().get(0).getType());
    assertEquals("email@whitbread.com",
        profileRequest.getProfileDetails().getEmails().getEmailInfo().get(0).getEmail().getEmailAddress());
  }

  @Test
  void updateBillingAddRequest__ShouldReturnOK() {

    //Arrange
    var profileType = createProfileType();
    var billingAddressRequest = updateBillingAddressRequest();

    //Act
    var profileRequest = bookerProfileOhipMapper.toBillingDto(profileType, billingAddressRequest);

    //Assert
    assertNotNull(profileRequest);

    assertEquals("123456", profileRequest.getProfileIdList().get(0).getId());
    assertEquals("Profile", profileRequest.getProfileIdList().get(0).getType());

    assertEquals("123456", profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getId());
    assertEquals("HOME", profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getType());
    assertEquals("London",
        profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getCityName());
    assertEquals("MZC AD",
        profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getPostalCode());
    assertEquals("addressLine1",
        profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(0));
    assertEquals("addressLine2",
        profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(1));
    assertEquals("addressLine3",
        profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(2));
    assertEquals("addressLine4",
        profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(3));

  }

  @Test
  void updateBillingAddressRequest__ShouldReturnException(){
    var profileType = createProfileType();
    BillingAddressRequest billingAddressRequest = null;

    assertThrows(NullPointerException.class,() ->bookerProfileOhipMapper.toBillingDto(profileType, billingAddressRequest));
  }

  @Test
  void updateBillingAddressRequest__ShouldReturnOK() {

    //Arrange
    var profileType = createProfileType();
    var billingAddressRequest = updateBillingAddressRequestWhenCityNameEqualsAddressLine4();

    //Act
    var profileRequest = bookerProfileOhipMapper.toBillingDto(profileType, billingAddressRequest);

    //Assert
    assertNotNull(profileRequest);

    assertEquals("123456", profileRequest.getProfileIdList().get(0).getId());
    assertEquals("Profile", profileRequest.getProfileIdList().get(0).getType());

    assertEquals("123456", profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getId());
    assertEquals("HOME", profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getType());
    assertEquals("London",
            profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getCityName());
    assertEquals("MZC AD",
            profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getPostalCode());
    assertEquals("addressLine1",
            profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(0));
    assertEquals("addressLine2",
            profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(1));
    assertEquals("addressLine3",
            profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(2));
      assertEquals(3, profileRequest.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().size());

  }

  @Test
  void createSaveProfileResponse__ShouldReturnOK() {

    //Arrange
    var profile = createProfile();

    //Act
    var profileResponse = bookerProfileOhipMapper.toModel(profile);

    //Assert
    assertNotNull(profileResponse);

    assertEquals("123456", profileResponse.getProfileId().getId());
    assertEquals("Profile", profileResponse.getProfileId().getType());

    assertEquals("123456", profileResponse.getEmail().getId());
    assertEquals("HOME", profileResponse.getEmail().getType());

    assertEquals("123456", profileResponse.getAddress().getId());
    assertEquals("HOME", profileResponse.getAddress().getType());
    assertEquals("London", profileResponse.getAddress().getCityName());
  }

  private BillingAddressRequest updateBillingAddressRequest() {

    final BookerAddress address = BookerAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("addressLine1")
        .addressLine2("addressLine2")
        .addressLine3("addressLine3")
        .addressLine4("addressLine4")
        .cityName("London")
        .countryCode("UK")
        .build();
    return BillingAddressRequest.builder()
        .hotelId("LONEUS")
        .reservationIds(List.of("1234"))
        .booker(BookerDetails.builder()
            .firstName("Emma")
            .lastName("Watson")
            .address(address)
            .build())
        .build();
  }

  private BillingAddressRequest updateBillingAddressRequestWhenCityNameEqualsAddressLine4() {

    final BookerAddress address = BookerAddress.builder()
            .postalCode("MZC AD")
            .addressType("HOME")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .addressLine3("addressLine3")
            .addressLine4("London")
            .cityName("London")
            .countryCode("UK")
            .build();
    return BillingAddressRequest.builder()
            .hotelId("LONEUS")
            .reservationIds(List.of("1234"))
            .booker(BookerDetails.builder()
                    .firstName("Emma")
                    .lastName("Watson")
                    .address(address)
                    .build())
            .build();
  }

  private Profile createProfile() {
    var profile = new Profile();
    var profileDetails = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();

    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setId("123456");
    uniqueIdType.setType("Profile");
    profile.setProfileIdList(List.of(uniqueIdType));

    var addressType = new AddressType();
    addressType.setAddressLine(Stream.of("addressLine1", "addressLine2", "addressLine3", "addressLine4").toList());
    addressType.setType("HOME");
    addressType.setCityName("London");
    addressType.setPostalCode("WC4 324");

    AddressInfoType addressInfoType = new AddressInfoType();
    addressInfoType.setAddress(addressType);
    addressInfoType.setId("123456");

    var profileTypeAddresses = new ProfileTypeAddresses();
    profileTypeAddresses.setAddressInfo(List.of(addressInfoType));
    profileDetails.setAddresses(profileTypeAddresses);

    var emailType = new EmailType();
    emailType.setEmailAddress("email@whitbread.com");
    var emailInfoType = new EmailInfoType();
    emailInfoType.setEmail(emailType);
    emailInfoType.setId("123456");
    emailInfoType.setType("HOME");
    var companyProfileTypeEmails = new CompanyProfileTypeEmails();
    companyProfileTypeEmails.setEmailInfo(List.of(emailInfoType));
    profileDetails.setEmails(companyProfileTypeEmails);

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
        .address(AddressTypeResponse.builder().id("123456").type("HOME").cityName("London").build())
        .email(EmailTypeResponse.builder().id("123456").type("HOME").build())
        .build();
  }
}
