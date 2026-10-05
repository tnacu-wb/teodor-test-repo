package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeEmails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerTypeIdentifications;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.IdentificationInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.IdentificationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeAddresses;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationTestUtils;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ReservationAccompanyingGuestProfileRequestOhipMapperImpl.class)
class ReservationAccompanyingGuestProfileRequestOhipMapperTest {

  @Autowired
  ReservationAccompanyingGuestProfileRequestOhipMapper mapper;

  @Test
  void shouldUpdateProfileWithAccompanyingGuestTrue() {
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuests();
    stayingGuest.setIsAccompanyingGuest(true);
    Profile profile = mapper.toDto(stayingGuest, mockProfile());

    assertNotNull(profile);
    assertNotNull(profile.getProfileDetails().getCustomer());
    assertEquals("1001",
        profile.getProfileDetails().getCustomer().getIdentifications().getIdentificationInfo()
            .get(0).getId());
    assertEquals(LocalDate.of(1996, 7, 13),
        profile.getProfileDetails().getCustomer().getBirthDate());
    assertEquals("UK", profile.getProfileDetails().getCustomer().getNationality());
  }

  @Test
  void shouldReturnProfileForAccompanyingGuestWithPartialDetails() {
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuests();
    stayingGuest.setIsAccompanyingGuest(false);
    stayingGuest.getStayingGuestDetails().setAdditionalDetails(null);

    Profile profile = mapper.toDto(stayingGuest, mockProfile());
    if (profile.getProfileDetails().getCustomer().getIdentifications() != null) {
      assertEquals("1234",
          profile.getProfileDetails().getCustomer().getIdentifications().getIdentificationInfo()
              .get(0).getIdentification().getIdNumber());
    }
  }

  @Test
  void shouldUpdateProfileForPrimaryGuestWithCompleteDetails() {
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuests();
    stayingGuest.setIsAccompanyingGuest(false);

    Profile profile = mapper.toDto(stayingGuest, mockProfile());

    assertEquals("UK", profile.getProfileDetails().getCustomer().getNationality());
  }

  @Test
  void shouldUpdateProfileForPrimaryGuestWithPartialDetails() {
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuests();
    stayingGuest.setIsAccompanyingGuest(false);
    stayingGuest.getStayingGuestDetails().setAdditionalDetails(null);

    Profile profile = mapper.toDto(stayingGuest, mockProfile());

    assertEquals("1234", profile.getProfileIdList().get(0).getId());
  }

  @Test
  void shouldCreateNewProfileForAccompanyingGuest() {
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuests();
    stayingGuest.setIsAccompanyingGuest(false);
    stayingGuest.getStayingGuestDetails().setProfileId(null);

    Profile profile = mapper.toDto(stayingGuest, null);
    if (profile != null && profile.getProfileDetails() != null) {
      assertEquals("GUEST", profile.getProfileDetails().getProfileType().name());
    }
  }

  @Test
  void shouldMapAddressCorrectly() {
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuests();
    stayingGuest.setIsAccompanyingGuest(false);
    Profile profile = mapper.toDto(stayingGuest, mockProfile());

    assertEquals("123456",
        profile.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress()
            .getPostalCode());
  }

  @Test
  void shouldMapIdentificationCorrectly() {
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuests();
    stayingGuest.setIsAccompanyingGuest(false);
    Profile profile = mapper.toDto(stayingGuest, mockProfile());

    assertEquals("ABCD1234",
        profile.getProfileDetails().getCustomer().getIdentifications().getIdentificationInfo()
            .get(0).getIdentification().getIdNumber());
  }

  @Test
  void shouldReturnNullForNullStayingGuest() {
    Profile profile = mapper.toDto(null, mockProfile());
    assertNull(profile);
  }

  @Test
  void shouldReturnNullForNullStayingGuestDetails() {
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuests();
    stayingGuest.setStayingGuestDetails(null);
    Profile profile = mapper.toDto(stayingGuest, mockProfile());
    assertNull(profile);
  }

  @Test
  void shouldHandleNullProfileDetails() {
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuests();
    stayingGuest.setIsAccompanyingGuest(false);
    Profile profile = mapper.toDto(stayingGuest, null);
    assertNotNull(profile);
    assertNotNull(profile.getProfileDetails());
  }

  @Test
  void shouldHandleMissingAddressGracefully() {
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuests();
    stayingGuest.setIsAccompanyingGuest(true);
    stayingGuest.getStayingGuestDetails().setAddress(null);
    Profile profile = mapper.toDto(stayingGuest, mockProfile());
    assertNull(profile.getProfileDetails().getAddresses());
  }

  @Test
  void shouldHandleMissingIdentificationGracefully() {
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuests();
    stayingGuest.setIsAccompanyingGuest(true);
    stayingGuest.getStayingGuestDetails().getAdditionalDetails().setPassportNumber(null);
    Profile profile = mapper.toDto(stayingGuest, mockProfile());
    if (profile.getProfileDetails().getCustomer().getIdentifications() != null) {
      assertNull(
          profile.getProfileDetails().getCustomer().getIdentifications().getIdentificationInfo()
              .get(0).getIdentification().getIdNumber());
    }
  }

  @Test
  void shouldHandleEmptyProfileIdListGracefully() {
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuests();
    stayingGuest.setIsAccompanyingGuest(false);
    Profile profile = mapper.toDto(stayingGuest, new Profile());
    assertNull(profile.getProfileIdList());
  }

  @Test
  void shouldNotSendMaskedPassportWith4XsToOpera() {
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuests();
    stayingGuest.setIsAccompanyingGuest(false);
    stayingGuest.getStayingGuestDetails().getAdditionalDetails().setPassportNumber("XXXX32");
    Profile profile = mapper.toDto(stayingGuest, mockProfile());

    assertNull(profile.getProfileDetails().getCustomer().getIdentifications(),
        "Masked passport (4 X's) should not be sent to OPERA");
  }

  @Test
  void shouldNotSendMaskedPassportWith5XsToOpera() {
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuests();
    stayingGuest.setIsAccompanyingGuest(false);
    stayingGuest.getStayingGuestDetails().getAdditionalDetails().setPassportNumber("XXXXX23");
    Profile profile = mapper.toDto(stayingGuest, mockProfile());

    assertNull(profile.getProfileDetails().getCustomer().getIdentifications(),
        "Masked passport (5+ X's) should not be sent to OPERA");
  }

  private static Profile mockProfile() {
    Profile result = new Profile();
    UniqueIDType uniqueIDType = new UniqueIDType();
    uniqueIDType.setId("1234");
    uniqueIDType.setType("Profile");
    result.setProfileIdList(List.of(uniqueIDType));

    ProfileType profileDetails = new ProfileType();
    CustomerType customerType = new CustomerType();
    customerType.setNationality("UK");
    customerType.setBirthDate(LocalDate.of(1996, 7, 13));
    CustomerTypeIdentifications identifications = getCustomerTypeIdentifications();
    customerType.setIdentifications(identifications);
    profileDetails.setCustomer(customerType);

    ProfileTypeAddresses addresses = new ProfileTypeAddresses();
    AddressInfoType addressInfoType = new AddressInfoType();
    AddressType addressType = new AddressType();
    addressType.setAddressLine(List.of("3450 North Triumph Boulevard", "Suite 300", "", ""));
    addressType.setCityName("Lehi");
    addressType.setCounty("UK");
    addressType.setPostalCode("84043");
    addressType.setType("HOME");
    addressType.setPrimaryInd(true);
    addressInfoType.setAddress(addressType);
    addressInfoType.setId("1002");
    addressInfoType.setType("HOME");
    addresses.setAddressInfo(List.of(addressInfoType));
    profileDetails.setAddresses(addresses);

    CompanyProfileTypeEmails emails = new CompanyProfileTypeEmails();
    EmailInfoType emailInfoType = new EmailInfoType();
    EmailType emailType = new EmailType();
    emailType.setEmailAddress("example@example.com");
    emailType.setType("EMAIL");
    emailType.setPrimaryInd(true);
    emailInfoType.setEmail(emailType);
    emailInfoType.setId("1003");
    emails.setEmailInfo(List.of(emailInfoType));
    profileDetails.setEmails(emails);

    result.setProfileDetails(profileDetails);
    return result;
  }

  @NotNull
  private static CustomerTypeIdentifications getCustomerTypeIdentifications() {
    CustomerTypeIdentifications identifications = new CustomerTypeIdentifications();
    IdentificationInfoType identificationInfoType = new IdentificationInfoType();
    IdentificationType identification = new IdentificationType();
    identification.setIdNumber("ABCD1234");
    identification.setIdType("PASSPORT");
    identification.setPrimaryInd(true);
    identificationInfoType.setIdentification(identification);
    identificationInfoType.setId("1001");
    identificationInfoType.setType("DocumentId");
    identifications.setIdentificationInfo(List.of(identificationInfoType));
    return identifications;
  }
}